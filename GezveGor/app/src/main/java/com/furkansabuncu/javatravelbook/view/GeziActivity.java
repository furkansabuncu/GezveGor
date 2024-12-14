package com.furkansabuncu.javatravelbook.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.furkansabuncu.javatravelbook.adapter.PostAdapter;
import com.furkansabuncu.javatravelbook.databinding.ActivityGeziBinding;
import com.furkansabuncu.javatravelbook.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class GeziActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post> postArrayList;
    private ActivityGeziBinding binding;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGeziBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        postArrayList=new ArrayList<>();

        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        getData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter=new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);

        binding.anasayfaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GeziActivity.this, MainPage.class);
                startActivity(intent);
                finish();
            }
        });
        binding.personLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GeziActivity.this, ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GeziActivity.this, MainPage.class);
                startActivity(intent);
                finish();
            }
        });



    }

    public void getData(){
        //snapshot dinleyici koyuyoruz bir değişim oldugunda haberimiz olması adına
    firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
        @SuppressLint("NotifyDataSetChanged") //uyarı gelmesini engelleme
        @Override
        //onevent veritababından alınan anlık sonucları işleyen fonksiyon
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        if(error!=null){
            Toast.makeText(GeziActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
        if(value!=null){
            for(DocumentSnapshot snapshot : value.getDocuments()){
                Map<String,Object> data= snapshot.getData();
                String userEmail= (String) data.get("useremail");
                String comment= (String) data.get("comment");
                String title= (String) data.get("title");
                String downloadUrl=(String) data.get("downloadurl");
                String fullName=(String) data.get("fullName");

                Post post=new Post(comment,downloadUrl,title,fullName,userEmail);
                postArrayList.add(post);
            }
            postAdapter.notifyDataSetChanged();
        }
        }
    });
    }
}