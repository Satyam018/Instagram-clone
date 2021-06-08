package com.example.instagramclone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.adapter.Photosadapter;
import com.example.instagramclone.model.Post;
import com.example.instagramclone.model.Save;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class profileFragment extends Fragment {
    private static final String TAG ="TAG" ;
    ImageView profileimg,options;
    TextView posts,followers,following,fullname,bio,username;
    Button editprofile;
    FirebaseUser firebaseUser;
    String profileid;
    ImageView myphotos,savedphotos;

    RecyclerView recyclerView;
    Photosadapter photosadapters;
    List<Post> postlist;

    private List<String> saves;
    ArrayList<String>  mysaves;
     RecyclerView recyclerViewsaves;
     Photosadapter photosadapterssaves;
     List<Post> postListsaves;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=prefs.getString("profileid","none");
        profileimg=view.findViewById(R.id.profileimage);
        options=view.findViewById(R.id.option);
        myphotos=view.findViewById(R.id.myphotos);
        savedphotos=view.findViewById(R.id.savedphotos);
        posts=view.findViewById(R.id.posts);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        fullname=view.findViewById(R.id.fullname);
        bio=view.findViewById(R.id.bio);
        username=view.findViewById(R.id.usernameprofile);
        editprofile=view.findViewById(R.id.editprofile);
        recyclerView=view.findViewById(R.id.profilerecycler);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        postlist=new ArrayList<>();
        photosadapters =new Photosadapter(getContext(),postlist);
        recyclerView.setAdapter(photosadapters);

        recyclerViewsaves=view.findViewById(R.id.profilerecyclersaves);
        GridLayoutManager gridLayoutManager1=new GridLayoutManager(getContext(),3);
        recyclerViewsaves.setLayoutManager(gridLayoutManager1);
        postListsaves=new ArrayList<>();
        photosadapterssaves=new Photosadapter(getContext(),postListsaves);
        recyclerViewsaves.setAdapter(photosadapterssaves);



        userinfo();
        getfollowers();
        getrpost();
        myposts();





        if (profileid.equals(firebaseUser.getUid())){
            editprofile.setText("Edit Profile");
        }else{
            checkfollow();
            savedphotos.setVisibility(View.INVISIBLE);
        }

        recyclerView.setVisibility(View.VISIBLE);
        myphotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewsaves.setVisibility(View.INVISIBLE);
                myposts();
            }
        });
        savedphotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerView.setVisibility(View.INVISIBLE);
                    postlist.clear();
                    photosadapters.notifyDataSetChanged();
                recyclerViewsaves.setVisibility(View.VISIBLE);
                mysaves();



            }
        });
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn=editprofile.getText().toString();
                if (btn.equals("Edit Profile")){
                    //go to edit profile
                }else if (btn.equals("follow")) {

                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").child(profileid).
                            setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(profileid).child("followers").child(firebaseUser.getUid()).
                            setValue(true);
                }else if (btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").
                            child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(profileid).child("followers").
                            child(firebaseUser.getUid()).removeValue();

                }
            }
        });


        return view;
    }
    private void userinfo(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("user").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext()==null){
                    return;
                }
                String usernames=snapshot.child("username").getValue().toString();
                String imgurl=snapshot.child("imgurl").getValue().toString();
                String fullnames=snapshot.child("fullname").getValue().toString();
                fullname.setText(fullnames);
                username.setText(usernames);
                Glide.with(getContext()).load(imgurl).into(profileimg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkfollow(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()){
                    editprofile.setText("following");
                }else{
                    editprofile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getfollowers(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            following.setText(""+snapshot.getChildrenCount());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

}
private void getrpost(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    String id=snapshot1.child("publisher").getValue().toString();
                    if (id.equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


}

private void myposts(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlist.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String id=dataSnapshot.child("publisher").getValue().toString();
                    String postid=dataSnapshot.child("postid").getValue().toString();
                    String postimage=dataSnapshot.child("postimage").getValue().toString();
                    String description=dataSnapshot.child("description").getValue().toString();
                    if (id.equals(profileid)){
                        postlist.add(new Post(postid,postimage,description,id));
                    }
                }
                Log.e(TAG, "onDataChange: "+postlist.size() );
                Collections.reverse(postlist);
                photosadapters.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}
private void mysaves(){
        mysaves=new ArrayList();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    mysaves.add(dataSnapshot.getKey());
                }

                readsaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readsaves(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postListsaves.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String id=dataSnapshot.child("postid").getValue().toString();
                    for (String ids:mysaves){
                        if (ids.equals(id)){
                            String publisher=dataSnapshot.child("publisher").getValue().toString();
                            String postimage=dataSnapshot.child("postimage").getValue().toString();
                            String description=dataSnapshot.child("description").getValue().toString();
                            postListsaves.add(new Post(id,postimage,description,publisher));



                        }

                    }
                }
                Log.e(TAG, "onDataChange: "+ postListsaves.size());

                photosadapterssaves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}