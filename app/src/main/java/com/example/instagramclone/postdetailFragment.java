package com.example.instagramclone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.adapter.postadapter;
import com.example.instagramclone.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class postdetailFragment extends Fragment {
    String postid;
    List<Post> postlist;
    private com.example.instagramclone.adapter.postadapter postadapter;
    private RecyclerView recyclerView;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view= inflater.inflate(R.layout.fragment_postdetail, container, false);
        SharedPreferences preferences=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid=preferences.getString("postid","none");
        recyclerView=view.findViewById(R.id.recyclerpostdetail);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        postlist=new ArrayList<>();
        postadapter=new postadapter(getContext(),postlist);
        recyclerView.setAdapter(postadapter);

        readpost();




      return view;
    }
    private void readpost(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            postlist.clear();
                String postid=snapshot.child("postid").getValue().toString();
                String description=snapshot.child("description").getValue().toString();
                String postimage=snapshot.child("postimage").getValue().toString();
                String publihser=snapshot.child("publisher").getValue().toString();
                postlist.add(new Post(postid,postimage,description,publihser));
                postadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}