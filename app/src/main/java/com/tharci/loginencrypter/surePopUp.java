package com.tharci.loginencrypter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class surePopUp extends Activity
{
    Button yesButton;
    Button noButton;
    SharedStuff sharedStuff;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sure_layout);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (370));

        getWindow().setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));


        listLoginFragment act = new listLoginFragment();

        sharedStuff = SharedStuff.getInstance();
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteLogin(sharedStuff.deletable);

                String[] data = readFile().split("\n");
                FileOutputStream fos = null;

                try {
                    FileOutputStream fosX = openFileOutput("data.txt", Context.MODE_PRIVATE);
                    fosX.close();

                    fos = openFileOutput("data.txt", Context.MODE_APPEND);
                } catch (Exception e) { e.printStackTrace(); }

                for (Integer i=0; i<data.length; i++)
                {
                    if(i != sharedStuff.deletable)
                    {
                        try { fos.write((data[i]+"\n").getBytes()); } catch (IOException e) {e.printStackTrace();}
                    }
                }
                try {fos.close();} catch (IOException e) {e.printStackTrace();}

                sharedStuff.saved = true;

                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedStuff.saved = false;
                finish();
            }
        });
    }

    private String readFile()
    {
        String s = "";
        try {
            FileInputStream fileIn = openFileInput("data.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer= new char[10000];
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                String readstring = String.copyValueOf(inputBuffer,0,charRead);
                s += readstring;
            }
            InputRead.close();
            //Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }

}