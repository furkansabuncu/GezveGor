package com.furkansabuncu.javatravelbook.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furkansabuncu.javatravelbook.databinding.RecyclerRow2Binding;
import com.furkansabuncu.javatravelbook.model.Places;
import com.furkansabuncu.javatravelbook.model.Post;
import com.furkansabuncu.javatravelbook.view.PlaceDetails;
import com.furkansabuncu.javatravelbook.view.PostDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder>{

    private ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList){
        this.postArrayList=postArrayList;
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRow2Binding recyclerRow2Binding=RecyclerRow2Binding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recyclerRow2Binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recyclerRow2Binding.recyclerViewBaslikText.setText(postArrayList.get(position).title);
        holder.recyclerRow2Binding.recyclerViewFullNameText.setText(postArrayList.get(position).fullName);
        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recyclerRow2Binding.recyclerViewImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tıklanan öğenin pozisyonunu al
                int clickedPosition = holder.getAdapterPosition();

                // Tıklanan öğenin Place nesnesini al
                Post clickedPost = postArrayList.get(clickedPosition);

                // PlaceDetails aktivitesine geçmek için Intent oluştur
                Intent intent = new Intent(holder.itemView.getContext(), PostDetails.class);

                // Intent'e tıklanan Place nesnesini ekleyerek gönder
                intent.putExtra("clickedPost", clickedPost);

                // Intent ile PlaceDetails aktivitesine geç
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        RecyclerRow2Binding recyclerRow2Binding;

         public PostHolder(RecyclerRow2Binding recyclerRow2Binding) {
             super(recyclerRow2Binding.getRoot());
             this.recyclerRow2Binding=recyclerRow2Binding;
         }
     }
}
