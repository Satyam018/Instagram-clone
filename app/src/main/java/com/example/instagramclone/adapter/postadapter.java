package com.example.instagramclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class postadapter extends RecyclerView.Adapter<postadapter.Viewholder> {

    Context context;
    List<Post> posts;
    FirebaseUser firebaseUser;

    public postadapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.postitem,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            Post temp=posts.get(position);
            Glide.with(context).load(temp.getPostimage()).into(holder.postimage);
            if (temp.getDescription().equals("")){
                holder.description.setVisibility(View.INVISIBLE);

            }else {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(temp.getDescription());
            }
            publisherinfo(holder.imageprofile,holder.username,holder.publisher,temp.getPublisher());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{
        public ImageView imageprofile,postimage,like,comment,save;
        public TextView username,likes,publisher,description,comments;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imageprofile=itemView.findViewById(R.id.profile);
            postimage=itemView.findViewById(R.id.postimage);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            save=itemView.findViewById(R.id.save);
            username=itemView.findViewById(R.id.usern);
            likes=itemView.findViewById(R.id.likes);
            publisher=itemView.findViewById(R.id.publisher);
            description=itemView.findViewById(R.id.descriptions);
            comments=itemView.findViewById(R.id.comments);

        }
    }
    private void publisherinfo(final ImageView imagaprofile,TextView username,TextView publisher,final String userid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("user").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img=snapshot.child("imgurl").getValue().toString();
                Glide.with(context).load(img).into(imagaprofile);
                String usernames=snapshot.child("username").getValue().toString();
                username.setText(usernames);
                publisher.setText(usernames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
