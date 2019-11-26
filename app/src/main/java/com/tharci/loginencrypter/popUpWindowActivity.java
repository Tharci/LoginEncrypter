/*package com.tharci.loginencrypter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class popUpWindowActivity extends Activity
{
    Button yesButton;
    Button noButton;
    SharedStuff sharedStuff;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sure_layout);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (370));

        getWindow().setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));


        listLoginFragment act = new listLoginFragment();

        sharedStuff = SharedStuff.getInstance();
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] data = {};
                try {
                    data = sharedStuff.readData().split("\n");
                } catch (Exception e) {
                    sharedStuff.saved = false;
                    finish();
                }

                StringBuilder dataOut = new StringBuilder();
                for (int i=0; i<data.length; i++) {
                    if(i != sharedStuff.deletable) {
                        dataOut.append(data[i]).append("\n");
                    }
                }

                try {
                    sharedStuff.saveData(dataOut.toString());
                } catch (Exception e) {
                    sharedStuff.saved = false;
                    finish();
                }

                sharedStuff.saved = true;
                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedStuff.saved = false;
                finish();
            }
        });
    }
}*/

package com.tharci.loginencrypter;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class popUpWindowActivity extends Activity {
    Button yesBtn;
    Button noBtn;

    SharedStuff sharedStuff;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sure_layout);

        sharedStuff = SharedStuff.getInstance();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;

        getWindow().setLayout((int) (width*0.8), 370);

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