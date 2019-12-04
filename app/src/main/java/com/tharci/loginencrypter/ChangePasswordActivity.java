package com.tharci.loginencrypter;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePasswordActivity extends Activity {

    SharedStuff sharedStuff;

    EditText oldPwET;
    EditText newPwET;
    EditText newPwAgainET;
    TextView errorMsgTV;
    Button savePwBtn;

    Handler errorMsgHandler;
    Runnable errorMsgRunnable;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        sharedStuff = SharedStuff.getInstance();

        getWindow().setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));

        oldPwET = findViewById(R.id.oldPwET);
        newPwET = findViewById(R.id.newPwET);
        newPwAgainET = findViewById(R.id.newPwAgainET);
        errorMsgTV = findViewById(R.id.errorMsgTV);
        savePwBtn = findViewById(R.id.savePwBtn);

        errorMsgHandler = new Handler();
        errorMsgRunnable = new Runnable() {
            @Override
            public void run() {
                errorMsgTV.setText("");
            }
        };

        savePwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedStuff.hashPassword(oldPwET.getText().toString()).equals(sharedStuff.passwordHash)) {
                    if (newPwET.getText().toString().equals(newPwAgainET.getText().toString())) {
                        String passwordValidationResult = sharedStuff.validatePassword(newPwET.getText().toString());
                        if (passwordValidationResult.equals("")) {
                            try {
                                String data = sharedStuff.loadData();
                                sharedStuff.passwordHash = sharedStuff.hashPassword(newPwET.getText().toString());
                                sharedStuff.saveData(data);
                            } catch (Exception e) {
                                showError("Failed to save password.");
                            }

                            if (sharedStuff.isFingerprintAuthSetup()) {
                                try {
                                    sharedStuff.createFingerprintAuth();
                                } catch (Exception e) {
                                    sharedStuff.deleteFingerprintAuth();
                                    showError("Failed to save fingerprint.");
                                }
                            }
                        } else {
                            showError(passwordValidationResult);
                        }
                    } else {
                        showError("New Passwords don't match.");
                    }
                } else {
                    showError("Old Password is incorrect.");
                }
            }
        });
    }

    void showError(String errorMsg) {
        errorMsgTV.setText(errorMsg);
        errorMsgHandler.removeCallbacks(errorMsgRunnable);
        errorMsgHandler.postDelayed(errorMsgRunnable, 2000);
    }
}