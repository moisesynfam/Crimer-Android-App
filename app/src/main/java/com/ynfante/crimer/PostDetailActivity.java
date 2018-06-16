package com.ynfante.crimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ynfante.crimer.Models.Post;
import com.ynfante.crimer.Models.User;
import com.ynfante.crimer.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;


public class PostDetailActivity extends AppCompatActivity {

    private TextView name, username, title, content, location, date;
    private ImageView displayPicture, postImage;
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
        date = findViewById(R.id.post_detail_date);

        User userPost = post.getUser();

        name.setText(userPost.getName());
        username.setText("@"+userPost.getUsername());
        title.setText(post.getTitle());
        content.setText(post.getContent());
        location.setText(post.getLocation().getPlace());

        date.setText(DateFormat.getDateInstance(DateFormat.LONG).format(post.getPublishedDate()));

        Glide.with(this)
                .load(post.getImageUrl())
                .into(postImage);

        Glide.with(this)
                .load(userPost.getPhotoUrl())
                .into(displayPicture);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.post_detail_share) {
//            sharePost();
            newShare();
        }
        return super.onOptionsItemSelected(item);
    }

    public void sharePost() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        Intent shareIntent;

        Bitmap bitmap = ((BitmapDrawable)postImage.getDrawable()).getBitmap();

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Share.jpeg";
        OutputStream out = null;
        File file=new File(path);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        path=file.getPath();
        Uri bmpUri = Uri.parse("file://"+path);
        Log.d("SHARE", bmpUri.toString());
        shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT,post.getTitle() + " - " + post.getContent());
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent,getString(R.string.general_share) ));




    }

    private void newShare() {
        Bitmap bitmap = ((BitmapDrawable)postImage.getDrawable()).getBitmap();
        try {

            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.jpeg"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.jpeg");
        Uri contentUri = FileProvider.getUriForFile(this, "com.ynfante.crimer.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT,post.getTitle() + "\n\n" + post.getContent());
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }

    }
}
