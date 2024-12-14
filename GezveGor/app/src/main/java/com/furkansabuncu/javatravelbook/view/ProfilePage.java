package com.furkansabuncu.javatravelbook.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.furkansabuncu.javatravelbook.R;
import com.furkansabuncu.javatravelbook.databinding.ActivityProfilePageBinding;
import com.furkansabuncu.javatravelbook.model.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ProfilePage extends AppCompatActivity {

    ActivityProfilePageBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    ArrayList<Places> placesArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityProfilePageBinding.inflate(LayoutInflater.from(ProfilePage.this));
        View view = binding.getRoot();
        setContentView(view);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        placesArrayList = new ArrayList<>();


        FirebaseUser user = auth.getCurrentUser();


    binding.button4.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getLocationData(user.getEmail());
        }
    });

        binding.anasayfaGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, MainPage.class);
                startActivity(intent);
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intentToMain = new Intent(ProfilePage.this, LoginActivity.class);
                startActivity(intentToMain);
                finish();
            }
        });
        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMain = new Intent(ProfilePage.this, StartTravelActivity.class);
                startActivity(intentToMain);
                finish();
            }
        });

        assert user != null;

        CollectionReference usersRef = db.collection("Profiles");

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

                        binding.username.setText("@"+username);
                        binding.fullName.setText(name+" "+ surname);

                        Glide.with(ProfilePage.this)
                                .load(downloadUrl)
                                .transform(new CircleCrop())
                                .into(binding.profileImage);

                    }
                } else {
                    Log.w("FirestoreExample", "Error getting documents.", task.getException());
                }
            }
        });


    }

    public void getLocationData(String email) {
        placesArrayList.clear(); // Önceki verileri temizle

        db.collection("Durum")
                .whereEqualTo("email", email)
                .whereEqualTo("go", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ProfilePage.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                if (data != null) {
                                    Double latitude = (Double) data.get("latitude");
                                    Double longitude = (Double) data.get("longitude");

                                    // Places koleksiyonundan ilgili verileri çek
                                    fetchPlacesData(latitude, longitude);
                                }
                            }

                            if (value.isEmpty()) {
                                Toast.makeText(ProfilePage.this, "No matching documents in 'Durum'", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ProfilePage.this, "Error fetching 'Durum' data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void fetchPlacesData(Double latitude, Double longitude) {
        db.collection("Places")
                .whereEqualTo("latitude", latitude)
                .whereEqualTo("longitude", longitude)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String placeName = document.getString("place");
                                String downloadUrl = document.getString("downloadurl");
                                String city = document.getString("city");
                                Double latitude = document.getDouble("latitude");
                                Double longitude = document.getDouble("longitude");
                                String comment = document.getString("comment");

                                Places places = new Places(placeName, downloadUrl, city,longitude,latitude ,comment);
                                placesArrayList.add(places);
                            }

                            if (!placesArrayList.isEmpty()) {
                                Intent intent = new Intent(ProfilePage.this, MyPlacesActivity.class);
                                intent.putParcelableArrayListExtra("myList", placesArrayList);
                                startActivity(intent);

                            } else {
                                Toast.makeText(ProfilePage.this, "No places found", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ProfilePage.this, "Error fetching places data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



}
