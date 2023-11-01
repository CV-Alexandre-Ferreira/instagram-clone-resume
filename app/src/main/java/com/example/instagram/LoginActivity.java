package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.helper.FirebaseConfig;
import com.example.instagram.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button buttonEnter;
    private ProgressBar progressBar;

    private User user;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verifyLoggedUser();
        initializeComponents();

        //do user login
        progressBar.setVisibility( View.GONE );
        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textEmail = emailField.getText().toString();
                String textPassword = passwordField.getText().toString();

                if( !textEmail.isEmpty() ){
                    if( !textPassword.isEmpty() ){

                        user = new User();
                        user.setEmail( textEmail );
                        user.setPassword( textPassword );
                        validateLogin(user);

                    }else{
                        Toast.makeText(LoginActivity.this,
                                R.string.fill_in_password,
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,
                            R.string.fill_in_email,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void verifyLoggedUser(){
        auth = FirebaseConfig.getFirebaseAutenticacao();
        if( auth.getCurrentUser() != null ){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void validateLogin(User user ){

        progressBar.setVisibility( View.VISIBLE );
        auth = FirebaseConfig.getFirebaseAutenticacao();

        auth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ){
                    progressBar.setVisibility( View.GONE );
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this,
                            R.string.login_error,
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility( View.GONE );
                }

            }
        });


    }

    public void openSignUp(View view){
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity( i );
    }

    public void initializeComponents(){

        emailField = findViewById(R.id.editLoginEmail);
        passwordField = findViewById(R.id.editLoginSenha);
        buttonEnter = findViewById(R.id.buttonEntrar);
        progressBar  = findViewById(R.id.progressLogin);

        emailField.requestFocus();


    }

}
