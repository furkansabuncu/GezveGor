package com.furkansabuncu.javatravelbook.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.furkansabuncu.javatravelbook.R;
import com.furkansabuncu.javatravelbook.adapter.PostAdapter;
import com.furkansabuncu.javatravelbook.databinding.ActivityMainPageBinding;
import com.furkansabuncu.javatravelbook.model.Places;
import com.furkansabuncu.javatravelbook.model.Post;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class MainPage extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    PostAdapter postAdapter;
    ArrayList<Post> postArrayList;
    private FirebaseAuth auth;
    private FusedLocationProviderClient fusedLocationClient;
    double currentLatitude;
    double currentLongitude;
    private static final String GEOCODING_API_BASE_URL = "https://maps.googleapis.com/maps/api/";

    ActivityMainPageBinding binding;
    private static final String API_KEY = "AIzaSyBa6ydcVPWa873AP6AGpqATRjnuiCT3C30"; // Google Geocoding API anahtarınızı buraya ekleyin

    private FirebaseFirestore db;
    List<Places> placesList2;
    String gezilecekYer;
    Post clickedPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        postArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        postAdapter = new PostAdapter(postArrayList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyChannel";
            String description = "My channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        db = FirebaseFirestore.getInstance();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLatitAndLongit();





        // Menüyü göstermek için kullanılacak ImageView elemanını bulma
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menüyü göstermek için bir PopupMenu oluşturma
                PopupMenu popupMenu = new PopupMenu(MainPage.this, binding.imageView2);
                // PopupMenu'ya menüyü yüklemek
                popupMenu.getMenuInflater().inflate(R.menu.travel_menu, popupMenu.getMenu());
                // PopupMenu'da bir menü öğesine tıklandığında gerçekleştirilecek işlemleri belirleme
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Tıklanan menü öğesine göre işlemleri belirleme
                        if (item.getItemId() == R.id.logout) {// "Çıkış Yap" seçeneğine tıklanınca yapılacak işlemler
                            auth.signOut();
                            Intent intentToMain = new Intent(MainPage.this, LoginActivity.class);
                            startActivity(intentToMain);
                            finish();
                        }
                        

                        return false;
                    }
                });
                // PopupMenu'yu gösterme
                popupMenu.show();
            }
        });

        binding.kisayolClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, MainActivity.class);
                startActivity(intent);

            }
        });
        binding.kisayolEkleClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, MapsActivity.class);
                intent.putExtra("info", "new");
                startActivity(intent);


            }
        });

        binding.imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndexPostAndGo(0);
            }
        });
        binding.denemeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndexPostAndGo(1);
            }
        });
        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndexPostAndGo(2);
            }
        });
        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndexPostAndGo(3);
            }
        });
        binding.imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndexPostAndGo(4);
            }
        });
        binding.imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndexPostAndGo(5);
            }
        });

        binding.cardOneri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, GeziEklemeActivity.class);
                startActivity(intent);
            }
        });
        binding.cardViewArgue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, GeziActivity.class);
                startActivity(intent);
            }
        });

        binding.personLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, ProfilePage.class);
                startActivity(intent);
            }
        });


        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, StartTravelActivity.class);
                startActivity(intent);
            }
        });

        binding.gezCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, StartTravelActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getData() {
        //snapshot dinleyici koyuyoruz bir değişim oldugunda haberimiz olması adına
        firebaseFirestore.collection("Posts")
                .orderBy("date", Query.Direction.DESCENDING) // "date" alanına göre sırala (en son eklenen en üstte olacak)
                .limit(6).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged") //uyarı gelmesini engelleme
                    @Override
                    //onevent veritababından alınan anlık sonucları işleyen fonksiyon
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(MainPage.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                String userEmail = (String) data.get("useremail");
                                String comment = (String) data.get("comment");
                                String downloadUrl = (String) data.get("downloadurl");
                                String title = (String) data.get("title");
                                String fullName=(String) data.get("fullName");

                                Post post = new Post(comment, downloadUrl,title,fullName,userEmail);
                                postArrayList.add(post);
                            }
                            // PostArrayList'i doldurduktan sonra resmi yükle
                            if (postArrayList.size() == 6) {
                                Picasso.get().load(postArrayList.get(0).downloadUrl).into(binding.imageView1);
                                Picasso.get().load(postArrayList.get(1).downloadUrl).into(binding.denemeimage);
                                Picasso.get().load(postArrayList.get(2).downloadUrl).into(binding.imageView3);
                                Picasso.get().load(postArrayList.get(3).downloadUrl).into(binding.imageView4);
                                Picasso.get().load(postArrayList.get(4).downloadUrl).into(binding.imageView5);
                                Picasso.get().load(postArrayList.get(5).downloadUrl).into(binding.imageView6);


                                binding.yeradi1.setText(postArrayList.get(0).title);
                                binding.yeradi2.setText(postArrayList.get(1).title);
                                binding.yeradi3.setText(postArrayList.get(2).title);
                                binding.yeradi4.setText(postArrayList.get(3).title);
                                binding.yeradi5.setText(postArrayList.get(4).title);
                                binding.yeradi6.setText(postArrayList.get(5).title);




                                binding.sehir1.setText(postArrayList.get(0).fullName);
                                binding.sehir2.setText(postArrayList.get(1).fullName);
                                binding.sehir3.setText(postArrayList.get(2).fullName);
                                binding.sehir4.setText(postArrayList.get(3).fullName);
                                binding.sehir5.setText(postArrayList.get(4).fullName);
                                binding.sehir6.setText(postArrayList.get(5).fullName);



                            }
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    @Override
    protected void onResume() {

        super.onResume();
        getData();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendNotification(gezilecekYer,0.0,0.0,0.0);
            } else {
                Toast.makeText(this, "Bildirim izni reddedildi", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void sendNotification(String key, Double num,Double latitude,Double longitude) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                checkNotificationPermission();
                return;
            }
        }
        // Bildirime tıklandığında açılacak olan aktiviteyi belirleyin
        Intent intent = new Intent(MainPage.this, MapsActivityForTravel.class);
        intent.putExtra("place", key); // İhtiyacınıza göre ekstra veri ekleyebilirsiniz
        intent.putExtra("latitude", latitude); // İhtiyacınıza göre ekstra veri ekleyebilirsiniz
        intent.putExtra("longitude", longitude); // İhtiyacınıza göre ekstra veri ekleyebilirsiniz

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        String formattedNum = String.format("%.2f", num);
        // Başındaki "0." ifadesinin kaldırılması
        if (formattedNum.startsWith("0.")) {
            formattedNum = formattedNum.substring(1);
        }

        // Bildirim oluşturma
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.notfound) // Bildirim ikonu
                .setContentTitle("Buraya gittin mi??")
                .setContentText(" "+formattedNum +" " +"km uzağında"+" "+key+" "+"var, göz atabilirsiniz")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // Bildirime tıklandığında yapılacak eylemi belirle
                .setAutoCancel(true); // Bildirime tıklandığında otomatik olarak kapat
        // Bildirimi gönderme
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
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
    private void getCityName(double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GEOCODING_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StartTravelActivity.GeocodingApiService apiService = retrofit.create(StartTravelActivity.GeocodingApiService.class);
        Call<JsonObject> call = apiService.getCityName(latitude + "," + longitude, API_KEY);

        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseObject = response.body();
                    String cityName = parseCityNameFromResponse(responseObject);

                    Log.d("Geocoding", "City Name: " + cityName);  // Şehir ismini loglama

                    if (cityName != null) {
                        fetchPlacesByCity(cityName);
                    } else {
                        showToast("bruaya girdi");
                    }
                } else {
                    showToast("Geocoding hatası: API yanıtı başarısız");
                    Log.e("Geocoding", "API response unsuccessful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showToast("Geocoding hatası: " + t.getMessage());
                Log.e("Geocoding", "API call failed: " + t.getMessage());
            }
        });
    }

    private String parseCityNameFromResponse(JsonObject responseObject) {
        if (responseObject.has("results")) {
            JsonArray resultsArray = responseObject.getAsJsonArray("results");
            for (JsonElement result : resultsArray) {
                JsonObject resultJsonObject = result.getAsJsonObject();
                JsonArray addressComponentsArray = resultJsonObject.getAsJsonArray("address_components");

                for (JsonElement addressComponent : addressComponentsArray) {
                    JsonObject addressComponentObject = addressComponent.getAsJsonObject();
                    JsonArray typesArray = addressComponentObject.getAsJsonArray("types");

                    for (JsonElement typeElement : typesArray) {
                        String type = typeElement.getAsString();
                        if (type.equals("locality") || type.equals("administrative_area_level_1") || type.equals("sublocality") || type.equals("neighborhood")) {
                            String cityName = addressComponentObject.get("long_name").getAsString();
                            Log.d("Geocoding", "City found: " + cityName);  // Şehir ismini loglama
                            return cityName;
                        }
                    }
                }
            }
        }
        Log.d("Geocoding", "No city found in response");  // Şehir ismi bulunamadığını loglama
        return null;
    }

    private void showToast(String message) {
        Toast.makeText(MainPage.this, message, Toast.LENGTH_SHORT).show();
    }


    interface GeocodingApiService {
        @GET("geocode/json")
        Call<JsonObject> getCityName(@retrofit2.http.Query("latlng") String latlng, @retrofit2.http.Query("key") String apiKey);
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
                                    String fetchedCityName = document.getString("city");
                                    String downloadUrl = document.getString("downloadurl");
                                    String comment = document.getString("comment");
                                    Double latitude = document.getDouble("latitude");
                                    Double longitude = document.getDouble("longitude");
                                    String placeName = document.getString("place");
                                    double distance = calculateDistance(currentLatitude, currentLongitude, latitude, longitude);

                                    // Yeni bir Places nesnesi oluştur ve listeye ekle

                                        Places place = new Places(placeName, downloadUrl, fetchedCityName, longitude, latitude, comment, distance);
                                        placesList2.add(place);



                                }

                                // Listeden en yakın yeri bul
                                if (!placesList2.isEmpty()) {
                                    Places closestPlace = placesList2.get(0);
                                    for (Places place : placesList2) {
                                        if (place.distance < closestPlace.distance) {
                                            closestPlace = place;
                                        }
                                    }
                                    // Bildirim gönder
                                    sendNotification(closestPlace.place, closestPlace.distance,closestPlace.latitude,closestPlace.longitude);
                                } else {
                                    Toast.makeText(MainPage.this, "Şehirde yer bulunamadı", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(MainPage.this, "Places verisi alınamadı", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Şehir adı geçersiz", Toast.LENGTH_SHORT).show();
        }
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formülü kullanarak iki nokta arasındaki mesafeyi hesapla
        double R = 6371; // Yeryüzünün yarıçapı (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }


    public void getIndexPostAndGo(int index){
        clickedPost=new Post(postArrayList.get(index).comment,postArrayList.get(index).downloadUrl,postArrayList.get(index).title,postArrayList.get(index).fullName, postArrayList.get(index).email);
        Intent intent = new Intent(MainPage.this, PostDetails.class);
        intent.putExtra("clickedPost", clickedPost);

        startActivity(intent);
    }






}
