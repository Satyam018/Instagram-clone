package com.example.instagramclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.MainActivity3;
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
            islike(temp.getPostid(),holder.like);
            nrliskes(holder.likes,temp.getPostid());
            getcomment(temp.getPostid(),holder.comments);
            isSaved(temp.getPostid(),holder.save);


            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.like.getTag().equals("like")){
                        FirebaseDatabase.getInstance().getReference().child("likes").child(temp.getPostid())
                                .child(firebaseUser.getUid()).setValue(true);
                        islike(temp.getPostid(),holder.like);
                        nrliskes(holder.likes,temp.getPostid());
                    }else{
                        FirebaseDatabase.getInstance().getReference().child("likes").child(temp.getPostid())
                                .child(firebaseUser.getUid()).removeValue();
                        islike(temp.getPostid(),holder.like);
                        nrliskes(holder.likes,temp.getPostid());
                    }
                }
            });
            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, MainActivity3.class);
                    intent.putExtra("postid",temp.getPostid());
                    intent.putExtra("publisherid",temp.getPublisher());
                    context.startActivity(intent);
                }
            });
            holder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.save.getTag().equals("save")){
                        FirebaseDatabase.getInstance().getReference("saves").child(firebaseUser.getUid()).child(temp.getPostid()).setValue(true);
                    }else {
                        FirebaseDatabase.getInstance().getReference("saves").child(firebaseUser.getUid()).child(temp.getPostid()).removeValue();
                    }
                }
            });
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
    private void islike(String postid,ImageView imageView){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("likes").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_24);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(R.drawable.like);
                    imageView.setTag("like");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void nrliskes(TextView likes,String postid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+"likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getcomment(String postid,final TextView commet){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                commet.setText("view all"+snapshot.getChildrenCount()+"comments");}
                else {
                    commet.setText("No comment yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void isSaved(String postid, ImageView imageView){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()){
                imageView.setImageResource(R.drawable.ic_baseline_bookmark_24);
                imageView.setTag("saved");
                }else {
                    imageView.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

