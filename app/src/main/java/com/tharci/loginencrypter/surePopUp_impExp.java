package com.tharci.loginencrypter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class surePopUp_impExp extends Activity
{

    Button yesButton;
    Button noButton;
    TextView sureTW;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sure_layout);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        getWindow().setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));


        listLoginFragment act = new listLoginFragment();

        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        sureTW = findViewById(R.id.sureTW);
        sureTW.setText("Are you sure you want to import?\nCurrend data will be removed.");
        sureTW.setTextSize(16);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "";

                try {
                    /*FileInputStream fis = openFileInput(Environment.getExternalStorageDirectory().getPath() + "/data.txt");
                    int size = fis.available();
                    byte[] buffer = new byte[size];
                    fis.read(buffer);
                    fis.close();
                    text = new String(buffer);
                    //yesButton.setText(text);*/
                    StringBuilder sb = new StringBuilder();
                    File fileIn = new File(Environment.getExternalStorageDirectory().getPath() + "/data.txt");
                    FileInputStream fis = new FileInputStream(fileIn);

                    if(fis!=null)
                    {
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader br = new BufferedReader(isr);

                        String line = null;
                        Boolean firstLine = true;
                        while((line = br.readLine()) != null)
                        {
                            if(!firstLine){sb.append("\n");}
                            firstLine = false;
                            sb.append(line);
                        }
                        fis.close();
                        text = sb.toString();
                    }

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Failed to import", Toast.LENGTH_SHORT).show();
                }

                try
                {
                    writeToFile(text);
                    Toast.makeText(getBaseContext(), "Successfully imported.",Toast.LENGTH_SHORT).show();
                } catch (Exception e)
                {
                    Toast.makeText(getBaseContext(), "Failed to import.",Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
    }



    public void writeToFile (String string) {

        BufferedWriter bw = null;

        try {

            /*//// DELETES FILE
            File dir = getFilesDir();
            File file = new File(dir, "data.txt");
            file.delete();

            try
            {
                InputStream inputStream = openFileInput("data.txt");
            } catch(Exception e){
            }*/

            FileOutputStream fos = openFileOutput("data.txt", Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) { }
        }
    }


    //Environment.getExternalStorageDirectory().getPath() + "/LoginEncrypter/data.txt"
}
