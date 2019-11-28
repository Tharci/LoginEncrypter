package com.tharci.loginencrypter;

import android.app.Fragment;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
        if (sharedStuff.fileExists(SharedStuff.FINGERPRINT_FILENAME)) {
            fingerprintSwitch.setChecked(true);
        } else {
            fingerprintSwitch.setChecked(false);
        }

        fingerprintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setUpFingerprintAuth();
                } else {
                    sharedStuff.deleteFile(SharedStuff.FINGERPRINT_FILENAME);
                }
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
                    generateSecretKey(new KeyGenParameterSpec.Builder(
                            "KEY",
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .setUserAuthenticationRequired(true)
                            .setInvalidatedByBiometricEnrollment(false)
                            .build());
                    sharedStuff.savePwHash_Fingerprint(sharedStuff.getSecretKey().hashCode());//result.getCryptoObject().getCipher().getIV().toString());
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

    private void generateSecretKey(KeyGenParameterSpec keyGenParameterSpec) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyGenerator.init(keyGenParameterSpec);
        keyGenerator.generateKey();
    }
}
