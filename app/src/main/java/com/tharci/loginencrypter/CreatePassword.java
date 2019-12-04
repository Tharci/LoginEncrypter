package com.tharci.loginencrypter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CreatePassword extends AppCompatActivity {

    TextView errorMsgTV;
    Button saveBtn;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        errorMsgTV = findViewById(R.id.errorTV);
        saveBtn = findViewById(R.id.saveBtn);
        passwordET = findViewById(R.id.passwordCreateET);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotItButton_onClick();
            }
        });
    }

    public void gotItButton_onClick()
    {
        String passwordValidationResult = DataService.validatePassword(passwordET.getText().toString());
        if(!passwordValidationResult.equals(""))
        {
            errorMsgTV.setText(passwordValidationResult);
        }
        else
        {
            try
            {
                DataService.passwordHash = DataService.hashPassword(passwordET.getText().toString());

                DataService.saveData(this, "");

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();

            } catch (Exception e) {
                errorMsgTV.setText("Failed to save password.");}
        }

    }
}
