package com.example.instagramclone.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.R;
import com.example.instagramclone.model.Notification;
import com.example.instagramclone.postdetailFragment;
import com.example.instagramclone.profileFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Notificationadapter extends RecyclerView.Adapter<Notificationadapter.Viewholder> {
    private Context context;
    private List<Notification> notificationList;

    public Notificationadapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notifictationitem,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Notification temp=notificationList.get(position);

        holder.text.setText(temp.getText());
        getuserinfo(holder.imageprofile,holder.username,temp.getUserid());
        if (temp.isIspost()){
            holder.postimage.setVisibility(View.VISIBLE);
            getpostimage(holder.postimage,temp.getPostid());
        }else{
            holder.postimage.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp.isIspost()){
                SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",temp.getPostid());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame1,new postdetailFragment()).commit();
            }else {
                    SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",temp.getUserid());
                    editor.apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame1,new profileFragment()).commit();
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{
        ImageView imageprofile,postimage;
        TextView username,text;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imageprofile=itemView.findViewById(R.id.notificationimageprofile);
            postimage=itemView.findViewById(R.id.notipostimage);
            username=itemView.findViewById(R.id.notiusername);
            text=itemView.findViewById(R.id.noticomment);
        }
    }

    private  void getuserinfo(ImageView profile,TextView username,String publisherid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("user").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img=snapshot.child("imgurl").getValue().toString();
                String usernames=snapshot.child("username").getValue().toString();
                Glide.with(context).load(img).into(profile);
                username.setText(usernames);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getpostimage(ImageView imageView,String postid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img=snapshot.child("postimage").getValue().toString();
                Glide.with(context).load(img).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
