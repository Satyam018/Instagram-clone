package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
            TextView signup;
            EditText email,password;
            Button signin;
            ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        signup=(TextView)findViewById(R.id.signup);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        signin=(Button)findViewById(R.id.login);
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            Intent i=new Intent(MainActivity.this,MainActivity2.class);
            startActivity(i);
            finish();
        }


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,registeractivity.class);
                startActivity(i);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

    }
    private void validate(){
        String emails=email.getText().toString();
        String passwords=password.getText().toString();
        pd=new ProgressDialog(MainActivity.this);
        if (TextUtils.isEmpty(emails)||TextUtils.isEmpty(passwords)){
            Toast.makeText(MainActivity.this,"All fields are necessary",Toast.LENGTH_SHORT).show();
        }else if (passwords.length()<7){
            Toast.makeText(MainActivity.this,"",Toast.LENGTH_SHORT).show();
        }
        else{
           pd.setTitle("Signing in");
           pd.setMessage("Please wait");
           pd.show();
            checkuser(emails,passwords);
        }

    }
    private void checkuser(String emails,String passwords){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        firebaseAuth.signInWithEmailAndPassword(emails,passwords).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                pd.dismiss();
                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        })  .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               pd.dismiss();
                Toast.makeText(MainActivity.this,"Unable to login",Toast.LENGTH_SHORT).show();
            }
        });
    }
}