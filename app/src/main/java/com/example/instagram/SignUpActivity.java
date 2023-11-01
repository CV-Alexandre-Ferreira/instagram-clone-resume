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
import com.example.instagram.helper.UserFirebaseHelper;
import com.example.instagram.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameField, emailField, passwordField;
    private Button buttonSignUp;
    private ProgressBar progressBar;
    private User user;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeComponents();

        //register user
        progressBar.setVisibility(View.GONE);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textName  = nameField.getText().toString();
                String textEmail = emailField.getText().toString();
                String textPassword = passwordField.getText().toString();

                if( !textName.isEmpty() ){
                    if( !textEmail.isEmpty() ){
                        if( !textPassword.isEmpty() ){

                            user = new User();
                            user.setName( textName );
                            user.setEmail( textEmail );
                            user.setPassword( textPassword );
                            register(user);

                        }else{
                            Toast.makeText(SignUpActivity.this,
                                    R.string.fill_in_password,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(SignUpActivity.this,
                                R.string.fill_in_email,
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SignUpActivity.this,
                            R.string.fill_in_name,
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public void register(final User user){

        progressBar.setVisibility(View.VISIBLE);
        auth = FirebaseConfig.getFirebaseAutenticacao();
        auth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if( task.isSuccessful() ){

                            try {

                                progressBar.setVisibility(View.GONE);

                                //save data on firebase
                                String userId = task.getResult().getUser().getUid();
                                user.setId( userId );
                                user.save();

                                //save data on firebase profile
                                UserFirebaseHelper.updateUserName( user.getName() );

                                Toast.makeText(SignUpActivity.this,
                                        R.string.register_success,
                                        Toast.LENGTH_SHORT).show();

                                startActivity( new Intent(getApplicationContext(), MainActivity.class));
                                finish();

                            }catch (Exception e){
                                e.printStackTrace();
                            }



                        }else {

                            progressBar.setVisibility( View.GONE );

                            String exceptionError = "";
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                exceptionError = getResources().getString(R.string.weak_password);
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                exceptionError = getResources().getString(R.string.invalid_email);
                            }catch (FirebaseAuthUserCollisionException e){
                                exceptionError = getResources().getString(R.string.already_registered);
                            } catch (Exception e) {
                                exceptionError = getResources().getString(R.string.on_registering_user)  + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(SignUpActivity.this,
                                    R.string.error + exceptionError ,
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                }
        );

    }

    public void initializeComponents(){

        nameField = findViewById(R.id.editCadastroNome);
        emailField = findViewById(R.id.editCadastroEmail);
        passwordField = findViewById(R.id.editCadastroSenha);
        buttonSignUp = findViewById(R.id.buttonEntrar);
        progressBar     = findViewById(R.id.progressCadastro);

        nameField.requestFocus();

    }

}
