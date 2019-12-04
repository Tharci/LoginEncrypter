package com.tharci.loginencrypter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;
import javax.crypto.KeyGenerator;

public class SettingsFragment extends Fragment {

    View myView;
    SharedStuff sharedStuff;

    Switch fingerprintSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.settings_layout, container, false);

        sharedStuff = SharedStuff.getInstance();

        fingerprintSwitch = myView.findViewById(R.id.fingerprintSwitch);
        if (sharedStuff.isFingerprintAuthSetup()) {
            fingerprintSwitch.setChecked(true);
        } else {
            fingerprintSwitch.setChecked(false);
        }

        fingerprintSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fingerprintSwitch.isChecked()) {
                    setUpFingerprintAuth();
                } else {
                    fingerprintSwitch.setChecked(true);
                    SharedStuff.popUpWindowRunnable = new Runnable() {
                        @Override
                        public void run() {
                            sharedStuff.deleteFingerprintAuth();
                            fingerprintSwitch.setChecked(false);
                        }
                    };
                    startActivity(new Intent(getActivity(), PopUpWindowActivity.class));
                }
            }
        });

        myView.findViewById(R.id.changePwBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        return myView;
    }

    void setUpFingerprintAuth() {
        final BiometricPrompt biometricPrompt;
        final BiometricPrompt.PromptInfo promptInfo;
        final Executor executor = ContextCompat.getMainExecutor(getContext());

        biometricPrompt = new BiometricPrompt((FragmentActivity) getActivity(),
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
                fingerprintSwitch.setChecked(false);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                try {
                    sharedStuff.createFingerprintAuth();
                    fingerprintSwitch.setChecked(true);
                    Toast.makeText(getContext(),
                            "Authentication successfully saved!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    fingerprintSwitch.setChecked(false);
                    Toast.makeText(getContext(),
                            "Failed to save authentication.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
                fingerprintSwitch.setChecked(false);
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
