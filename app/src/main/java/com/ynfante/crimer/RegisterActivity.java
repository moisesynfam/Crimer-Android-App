package com.ynfante.crimer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;
import com.ynfante.crimer.Models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements Validator.ValidationListener{

    private final String TAG = RegisterActivity.class.getSimpleName();

    //ACTIVITY RESULTS CODES
    private final int PICK_IMAGE_REQUEST = 100;
    private final int TAKE_IMAGE_REQUEST = 101;

    //PERMISSION REQUEST CODE
    private final int STORAGE_PERMISSION_REQUEST = 200;
    private final int CAMERA_PERMISSION_REQUEST = 201;

    @NotEmpty
    private EditText name;

    @NotEmpty
    private EditText username;

    @NotEmpty
    @Email
    private EditText email;

    @NotEmpty
    @Password()
    private EditText password;

    private Button registerBtn;
    private CircularImageView profileImage;

    private Validator registerValidator;

    private FirebaseAuth mAuth;

    private Uri profilePicturePath;

    private ImagePickerBottomSheetFragment imagePickerFragment;

    private ImagePicker imagePicker;
    private FirebaseStorage firebaseStorage;
    private StorageReference profilePictureReference;
    private FirebaseFirestore database;


    private MaterialDialog userProgressDialog, imageErrorDialog, userErrorDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.register_username_input);
        name = findViewById(R.id.register_name_input);
        password = findViewById(R.id.register_password_input);
        email = findViewById(R.id.register_email_input);
        profileImage =  findViewById(R.id.register_profile_picture);

        registerValidator = new Validator(this);
        registerValidator.setValidationListener(this);

        registerBtn = findViewById(R.id.register_register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerValidator.validate();
            }
        });
        imagePickerFragment = ImagePickerBottomSheetFragment.newInstance();
        imagePickerFragment.addButtonClickListener(new ImagePickerBottomSheetFragment.ButtonClickListener() {
            @Override
            public void onClick(View button) {

                 switch (button.getId()) {

                     case R.id.img_bttm_sheet_camera_btn:
                         if(checkForPicturesPermissions(true)) {
                             takeImage();
                         }
                         break;
                     case R.id.img_bttm_sheet_gallery_btn:
                         if(checkForPicturesPermissions(false)) {
                             chooseImage();
                         }
                         break;
                     case R.id.img_bttm_sheet_delete_btn:
                         break;
                 }
                imagePickerFragment.dismiss();
            }
        });

        imagePicker = new ImagePicker(this, null, new OnImagePickedListener() {
            @Override
            public void onImagePicked(Uri imageUri) {
                profilePicturePath = imageUri;
                Glide.with(RegisterActivity.this)
                        .load(profilePicturePath)
                        .apply(RequestOptions.centerCropTransform())
                        .into(profileImage);
            }
        }).setWithImageCrop(1,1);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if all the permissions are granted proceed to pick the image
//                if(checkForPermissions())
//                    chooseImage();

//                imagePickerFragment.show(getSupportFragmentManager(),
//                        "pick_photo_dialog_fragment");
                imagePicker.choosePicture(true);


            }
        });
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        profilePictureReference = firebaseStorage.getReference().child("images/profilePictures");
        database = FirebaseFirestore.getInstance();
        buildDialogs();



    }

    public void  buildDialogs() {
        userProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.register_user_loading_title)
                .progress(true, -1)
                .autoDismiss(false)
                .cancelable(false)
                .build();

        imageErrorDialog = new MaterialDialog.Builder(this)
                .title(R.string.register_image_uploading_error_title)
                .content(R.string.register_image_uploading_error_content)
                .positiveText(R.string.general_dismiss)
                .build();

        userErrorDialog = new MaterialDialog.Builder(this)
                .title(R.string.register_user_error_title)
                .content(R.string.register_user_error_content)
                .positiveText(R.string.general_dismiss)
                .build();
    }

    public void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");


        startActivityForResult(Intent.createChooser(intent, getString(R.string.register_select_image_dialog)), PICK_IMAGE_REQUEST);
    }

    public void takeImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.setType("image/*");
        startActivityForResult(intent, TAKE_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode, requestCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            //The result from the data is the URI of the picture
            profilePicturePath = data.getData();
            Log.d(TAG, profilePicturePath.toString());
            try {
                //Used to get a bitmap from the profile picture
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profilePicturePath);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    profileImage.setImageTintList(null);
                } else {
                    ImageViewCompat.setImageTintList(profileImage, null);
                }

                Glide.with(this)
                        .load(profilePicturePath)
                        .apply(RequestOptions.centerCropTransform())
                        .into(profileImage);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (requestCode == TAKE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            profilePicturePath= data.getData();
            Log.d(TAG, "Image loaded");
            Glide.with(this)
                    .load(imageBitmap)
                    .apply(RequestOptions.centerCropTransform())
                    .into(profileImage);

        }
    }

    @Override
    public void onValidationSucceeded() {
        Log.d(TAG, "Form succeded");
        registerUser(name.getText().toString(), username.getText().toString(), email.getText().toString(), password.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void registerUser(final String name, final String username, String email, String password) {
        userProgressDialog.show();
        Log.d(TAG, "registerUser:started");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            storeNewUser(user, name, username);

                        } else {
                            Log.d(TAG, "createUserWithEmail:fail");
                            Log.d(TAG, task.getException().getMessage());
                        }
                    }
                });
    }

    public void storeNewUser(final FirebaseUser user, String name, String username) {
        Log.d(TAG, "storeNewUser:savingToDatabase");
        User newUser = new User(username, name, null, null);
        database.collection("users").document(user.getUid()).set(newUser)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    storeProfilePicture(user);
                    Log.d(TAG, "USER SAVED");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            });
    }

    public void storeProfilePicture (final FirebaseUser user) {
        if( profilePicturePath != null) {
            final StorageReference newPictureRef = profilePictureReference.child(user.getUid() + ".jpeg");
            UploadTask profileUploadTask = newPictureRef.putFile(profilePicturePath);
            profileUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = newPictureRef.getDownloadUrl().getResult();
                        saveProfilePicture(user, downloadUri);
                    } else {
                        //here we display error message
                        goToPosts();
                    }
                }
            });
        } else {
            goToPosts();
        }
    }

    public void goToPosts() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void saveProfilePicture(FirebaseUser user, Uri downloadUrl) {

        database.collection("users").document(user.getUid()).update("photoUrl", downloadUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if( !task.isSuccessful() ) {
                            Log.w(TAG, task.getException().getMessage());
                        }
                        goToPosts();
                    }
                });

    }

    private boolean checkForPicturesPermissions(boolean forCamera) {

        ArrayList<String> permissionsNeeded = new ArrayList<>();

        if ( forCamera && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        //if the list is not empty we proceed to task the user for permission
        if (permissionsNeeded.size() > 0) {
            if(forCamera) {
                ActivityCompat.requestPermissions(this,
                        //array of permissions to request
                        permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                        //Request code
                        CAMERA_PERMISSION_REQUEST
                );
            } else {
                ActivityCompat.requestPermissions(this,
                        //array of permissions to request
                        permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                        //Request code
                        STORAGE_PERMISSION_REQUEST
                );
            }

            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.handlePermission(requestCode, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                    chooseImage();
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                break;
            }
            case CAMERA_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                    takeImage();
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                break;
            }
        }
    }
}
