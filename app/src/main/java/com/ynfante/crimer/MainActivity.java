package com.ynfante.crimer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.ynfante.crimer.Models.Post;
import com.ynfante.crimer.Models.User;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private FirebaseUser signedUser;
    private FirebaseAuth firebaseAuth;
    private Button logOutBtn;
    private FirebaseFirestore database;
    private User userInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        signedUser = firebaseAuth.getCurrentUser();


        if(signedUser == null) {

            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout =  findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.general_posts));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.general_profile));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

         ViewPager viewPager =  findViewById(R.id.pager);
         TabsPagerAdapter adapter = new TabsPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        database.collection("users").document(signedUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    userInstance = task.getResult().toObject(User.class);
                    Log.d(TAG, userInstance.getName());

                } else {
                    Log.e(TAG, task.getException().getMessage());
                }
            }
        });

    }

    public void goToCreatePost() {
        Intent intent = new Intent(this, NewPostActivity.class);
        intent.putExtra("userInstance", userInstance);
        startActivity(intent);
    }

    public void goToDetailedActivity(Post post) {
        Intent intent = new Intent(this,  PostDetailActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }

    public User getUserInstance() {
        return userInstance;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.log_out_button) {
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
