package com.tharci.loginencrypter;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.io.IOException;
import java.util.ArrayList;

public class ListLoginFragment extends Fragment {

    View myView;
    LayoutInflater inflater;

    String[][] data;

    SharedStuff sharedStuff;

    Button discardButton;
    Button saveButton;

    Handler mainHandler;
    LinearLayout mainLayout;

    Handler animHandler;

    ArrayList<LinearLayout> expandableLayouts;
    ArrayList<Boolean> expandableLayoutsStatus;
    final int ANIM_DURATION = 310; // +10 millisecond offset
    ArrayList<Boolean> expandableLayoutsAnimOngoing;
    ArrayList<LinearLayout> loginDataLayouts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        myView = inflater.inflate(R.layout.list_login, container, false);

        sharedStuff = SharedStuff.getInstance();

        mainLayout = myView.findViewById(R.id.mainLayout);

        discardButton = myView.findViewById(R.id.discardButton);
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
                Toast.makeText(getActivity().getBaseContext(), "Changes have been discarded.",Toast.LENGTH_SHORT).show();
            }
        });


        expandableLayouts = new ArrayList<>();
        expandableLayoutsStatus = new ArrayList<>();
        expandableLayoutsAnimOngoing = new ArrayList<>();
        loginDataLayouts = new ArrayList<>();

        saveButton = myView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        mainHandler = new Handler();
        animHandler = new Handler();

        loadData();

        return myView;
    }

    @SuppressLint("ResourceType")
    public void loadData()
    {
        listLoginRunnable runnable = new listLoginRunnable();
        new Thread(runnable).start();
    }

    void saveData() {
        StringBuilder dataOut = new StringBuilder();
        for (int j = 0; j < data.length; j++)
        {
            EditText platformET = (EditText)((LinearLayout)((LinearLayout) loginDataLayouts.get(j).getChildAt(0)).getChildAt(1)).getChildAt(0);
            LinearLayout detailLayout = expandableLayouts.get(j);

            for (int k = 0; k < 5; k++)
            {
                EditText editText;
                if (k == 0)
                {
                    editText = platformET;
                } else {
                    editText = (EditText) ((LinearLayout) detailLayout.getChildAt(k-1)).getChildAt(1);
                }

                String text = editText.getText().toString();
                dataOut.append(text).append("÷÷");
            }

            dataOut.append("\n");
        }

        try {
            sharedStuff.saveData(dataOut.toString());

            Toast.makeText(getActivity().getBaseContext(), "Changes have been saved.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getActivity().getBaseContext(), "Failed to save changes. #2",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    class listLoginRunnable implements Runnable {

        LinearLayout mainLayout_2;

        @Override
        public void run() {
            loginDataLayouts.clear();
            expandableLayouts.clear();
            expandableLayoutsStatus.clear();

            String[] dataByLines;
            try {
                dataByLines = sharedStuff.loadData().split("\n");
            } catch (Exception e) {
                Toast.makeText(getActivity().getBaseContext(), "Failed to decrypt data.",Toast.LENGTH_SHORT).show();
                return;
            }

            data = new String[dataByLines.length][];

            for (int i=0; i<dataByLines.length; i++)
            {
                data[i] = dataByLines[i].split("÷÷");
            }

            final Context context = getActivity().getApplicationContext();
            mainLayout_2 = new LinearLayout(context);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mainLayout.removeAllViews();
                }
            });

            if (dataByLines[0].length()>1) {
                for (int i = 0; i < data.length; i++) {
                    final LinearLayout loginDataLayout = (LinearLayout) inflater.inflate(R.layout.login_data_layout, null);

                    String platform = data[i][0];

                    final EditText platformET = loginDataLayout.findViewById(R.id.platformET);
                    platformET.setText(platform);
                    platformET.setId(i * 5);

                    final ImageView deleteButton = loginDataLayout.findViewById(R.id.deleteBtn);
                    deleteButton.setContentDescription(Integer.toString(i));

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final int delete_idx = Integer.parseInt(deleteButton.getContentDescription().toString());

                            SharedStuff.popUpWindowRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    String[] data;
                                    try {
                                        data = sharedStuff.loadData().split("\n");
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity().getBaseContext(), "Could not save changes.",Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    StringBuilder dataOut = new StringBuilder();
                                    for (int i=0; i<data.length; i++) {
                                        if (i != delete_idx) {
                                            dataOut.append(data[i]).append("\n");
                                        }
                                    }

                                    try {
                                        sharedStuff.saveData(dataOut.toString());
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity().getBaseContext(), "Could not save changes.",Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    Toast.makeText(getActivity().getBaseContext(), "Changes have been saved.",Toast.LENGTH_SHORT).show();
                                    loadData();
                                }
                            };
                            startActivity(new Intent(getActivity(), PopUpWindowActivity.class));
                        }
                    });


                    int[] valueIDs = {R.id.usernameET, R.id.passwordET, R.id.emailET, R.id.addInfoET};
                    for (int k = 0; k < 4; k++)
                    {
                        String fieldValue = "";
                        if (k+1 < data[i].length) {
                            fieldValue = data[i][k+1];
                        }

                        EditText editText = loginDataLayout.findViewById(valueIDs[k]);
                        editText.setText(fieldValue);
                        editText.setId(i * 5 + (k+1));
                    }

                    final ExpandableLinearLayout expandableLayout = loginDataLayout.findViewById(R.id.detailLayout);
                    expandableLayouts.add(expandableLayout);
                    expandableLayoutsStatus.add(false);
                    expandableLayoutsAnimOngoing.add(false);
                    loginDataLayouts.add(loginDataLayout);

                    final ImageView arrowButton = loginDataLayout.findViewById(R.id.arrowBtn);
                    arrowButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final int index = (int)(platformET.getId() * 0.2);

                            if (!expandableLayoutsAnimOngoing.get(index)) {

                                expandableLayoutsAnimOngoing.set(index, true);
                                animHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        expandableLayoutsAnimOngoing.set(index, false);
                                    }
                                }, ANIM_DURATION);

                                if (expandableLayoutsStatus.get(index)) {
                                    Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_up);
                                    arrowButton.startAnimation(rotateAnimation);

                                    expandableLayoutsStatus.set(index, false);
                                } else {
                                    Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_down);
                                    arrowButton.startAnimation(rotateAnimation);

                                    expandableLayoutsStatus.set(index, true);
                                }
                            }

                            expandableLayout.toggle(ANIM_DURATION, null);
                        }
                    });

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mainLayout.addView(loginDataLayout);
                        }
                    });
                }
            }
        }
    }
}
