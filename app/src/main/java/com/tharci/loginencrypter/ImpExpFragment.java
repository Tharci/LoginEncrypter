package com.tharci.loginencrypter;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImpExpFragment extends Fragment
{
    View myView;
    Button impBtn;
    Button expBtn;

    String filePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.layout_imp_exp, container, false);

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DataService.DATA_FILENAME;

        impBtn = myView.findViewById(R.id.btn_import);
        expBtn = myView.findViewById(R.id.btn_export);

        impBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataService.popUpWindowRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!checkPermissionForReadExtertalStorage()) {
                            requestPermissionForReadExtertalStorage();
                        } else {
                            importData();
                        }
                    }
                };
                startActivity(new Intent(getActivity(), PopUpWindowActivity.class));
            }
        });

        expBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    exportData();
                }
            }
        });

        return myView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                case 2:
                    exportData();
                    break;
                case 3:
                    importData();
                    break;
            }
        }
    }

    void exportData() {
        try
        {
            FileInputStream fis = getActivity().openFileInput(DataService.DATA_FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.flush();
            fos.close();

            Toast.makeText(getActivity().getBaseContext(), "Successfully exported.",Toast.LENGTH_SHORT).show();
        } catch (Exception e)
        {
            Toast.makeText(getActivity().getBaseContext(), "Failed to export.",Toast.LENGTH_SHORT).show();
        }
    }

    void importData() {
        String text = "";
        try {
            StringBuilder sb = new StringBuilder();
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + DataService.DATA_FILENAME);
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            int size = (int) file.length();
            byte[] data = new byte[size];

            bis.read(data, 0, data.length);
            bis.close();
            fis.close();

            try {
                FileOutputStream fos = getContext().openFileOutput(DataService.DATA_FILENAME, Context.MODE_PRIVATE);
                fos.write(data);
                fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Toast.makeText(getContext(), "Failed to import", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Successfully imported.",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), "Failed to import", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() {
        try {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
