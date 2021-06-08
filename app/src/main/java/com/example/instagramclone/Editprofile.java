package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class Editprofile extends AppCompatActivity {
            ImageView close,photo;
            TextView save,changephoto;
            MaterialEditText fullname,username,bio;

            FirebaseUser firebaseUser;

            private Uri imageuri;

            private StorageTask storageTask;
            StorageReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        close=(ImageView)findViewById(R.id.close);
        photo=(ImageView)findViewById(R.id.editimage);
        save=(TextView)findViewById(R.id.save);
        changephoto=(TextView)findViewById(R.id.editchangephoto);
        fullname=(MaterialEditText)findViewById(R.id.editfullname);
        username=(MaterialEditText)findViewById(R.id.editusername);
        bio=(MaterialEditText)findViewById(R.id.editbio);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseStorage.getInstance().getReference("uploads");
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("user").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullnames=snapshot.child("fullname").getValue().toString();
                String bios=snapshot.child("bio").getValue().toString();
                String usernames=snapshot.child("username").getValue().toString();
                fullname.setText(fullnames);
                bio.setText(bios);
                username.setText(usernames);
                String imgurl=snapshot.child("imgurl").getValue().toString();
                Glide.with(Editprofile.this).load(imgurl).into(photo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        changephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(Editprofile.this);
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(Editprofile.this);

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullnames=fullname.getText().toString();
                String usernames=username.getText().toString();
                String bois=bio.getText().toString();
                if (TextUtils.isEmpty(fullnames)||TextUtils.isEmpty(usernames)||TextUtils.isEmpty(bois)){
                    Toast.makeText(Editprofile.this,"All field are necessary",Toast.LENGTH_LONG).show();
                }else{
                updateprofile(usernames,fullnames,bois);}
                Toast.makeText(Editprofile.this,"Successfully Uploaded",Toast.LENGTH_LONG).show();
            }
        });

    }
    private void updateprofile(String username,String fullname,String bio){
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("user").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("fullname",fullname);
        hashMap.put("username",username);
        hashMap.put("bio",bio);
        ref.updateChildren(hashMap);

    }
            private String getfileextension(Uri uri){
                ContentResolver contentResolver=getContentResolver();
                MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
                return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
            }
            private void uploadimage(){
                ProgressDialog pd=new ProgressDialog(Editprofile.this);
                pd.setTitle("Please wait");
                pd.setMessage("Uploading");
                pd.show();

                if (imageuri!=null){
                   StorageReference filerefernece=reference.child(System.currentTimeMillis()+""+getfileextension(imageuri));
                   storageTask=filerefernece.putFile(imageuri);
                   storageTask.continueWithTask(new Continuation() {
                       @Override
                       public Object then(@NonNull Task task) throws Exception {
                           if (!task.isSuccessful()){
                               throw task.getException();
                           }
                           return filerefernece.getDownloadUrl();
                       }
                   }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                       @Override
                       public void onComplete(@NonNull Task<Uri> task) {
                           if (task.isSuccessful()) {
                               Uri downloadUri = task.getResult();
                               String myurl=downloadUri.toString();
                               DatabaseReference reference=FirebaseDatabase.getInstance().getReference("user").child(firebaseUser.getUid());
                               HashMap<String,Object> hashMap=new HashMap<>();
                               hashMap.put("imgurl",myurl);
                               reference.updateChildren(hashMap);
                               pd.dismiss();



                           } else {
                               // Handle failures
                               // ...
                               Toast.makeText(Editprofile.this,"Failed",Toast.LENGTH_LONG).show();
                           }

                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Editprofile.this,"failed to obtain",Toast.LENGTH_LONG).show();
                       }
                   });


                }else {
                    Toast.makeText(Editprofile.this,"No image selected",Toast.LENGTH_LONG).show();
                }
            }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageuri=result.getUri();
            uploadimage();
        }else{
            Toast.makeText(Editprofile.this,"Something went wrong",Toast.LENGTH_LONG).show();
        }
    }
}