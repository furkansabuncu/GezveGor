package com.furkansabuncu.javatravelbook.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import com.furkansabuncu.javatravelbook.R;
import com.furkansabuncu.javatravelbook.databinding.ActivityProfilePageBinding;
import com.furkansabuncu.javatravelbook.databinding.ActivityStartTravelBinding;
import com.furkansabuncu.javatravelbook.model.Place;
import com.furkansabuncu.javatravelbook.model.Places;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class StartTravelActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final double EARTH_RADIUS_KM = 6371.0;

    ActivityStartTravelBinding binding;
    private FirebaseFirestore db;
    private List<String> placesList;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<Place> placeArrayList;
    private static final String GEOCODING_API_BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static final String API_KEY = "AIzaSyBa6ydcVPWa873AP6AGpqATRjnuiCT3C30"; // Google Geocoding API anahtarınızı buraya ekleyin
    String cityName;
    private ProgressDialog progressDialog;
    List<Places> placesList2;
    double currentLatitude;
    double currentLongitude;
    double seekBarValue;
    double distance;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Binding ekleme
        binding = ActivityStartTravelBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        // binding ekleme son

        binding.buttonToFarPlaces.setEnabled(false);

        db = FirebaseFirestore.getInstance();
        placesList = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        loadPlacesList();
        seekBarValue=0.0;


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, placesList);

        // ArrayAdapter ComboBox'a atanır
        binding.autoCompleteTextView.setAdapter(adapter);

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue=progress;
                binding.textViewSeekBarValue.setText(String.valueOf(progress)+"km");

                if(seekBarValue!=0) {
                    binding.buttonToFarPlaces.setEnabled(true);
                }
                else{
                    binding.buttonToFarPlaces.setEnabled(false);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.personLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTravelActivity.this, ProfilePage.class);
                startActivity(intent);
            }
        });

        binding.imageViewAnkara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTravelActivity.this, ErrorActivity.class);
                startActivity(intent);
                finish();
            }
        });
        binding.imageViewIst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTravelActivity.this, ErrorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.imageViewMugla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTravelActivity.this, ErrorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTravelActivity.this, ErrorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.henuzEklenmedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTravelActivity.this, ErrorActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    private void loadPlacesList() {
        // Firestore'dan "places" koleksiyonundaki veriler alınır
        db.collection("Places")
                .orderBy("city")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> cities = new ArrayList<>();
                            List<String> places = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String city = document.getString("city");
                                String place = document.getString("place");

                                // Eğer şehir daha önce eklenmediyse listeye ekle
                                if (!cities.contains(city)) {
                                    cities.add(city);
                                }

                                // Eğer yer daha önce eklenmediyse listeye ekle
                                if (!places.contains(place)) {
                                    places.add(place);
                                }
                            }

                            // Şehir ve yer isimleri placesList'e eklenir
                            placesList.addAll(cities);
                            placesList.addAll(places);

                            // ArrayAdapter güncellenir
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(StartTravelActivity.this, android.R.layout.simple_dropdown_item_1line, placesList);
                            binding.autoCompleteTextView.setAdapter(adapter);
                        }
                    }
                });
    }

  public void aramaYap(View view){
      String myString = binding.autoCompleteTextView.getText().toString();


      if(myString==null || myString==""){
          Intent intent = new Intent(StartTravelActivity.this, ErrorActivity.class);
          startActivity(intent);
      }
      else{
          Intent intent = new Intent(StartTravelActivity.this, MyPlacesActivity.class);
          intent.putExtra("searched_key", myString);
          startActivity(intent);
      }

   }

   public void mainpageGec(View view){
       Intent intent = new Intent(StartTravelActivity.this, MainPage.class);
       startActivity(intent);
   }

   //yakın noktaları getir butonuna basılınca olan şeyler
   public void toFarPlaces(View view)  {
        showLoadingIndicator();
        getLatitAndLongit();

   }
   // yakın noktaları getirme bitimi


    //kulalnıcının latitude ve longitude noktasını getiren fonskiyon
    public void getLatitAndLongit() {
        // Konum izni kontrol edilir ve izin istenir
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // İzin verilmişse konum alınır
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location location = task.getResult();
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                currentLatitude=latitude;
                                currentLongitude=longitude;
                                getCityName(latitude, longitude);
                                // Latitude ve Longitude ile yapmanız gereken işlemleri burada yapın

                            }


                        }
                    });
        }
    }
    // kullanıcıdan longitude ve latitude çeken fonksiyon sonu


    private void getCityName(double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GEOCODING_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GeocodingApiService apiService = retrofit.create(GeocodingApiService.class);
        Call<JsonObject> call = apiService.getCityName(latitude + "," + longitude, API_KEY);

        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseObject = response.body();
                    cityName = null;

                    if (responseObject.has("results")) {
                        JsonArray resultsArray = responseObject.getAsJsonArray("results");
                        for (JsonElement result : resultsArray) {
                            JsonObject resultJsonObject = result.getAsJsonObject();
                            JsonArray addressComponentsArray = resultJsonObject.getAsJsonArray("address_components");
                            for (JsonElement addressComponent : addressComponentsArray) {
                                JsonObject addressComponentObject = addressComponent.getAsJsonObject();
                                JsonArray typesArray = addressComponentObject.getAsJsonArray("types");

                                if (typesArray != null) {
                                    for (JsonElement typeElement : typesArray) {
                                        String type = typeElement.getAsString();
                                        if (type.equals("locality") || type.equals("administrative_area_level_1") || type.equals("sublocality") || type.equals("neighborhood")) {
                                            cityName = addressComponentObject.get("long_name").getAsString();
                                            break;
                                        }
                                    }
                                }

                                if (cityName != null) {
                                    break;
                                }
                            }

                            if (cityName != null) {
                                break;
                            }
                        }

                        if (cityName != null) {
                            hideLoadingIndicator();
                            fetchPlacesByCity(cityName);
                        } else {
                            hideLoadingIndicator();
                            Toast.makeText(StartTravelActivity.this, "Şehir bulunamadı", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(StartTravelActivity.this, "Geçerli sonuçlar alınamadı", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StartTravelActivity.this, "Geocoding hatası: API yanıtı başarısız", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(StartTravelActivity.this, "Geocoding hatası: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    interface GeocodingApiService {
        @GET("geocode/json")
        Call<JsonObject> getCityName(@Query("latlng") String latlng, @Query("key") String apiKey);
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

    private void fetchPlacesByCity(String cityName) {
        if (cityName != null && !cityName.isEmpty()) {
            // Firestore sorgusu oluştur
            db.collection("Places")
                    .whereEqualTo("city", cityName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                 placesList2 = new ArrayList<>();

                                // Her belge için dön
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Belgeden gerekli alanları oku
                                    String cityName = document.getString("city");
                                    String downloadUrl = document.getString("downloadurl");
                                    String comment = document.getString("comment");
                                    Double latitude = document.getDouble("latitude");
                                    Double longitude = document.getDouble("longitude");
                                    String placeName = document.getString("place");
                                    distance = calculateDistance(currentLatitude, currentLongitude, latitude, longitude);


                                    // Yeni bir Places nesnesi oluştur ve listeye ekle
                                    Places place = new Places(placeName, downloadUrl, cityName, longitude, latitude, comment,distance);
                                    placesList2.add(place);
                                }
                                if(seekBarValue!=0){
                                    filterPlacesByDistance(seekBarValue);
                                    updateRecyclerView(new ArrayList<>(placesList2));
                                }
                                else {
                                    hideLoadingIndicator();
                                    Toast.makeText(StartTravelActivity.this,"değer 0 olamaz",Toast.LENGTH_LONG).show();
                                }







                                // Oluşturulan listeyle bir şeyler yapabilirsiniz, örneğin:
                                // Başka bir metoda geçir, bir adaptörle RecyclerView'e yükle, vb.
                            } else {
                                Toast.makeText(StartTravelActivity.this, "Places verisi alınamadı", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Şehir adı geçersiz", Toast.LENGTH_SHORT).show();
        }



    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        GeodesicData result = Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2);
        return result.s12 / 1000; // Mesafeyi kilometreye dönüştür
    }

    private void filterPlacesByDistance(double maxDistanceKm) {
        ArrayList<Places> filteredList = new ArrayList<>();
        for (Places place : placesList2) {
            if (place.getDistance() <= maxDistanceKm) {
                filteredList.add(place);
            }
        }
        placesList2 = filteredList;


    }
    // bu aşamada elimizde 30kmden yakın olan tüm noktalar var şimdi bunları aktarmamız gerekiyor


    private void updateRecyclerView(ArrayList<Places> placesList) {
        // RecyclerView veya adaptörünüzü burada güncelleyin
        // Örneğin:
        Intent intent = new Intent(StartTravelActivity.this, MyPlacesActivity.class);
        intent.putParcelableArrayListExtra("myList", placesList);
        startActivity(intent);
        finish();
    }





}