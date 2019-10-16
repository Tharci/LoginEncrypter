package com.tharci.loginencrypter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class impExpFragment extends Fragment
{
    View myView;
    Button impBtn;
    Button expBtn;

    String filePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.layout_imp_exp, container, false);

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.txt";

        impBtn = myView.findViewById(R.id.btn_import);
        expBtn = myView.findViewById(R.id.btn_export);

        impBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //impBtn.setText(Environment.getExternalStorageDirectory().getPath());
                startActivity(new Intent(getActivity(), surePopUp_impExp.class));
            }
        });

        expBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = "";

                try
                {
                    FileInputStream fis = getActivity().openFileInput("data.txt");
                    int size = fis.available();
                    byte[] buffer = new byte[size];
                    fis.read(buffer);
                    fis.close();
                    text = new String(buffer);
                } catch (Exception e)
                {
                    Toast.makeText(getActivity().getBaseContext(), "Failed to export. 01",Toast.LENGTH_SHORT).show();
                }

                try
                {
                    writeToFile(text);
                    Toast.makeText(getActivity().getBaseContext(), "Successfully exported.",Toast.LENGTH_SHORT).show();
                } catch (Exception e)
                {
                    Toast.makeText(getActivity().getBaseContext(), "Failed to export. 02",Toast.LENGTH_SHORT).show();
                }
            }
        });



        return myView;
    }


    public void writeToFile (String string) {

        File file = new File(filePath);

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(string);
            fw.flush();
            fw.close();
        } catch (Exception e){}

        //BufferedWriter bw = null;

        //try {

            //// DELETES FILE
            /*File dir = getActivity().getFilesDir();
            File file = new File(dir, "data.txt");
            file.delete();*/

            /*try
            {
                expBtn.setText("asdasd");
                InputStream inputStream = getActivity().openFileInput(filePath);
            } catch(Exception e){
            }*/

            /*FileOutputStream fos = getActivity().openFileOutput(filePath, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) { }
        }*/

    }
}
