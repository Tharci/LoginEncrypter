package com.tharci.loginencrypter;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class listLoginFragment extends Fragment {

    View myView;

    String[] abc;
    Integer abcLen;

    String[][] data;

    SharedStuff sharedStuff;

    Button discardButton;
    Button saveButton;


    Integer pointerMax = 64;

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
        abc = sharedStuff.abc;
        abcLen = sharedStuff.abcLen;

        mainLayout = myView.findViewById(R.id.mainLayout);

        discardButton = myView.findViewById(R.id.discardButton);
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadData();

                /*Snackbar.make(myView, "Changes have been discarded.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
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

                                                            /*try {
                                                                sharedStuff.pinHash = sharedStuff.hash("123");
                                                            } catch (NoSuchAlgorithmException e) {
                                                                e.printStackTrace();
                                                            }*/

                Boolean isSaved = false;

                FileOutputStream fos = null;
                try {
                    fos = getActivity().openFileOutput("data_tmp.txt", Context.MODE_PRIVATE);

                    for(Integer j=0; j<data.length; j++)
                    {
                        String enData[] = {"", "", "", "", ""};
                        Integer pointer = 0;

                        EditText platformET = (EditText)((LinearLayout)((LinearLayout) verticalLayouts.get(j).getChildAt(0)).getChildAt(1)).getChildAt(0);
                        LinearLayout detailLayout = detailLayouts.get(j);

                        for (Integer k = 0; k < 5; k++)
                        {
                            EditText editText;
                            if (k == 0)
                            {
                                editText = platformET;
                            } else {
                                editText = (EditText) ((LinearLayout) detailLayout.getChildAt(k-1)).getChildAt(1);
                            }

                            //editText = getActivity().findViewById(j*5+k);
                            String text = editText.getText().toString();

                            editText.setSingleLine();

                            for (Integer i=0; i<text.length(); i++)
                            {
                                enData[k] += encryptChar(text.charAt(i), pointer);
                                pointer++;
                                if (pointer == pointerMax) {pointer = 0;}
                            }
                        }



                        writeToFile("data_tmp.txt", enData[0] + "÷÷" + enData[1] + "÷÷" + enData[2] + "÷÷" + enData[3] + "÷÷" + enData[4] + "÷÷\n");

                        isSaved = true;

                    }
                } catch (Exception e) { isSaved = false; }


                try {fos.close();} catch (IOException e) {}



                File from = new File("data_tmp.txt");
                File to = new File("data.txt");

                if (isSaved) {
                    try {
                        fos = getActivity().openFileOutput("data.txt", Context.MODE_PRIVATE);
                        fos.close();
                        String s = readFile("data_tmp.txt");
                        writeToFile("data.txt", s);


                        Toast.makeText(getActivity().getBaseContext(), "Changes have been saved.", Toast.LENGTH_SHORT).show();
                        /*Snackbar.make(myView, "Changes have been saved.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/
                    } catch (IOException e) {
                        Toast.makeText(getActivity().getBaseContext(), "Failed to save changes. #2",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "Failed to save changes. #1",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mainHandler = new Handler();
        loadData();

        return myView;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (sharedStuff.saved)
        {
            loadData();

            Toast.makeText(getActivity().getBaseContext(), "Data deleted.",Toast.LENGTH_SHORT).show();
            /*Snackbar.make(myView, "Data deleted.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
            sharedStuff.saved = false;
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

    @SuppressLint("ResourceType")
    public void loadData()
    {
        listLoginRunnable runnable = new listLoginRunnable();
        new Thread(runnable).start();
    }

    public void addView(LinearLayout linearLayout)
    {
        LinearLayout mainLayout = myView.findViewById(R.id.mainLayout);
        mainLayout.removeAllViews();
        mainLayout.addView(linearLayout);
    }

    public void writeToFile (String fileName, String string) {

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
                InputStream inputStream = getActivity().openFileInput(fileName);
                inputStream.close();
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
            FileOutputStream fos = getActivity().openFileOutput(fileName, Context.MODE_APPEND);
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

    private String readFile(String fileName)
    {
        String s = "";
        FileInputStream fileIn=null;

        try
        {
            try
            {
                //FileOutputStream fosX = getActivity().openFileOutput("data.txt", Context.MODE_PRIVATE);
                //fosX.close();

                fileIn = getActivity().openFileInput(fileName);
            } catch(Exception e)
            {
                FileOutputStream fosX = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
                fosX.close();

                fileIn = getActivity().openFileInput(fileName);
            }

            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer= new char[2000];
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                String readstring = String.copyValueOf(inputBuffer,0,charRead);
                s += readstring;
            }
            InputRead.close();
            //Toast.makeText(getActivity().getBaseContext(), s,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
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


    class listLoginRunnable implements Runnable {

        LinearLayout mainLayout_2;

        @Override
        public void run() {
            //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);



                                            /*try {
                                                sharedStuff.pinHash = sharedStuff.hashSimple("123");
                                            } catch (NoSuchAlgorithmException e) {
                                                e.printStackTrace();
                                            }*/
            verticalLayouts.clear();
            detailLayouts.clear();
            detailLayoutsStatus.clear();

            String[] dataByLines = readFile("data.txt").split("\n");
            data = new String[dataByLines.length][];

            for (Integer i=0; i<dataByLines.length; i++)
            {
                data[i] = dataByLines[i].split("÷÷");
            }

            final Context context = getActivity().getApplicationContext();
            mainLayout_2 = new LinearLayout(context);
            //LinearLayout mainLayout = myView.findViewById(R.id.mainLayout);
            //mainLayout.removeAllViews();
            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mainLayout.removeAllViews();
                }
            });

            Integer tv_width = 250;

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
                //spaceFillParams.width=0;

                LinearLayoutCompat.LayoutParams deleteBtnParams = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                //deleteBtnParams.width=0;

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



                for (Integer i = 0; i < data.length; i++) {
                    try {


                        final LinearLayout verticalLayout = new LinearLayout(context);
                        //verticalLayout.setLayoutParams(layoutParams);
                        verticalLayout.setOrientation(LinearLayout.VERTICAL);
                        verticalLayout.setPadding(7, 7, 7, 7);
                        verticalLayout.setLayoutParams(verticalL_params);
                        verticalLayout.setBackgroundResource(R.drawable.row_listlogin);

                        Integer pointer = 0;


                        String dePlat = "";
                        try {
                            for (Integer j = 0; j < data[i][0].length(); j++) {
                                dePlat += decryptChar(data[i][0].charAt(j), pointer);
                                pointer++;
                                if (pointer == pointerMax) {
                                    pointer = 0;
                                }
                            }
                        } catch (Exception e) {}


                        LinearLayout platformRowHL = new LinearLayout(context);
                        //platformHL.setLayoutParams(layoutParams);
                        platformRowHL.setOrientation(LinearLayout.HORIZONTAL);
                        platformRowHL.setLayoutParams(platRowHLParams);

                        LinearLayout platformHL = new LinearLayout(context);
                        //platformHL.setLayoutParams(layoutParams);
                        platformHL.setOrientation(LinearLayout.HORIZONTAL);
                        platformHL.setLayoutParams(platHLParams);
                        platformHL.setBackgroundResource(R.drawable.platform_listlogin);
                        //platformHL.setPadding(50, 5, 50, 5);

                        TextView platformET = new EditText(context);
                        platformET.setTextSize(20);
                        platformET.setId(i * 5 + 0);
                        platformET.setPadding(20, 0, 30, 8);
                        platformET.setTypeface(null, Typeface.BOLD);
                        platformET.setText(dePlat);
                        platformET.setSingleLine();
                        platformET.setBackgroundResource(R.drawable.edittext);
                        platformET.setWidth(700);
                        platformET.setLayoutParams(spaceFillParams);
                        platformET.setTextColor(getResources().getColor(R.color.white));
                        platformHL.addView(platformET);

                        /*View spaceFill = new View(context);
                        spaceFill.setLayoutParams(spaceFillParams);
                        platformHL.addView(spaceFill);*/

                        final ImageButton deleteButton = new ImageButton(context);
                        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.trash_icon);
                        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 65, 65, true);
                        deleteButton.setImageBitmap(bMapScaled);
                        deleteButton.setPadding(0, 0, 0, 0);
                        deleteButton.setLayoutParams(deleteBtnParams);
                        deleteButton.setContentDescription(i.toString());
                        deleteButton.setBackgroundResource(R.drawable.edittext);
                        platformHL.addView(deleteButton);

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //deleteLogin(Integer.parseInt(deleteButton.getContentDescription().toString()));
                                //deleteButton.setText(deleteButton.getContentDescription().toString());
                                sharedStuff.deletable = Integer.parseInt(deleteButton.getContentDescription().toString());

                                startActivity(new Intent(getActivity(), surePopUp.class));

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



                        TextView asd = new TextView(context);
                        asd.setText("asdomina");
                        asd.setLayoutParams(deleteBtnParams);
                        //platformHL.addView(asd);



                        verticalLayout.addView(platformRowHL);



                        final ExpandableLinearLayout detailsVertL = new ExpandableLinearLayout(context);
                        //verticalLayout.setLayoutParams(layoutParams);
                        detailsVertL.setOrientation(LinearLayout.VERTICAL);
                        detailsVertL.setPadding(7, 7, 7, 20);
                        detailsVertL.setLayoutParams(details_params);

                        String[] titles = {"Username:", "Email:", "Password:", "Add. Info:"};

                        for (Integer k = 0; k < 4; k++)
                        {
                            String decriypted = "";

                            try {
                                for (Integer j = 0; j < data[i][k+1].length(); j++) {
                                    decriypted += decryptChar(data[i][k+1].charAt(j), pointer);
                                    pointer++;
                                    if (pointer == pointerMax) {
                                        pointer = 0;
                                    }
                                }
                            } catch (Exception e) {}



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
                            editText.setText(decriypted);
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

                        //verticalLayout.addView(detailsVertL);


                        //final Boolean details_on = false;

                        arrowButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Integer index = (int)(((LinearLayout)((LinearLayout)verticalLayout.getChildAt(0)).getChildAt(1)).getChildAt(0).getId() * 0.2);
                                //Integer asd = ((LinearLayout)verticalLayout.getChildAt(0)).getChildAt(0).getId();
                                if (detailLayoutsStatus.get(index))
                                {
                                    //verticalLayout.removeView(detailLayouts.get(index));

                                    Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_up);
                                    arrowButton.startAnimation(rotateAnimation);

                                    detailLayoutsStatus.set(index, false);
                                } else {
                                    //verticalLayout.addView(detailLayouts.get(index));

                                    Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_down);
                                    arrowButton.startAnimation(rotateAnimation);

                                    detailLayoutsStatus.set(index, true);
                                }

                                detailsVertL.toggle();
                            }
                        });


                        //mainLayout_2.addView(verticalLayout);

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mainLayout.addView(verticalLayout);
                            }
                        });
                    } catch (Exception e) {break;}

                }


            }



        }
    }

}
