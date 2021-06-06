package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity2 extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottomnav);
        frameLayout=(FrameLayout)findViewById(R.id.frame1);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame1,new Homefragemnt()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment temp=null;
                switch (item.getItemId()){
                    case R.id.navhome:
                        temp=new Homefragemnt();
                        break;
                    case R.id.navsearch:
                        temp=new SearchFragment();
                        break;
                    case R.id.navadd:
                        temp=null;
                        startActivity(new Intent(MainActivity2.this,Postactivity.class));
                        break;
                    case R.id.navheart:
                        temp=new NotificationFragment();
                        break;
                    case R.id.navprofile:
                        SharedPreferences.Editor    editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                        editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        temp=new profileFragment();
                        break;
                }
                if (temp!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame1,temp).commit();
                }

                return true;
            }
        });
    }
}