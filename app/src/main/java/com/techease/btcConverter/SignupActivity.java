package com.techease.btcConverter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnSignUp;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("Reg", 0);
        editor = sharedPreferences.edit();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        etName = (EditText) findViewById(R.id.etName);
        etEmail =(EditText) findViewById(R.id.etEmailLogin);
        etPassword = (EditText) findViewById(R.id.etPasswordLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validate()) {

                    return;
                }

                progressDialog.show();

                final String name = etName.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                             editor.putBoolean("Login", true);
                             editor.commit();

                            progressDialog.dismiss();
                             SignupActivity.this.finish();
                            // startActivity(new Intent(SignupActivity.this , MainActivity_conversion.class));
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));



                        }
                            //hdhhhh
                        else {
                            progressDialog.dismiss();
                            AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                            alert .setTitle("Error")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert);
                            if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                //If email already registered.
                                alert.setMessage("The email address is already in use!").show();

                            }else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                //If email is incorrect
                                alert.setMessage("The password is invalid!").show();

                            }else
                            {
                                //OTHER THING
                                alert.setMessage(task.getException().toString()).show();
                            }

                        }

                    }
                });
            }
        });

    }
    public boolean validate() {
        boolean valid = true;
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email is empty or invalid!!");
            valid = false;
        } else {
            etEmail.setError(null);
        }


        if (password.isEmpty() || password.length() < 4 ) {
            etPassword.setError("Enter alphanumeric password!!");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (name.isEmpty() ) {
            etName.setError("Name field is empty!!");
            valid = false;
        } else {
            etName.setError(null);
        }
        return valid;
    }


}
