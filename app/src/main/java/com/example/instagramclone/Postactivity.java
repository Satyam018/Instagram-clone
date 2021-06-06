package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class Postactivity extends AppCompatActivity {
    Uri imgurl;
    String myurl="";
    StorageTask uploadtask;
    StorageReference storageReference;


    ImageView close,image_added;
    TextView post;
    EditText description;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postactivity);
        close=(ImageView)findViewById(R.id.close);
        image_added=(ImageView)findViewById(R.id.imageadded);
        post=(TextView)findViewById(R.id.post);
        description=(EditText)findViewById(R.id.description);

        storageReference= FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Postactivity.this,MainActivity2.class));
                finish();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });
        CropImage.activity().setAspectRatio(1,1).start(Postactivity.this);
    }
        private void uploadimage(){
            ProgressDialog progressDialog=new ProgressDialog(Postactivity.this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("posting");
            progressDialog.show();
            if (imgurl!=null){
                StorageReference filerefernece=storageReference.child(System.currentTimeMillis()+"."+getfileextension(imgurl));
                uploadtask=filerefernece.putFile(imgurl);
                uploadtask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isComplete()){
                            throw task.getException();
                        }
                        return filerefernece.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloaduri=task.getResult();
                            myurl=downloaduri.toString();
                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("posts");
                            String psotid=reference.push().getKey();
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("postid",psotid);
                            hashMap.put("postimage",myurl);
                            hashMap.put("description",description.getText().toString());
                            hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            reference.child(psotid).setValue(hashMap);
                            progressDialog.dismiss();
                            startActivity(new Intent(Postactivity.this,MainActivity2.class));
                            finish();

                        }else {
                            Toast.makeText(Postactivity.this,"Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Postactivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(Postactivity.this,"No image Selected!",Toast.LENGTH_SHORT).show();
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imgurl=result.getUri();
            image_added.setImageURI(imgurl);
        }else {
            Toast.makeText(Postactivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Postactivity.this,MainActivity2.class));
            finish();
        }
    }
    private String getfileextension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}