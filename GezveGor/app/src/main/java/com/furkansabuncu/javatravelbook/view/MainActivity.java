package com.furkansabuncu.javatravelbook.view;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.furkansabuncu.javatravelbook.R;
import com.furkansabuncu.javatravelbook.adapter.PlaceAdapter;
import com.furkansabuncu.javatravelbook.databinding.ActivityMainBinding;
import com.furkansabuncu.javatravelbook.model.Place;
import com.furkansabuncu.javatravelbook.roomdb.PlaceDao;
import com.furkansabuncu.javatravelbook.roomdb.PlaceDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {




    private FirebaseAuth auth;
    Vibrator vibrator;


    public ActivityMainBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    ArrayList<Place> places;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        places = new ArrayList<>();

        PlaceDatabase db = Room.databaseBuilder(getApplicationContext(),
                PlaceDatabase.class, "Places").allowMainThreadQueries().build();

        PlaceDao placeDao = db.placeDao();

        mDisposable.add(placeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse));


        binding.anasayfaGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MainPage.class);
                startActivity(intent);
                finish();
            }
        });




    }

    private void handleResponse(List<Place> placeList) {

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PlaceAdapter placeAdapter = new PlaceAdapter(placeList);
        binding.recyclerView.setAdapter(placeAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.travel_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            playSound();
            auth.signOut();
            Intent intentToMain = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentToMain);
            finish();


        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void vibrate() {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(2000);
        }
    }
    private void playSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.oturumkapat);
        }
        // Ses dosyasını baştan çal
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }
    protected void onResume() {
        super.onResume();


    }



}