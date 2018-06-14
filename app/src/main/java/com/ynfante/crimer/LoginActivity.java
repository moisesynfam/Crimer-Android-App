package com.ynfante.crimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;
import java.util.Objects;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity {

    private Button registerBtn;
    private Button logInBtn;

    @NotEmpty
    @Email
    private EditText email;

    @Password
    @NotEmpty
    private EditText password;

    private Validator formValidator;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logInBtn = findViewById(R.id.login_log_in_btn);
        registerBtn = findViewById(R.id.login_register_btn);

        email = findViewById(R.id.login_email_input);
        password = findViewById(R.id.login_password_input);
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidator.validate();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivityIntent = new Intent( LoginActivity.this, RegisterActivity.class);
                startActivity(registerActivityIntent);
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();

        formValidator = new Validator(this);
        formValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                logIn(email.getText().toString(), password.getText().toString());
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    View view = error.getView();
                    String message = error.getCollatedErrorMessage(LoginActivity.this);

                    // Display error messages
                    if (view instanceof EditText) {
                        ((EditText) view).setError(message);
                    } else {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void logIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    goToPosts();
                } else {
                    String exceptionMessage = Objects.requireNonNull(task.getException()).getMessage();
                    if (exceptionMessage.length() <= 0){
                        exceptionMessage = getString(R.string.login_error_message);
                    }
                    Toast.makeText(LoginActivity.this, exceptionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void goToPosts() {
        Intent intent = new Intent(this, MainActivity.class);
        //This is used to close all other activities and start the main one
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
