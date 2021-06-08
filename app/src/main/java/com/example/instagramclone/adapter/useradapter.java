package com.example.instagramclone.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.example.instagramclone.MainActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.model.user;
import com.example.instagramclone.profileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class useradapter extends RecyclerView.Adapter<useradapter.Viewholder> {
    private static final String TAG ="TAG" ;
    private Context context;
        private List<user> users;

    public useradapter(Context context, List<user> users) {
        this.context = context;
        this.users = users;
    }
    private FirebaseUser firebaseUser;

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.useritem,parent,false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final user user=users.get(position);
        holder.btnfollow.setVisibility(View.VISIBLE);
        holder.fullname.setText(user.getFullname());
        holder.username.setText(user.getUsername());
        Glide.with(context).load(user.getImgurl()).into(holder.imgprofile);
        isfollowing(user.getId(),holder.btnfollow);
        Log.e(TAG, "onBindViewHolder: "+user.getId() );
        Log.e(TAG, "onBindViewHolder: "+firebaseUser.getUid());
        if (user.getId().equals(firebaseUser.getUid())){
            holder.btnfollow.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",user.getId());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame1,new profileFragment()).commit();

            }
        });
        holder.btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnfollow.getText().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").child(user.getId()).
                            setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).
                            setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").
                            child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId()).child("followers").
                            child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        public TextView username,fullname;
        public ImageView imgprofile;
        public Button btnfollow;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.uname);
            fullname=itemView.findViewById(R.id.fname);
            imgprofile=itemView.findViewById(R.id.image);
            btnfollow=itemView.findViewById(R.id.btnfollow);

        }
    }
    private void isfollowing(String userid,Button button){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()){
                    button.setText("following");
                }else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}

