package com.ynfante.crimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ynfante.crimer.Models.Post;
import com.ynfante.crimer.Models.User;
import com.ynfante.crimer.R;

public class PostDetailActivity extends AppCompatActivity {

    private TextView name, username, title, content, location;
    private ImageView displayPicture, postImage, shareButton;

    private Post post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        post = (Post) getIntent().getSerializableExtra("post");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        name = findViewById(R.id.post_detail_user);
        username = findViewById(R.id.post_detail_username);
        title = findViewById(R.id.post_detail_title);
        content = findViewById(R.id.post_detail_content);
        location = findViewById(R.id.post_detail_location);
        displayPicture = findViewById(R.id.post_detail_display_picture);
        postImage = findViewById(R.id.post_detail_image);
        shareButton = findViewById(R.id.post_detail_share_btn);

        User userPost = post.getUser();

        name.setText(userPost.getName());
        username.setText(userPost.getUsername());
        title.setText(post.getTitle());
        content.setText(post.getContent());
        location.setText(post.getLocation().getPlace());

        Glide.with(this)
                .load(post.getImageUrl())
                .into(postImage);

        Glide.with(this)
                .load(userPost.getPhotoUrl())
                .into(displayPicture);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
