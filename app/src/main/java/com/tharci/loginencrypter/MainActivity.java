package com.tharci.loginencrypter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    Button hereUGoButton;
    EditText pinEditText;
    TextView errorMessage;
    //TextView textView6;
    String output;
    String pinHash;

    SharedStuff sharedStuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TextView textView = findViewById(R.id.textView);
        hereUGoButton = findViewById(R.id.HereUGoButton);
        pinEditText = findViewById(R.id.pinEditText);
        errorMessage = findViewById(R.id.errorMessage);
        //textView6 = findViewById(R.id.textView6);

        sharedStuff = SharedStuff.getInstance();


        hereUGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hereUGo_onClick();
            }
        });

            //DELETES PW.TXT
            /*File dir = getFilesDir();
            File file = new File(dir, "pw.txt");
            file.delete();*/

        //Check if pw.txt exists
        try
        {
            InputStream inputStream = this.openFileInput("pw.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null )
                {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                output = stringBuilder.toString();
                pinHash = output.split("-")[0];
                //errorMessage.setText(pinHash);
                //textView.setText(output);
            }


        } catch (Exception e)
        {
            Intent intent = new Intent(this, CreatePassword.class);
            startActivity(intent);
            finish();
        }



                                                            /*try
                                                            {
                                                                String pinHash = sharedStuff.hash("123");
                                                                sharedStuff.pinHash = pinHash;

                                                                //errorMessage.setText("1");

                                                                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("pw.txt", Context.MODE_PRIVATE));
                                                                outputStreamWriter.write(pinHash + "-" + "tmarcellt.2000@gmail.com");
                                                                outputStreamWriter.close();

                                                            } catch (Exception e) {errorMessage.setText("asd :c");}*/


    }


    void hereUGo_onClick()
    {

        try
        {

            if (sharedStuff.hash(pinEditText.getText().toString()).equals(pinHash))
            {
                SharedStuff sharedStuff = SharedStuff.getInstance();
                sharedStuff.pin = Integer.parseInt(pinEditText.getText().toString());
                sharedStuff.pinHash = pinHash;

                pinEditText.setText("");
                errorMessage.setText("");

                Intent intent = new Intent(this, NavigationMainActivity.class);
                startActivity(intent);
            } else
            {
                errorMessage.setText("Sorry, bruh. You've got the wring PIN.");
            }
        } catch (NoSuchAlgorithmException e) {}
    }
}
