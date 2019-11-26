package com.tharci.loginencrypter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    Button logInBtn;
    EditText passwordET;
    TextView errorMsgTV;

    SharedStuff sharedStuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logInBtn = findViewById(R.id.logInBtn);
        passwordET = findViewById(R.id.passwordLoginET);
        errorMsgTV = findViewById(R.id.errorMsgTV);

        sharedStuff = SharedStuff.getInstance();
        sharedStuff.context = this;


        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

            //DELETES data.dat
            /*File dir = getFilesDir();
            File file = new File(dir, "data.dat");
            file.delete();*/

        //Checks if data.dat exists
        try
        {
            InputStream inputStream = openFileInput(sharedStuff.DATAPATH);
            inputStream.close();
        } catch (Exception e)
        {
            Intent intent = new Intent(this, CreatePassword.class);
            startActivity(intent);
            finish();
        }
    }


    void logIn()
    {
        sharedStuff.passwordHash = sharedStuff.hash(passwordET.getText().toString());

        if (sharedStuff.authenticate())
        {
            passwordET.setText("");
            errorMsgTV.setText("");
            Intent intent = new Intent(this, NavigationMainActivity.class);
            startActivity(intent);
        } else {
            errorMsgTV.setText("I'm sorry. You've got the wrong Password.");
        }
    }
}
