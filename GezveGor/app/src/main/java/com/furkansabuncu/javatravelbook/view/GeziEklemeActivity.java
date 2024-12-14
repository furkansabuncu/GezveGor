package com.furkansabuncu.javatravelbook.view;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.furkansabuncu.javatravelbook.databinding.ActivityGeziEklemeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.Timestamp;


import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class GeziEklemeActivity extends AppCompatActivity {
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    Uri imageData;

    Timestamp timestamp;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
     ActivityGeziEklemeBinding binding;
    String fullName;

    Bitmap selectedBitmap;
    String[] sehirler= {"Şehir seçiniz","İstanbul", "Ankara", "İzmir", "Bursa", "Adana", "Antalya", "Konya",
            "Kayseri", "Eskişehir", "Diyarbakır", "Trabzon", "Samsun", "Mersin", "Kocaeli",
            "Gaziantep", "Denizli", "Şanlıurfa", "Manisa", "Hatay", "Balıkesir", "Kahramanmaraş",
            "Malatya", "Van", "Erzurum", "Erzincan", "Tekirdağ", "Rize", "Batman", "Sivas", "Ordu",
            "Aydın", "Aydın", "Muğla", "Kütahya", "Afyonkarahisar", "Osmaniye", "Bolu", "Çanakkale",
            "Yalova", "Aksaray", "Nevşehir", "Kırşehir", "Bartın", "Kırıkkale", "Karaman", "Isparta",
            "Kilis", "Edirne", "Zonguldak", "Niğde", "Karabük", "Çankırı", "Sinop",
            "Giresun", "Gümüşhane", "Tunceli", "Bayburt", "Artvin", "Ardahan", "Iğdır", "Kars"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding
        binding=ActivityGeziEklemeBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        // binding sonu




        // spinnera şehirleri ekleyelim
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(GeziEklemeActivity.this, android.R.layout.simple_spinner_item, sehirler);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner sehir ekleme sonu

        //Launcherler onCreate kısmında tanımlanmalı yoksa uygulama çöker
        registerLauncher();
        //Launcher tanımlama sonu


        //tanımlamalar
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        FirebaseUser user =auth.getCurrentUser();
        storageReference=firebaseStorage.getReference();
        //tanımlamalar sonu

        assert user != null;

        CollectionReference usersRef = firebaseFirestore.collection("Profiles");

        Query query = usersRef.whereEqualTo("email", user.getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Kullanıcı bilgilerini çekme
                        String name = document.getString("name");
                        String surname = document.getString("surname");
                        String username = document.getString("username");
                        String downloadUrl = document.getString("downloadurl");

                        // Bilgileri kullanma

                        fullName=name+" "+surname;



                    }
                } else {
                    Log.w("FirestoreExample", "Error getting documents.", task.getException());
                }
            }
        });


    }



    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void imageYukle(View view) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                Snackbar.make(view,"İzin gereklidir",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //izin iste
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                    }
                }).show();
            }else{
                //izin iste
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        }else{
            //izin verilmiş
            Intent intenToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intenToGallery);
        }
    }
    public void yerEkle(View view){
        showLoadingIndicator();
    if(imageData!=null){
  // resim eklerken her seferinde image.jpg olarak atadığı için farklı resim kyounca öncekinin üsttüne
  // gidiyor bunu çözmek için uuid yapısını kullanmamız gerekir
        UUID uuid=UUID.randomUUID();
        String imageName="images/"+uuid+".jpg";

        //klasor oluşturalım direkk koymamız iyi olamz
    storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        StorageReference newReference=firebaseStorage.getReference(imageName);
        newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
            String dowloadUrl=uri.toString();
            String comment =binding.editTextText.getText().toString();
            FirebaseUser user=auth.getCurrentUser();
            String title=binding.textBaslik.getText().toString();
                assert user != null;
                String email=user.getEmail();


                //anahtar string , değer herhangi bir şey olabilir sadece string kayıtı olmucak
                HashMap<String,Object> postData=new HashMap<>();
                postData.put("useremail",email);
                postData.put("downloadurl",dowloadUrl);
                postData.put("title",title);
                postData.put("comment",comment);
                postData.put("fullName",fullName);

                //güncel tarih
                //postData.put("date", FieldValue.serverTimestamp());
                //odev geregi tarihi kendimiz seçicez
                timestamp=Timestamp.now();
                postData.put("date",timestamp );

               firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                   @Override
                   public void onSuccess(DocumentReference documentReference) {
                    Intent intent=new Intent(GeziEklemeActivity.this, GeziActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    hideLoadingIndicator();
                    finish();
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GeziEklemeActivity.this,"hata",Toast.LENGTH_SHORT).show();
                   }
               });
            }
        });
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
        Toast.makeText(GeziEklemeActivity.this,"Hata lütfen değerleri giriniz",Toast.LENGTH_LONG).show();
        }
    });
    }
    }
    private void registerLauncher() {
        //activityresult launcheri bir sonuc için başlatıyoruz sonucu callback olarak veriyoruz
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if (o.getResultCode() == Activity.RESULT_OK) {
                    Intent intenToResult = o.getData();
                    if (intenToResult != null) {
                        imageData = intenToResult.getData();
                        binding.imageView.setImageURI(imageData);
                /*
                    try{
                        if(Build.VERSION.SDK_INT>=28){

                            ImageDecoder.Source source=ImageDecoder.createSource(getApplication().getContentResolver(),imageData);
                            selectedBitmap=ImageDecoder.decodeBitmap(source);
                            binding.imageView.setImageBitmap(selectedBitmap);
                        }else{
                            selectedBitmap=MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),imageData);
                            binding.imageView.setImageBitmap(selectedBitmap);

                        }

                    }catch (Exception e){

                    }

                 */
                    }
                }
            }
        });
        //izin almak için kullandıgımız yapı
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if(o){
                Intent intenToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intenToGallery);
                }else{
                    Toast.makeText(GeziEklemeActivity.this,"izin gerekli",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void showLoadingIndicator() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Yükleniyor..."); // Yükleme mesajı
        progressDialog.setCancelable(false); // Kullanıcının iptal etmesine izin verilmez
        progressDialog.show(); // Progress Dialog'u göster
    }

    private void hideLoadingIndicator() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss(); // Progress Dialog'u gizle
        }
    }


}