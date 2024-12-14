package com.furkansabuncu.javatravelbook.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.furkansabuncu.javatravelbook.R;
import com.furkansabuncu.javatravelbook.databinding.ActivityPostDetailsBinding;
import com.furkansabuncu.javatravelbook.model.Places;
import com.furkansabuncu.javatravelbook.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PostDetails extends AppCompatActivity {

    ActivityPostDetailsBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityPostDetailsBinding.inflate(getLayoutInflater());
        View view;
        view = binding.getRoot();
        setContentView(view);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();


        Intent intent = getIntent();
        Post clickedPost = (Post) intent.getParcelableExtra("clickedPost");

        // şimdi bu verileri kullanabiliriz

        binding.placeTitleTextView.setText(clickedPost.title);
        binding.placeDescriptionTextView.setText(clickedPost.comment);

        Picasso.get().load(clickedPost.downloadUrl).into(binding.placeImageView);

        binding.mainPageLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PostDetails.this, MainPage.class);
                startActivity(intent);
                finish();
            }
        });

        binding.personLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetails.this, ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetails.this, StartTravelActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }
}