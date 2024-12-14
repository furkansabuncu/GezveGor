package com.furkansabuncu.javatravelbook.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.furkansabuncu.javatravelbook.R;
import com.furkansabuncu.javatravelbook.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;


    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        auth= FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseUser user= auth.getCurrentUser();


        //kullanici giriş yapti ise tekrar giriş yapmasına gerek yok.
        if(user!=null){
            Intent intent=new Intent(LoginActivity.this,MainPage.class);
            startActivity(intent);
            finish();
        }

    }

    public void giris(View view){
        String email=binding.emailText.getText().toString();
        String pass=binding.passText.getText().toString();
        if(email.equals("") && pass.equals("")) {
            Toast.makeText(LoginActivity.this,"Hatalı giriş",Toast.LENGTH_LONG).show();
        }else{
            auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    FirebaseUser user= auth.getCurrentUser();
                    checkUserProfile(user);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this,"Başarısız giriş tekrar deneyebilirsiniz",Toast.LENGTH_LONG).show();
                }
            });
        }




    }

    public void kayit(View view){
    String email=binding.emailText.getText().toString();
    String pass=binding.passText.getText().toString();

    if(email.equals("") && pass.equals("")){
        Toast.makeText(LoginActivity.this,"İlgili alanları doldurunuz",Toast.LENGTH_LONG).show();
    }else{
        auth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this,"Ekleme başarılı",Toast.LENGTH_LONG).show();
                binding.passText.setText("");
                binding.emailText.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
    private void checkUserProfile(FirebaseUser user) {
        String email = user.getEmail();

        if (email != null) {
            firebaseFirestore.collection("Profiles")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                    // Kullanıcı profili mevcut
                                    Intent intent = new Intent(LoginActivity.this, MainPage.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Kullanıcı profili yok
                                    Intent intent = new Intent(LoginActivity.this, InformationActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Log.e("Firestore Error", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }


}