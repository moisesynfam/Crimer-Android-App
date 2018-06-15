package com.ynfante.crimer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;

import java.util.List;

public class NewPostActivity extends AppCompatActivity {

    private final String TAG = NewPostActivity.class.getSimpleName();

    @NotEmpty
    private EditText title;

    @NotEmpty
    private EditText content;

    private Uri imageUri;

    private RelativeLayout imagePanel;

    private FloatingActionButton removeImageFab;

    private ImageButton selectPictureBtn;

    private ImageView postImage;

    private MaterialDialog cancelDialog;

    private Validator formValidator;

    private ImagePicker imagePicker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imagePanel = findViewById(R.id.new_post_image_panel);
        removeImageFab = findViewById(R.id.new_post_remove_image_btn);
        selectPictureBtn = findViewById(R.id.new_post_add_image_btn);
        content = findViewById(R.id.new_post_content);
        title = findViewById(R.id.new_post_title);
        postImage = findViewById(R.id.new_post_image);

        formValidator = new Validator(this);

        formValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                if(imageUri == null) {
                    Toast.makeText(NewPostActivity.this, R.string.new_post_no_image_error_msg, Toast.LENGTH_LONG).show();
                    return;
                }

                submitPost();


            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    View view = error.getView();
                    String message = error.getCollatedErrorMessage(NewPostActivity.this);

                    // Display error messages
                    if (view instanceof EditText) {
                        ((EditText) view).setError(message);
                    } else {
                        Toast.makeText(NewPostActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }

                if(imageUri == null) {
                    Toast.makeText(NewPostActivity.this, R.string.new_post_no_image_error_msg, Toast.LENGTH_LONG).show();
                }
            }
        });

        imagePicker = new ImagePicker(this, null, new OnImagePickedListener() {
            @Override
            public void onImagePicked(Uri imageUrl) {
                Log.d(TAG, "Image Found: RESULT2");
                Log.d(TAG, "Image Found:" + imageUrl.toString());
                imageUri = imageUrl;
                imagePanel.setVisibility(View.VISIBLE);
                selectPictureBtn.setVisibility(View.GONE);
                Glide.with(NewPostActivity.this)
                        .load(imageUri)

                        .into(postImage);

            }
        });

        cancelDialog = new MaterialDialog.Builder(this)
                .title(R.string.new_post_cancel_dialog_title)
                .positiveText(R.string.general_yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .negativeText(R.string.general_no)
                .autoDismiss(true)
                .build();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog.show();
            }
        });


        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        selectPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        removeImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.new_post_submit) {
            formValidator.validate();

        }
        return super.onOptionsItemSelected(item);
    }

    public void submitPost () {

    }

    public void addImage() {
        imagePicker.choosePicture(true);
    }

    public void removeImage() {
        imageUri = null;
        postImage.setImageDrawable(null);
        imagePanel.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imagePicker.handlePermission(requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Image Found: RESULT");
        super.onActivityResult(requestCode, resultCode, data);


        imagePicker.handleActivityResult(resultCode, requestCode, data);

    }
}
