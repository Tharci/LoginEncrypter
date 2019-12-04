package com.tharci.loginencrypter;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PopUpWindowActivity extends Activity {
    Button yesBtn;
    Button noBtn;

    SharedStuff sharedStuff;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_layout);

        sharedStuff = SharedStuff.getInstance();

        getWindow().setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));


        yesBtn = findViewById(R.id.yesButton);
        noBtn = findViewById(R.id.noButton);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedStuff.popUopDOIT();
                finish();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}