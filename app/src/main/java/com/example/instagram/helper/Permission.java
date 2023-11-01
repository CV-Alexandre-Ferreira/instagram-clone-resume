package com.example.instagram.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {

    public static boolean validatePermissions(String[] permissions, Activity activity, int requestCode){

        if (Build.VERSION.SDK_INT >= 23 ){

            List<String> permissionsList = new ArrayList<>();

            /* passes on permissions to check if it's already allowed */
            for ( String permission : permissions ){
                Boolean havePermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if ( !havePermission ) permissionsList.add(permission);
            }

            /*if list is empty is not necessary to ask for permissions*/

            if ( permissionsList.isEmpty() ) return true;
            String[] newPermissions = new String[ permissionsList.size() ];
            permissionsList.toArray( newPermissions );

            //asks for permission
            ActivityCompat.requestPermissions(activity, newPermissions, requestCode );


        }

        return true;

    }

}
