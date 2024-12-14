package com.furkansabuncu.javatravelbook.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furkansabuncu.javatravelbook.databinding.RecyclerRow3Binding;
import com.furkansabuncu.javatravelbook.model.Places;
import com.furkansabuncu.javatravelbook.view.PlaceDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesHolder> {
    ArrayList<Places> myPlaces;

    public PlacesAdapter(ArrayList<Places> placesArrayList){
        this.myPlaces=placesArrayList;
    }

    @NonNull
    @Override
    public PlacesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRow3Binding recyclerRow3Binding= RecyclerRow3Binding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PlacesHolder(recyclerRow3Binding);


    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull PlacesHolder holder, int position) {
        holder.recyclerRow3Binding.recyclerViewContentText.setText(myPlaces.get(position).place);
        holder.recyclerRow3Binding.recyclerViewSehirText.setText(myPlaces.get(position).city);
        Picasso.get().load(myPlaces.get(position).downloadUrl).into(holder.recyclerRow3Binding.recyclerViewImageView);
        if(myPlaces.get(position).distance!=0.00){
            holder.recyclerRow3Binding.distanceTextView.setText(String.format("%.2f km", myPlaces.get(position).distance));
        }
        else{
            holder.recyclerRow3Binding.distanceTextView.setText("");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tıklanan öğenin pozisyonunu al
                int clickedPosition = holder.getAdapterPosition();

                // Tıklanan öğenin Place nesnesini al
                Places clickedPlace = myPlaces.get(clickedPosition);

                // PlaceDetails aktivitesine geçmek için Intent oluştur
                Intent intent = new Intent(holder.itemView.getContext(), PlaceDetails.class);

                // Intent'e tıklanan Place nesnesini ekleyerek gönder
                intent.putExtra("clickedPlace", clickedPlace);

                // Intent ile PlaceDetails aktivitesine geç
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
       return myPlaces.size();
    }

    static class PlacesHolder extends RecyclerView.ViewHolder{

       RecyclerRow3Binding recyclerRow3Binding;

        public PlacesHolder(RecyclerRow3Binding recyclerRow3Binding) {
            super(recyclerRow3Binding.getRoot());
            this.recyclerRow3Binding=recyclerRow3Binding;
        }
    }
}
