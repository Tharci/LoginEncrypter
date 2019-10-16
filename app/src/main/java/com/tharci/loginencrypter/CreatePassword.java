package com.tharci.loginencrypter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreatePassword extends AppCompatActivity {

    TextView errorMessage;
    Button gotItButton;
    EditText pinEditText;
    EditText emailEditText;

    SharedStuff sharedStuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        errorMessage = findViewById(R.id.errorTextView);
        gotItButton = findViewById(R.id.gotItButton);
        pinEditText = findViewById(R.id.pinEditText);
        emailEditText = findViewById(R.id.emailEditText);

        sharedStuff = SharedStuff.getInstance();

        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotItButton_onClick();
            }
        });
    }

    public void gotItButton_onClick()
    {
        Integer pin = null;
        String email = null;

        try
        {
            pin = Integer.parseInt(pinEditText.getText().toString());
            email = emailEditText.getText().toString();
            //errorMessage.setText(email);

        } catch (Exception e)
        {
            errorMessage.setText("Oh god... You only had to fill in two fields.\nTry again!");
        }

        if((pin == null)  || (email.length() < 1))
        {
            errorMessage.setText("Oh god... You only had to fill in two fields.\nTry again!");
        }
        else
        {
            try
            {
                String pinHash = sharedStuff.hash(pin.toString());

                //errorMessage.setText("1");

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("pw.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(pinHash + "-" + email);
                outputStreamWriter.close();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();

            } catch (Exception e) {errorMessage.setText("asd :c");}


        }

    }

    /*String hash(Integer integer) throws NoSuchAlgorithmException
    {
        String string = integer.toString();
        String hash;

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(string.getBytes());

        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b: digest)
        {
            sb.append(String.format("%02x", b & 0xff));
        }

        hash = sb.toString();
        //errorMessage.setText(hash);
        return hash;
    }*/

}
