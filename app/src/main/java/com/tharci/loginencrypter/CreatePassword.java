package com.tharci.loginencrypter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreatePassword extends AppCompatActivity {

    TextView errorMsgTV;
    Button saveBtn;
    EditText passwordET;

    SharedStuff sharedStuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        errorMsgTV = findViewById(R.id.errorTV);
        saveBtn = findViewById(R.id.saveBtn);
        passwordET = findViewById(R.id.passwordCreateET);

        sharedStuff = SharedStuff.getInstance();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotItButton_onClick();
            }
        });
    }

    public void gotItButton_onClick()
    {
        if(passwordET.getText().toString().length() < 6)
        {
            errorMsgTV.setText("The minimum password length is 6.");
        }
        else
        {
            try
            {
                sharedStuff.passwordHash = sharedStuff.hash(passwordET.getText().toString());

                sharedStuff.saveData("");

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();

            } catch (Exception e) {
                errorMsgTV.setText("Failed to save password.");}
        }

    }
}
