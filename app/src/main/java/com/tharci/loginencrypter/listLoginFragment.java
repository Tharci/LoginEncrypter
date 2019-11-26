package com.tharci.loginencrypter;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class listLoginFragment extends Fragment {

    View myView;

    String[][] data;

    SharedStuff sharedStuff;

    Button discardButton;
    Button saveButton;

    Handler mainHandler;
    LinearLayout mainLayout;

    ArrayList<LinearLayout> detailLayouts;
    ArrayList<Boolean> detailLayoutsStatus;
    ArrayList<LinearLayout> verticalLayouts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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


        detailLayouts = new ArrayList<LinearLayout>();
        detailLayoutsStatus = new ArrayList<Boolean>();
        verticalLayouts = new ArrayList<LinearLayout>();

        saveButton = myView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        mainHandler = new Handler();

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
            EditText platformET = (EditText)((LinearLayout)((LinearLayout) verticalLayouts.get(j).getChildAt(0)).getChildAt(1)).getChildAt(0);
            LinearLayout detailLayout = detailLayouts.get(j);

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

                // editText.setSingleLine();
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

    public void addView(LinearLayout linearLayout)
    {
        LinearLayout mainLayout = myView.findViewById(R.id.mainLayout);
        mainLayout.removeAllViews();
        mainLayout.addView(linearLayout);
    }


    class listLoginRunnable implements Runnable {

        LinearLayout mainLayout_2;

        @Override
        public void run() {
            verticalLayouts.clear();
            detailLayouts.clear();
            detailLayoutsStatus.clear();

            String[] dataByLines;
            try {
                dataByLines = sharedStuff.readData().split("\n");
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

            int tv_width = 250;

            if (dataByLines[0].length()>1) {

                final LinearLayoutCompat.LayoutParams verticalL_params = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                );
                verticalL_params.setMargins(7, 7, 7, 7);

                final LinearLayoutCompat.LayoutParams details_params = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT
                );
                details_params.setMargins(7, 7, 7, 7);


                LinearLayoutCompat.LayoutParams spaceFillParams = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        100.0f
                );

                LinearLayoutCompat.LayoutParams deleteBtnParams = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        1.0f
                );

                LinearLayoutCompat.LayoutParams platHLParams = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
                platHLParams.setMargins(30, 20, 0, 5);

                LinearLayoutCompat.LayoutParams platRowHLParams = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                );
                platRowHLParams.setMargins(30, 0, 100, 0);

                for (int i = 0; i < data.length; i++) {
                    try {
                        final LinearLayout verticalLayout = new LinearLayout(context);
                        verticalLayout.setOrientation(LinearLayout.VERTICAL);
                        verticalLayout.setPadding(7, 7, 7, 7);
                        verticalLayout.setLayoutParams(verticalL_params);
                        verticalLayout.setBackgroundResource(R.drawable.row_listlogin);

                        String platform = data[i][0];

                        LinearLayout platformRowHL = new LinearLayout(context);
                        platformRowHL.setOrientation(LinearLayout.HORIZONTAL);
                        platformRowHL.setLayoutParams(platRowHLParams);

                        LinearLayout platformHL = new LinearLayout(context);
                        platformHL.setOrientation(LinearLayout.HORIZONTAL);
                        platformHL.setLayoutParams(platHLParams);
                        platformHL.setBackgroundResource(R.drawable.platform_listlogin);

                        TextView platformET = new EditText(context);
                        platformET.setTextSize(20);
                        platformET.setId(i * 5);
                        platformET.setPadding(20, 0, 30, 8);
                        platformET.setTypeface(null, Typeface.BOLD);
                        platformET.setText(platform);
                        platformET.setSingleLine();
                        platformET.setBackgroundResource(R.drawable.edittext);
                        platformET.setWidth(700);
                        platformET.setLayoutParams(spaceFillParams);
                        platformET.setTextColor(getResources().getColor(R.color.white));
                        platformHL.addView(platformET);

                        final ImageButton deleteButton = new ImageButton(context);
                        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.trash_icon);
                        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 65, 65, true);
                        deleteButton.setImageBitmap(bMapScaled);
                        deleteButton.setPadding(0, 0, 0, 0);
                        deleteButton.setLayoutParams(deleteBtnParams);
                        deleteButton.setContentDescription(Integer.toString(i));
                        deleteButton.setBackgroundResource(R.drawable.edittext);
                        platformHL.addView(deleteButton);

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int delete_idx = Integer.parseInt(deleteButton.getContentDescription().toString());

                                SharedStuff.popUpWindowRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        String[] data;
                                        try {
                                            data = sharedStuff.readData().split("\n");
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
                                startActivity(new Intent(getActivity(), popUpWindowActivity.class));
                            }
                        });


                        final ImageView arrowButton = new ImageButton(context);
                        Bitmap bMap_2 = BitmapFactory.decodeResource(getResources(), R.drawable.triangle_arrow);
                        Bitmap bMapScaled_2 = Bitmap.createScaledBitmap(bMap_2, 60, 60, true);
                        arrowButton.setImageBitmap(bMapScaled_2);
                        arrowButton.setPadding(10, 10, 0, 0);
                        arrowButton.setLayoutParams(deleteBtnParams);
                        arrowButton.setBackgroundResource(R.drawable.edittext);
                        platformRowHL.addView(arrowButton);

                        platformRowHL.addView(platformHL);

                        verticalLayout.addView(platformRowHL);

                        final ExpandableLinearLayout detailsVertL = new ExpandableLinearLayout(context);
                        detailsVertL.setOrientation(LinearLayout.VERTICAL);
                        detailsVertL.setPadding(7, 7, 7, 20);
                        detailsVertL.setLayoutParams(details_params);

                        String[] titles = {"Username:", "Email:", "Password:", "Add. Info:"};

                        for (int k = 0; k < 4; k++)
                        {
                            String fieldValue = "";
                            if (k+1 < data[i].length) {
                                fieldValue = data[i][k+1];
                            }

                            LinearLayout horLayout = new LinearLayout(context);
                            horLayout.setOrientation(LinearLayout.HORIZONTAL);

                            TextView titleTV = new TextView(context);
                            titleTV.setText(titles[k]);
                            titleTV.setPadding(40, 5, 5, 5);
                            titleTV.setTextColor(getResources().getColor(R.color.colorDark0));
                            titleTV.setWidth(tv_width);
                            horLayout.addView(titleTV);

                            EditText editText = new EditText(context);
                            editText.setSingleLine();
                            editText.setId(i * 5 + (k+1));
                            editText.setPadding(5, 5, 5, 5);
                            editText.setTextColor(getResources().getColor(R.color.white));
                            editText.setText(fieldValue);
                            editText.setBackgroundResource(R.drawable.edittext);
                            editText.setSingleLine();
                            editText.setMinWidth(400);
                            horLayout.addView(editText);

                            detailsVertL.addView(horLayout);
                        }

                        verticalLayout.addView(detailsVertL);

                        detailLayouts.add(detailsVertL);
                        detailLayoutsStatus.add(false);
                        verticalLayouts.add(verticalLayout);

                        arrowButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index = (int)(((LinearLayout)((LinearLayout)verticalLayout.getChildAt(0)).getChildAt(1)).getChildAt(0).getId() * 0.2);
                                if (detailLayoutsStatus.get(index))
                                {
                                    Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_up);
                                    arrowButton.startAnimation(rotateAnimation);

                                    detailLayoutsStatus.set(index, false);
                                } else {
                                    Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_down);
                                    arrowButton.startAnimation(rotateAnimation);

                                    detailLayoutsStatus.set(index, true);
                                }

                                detailsVertL.toggle();
                            }
                        });

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mainLayout.addView(verticalLayout);
                            }
                        });
                    } catch (Exception e) {}
                }
            }
        }
    }
}
