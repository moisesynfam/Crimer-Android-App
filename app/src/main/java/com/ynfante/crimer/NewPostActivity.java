package com.ynfante.crimer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;
import com.ynfante.crimer.Models.Post;
import com.ynfante.crimer.Models.User;

import java.util.List;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private final String TAG = NewPostActivity.class.getSimpleName();

    @NotEmpty
    private EditText title;

    @NotEmpty
    private EditText content;

    private Uri imageUri;

    private CardView imagePanel;

    private FloatingActionButton removeImageFab;

    private ImageButton selectPictureBtn;

    private ImageView postImage;

    private MaterialDialog cancelDialog;

    private Validator formValidator;

    private ImagePicker imagePicker;

    private MaterialDialog imageUploadingDialog;
    private MaterialDialog errorUploadingDialog;


    private StorageReference postPicturesReference;
    private FirebaseFirestore database;
    private FirebaseUser user;
    private String postId;

    private User userInstance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        userInstance = (User) getIntent().getSerializableExtra("userInstance");
        Log.d(TAG, "User: " + userInstance.getName());
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


        user = FirebaseAuth.getInstance().getCurrentUser();
        postPicturesReference = FirebaseStorage.getInstance().getReference("images/posts");
        database = FirebaseFirestore.getInstance();

        initDialogs();
    }

    public void initDialogs() {
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

        errorUploadingDialog = new MaterialDialog.Builder(this)
                .title("Error Submitting Post")
                .content("There was an error trying to submit your posts to the server. Please try again later.")
                .positiveText(R.string.general_dismiss)
                .build();

        imageUploadingDialog = new MaterialDialog.Builder(this)
                .content("Your post is being submitted. Please Wait...")
                .progress(false, 0)
                .canceledOnTouchOutside(false)
                .build();
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
        postId = createTransactionID();
        Log.d(TAG, "NEW UUID: " + postId);
        final StorageReference newPictureRef = postPicturesReference.child(postId);

        newPictureRef.putFile(imageUri)
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    if(!imageUploadingDialog.isShowing()) {
                        imageUploadingDialog.setMaxProgress((int) taskSnapshot.getTotalByteCount());
                        imageUploadingDialog.show();
                    }
                    imageUploadingDialog.setProgress((int) taskSnapshot.getBytesTransferred());

                }
            })
            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {

                        throw task.getException();
                    }

                    return  newPictureRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    imageUploadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d(TAG,"Image URI:" + downloadUri.toString());
                        storePost( downloadUri.toString());
                    } else {
                        //here we display error message
                        errorUploadingDialog.show();
                        Log.e(TAG,task.getException().getMessage());
                    }
                }
            });


    }

    public void storePost(String imageUrl) {

        Post newPost = new Post(user.getUid(), imageUrl, title.getText().toString(), content.getText().toString(), userInstance);
        database.collection("posts").document(postId).set(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "New Post: "+postId);
                    finish();
                } else {
                    errorUploadingDialog.show();
                    Log.d(TAG,  task.getException().getMessage());
                }
            }
        });



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

    public String createTransactionID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
