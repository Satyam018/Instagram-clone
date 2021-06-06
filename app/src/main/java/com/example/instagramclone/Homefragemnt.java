package com.example.instagramclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.instagramclone.adapter.postadapter;
import com.example.instagramclone.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Homefragemnt extends Fragment {
        private RecyclerView recyclerView;
        private postadapter postadapter;
        private List<Post> posts;
        private List<String> following;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_homefragemnt, container, false);

        recyclerView=view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        posts=new ArrayList<>();
        postadapter=new postadapter(getContext(),posts);
        recyclerView.setAdapter(postadapter);
        checkfollowing();


        return view;
    }
    private void checkfollowing(){
        following=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 following.clear();
                 for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                     following.add(dataSnapshot.getKey());
                 }
                 readpost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readpost(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    String id=snapshot1.child("publisher").getValue().toString();
                    for (String ids:following){
                        if (id.equals(ids)){
                            String image=snapshot1.child("postimage").getValue().toString();
                            String description=snapshot1.child("description").getValue().toString();
                            String postid=snapshot1.child("postid").getValue().toString();
                            posts.add(new Post(postid,image,description,id));


                        }
                    }
                }
                postadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}