package com.furkansabuncu.javatravelbook.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.furkansabuncu.javatravelbook.adapter.PlacesAdapter;
import com.furkansabuncu.javatravelbook.databinding.ActivityMyPlacesBinding;
import com.furkansabuncu.javatravelbook.model.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MyPlacesActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Places> placesArrayList;
    private ActivityMyPlacesBinding binding;
    PlacesAdapter placesAdapter;
    String searchWord;


    //Constructor yapılarımızı tanımladık birinde aranan kelime var diğerinde aranan kelime olmayacak.
    public MyPlacesActivity(){
    }
    // yapıcı fonksiyon tanımlama sonu


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMyPlacesBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);


        placesArrayList=new ArrayList<>();

        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();


        Intent intent = getIntent();
        String searchedKey = intent.getStringExtra("searched_key");
        ArrayList<Places> placesListFromIntent = intent.getParcelableArrayListExtra("myList");
        if(placesListFromIntent!=null){
            placesArrayList=placesListFromIntent;

        }

        else if (searchedKey != null && placesListFromIntent==null) {
            // Veriyi kullanarak işlemleri başlat
            getData(searchedKey);
        }


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        placesAdapter=new PlacesAdapter(placesArrayList);
        binding.recyclerView.setAdapter(placesAdapter);



    binding.favorite.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MyPlacesActivity.this, StartTravelActivity.class);
            startActivity(intent);
            finish();
        }
    });

    binding.anasayfagec.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MyPlacesActivity.this, MainPage.class);
            startActivity(intent);
            finish();
        }
    });
    binding.personLinearLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MyPlacesActivity.this, ProfilePage.class);
            startActivity(intent);
            finish();
        }
    });


    }

    public void getData(String key) {
        placesArrayList.clear(); // Önceki verileri temizle

        // City alanının searchedWord'e eşit olduğu belgeleri sorgula
        firebaseFirestore.collection("Places")
                .whereEqualTo("city", key)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(MyPlacesActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                if (data != null) {
                                    String city = (String) data.get("city");
                                    String downloadUrl = (String) data.get("downloadurl");
                                    String comment = (String) data.get("comment");
                                    Double latitude = (Double) data.get("latitude");
                                    Double longitude = (Double) data.get("longitude");
                                    String place = (String) data.get("place");

                                    Places places = new Places(place, downloadUrl, city, longitude, latitude, comment);
                                    placesArrayList.add(places);
                                }
                            }
                            placesAdapter.notifyDataSetChanged();
                        }
                    }
                });

        // Place alanının searchedWord'e eşit olduğu belgeleri sorgula
        firebaseFirestore.collection("Places")
                .whereEqualTo("place", key)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(MyPlacesActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                if (data != null) {
                                    String city = (String) data.get("city");
                                    String downloadUrl = (String) data.get("downloadurl");
                                    String comment = (String) data.get("comment");
                                    Double latitude = (Double) data.get("latitude");
                                    Double longitude = (Double) data.get("longitude");
                                    String place = (String) data.get("place");

                                    // Aynı nesne tekrar eklenmesin diye kontrol edin
                                    boolean exists = false;
                                    for (Places p : placesArrayList) {
                                        if (p.getCity().equals(city) && p.getPlace().equals(place)) {
                                            exists = true;
                                            break;
                                        }
                                    }
                                    if (!exists) {
                                        Places places = new Places(place, downloadUrl, city, longitude, latitude, comment);
                                        placesArrayList.add(places);
                                    }
                                }
                            }
                            placesAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyPlacesActivity.this, StartTravelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

}



