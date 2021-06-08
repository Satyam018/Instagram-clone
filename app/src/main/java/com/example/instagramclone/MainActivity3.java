package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.adapter.Commentadapter;
import com.example.instagramclone.model.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {
    TextView post;
    EditText comment;
    ImageView userimg;
    String postid;
    RecyclerView recyclerView;
    private Commentadapter commentadapters;
    private List<Comment> commentslists;
    String publisher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
      Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        post=(TextView)findViewById(R.id.postcomment);
        comment=(EditText)findViewById(R.id.entercomment);
        userimg=(ImageView)findViewById(R.id.userimgcommsection);
        recyclerView=(RecyclerView) findViewById(R.id.commentshorecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout=new LinearLayoutManager(MainActivity3.this);
        recyclerView.setLayoutManager(linearLayout);
        commentslists=new ArrayList<>();
        commentadapters=new Commentadapter(MainActivity3.this,commentslists);
        recyclerView.setAdapter(commentadapters);

        Intent intent=getIntent();
         postid=intent.getStringExtra("postid").toString();
       publisher=intent.getStringExtra("publisherid").toString();
       post.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String comments=comment.getText().toString();
               if (TextUtils.isEmpty(comments)){
                   Toast.makeText(MainActivity3.this,"enter a cooment",Toast.LENGTH_SHORT).show();

               }else {
                   addcomment(comments);
               }
           }
       });
       getimg();
       readcomments();
    }
    private void addcomment(String comments){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("comments").child(postid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("comment",comments);
        hashMap.put("publisherid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.push().setValue(hashMap);
        comment.setText("");
    }
    private void getimg(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img=snapshot.child("imgurl").getValue().toString();
                Glide.with(MainActivity3.this).load(img).into(userimg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readcomments(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentslists.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String comment=dataSnapshot.child("comment").getValue().toString();
                    String publihserid=dataSnapshot.child("publisherid").getValue().toString();
                    commentslists.add(new Comment(comment,publihserid));
                }
                commentadapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addnotifictaion(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("notifications").child(publisher);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("text","commented on the post");
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);
    }

}