package com.example.instagram.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.instagram.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
public class UserFirebaseHelper {

    public static FirebaseUser getCurrentUser(){

        FirebaseAuth user = FirebaseConfig.getFirebaseAutenticacao();
        return user.getCurrentUser();

    }

    public static String getUserId(){
        return getCurrentUser().getUid();
    }

    public static void updateUserName(String name){

        try {

            FirebaseUser loggedUser = getCurrentUser();

            //set object to update profile
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName( name )
                    .build();
            loggedUser.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful() ){
                        Log.d("Profile","Error on profile name update" );
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void updateUserPicture(Uri url){

        try {

            FirebaseUser loggedUser = getCurrentUser();

            //set object to update profile
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri( url )
                    .build();
            loggedUser.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful() ){
                        Log.d("Profile","Error on profile image update." );
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static User getLoggedUserData(){

        FirebaseUser firebaseUser = getCurrentUser();

        User user = new User();
        user.setEmail( firebaseUser.getEmail() );
        user.setName( firebaseUser.getDisplayName() );
        user.setId( firebaseUser.getUid() );

        if ( firebaseUser.getPhotoUrl() == null ){
            user.setPicturePath("");
        }else{
            user.setPicturePath( firebaseUser.getPhotoUrl().toString() );
        }

        return user;

    }

}
