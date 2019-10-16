package com.tharci.loginencrypter;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.NoSuchFileException;

public class newLoginFragment extends Fragment {

    View myView;
    Button addLoginButton;

    String[] abc;
    Integer abcLen;

    SharedStuff sharedStuff;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.new_login, container, false);

        sharedStuff = SharedStuff.getInstance();
        abc = sharedStuff.abc;
        abcLen = sharedStuff.abcLen;

        addLoginButton = myView.findViewById(R.id.addLoginButton);
        addLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLogin();

                //// IDK WHY THIS CODE WAS HERE..
                /*InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);*/
            }
        });


        return myView;
    }

    public Integer charToNum(Character c)
    {
        Integer num = -1;
        for (Integer i=0; i < abc.length; i++)
        {
            if (c == abc[i].toCharArray()[0])
            {
                num = i;
                break;
            }
        }

        return num;
    }

    public String numToChar(Integer in)
    {
        return abc[in];
    }

    public void addLogin()
    {
        EditText platformET = getActivity().findViewById(R.id.platformET);
        EditText usernameET = getActivity().findViewById(R.id.usernameET);
        EditText emailET = getActivity().findViewById(R.id.emailET);
        EditText passwordET = getActivity().findViewById(R.id.passwordET);
        EditText addInfoET = getActivity().findViewById(R.id.addInfoET);

        String platform = platformET.getText().toString();
        String username = usernameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String addInfo = addInfoET.getText().toString();

        addInfoET.setSingleLine();

        if ( !(platform.trim().isEmpty()) ) {
            Integer pointer = 0;
            Integer pointerMax = 64;

            String enPlatform = "";
            for (Integer i = 0; i < platform.length(); i++) {
                enPlatform += encryptChar(platform.charAt(i), pointer);
                pointer++;
                if (pointer == pointerMax) {
                    pointer = 0;
                }
            }
            //platformET.setText(enPlatform);
            //usernameET.setText(dePlatform);

            String enUsername = "";
            for (Integer i = 0; i < username.length(); i++) {
                enUsername += encryptChar(username.charAt(i), pointer);
                pointer++;
                if (pointer == pointerMax) {
                    pointer = 0;
                }
            }

            String enEmail = "";
            for (Integer i = 0; i < email.length(); i++) {
                enEmail += encryptChar(email.charAt(i), pointer);
                pointer++;
                if (pointer == pointerMax) {
                    pointer = 0;
                }
            }

            String enPassword = "";
            for (Integer i = 0; i < password.length(); i++) {
                enPassword += encryptChar(password.charAt(i), pointer);
                pointer++;
                if (pointer == pointerMax) {
                    pointer = 0;
                }
            }

            String enAddInfo = "";
            for (Integer i = 0; i < addInfo.length(); i++) {
                enAddInfo += encryptChar(addInfo.charAt(i), pointer);
                pointer++;
                if (pointer == pointerMax) {
                    pointer = 0;
                }
            }

            writeToFile(enPlatform + "÷÷" + enUsername + "÷÷" + enEmail + "÷÷" + enPassword + "÷÷" + enAddInfo + "÷÷\n");

            platformET.setText("");
            usernameET.setText("");
            emailET.setText("");
            passwordET.setText("");
            addInfoET.setText("");


            Toast.makeText(getActivity().getBaseContext(), "Data has been saved.", Toast.LENGTH_SHORT).show();
            /*Snackbar.make(myView, "Data has been saved.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
        } else {
            Toast.makeText(getActivity().getBaseContext(), "You cant leave field platform empty!", Toast.LENGTH_SHORT).show();
        }
    }

    public String encryptChar(Character c, Integer pointer)
    {
        if (charToNum(c) >= 0)
        {
            return numToChar( ( charToNum(c) + sharedStuff.pin * charToNum(sharedStuff.pinHash.charAt(pointer))) % abcLen );
        } else
        {
            return "ß";
        }
    }

    public String decryptChar(Character c, Integer pointer)
    {
        if (c == 'ß')
        {
            return "?";
        } else
        {
            Integer deInt = (charToNum(c) - sharedStuff.pin * charToNum(sharedStuff.pinHash.charAt(pointer))) % abcLen;
            if (deInt < 0) {deInt += abcLen;}
            return numToChar(deInt);
        }
    }

    public void writeToFile (String string) {

        BufferedWriter bw = null;
        //EditText platformET = getActivity().findViewById(R.id.platformET);

        try {
            //platformET.setText("0");

            //// DELETES FILE
            /*File dir = getActivity().getFilesDir();
            File file = new File(dir, "data.txt");
            file.delete();*/

            try
            {
                //// CHECK IF DATA.TXT EXISTS
                InputStream inputStream = getActivity().openFileInput("data.txt");
            } catch(Exception e)
            {
                //// CREATE DATA.TXT
                //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput("data.txt", Context.MODE_PRIVATE));
                //outputStreamWriter.close();
            }

            //// APPEND MODE SET HERE
            /*bw = new BufferedWriter(new FileWriter("data.txt", true));
            platformET.setText("3");
            bw.write(s);
            bw.flush();*/
            FileOutputStream fos = getActivity().openFileOutput("data.txt", Context.MODE_APPEND);
            fos.write(string.getBytes());
            fos.close();
            //platformET.setText("DONE");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            //platformET.setText("Dck");
        } finally {
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) { }
        }

    }
}
