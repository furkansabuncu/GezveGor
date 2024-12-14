package com.furkansabuncu.javatravelbook.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.furkansabuncu.javatravelbook.R;
import com.furkansabuncu.javatravelbook.adapter.CommentAdapter;
import com.furkansabuncu.javatravelbook.adapter.PlacesAdapter;
import com.furkansabuncu.javatravelbook.databinding.ActivityGeziBinding;
import com.furkansabuncu.javatravelbook.databinding.ActivityPlaceDetailsBinding;
import com.furkansabuncu.javatravelbook.model.Comment;
import com.furkansabuncu.javatravelbook.model.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlaceDetails extends AppCompatActivity {

    private ActivityPlaceDetailsBinding binding;
    private double latitude;
    private double longitude;
    private String placeName;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String username;
    private String downloadUrl;
    private String email;
    CommentAdapter adapter;
    Drawable iconNo;
    Drawable iconYes;
    ArrayList < Comment > commentArrayList;
    Boolean go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Drawable icon = getResources().getDrawable(R.drawable.newichonmap);

        binding.button1.setCompoundDrawablesWithIntrinsicBounds(null,null,icon,null);

        iconYes = getResources().getDrawable(R.drawable.newichonyes);
        iconNo = getResources().getDrawable(R.drawable.newichonno);


        binding.button1.setCompoundDrawablesWithIntrinsicBounds(null, null,icon,null);// İkonunuzu burada belirtin


        FirebaseUser user = auth.getCurrentUser();
        email = user.getEmail();

        Intent intent = getIntent();
        Places clickedPlace = (Places) intent.getParcelableExtra("clickedPlace");

        // Aldığımız verileri TextView'lere yerleştirme

        binding.placeTitleTextView.setText(clickedPlace.place);
        binding.placeDescriptionTextView.setText(clickedPlace.comment);
        latitude = clickedPlace.latitude;
        longitude = clickedPlace.longitude;
        Picasso.get().load(clickedPlace.downloadUrl).into(binding.placeImageView);
        placeName = clickedPlace.place;

        // Yorumları göstermek için RecyclerView kurma
        commentArrayList = new ArrayList < > ();
        adapter = new CommentAdapter(commentArrayList);
        binding.recyclerviewComment.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewComment.setAdapter(adapter);
        binding.button2.setText("Gitmediniz");

        getDurum(email, latitude, longitude);

        // Yorumları çek
        fetchComments(latitude, longitude);

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.button2.getText() == "Gitmediniz") {
                    binding.button2.setText("Gittiniz");
                    binding.button2.setCompoundDrawablesWithIntrinsicBounds(null,null,iconYes,null);

                    go = true;

                    addDurum(email, latitude, longitude, go);
                } else {
                    binding.button2.setText("Gitmediniz");
                    go = false;
                    binding.button2.setCompoundDrawablesWithIntrinsicBounds(null,null,iconNo,null);
                    addDurum(email, latitude, longitude, go);

                }
            }
        });

    }

    public void goToPlace(View view) {

        Intent intent = new Intent(this, MapsActivityForTravel.class);

        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("place", placeName);

        startActivity(intent);
    }

    public void makeComment(double latitude, double longitude, String comment, String username, String downloadUrl) {
        // Comment nesnesini oluştur
        Date timestamp = new Date();
        Comment newComment = new Comment(longitude, latitude, comment, username, downloadUrl, timestamp);

        // Latitude ve longitude kullanarak benzersiz bir yer dokümanı referansı oluşturun
        String placeId = generatePlaceId(latitude, longitude);

        // "places" koleksiyonuna ve "comments" alt koleksiyonuna erişin
        db.collection("Comments")
                .document(placeId)
                .collection("comments")
                .add(newComment)
                .addOnSuccessListener(documentReference -> {
                    // Yorum başarıyla eklendi
                    binding.editTextTextMultiLine.setText("");
                    binding.editTextTextMultiLine.setVisibility(View.INVISIBLE);
                    binding.buttonCommentSend.setVisibility(View.INVISIBLE);
                })
                .addOnFailureListener(e -> {
                    // Yorum eklenirken hata oluştu
                    Toast.makeText(this, "Hata oluştu", Toast.LENGTH_LONG).show();
                });
    }

    // Latitude ve longitude kullanarak benzersiz bir yer ID'si oluşturma
    private String generatePlaceId(double latitude, double longitude) {
        return latitude + "_" + longitude;
    }

    public void openTextComment(View view) {

        binding.editTextTextMultiLine.setVisibility(View.VISIBLE);
        binding.buttonCommentSend.setVisibility(View.VISIBLE);
        getUserProfileByEmail(email);

    }
    public void buttonSendComment(View view) {
        String comment = binding.editTextTextMultiLine.getText().toString();

        if (comment != null && comment != "") {
            makeComment(latitude, longitude, comment, username, downloadUrl);
            fetchComments(latitude, longitude);
            binding.textView2.setText("Yorumlar");
        }

    }

    public void getUserProfileByEmail(String email) {
        db.collection("Profiles")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener < QuerySnapshot > () {
                    @Override
                    public void onComplete(Task < QuerySnapshot > task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (QueryDocumentSnapshot document: querySnapshot) {
                                    username = document.getString("name") + " " + document.getString("surname");
                                    downloadUrl = document.getString("downloadurl");

                                }
                            } else {
                                System.out.println("Kullanıcı bulunamadı.");
                            }
                        } else {
                            System.err.println("Veri çekilirken hata oluştu: " + task.getException());
                        }
                    }
                });
    }

    private void fetchComments(double latitude, double longitude) {
        // Latitude ve longitude kullanarak benzersiz bir yer ID'si oluşturma
        String placeId = generatePlaceId(latitude, longitude);

        // "Comments" koleksiyonundan yorumları çekme
        db.collection("Comments")
                .document(placeId)
                .collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener < QuerySnapshot > () {
                    @Override
                    public void onComplete(Task < QuerySnapshot > task) {
                        if (task.isSuccessful()) {
                            commentArrayList.clear();
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Comment comment = document.toObject(Comment.class);
                                commentArrayList.add(comment);

                            }
                            if (commentArrayList.size() == 0) {
                                binding.textView2.setText("Kimse yorum yapmamış!");
                            }

                            adapter.notifyDataSetChanged(); // RecyclerView adaptörünü güncelle
                        } else {
                            Toast.makeText(PlaceDetails.this, "Yorumlar alınırken hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void addDurum(String email, double latitude, double longitude, boolean go) {
        if (email == null) {
            Toast.makeText(this, "Kullanıcı girişi yapılmadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        String documentPath = email + "_" + latitude + "_" + longitude;

        Map < String, Object > durumData = new HashMap < > ();
        durumData.put("email", email);
        durumData.put("latitude", latitude);
        durumData.put("longitude", longitude);
        durumData.put("go", go);

        db.collection("Durum").document(documentPath)
                .set(durumData)
                .addOnSuccessListener(aVoid -> Toast.makeText(PlaceDetails.this, "Veri başarıyla eklendi", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PlaceDetails.this, "Veri ekleme başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    public void getDurum(String email, double latitude, double longitude) {
        if (email == null) {
            Toast.makeText(this, "Kullanıcı girişi yapılmadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        String documentPath = email + "_" + latitude + "_" + longitude;

        db.collection("Durum").document(documentPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener < DocumentSnapshot > () {
                    @Override
                    public void onComplete(@NonNull Task < DocumentSnapshot > task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Boolean go = document.getBoolean("go");
                                if (go != null && go) {
                                    binding.button2.setText("Gittiniz");
                                    binding.button2.setCompoundDrawablesWithIntrinsicBounds(null,null,iconYes,null);
                                } else {
                                    binding.button2.setText("Gitmediniz");
                                    binding.button2.setCompoundDrawablesWithIntrinsicBounds(null,null,iconNo,null);

                                }
                            } else {}
                        } else {
                            Toast.makeText(PlaceDetails.this, "Veri çekme başarısız: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}