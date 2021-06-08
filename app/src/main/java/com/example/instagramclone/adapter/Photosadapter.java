package com.example.instagramclone.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.model.Post;

import java.util.List;


public class Photosadapter extends RecyclerView.Adapter<Photosadapter.Viewholder> {
    private static final String TAG = "TAG";
    private Context context;
    private List<Post> posts;

    public Photosadapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;


    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.photositem,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Post temp=posts.get(position);

        Glide.with(context).load(temp.getPostimage()).into(holder.postphotos);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{
        ImageView postphotos;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            postphotos=itemView.findViewById(R.id.postedimage);
        }
    }
}

