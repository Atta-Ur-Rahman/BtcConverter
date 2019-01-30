package com.techease.btcConverter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private EditText loginEmail, loginPassword;
    private Button btnLogin;
    private TextView register;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    boolean isLogin;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = (EditText) findViewById(R.id.etEmailLogin);
        loginPassword = (EditText) findViewById(R.id.etPasswordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        register = (TextView) findViewById(R.id.tvRegister);

//        FirebaseApp.initializeApp(this);
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        user = firebaseAuth.getCurrentUser();

        sharedPreferences = getSharedPreferences("Reg", 0);
        editor = sharedPreferences.edit();
        isLogin = sharedPreferences.getBoolean("Login", false);


        if (isLogin) {


            startActivity(new Intent(LoginActivity.this, MainActivity_conversion.class));

            LoginActivity.this.finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validate()) {

                    return;
                }

                progressDialog.show();
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this,
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                editor.putBoolean("Login", true);
                                                editor.commit();
                                                LoginActivity.this.finish();
                                                startActivity(new Intent(LoginActivity.this, MainActivity_conversion.class));
                                                progressDialog.dismiss();

                                            } else {
                                                progressDialog.dismiss();
                                                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                                                alert.setTitle("Error")
                                                        .setPositiveButton(android.R.string.ok, null)
                                                        .setIcon(android.R.drawable.ic_dialog_alert);
                                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                                    //If email already registered.
                                                    alert.setMessage("No user found!").show();

                                                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                                    //If email is incorrect
                                                    alert.setMessage("The password is invalid!").show();

                                                } else {
                                                    //OTHER THING
                                                    alert.setMessage(task.getException().toString()).show();
                                                }

                                            }
                                        }
                                    });


                } else {
                    loginEmail.setError("Invalid Email");
                }
            }
        });

    }

    public boolean validate() {
        boolean valid = true;

        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Email is empty or invalid!!");
            valid = false;
        } else {
            loginEmail.setError(null);
        }


        if (password.isEmpty() || password.length() < 4) {
            loginPassword.setError("Invalid password!!");
            valid = false;
        } else {
            loginPassword.setError(null);
        }

        return valid;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }


}
