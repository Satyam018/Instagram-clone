package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class registeractivity extends AppCompatActivity {
    EditText email,password,name,username;
    Button signup;
    ProgressDialog progressDialog;
    String fname,passwords,emails,usernames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeractivity);
        email=(EditText)findViewById(R.id.emails);
        password=(EditText)findViewById(R.id.passwords);
        name=(EditText)findViewById(R.id.fullnames);
        signup=(Button)findViewById(R.id.register);
        username=(EditText)findViewById(R.id.usernames);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    private void register(){
                fname=name.getText().toString();
                emails=email.getText().toString();
                passwords=password.getText().toString();
                usernames=username.getText().toString();
                if (TextUtils.isEmpty(fname)||TextUtils.isEmpty(emails)||TextUtils.isEmpty(passwords)||TextUtils.isEmpty(usernames)){
                    Toast.makeText(registeractivity.this,"All fields are necessary",Toast.LENGTH_SHORT).show();
                }else if (passwords.length()<7){
                    Toast.makeText(registeractivity.this,"enter a Stronger password",Toast.LENGTH_SHORT).show();
                }else{
            progressDialog=new ProgressDialog(registeractivity.this);
            progressDialog.setTitle("Signing up");
            progressDialog.setMessage("Please wait");
            progressDialog.show();

                settofirebase(fname,emails,passwords,usernames);
                }
    }
    private void settofirebase(String fname,String emails,String passwords,String usernames){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emails,passwords).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                String uid=firebaseUser.getUid();
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("user");
                HashMap<String, Object> hashMap=new HashMap<>();
                hashMap.put("id",uid);
                hashMap.put("fullname",fname);
                hashMap.put("username",usernames.toLowerCase());
                hashMap.put("bio","");
                hashMap.put("imgurl","https://firebasestorage.googleapis.com/v0/b/instagramclone-6420d.appspot.com/o/taxi.jpg?alt=media&token=e67f41fe-f39f-4333-9267-efefc9abf9cf");
                 ref.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                         Intent i=new Intent(registeractivity.this,MainActivity2.class);
                         startActivity(i);
                         finish();
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         progressDialog.dismiss();
                         Toast.makeText(registeractivity.this,"Unable to register",Toast.LENGTH_SHORT).show();
                     }
                 });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(registeractivity.this,"Unable to register",Toast.LENGTH_SHORT).show();
            }
        });
    }
}