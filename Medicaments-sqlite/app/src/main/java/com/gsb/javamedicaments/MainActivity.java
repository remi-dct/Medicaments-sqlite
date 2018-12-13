package com.gsb.javamedicaments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION_EXTERNAL_CARD = 1;
    private static String DB_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator + "com.gsb.javamedicaments";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_EXTERNAL_CARD);
            } else {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_EXTERNAL_CARD);
            }
        } else {
            Toast.makeText(MainActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.activity_main);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_EXTERNAL_CARD:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission autorisée par l'utilisateur", Toast.LENGTH_SHORT).show();

                    String state = Environment.getExternalStorageState();
                    if(Environment.MEDIA_MOUNTED.equals(state)){
                        // Both Read and write operations available
                        Log.i("MainActivity", "Il y a bien une carte externe");

                        //je crée le répertoire pour placer la base de données sur la carte externe
                        File appDir = new File(DB_PATH);
                        if(!appDir.exists() && !appDir.isDirectory()){
                            if (appDir.mkdirs()){
                                Log.i("MainActivity","Répertoire Downloads/com.sqlite.gsb.testsqlite créé sur la carte externe");
                            }
                            else{
                                Log.w("MainActivity","Impossible de créer le répertoire Downloads/com.sqlite.gsb.testsqlite sur la carte externe");
                            }
                        }else{
                            Log.i("MainActivity","Le répertoire Downloads/com.sqlite.gsb.testsqlite existe déjà sur la carte externe");
                        }
                    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
                        // Only Read operation available
                        Toast.makeText(MainActivity.this, "Il y a bien une carte externe mais elle est en lecture seule\"", Toast.LENGTH_SHORT).show();
                    } else {
                        // SD card not mounted
                        Toast.makeText(MainActivity.this, "Il n'y a pas de carte externe", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Autorisation refusée par l'utilisateur", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }


}
