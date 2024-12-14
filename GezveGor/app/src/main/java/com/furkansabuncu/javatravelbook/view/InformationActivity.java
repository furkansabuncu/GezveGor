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

import com.furkansabuncu.javatravelbook.databinding.ActivityGeziEklemeBinding;
import com.furkansabuncu.javatravelbook.databinding.ActivityInformationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.Timestamp;


import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class InformationActivity extends AppCompatActivity {
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    Uri imageData;
    private ProgressDialog progressDialog;

    Timestamp timestamp;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    ActivityInformationBinding binding;
    Bitmap selectedBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding
        binding=ActivityInformationBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        // binding sonu


        //Launcherler onCreate kısmında tanımlanmalı yoksa uygulama çöker
        registerLauncher();
        //Launcher tanımlama sonu


        //tanımlamalar
        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();
        //tanımlamalar sonu

    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void profilResmiEkle(View view) {

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
    public void addProfile(View view){
        showLoadingIndicator();
        if(imageData!=null){
            // resim eklerken her seferinde image.jpg olarak atadığı için farklı resim kyounca öncekinin üsttüne
            // gidiyor bunu çözmek için uuid yapısını kullanmamız gerekir
            UUID uuid=UUID.randomUUID();
            String imageName="profileimages/"+uuid+".jpg";

            //klasor oluşturalım direkk koymamız iyi olamz
            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference newReference=firebaseStorage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl=uri.toString();
                            String name =binding.nameText.getText().toString();
                            String surname=binding.surnameText3.getText().toString();
                            String username=binding.usernameText.getText().toString();
                            FirebaseUser user=auth.getCurrentUser();

                            String email=user.getEmail();


                            //anahtar string , değer herhangi bir şey olabilir sadece string kayıtı olmucak
                            HashMap<String,Object> postData=new HashMap<>();
                            postData.put("email",email);
                            postData.put("downloadurl",downloadUrl);
                            postData.put("name",name);
                            postData.put("surname",surname);
                            postData.put("username",username);
                            //güncel tarih
                            //postData.put("date", FieldValue.serverTimestamp());
                            timestamp=Timestamp.now();
                            postData.put("registerdate",timestamp );

                            firebaseFirestore.collection("Profiles").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent=new Intent(InformationActivity.this, MainPage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    hideLoadingIndicator();
                                    startActivity(intent);

                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(InformationActivity.this,"hata",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(InformationActivity.this,"Hata lütfen değerleri giriniz",Toast.LENGTH_LONG).show();
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
                        binding.imageView7.setImageURI(imageData);
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
                    Toast.makeText(InformationActivity.this,"izin gerekli",Toast.LENGTH_LONG).show();
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