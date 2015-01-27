package com.example.marko.converter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.Menu;
import android.view.MenuItem;



public class MyActivity extends Activity {

    double inches,centimeters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //Code

        //Centimeters TextField
        final EditText editText = (EditText) findViewById(R.id.editText);
        //as placeholder before User Input
        editText.setText("0");

        //Inches TextField
        final EditText editText1 = (EditText) findViewById(R.id.editText2);
        //placeholder
        editText1.setText("0");

        //Button
        Button button = (Button) findViewById(R.id.button);
        Button button1 = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    centimeters = Double.valueOf(editText.getText().toString());
                    inches = centimeters * 0.393700787;
                    editText1.setText(String.valueOf(inches));

            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inches = Double.valueOf(editText1.getText().toString());
                centimeters = inches * 2.56;
                editText.setText(String.valueOf(centimeters));
            }
        });


    }



}
