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
import com.example.instagramclone.MainActivity2;
import com.example.instagramclone.R;
import com.example.instagramclone.model.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class Commentadapter extends RecyclerView.Adapter<Commentadapter.Viewholder> {
    Context context;
    List<Comment>mcomments;

    public Commentadapter(Context context, List<Comment> mcomments) {
        this.context = context;
        this.mcomments = mcomments;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.commentitem,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
            Comment temp=mcomments.get(position);
            holder.comment.setText(temp.getComment());
            getuserinfo(holder.imgview,holder.uname,temp.getPublihser());
            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, MainActivity2.class);
                    intent.putExtra("publihserid",temp.getPublihser());
                    context.startActivity(intent);
                }
            });
        holder.imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MainActivity2.class);
                intent.putExtra("publihserid",temp.getPublihser());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mcomments.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{
            ImageView imgview;
            TextView uname,comment;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imgview=itemView.findViewById(R.id.imagecommentprofile);
            uname=itemView.findViewById(R.id.commenteduser);
            comment=itemView.findViewById(R.id.commentbyuser);
        }
    }
    private void getuserinfo(ImageView imgview,TextView umane,String publihserid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("user").child(publihserid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              String imgurl=snapshot.child("imgurl").getValue().toString();
              String username=snapshot.child("username").getValue().toString();
                Glide.with(context).load(imgurl).into(imgview);
                umane.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
