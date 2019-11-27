package com.tharci.loginencrypter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NewLoginFragment extends Fragment {

    View myView;
    Button addLoginButton;

    SharedStuff sharedStuff;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.new_login, container, false);

        sharedStuff = SharedStuff.getInstance();

        addLoginButton = myView.findViewById(R.id.addLoginButton);
        addLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLogin();
            }
        });


        return myView;
    }

    public void addLogin()
    {
        EditText platformET = getActivity().findViewById(R.id.platformET);
        EditText usernameET = getActivity().findViewById(R.id.usernameET);
        EditText emailET = getActivity().findViewById(R.id.emailET);
        EditText passwordET = getActivity().findViewById(R.id.passwordCreateET);
        EditText addInfoET = getActivity().findViewById(R.id.addInfoET);

        String platform = platformET.getText().toString();
        String username = usernameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String addInfo = addInfoET.getText().toString();

        addInfoET.setSingleLine();

        if ( !(platform.trim().isEmpty()) ) {
            String dataRow = platform + "÷÷" + username + "÷÷" + email + "÷÷" + password + "÷÷" + addInfo + "÷÷\n";

            try {
                String data = sharedStuff.loadData() + dataRow;

                platformET.setText("");
                usernameET.setText("");
                emailET.setText("");
                passwordET.setText("");
                addInfoET.setText("");

                sharedStuff.saveData(data);

                Toast.makeText(getActivity().getBaseContext(), "Data has been saved.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity().getBaseContext(), "Save failed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity().getBaseContext(), "You cant leave field platform empty!", Toast.LENGTH_SHORT).show();
        }
    }
}
