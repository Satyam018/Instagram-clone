package com.example.instagramclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.instagramclone.adapter.useradapter;
import com.example.instagramclone.model.user;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private static final String TAG ="TAG" ;
    private RecyclerView recyclerView;
        private useradapter useradapter;
        private List<user> users;


        EditText searchbar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        searchbar=view.findViewById(R.id.searchbar);
        recyclerView=view.findViewById(R.id.recycler);
        users=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        useradapter=new useradapter(getContext(),users);
        recyclerView.setAdapter(useradapter);
        Log.e(TAG, "onCreateView: "+"search fragment" );
            readuser();
            searchbar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String data=s.toString();
                    Log.e(TAG, "onTextChanged: "+data );
                    searchuser(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });



        return view;
    }
    private void searchuser(String s){
        Query query= FirebaseDatabase.getInstance().getReference("user").orderByChild("username")
                .startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String id=dataSnapshot.child("id").getValue().toString();
                    String name=dataSnapshot.child("fullname").getValue().toString();
                    String bio=dataSnapshot.child("bio").getValue().toString();
                    String imageurl=dataSnapshot.child("imgurl").getValue().toString();
                    String username=dataSnapshot.child("username").getValue().toString();
                    user user1=new user(id,username,name,imageurl,bio);
                    users.add(user1);
                    Log.e(TAG, "onDataChange: "+users.size() );
                }
                useradapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readuser(){
        Log.e(TAG, "readuser: "+"readuser" );

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (searchbar.getText().toString().equals("")){
                    users.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String id=dataSnapshot.child("id").getValue().toString();
                        String name=dataSnapshot.child("fullname").getValue().toString();
                        String bio=dataSnapshot.child("bio").getValue().toString();
                        String imageurl=dataSnapshot.child("imgurl").getValue().toString();
                        String username=dataSnapshot.child("username").getValue().toString();
                        user user2=new user(id,username,name,imageurl,bio);



                        users.add(user2);
                       
                    }
                    useradapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}