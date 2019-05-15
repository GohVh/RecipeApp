package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;

public class DisplayMenu extends AppCompatActivity {
    TextView textViewName, textViewIngredients, textViewMethods;
    ImageView imageViewMenu;
    Button buttonEdit, buttonDelete;
    int menuID;
    private static int BACK_MAIN =2;

    public void displayData(String name, String ingredients, String methods, byte[] image){

        textViewName.setText(name);
        textViewIngredients.setText(ingredients);
        textViewMethods.setText(methods);

        if (image!=null){
            imageViewMenu.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }else {
            imageViewMenu.setEnabled(false);
        }

    }

    public void edit(){

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddMenu.class);
                intent.putExtra("menuID", menuID);
                intent.putExtra("edit menu from display", 5);
                startActivity(intent);
            }
        });
    }

    public void delete(){

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer deletedRows = MainActivity.myDB.deleteData(MainActivity.menuName.get(menuID));

                if (deletedRows >0){

                    Toast.makeText(getApplicationContext(),"Menu deleted", Toast.LENGTH_SHORT).show();
                    MainActivity.menuName.remove(menuID);
                    MainActivity.arrayAdapter.notifyDataSetChanged();

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.recipeapp", Context.MODE_PRIVATE);
                    HashSet<String> set = new HashSet<>(MainActivity.menuName);
                    sharedPreferences.edit().putStringSet("menuName",set).apply();

                    Intent intentMain = new Intent(DisplayMenu.this,MainActivity.class);
                    startActivityForResult(intentMain,BACK_MAIN);

                }else {
                    Toast.makeText(getApplicationContext(),"Menu not deleted", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu);

        textViewName = findViewById(R.id.textViewName);
        textViewIngredients = findViewById(R.id.textViewIngredients);
        textViewMethods = findViewById(R.id.textViewMethods);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        Intent intent = getIntent();
        menuID = intent.getIntExtra("menuID",-1);

        Cursor result = MainActivity.myDB.getData(MainActivity.menuName.get(menuID),"*");

        if (result.getCount() == 0){
            displayData("Error: No data","Error: No data", "Error: No data",null);
            return;
        }

        StringBuffer bufferName = new StringBuffer();
        StringBuffer bufferIngredients = new StringBuffer();
        StringBuffer bufferMethods = new StringBuffer();
        byte[] imageBuffer = new byte[]{};

        while (result.moveToNext()){
            bufferName.append(result.getString(1));
            bufferIngredients.append(result.getString(2));
            bufferMethods.append(result.getString(3));
            imageBuffer = result.getBlob(4);
        }

        displayData(bufferName.toString(),bufferIngredients.toString(),bufferMethods.toString(),imageBuffer);

        edit();
        delete();

    }
}
