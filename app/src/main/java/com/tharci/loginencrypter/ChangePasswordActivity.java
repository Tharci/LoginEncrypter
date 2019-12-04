package com.tharci.loginencrypter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePasswordActivity extends Activity {
    EditText oldPwET;
    EditText newPwET;
    EditText newPwAgainET;
    TextView errorMsgTV;
    Button savePwBtn;

    Handler errorMsgHandler;
    Runnable errorMsgRunnable;

    Context context;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        context = getApplicationContext();

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
                if (DataService.hashPassword(oldPwET.getText().toString()).equals(DataService.passwordHash)) {
                    if (newPwET.getText().toString().equals(newPwAgainET.getText().toString())) {
                        String passwordValidationResult = DataService.validatePassword(newPwET.getText().toString());
                        if (passwordValidationResult.equals("")) {
                            try {
                                String data = DataService.loadData(context);
                                DataService.passwordHash = DataService.hashPassword(newPwET.getText().toString());
                                DataService.saveData(context, data);
                            } catch (Exception e) {
                                showError("Failed to save password.");
                            }

                            if (FingerprintService.isFingerprintAuthSetup(context)) {
                                try {
                                    FingerprintService.createFingerprintAuth(context);
                                } catch (Exception e) {
                                    FingerprintService.deleteFingerprintAuth(context);
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