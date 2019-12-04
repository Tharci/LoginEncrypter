package com.tharci.loginencrypter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    Button logInBtn;
    EditText passwordET;
    TextView errorMsgTV;

    Handler errorMsgHandler;
    Runnable errorMsgRunnable;

    ImageView fingerprintBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login Encrypter");

        logInBtn = findViewById(R.id.logInBtn);
        passwordET = findViewById(R.id.passwordLoginET);
        errorMsgTV = findViewById(R.id.errorMsgTV);

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataService.passwordHash = DataService.hashPassword(passwordET.getText().toString());
                logIn();
            }
        });


        errorMsgHandler = new Handler();
        errorMsgRunnable = new Runnable() {
            @Override
            public void run() {
                errorMsgTV.setText("");
            }
        };


        if (!DataService.fileExists(this, DataService.DATA_FILENAME)) {
            Intent intent = new Intent(this, CreatePassword.class);
            startActivity(intent);
            finish();
        }


        /* Fingerprint Authentication */
        final BiometricPrompt biometricPrompt;
        final BiometricPrompt.PromptInfo promptInfo;
        final Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                try {
                    FingerprintService.loadPwHash_Fingerprint(getApplicationContext());
                    logIn();
                    Toast.makeText(getApplicationContext(),
                            "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Failed to load authentication data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(),
                        "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });


        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Cancel")
                .build();


        fingerprintBtn = findViewById(R.id.fingerprintBtn);
        fingerprintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FingerprintService.isFingerprintAuthSetup(getApplicationContext())) {
                    biometricPrompt.authenticate(promptInfo);
                } else {
                    Toast.makeText(getApplicationContext(), "Fingerprint authentication is not set up.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void logIn()
    {
        if (DataService.authenticate(this))
        {
            passwordET.setText("");
            errorMsgTV.setText("");
            Intent intent = new Intent(MainActivity.this, NavigationMainActivity.class);
            startActivity(intent);
        } else {
            showError("Wrong Password.");
        }
    }

    void showError(String errorMsg) {
        errorMsgTV.setText(errorMsg);
        errorMsgHandler.removeCallbacks(errorMsgRunnable);
        errorMsgHandler.postDelayed(errorMsgRunnable, 1500);
    }
}
