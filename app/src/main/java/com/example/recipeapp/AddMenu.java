package com.example.recipeapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

public class AddMenu extends AppCompatActivity {
    EditText editTextName, editTextIngredients, editTextMethods;
    Button addMenuButton, updateMenuButton;
    int menuID, addbuttonStatus, editButtonStatus;
    private static int ADD = 1;
    private static int UPDATE = 3;
    private static int BACK_MAIN =2;

    String name,ingredients,methods;
    static byte[] byteArray = {};

    public void getText(){
        name = editTextName.getText().toString();
        ingredients = editTextIngredients.getText().toString();
        methods = editTextMethods.getText().toString();
    }

    public void sharedReferences(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.recipeapp", Context.MODE_PRIVATE);
        HashSet<String> set = new HashSet<>(MainActivity.menuName);
        sharedPreferences.edit().putStringSet("menuName",set).apply();
    }

    public void intentBack(){
        Intent intentBACK = new Intent(AddMenu.this,MainActivity.class);
        startActivityForResult(intentBACK,BACK_MAIN);
        AddMenu.this.finish();
    }

    public void addMenu(){

        addMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getText();

                MainActivity.menuName.set(menuID,name);
                MainActivity.arrayAdapter.notifyDataSetChanged();

                Intent intentGallery = new Intent();
                intentGallery.setType("image/*");
                intentGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentGallery,"Select image"),ADD);
            }
        });

    }

    public void updateMenu(){

        updateMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getText();
                MainActivity.myDB.updateData(name,ingredients,methods );
                sharedReferences();

                AlertDialog.Builder builder = new AlertDialog.Builder(AddMenu.this);
                builder.setTitle("Upload new image?");
                builder.setMessage("Do you want to replace this image?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intentGallery2 = new Intent();
                        intentGallery2.setType("image/*");
                        intentGallery2.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intentGallery2,"Select image"),UPDATE);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intentBack();
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        editTextName = findViewById(R.id.editTextName);
        editTextIngredients = findViewById(R.id.editTextIngredients);
        editTextMethods = findViewById(R.id.editTextMethods);
        addMenuButton = findViewById(R.id.addMenuButton);
        updateMenuButton = findViewById(R.id.updateMenuButton);

        Intent intent = getIntent();
        menuID = intent.getIntExtra("menuID",-1);
        addbuttonStatus = intent.getIntExtra("add menu from main",-1);
        editButtonStatus = intent.getIntExtra("edit menu from display",-1);

        if (addbuttonStatus == 4 && editButtonStatus !=5){
            addMenuButton.setEnabled(true);
            updateMenuButton.setEnabled(false);
        }else if (addbuttonStatus !=4 && editButtonStatus ==5){
            addMenuButton.setEnabled(false);
            updateMenuButton.setEnabled(true);
            editTextName.setEnabled(false);
        }

        if (menuID != -1){

            Cursor result = MainActivity.myDB.getData(MainActivity.menuName.get(menuID),"*");

            StringBuffer bufferIngredients = new StringBuffer();
            StringBuffer bufferMethods = new StringBuffer();

            while (result.moveToNext()){
                bufferIngredients.append(result.getString(2));
                bufferMethods.append(result.getString(3));
            }

            editTextName.setText(MainActivity.menuName.get(menuID));
            editTextIngredients.setText(bufferIngredients.toString());
            editTextMethods.setText(bufferMethods.toString());

        }else {
            MainActivity.menuName.add("");
            menuID = MainActivity.menuName.size()-1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }

        addMenu();
        updateMenu();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD){

            if (resultCode == Activity.RESULT_OK){
                Uri uri = data.getData();
                try{

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);

                    byteArray=baos.toByteArray();
                    MainActivity.myDB.insertData(name,ingredients,methods,byteArray);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        if(requestCode == UPDATE){

            if (resultCode == Activity.RESULT_OK){
                Uri uri2 = data.getData();
                try{

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri2);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);

                    byteArray=baos.toByteArray();

                    MainActivity.myDB.updateImage(name,byteArray);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        sharedReferences();
        intentBack();
    }
}
