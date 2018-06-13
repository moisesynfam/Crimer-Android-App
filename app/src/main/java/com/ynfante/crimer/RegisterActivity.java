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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                         if(checkForCameraPermission()) {
                             takeImage();
                         }
                         break;
                     case R.id.img_bttm_sheet_gallery_btn:
                         if(checkForStoragePermissions()) {
                             chooseImage();
                         }
                         break;
                     case R.id.img_bttm_sheet_delete_btn:
                         break;
                 }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if all the permissions are granted proceed to pick the image
//                if(checkForPermissions())
//                    chooseImage();

                imagePickerFragment.show(getSupportFragmentManager(),
                        "pick_photo_dialog_fragment");


            }
        });




    }


    public void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");


        startActivityForResult(Intent.createChooser(intent, getString(R.string.register_select_image_dialog)), PICK_IMAGE_REQUEST);
    }

    public void takeImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.register_select_image_dialog)), TAKE_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    }

    @Override
    public void onValidationSucceeded() {
        Log.d(TAG, "Form succeded");
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

    public void registerUser(String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                        } else {

                        }
                    }
                });
    }

    private  boolean checkForCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    //array of permissions to request
                    new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST
                    //Request code
            );

            return false;
        }
        return true;
    }

    private boolean checkForStoragePermissions() {

        ArrayList<String> permissionsNeeded = new ArrayList<>();


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
            ActivityCompat.requestPermissions(this,
                    //array of permissions to request
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    //Request code
                    STORAGE_PERMISSION_REQUEST
                     );
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
