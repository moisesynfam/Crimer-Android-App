package com.ynfante.crimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class RegisterActivity extends AppCompatActivity implements Validator.ValidationListener{

    private final String TAG = RegisterActivity.class.getSimpleName();

    @NotEmpty
    private EditText name;

    @NotEmpty
    @Email
    private EditText email;

    @NotEmpty
    @Password()
    private EditText password;

    private Button registerBtn;

    private Validator registerValidator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.register_name_input);
        password = findViewById(R.id.register_password_input);
        email = findViewById(R.id.register_email_input);

        registerValidator = new Validator(this);
        registerValidator.setValidationListener(this);

        registerBtn = findViewById(R.id.register_register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerValidator.validate();
            }
        });


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
}
