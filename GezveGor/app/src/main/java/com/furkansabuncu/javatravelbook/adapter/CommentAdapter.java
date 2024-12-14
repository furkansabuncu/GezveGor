package com.furkansabuncu.javatravelbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furkansabuncu.javatravelbook.databinding.RecyclerRow2Binding;
import com.furkansabuncu.javatravelbook.databinding.RecyclerRow3Binding;
import com.furkansabuncu.javatravelbook.databinding.RecyclerRow4Binding;
import com.furkansabuncu.javatravelbook.model.Comment;
import com.furkansabuncu.javatravelbook.model.Places;
import com.furkansabuncu.javatravelbook.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    ArrayList<Comment> myComments;

    public void setComments(ArrayList<Comment> myComments){
        this.myComments=myComments;
    }

    public CommentAdapter(ArrayList<Comment> commentArrayList){
        this.myComments=commentArrayList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRow4Binding recyclerRow4Binding=RecyclerRow4Binding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CommentAdapter.CommentHolder(recyclerRow4Binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        Picasso.get().load(myComments.get(position).downloadUrl).into(holder.recyclerRow4Binding.imageViewProfile);
        holder.recyclerRow4Binding.textViewComment.setText(myComments.get(position).comment);
        holder.recyclerRow4Binding.textViewUsername.setText(myComments.get(position).username);
    }

    @Override
    public int getItemCount() {
        return myComments.size();
    }


    public class CommentHolder extends RecyclerView.ViewHolder{
        RecyclerRow4Binding recyclerRow4Binding;

        public CommentHolder(RecyclerRow4Binding recyclerRow4Binding) {
            super(recyclerRow4Binding.getRoot());
            this.recyclerRow4Binding=recyclerRow4Binding;
        }
    }




}
