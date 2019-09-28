package com.example.supot.spk;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.akexorcist.simpletcp.SimpleTcpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class fmEQ extends Fragment {


    public fmEQ() {
        // Required empty public constructor
    }
    private ArrayList<String> arrayAllIp;
    private Spinner spEQ;
    private Context context;
    private ArrayAdapter adapterEQ;
    private ArrayList<String> arrayEQ;
    private boolean isOnEQ = true;
    private SeekBar eqBar_20,eqBar_40,eqBar_63,eqBar_100,eqBar_160,eqBar_300,eqBar_500,eqBar_800,eqBar_1k,eqBar_1_2k,eqBar_2_2k,eqBar_5k,eqBar_10k,eqBar_12k,eqBar_16k,eqBar_20k,eqBar_Master;
    private Button butStopEq,butReEq,butMtg;
    private TextView tvEq;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String dataOutput = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fm_eq, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("EQUALIZER");
        sp = this.getActivity().getSharedPreferences(Const.sp_channel, Context.MODE_PRIVATE);
        editor = sp.edit();
        loadData();
        initEq(view);
        //butReEq.setBackgroundColor(getResources().getColor(R.color.color6));
       // butMtg.setBackgroundColor(getResources().getColor(R.color.color6));

        if(isOnEQ == sp.getBoolean(Const.stuEQ,true)) {
            onSeekBar();
            butStopEq.setBackgroundColor(getResources().getColor(R.color.color3));
            isOnEQ = true;
        }else {
            offSeekBar();
            butStopEq.setBackgroundColor(getResources().getColor(R.color.color5));
            isOnEQ = false;
        }
        return view;
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    private void loadData() {
        Gson gson = new Gson();
        String jsonAllIp = sp.getString(Const.list_AllIp, null);
        Type type = new TypeToken<ArrayList>() {}.getType();
        arrayAllIp = gson.fromJson(jsonAllIp, type);

        if (arrayAllIp == null) {
            arrayAllIp = new ArrayList<>();
        }
    }

    private void  initEq(View view){
        spEQ = (Spinner) view.findViewById(R.id.spEQ);
        eqBar_20 = (SeekBar) view.findViewById(R.id.eqBar_20);
        eqBar_40 = (SeekBar) view.findViewById(R.id.eqBar_40);
        eqBar_63 = (SeekBar) view.findViewById(R.id.eqBar_63);
        eqBar_100 = (SeekBar) view.findViewById(R.id.eqBar_100);
        eqBar_160 = (SeekBar) view.findViewById(R.id.eqBar_160);
        eqBar_300 = (SeekBar) view.findViewById(R.id.eqBar_300);
        eqBar_500 = (SeekBar) view.findViewById(R.id.eqBar_500);
        eqBar_800 = (SeekBar) view.findViewById(R.id.eqBar_800);
        eqBar_1k = (SeekBar) view.findViewById(R.id.eqBar_1k);
        eqBar_1_2k = (SeekBar) view.findViewById(R.id.eqBar_1_2k);
        eqBar_2_2k = (SeekBar) view.findViewById(R.id.eqBar_2_2k);
        eqBar_5k = (SeekBar) view.findViewById(R.id.eqBar_5k);
        eqBar_10k = (SeekBar) view.findViewById(R.id.eqBar_10k);
        eqBar_12k = (SeekBar) view.findViewById(R.id.eqBar_12k);
        eqBar_16k = (SeekBar) view.findViewById(R.id.eqBar_16k);
        eqBar_20k = (SeekBar) view.findViewById(R.id.eqBar_20k);
        eqBar_Master = (SeekBar) view.findViewById(R.id.BarG2);

        butMtg = (Button) view.findViewById(R.id.butMtg);
        butReEq = (Button) view.findViewById(R.id.butReEq);
        butStopEq = (Button) view.findViewById(R.id.butStopEq);

        tvEq = (TextView) view.findViewById(R.id.tvEq);
        arrayEQ = new ArrayList<>();
        for (int i=1;i<=6;i++){
            arrayEQ.add("EQ"+i);
        }
        adapterEQ = new ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_dropdown_item, arrayEQ);
        spEQ.setAdapter(adapterEQ);

        int saveProgress = sp.getInt(Const.master_eq_slide,40)-80;
        tvEq.setText(String.valueOf("MASTER : "+saveProgress+" dB"));
        initEqBar();

        eqBar_20.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("20Hz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/20/V/%.2f",progressChanged);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < arrayAllIp.size(); i++) {
                                SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                                //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                            }
                        }catch (Exception e) {}
                    }
                }).start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_1,value);
                editor.putString(Const.master_eq_slide_value_1_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_40.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("40Hz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/40/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_2,value);
                editor.putString(Const.master_eq_slide_value_2_string,dataOutput);
                editor.commit();

            }
        });
        eqBar_63.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("63Hz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/63/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_3,value);
                editor.putString(Const.master_eq_slide_value_3_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_100.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("100Hz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/100/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_4,value);
                editor.putString(Const.master_eq_slide_value_4_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_160.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("160Hz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/160/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_5,value);
                editor.putString(Const.master_eq_slide_value_5_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_300.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("300Hz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/300/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_6,value);
                editor.putString(Const.master_eq_slide_value_6_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_500.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("500Hz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/500/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_7,value);
                editor.putString(Const.master_eq_slide_value_7_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_800.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("800Hz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/800/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_8,value);
                editor.putString(Const.master_eq_slide_value_8_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_1k.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("1KHz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/1000/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_9,value);
                editor.putString(Const.master_eq_slide_value_9_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_1_2k.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("1.2KHz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/1200/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_10,value);
                editor.putString(Const.master_eq_slide_value_10_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_2_2k.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("2.2KHz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/2200/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_11,value);
                editor.putString(Const.master_eq_slide_value_11_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_5k.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("5KHz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/5000/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_12,value);
                editor.putString(Const.master_eq_slide_value_12_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_10k.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("10KHz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/10000/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_13,value);
                editor.putString(Const.master_eq_slide_value_13_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_12k.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("12KHz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/12000/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_14,value);
                editor.putString(Const.master_eq_slide_value_14_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_16k.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("16KHz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/16000/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_15,value);
                editor.putString(Const.master_eq_slide_value_15_string,dataOutput);
                editor.commit();
            }
        });
        eqBar_20k.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = (progress-150)*0.1f;
                value = progress;
                tvEq.setText(String.format("20KHz : %.2f dB",progressChanged));
                int posEQ = spEQ.getSelectedItemPosition();
                dataOutput = String.format(arrayEQ.get(posEQ)+"/F/20000/V/%.2f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide_value_16,value);
                editor.putString(Const.master_eq_slide_value_16_string,dataOutput);
                editor.commit();

            }
        });
        eqBar_Master.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressChanged;
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress-80;
                value = progress;
                tvEq.setText(String.format("MASTER : %.0f dB",progressChanged));
                dataOutput = String.format("MASTERVOL/%.0f",progressChanged);
                try {
                    for (int i = 0; i < arrayAllIp.size(); i++) {
                        SimpleTcpClient.send(dataOutput, arrayAllIp.get(i), Const.port);
                        //Log.d("26J", "MAS EQ : " + arrayAllIp.get(i) + "/" + dataOutput);
                    }
                }catch (Exception e) {}

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Const.master_eq_slide,value);
                editor.putString(Const.master_eq_slide_string,dataOutput);
                editor.commit();
            }
        });
        butStopEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnEQ==true) {
                    offSeekBar();
                    butStopEq.setBackgroundColor(getResources().getColor(R.color.color5));
                    isOnEQ = false;
                    editor.putBoolean(Const.stuEQ,isOnEQ);
                    editor.commit();
                }else{
                    onSeekBar();
                    butStopEq.setBackgroundColor(getResources().getColor(R.color.color3));
                    isOnEQ = true;
                    editor.putBoolean(Const.stuEQ,isOnEQ);
                    editor.commit();
                }
            }
        });

        butReEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnEQ) {
                    eqBar_20.setProgress(150);
                    eqBar_40.setProgress(150);
                    eqBar_63.setProgress(150);
                    eqBar_100.setProgress(150);
                    eqBar_160.setProgress(150);
                    eqBar_300.setProgress(150);
                    eqBar_500.setProgress(150);
                    eqBar_800.setProgress(150);
                    eqBar_1k.setProgress(150);
                    eqBar_1_2k.setProgress(150);
                    eqBar_2_2k.setProgress(150);
                    eqBar_5k.setProgress(150);
                    eqBar_10k.setProgress(150);
                    eqBar_12k.setProgress(150);
                    eqBar_16k.setProgress(150);
                    eqBar_20k.setProgress(150);

                    editor.putInt(Const.master_eq_slide_value_1,150);
                    editor.putInt(Const.master_eq_slide_value_2,150);
                    editor.putInt(Const.master_eq_slide_value_3,150);
                    editor.putInt(Const.master_eq_slide_value_4,150);
                    editor.putInt(Const.master_eq_slide_value_5,150);
                    editor.putInt(Const.master_eq_slide_value_6,150);
                    editor.putInt(Const.master_eq_slide_value_7,150);
                    editor.putInt(Const.master_eq_slide_value_8,150);
                    editor.putInt(Const.master_eq_slide_value_9,150);
                    editor.putInt(Const.master_eq_slide_value_10,150);
                    editor.putInt(Const.master_eq_slide_value_11,150);
                    editor.putInt(Const.master_eq_slide_value_12,150);
                    editor.putInt(Const.master_eq_slide_value_13,150);
                    editor.putInt(Const.master_eq_slide_value_14,150);
                    editor.putInt(Const.master_eq_slide_value_15,150);
                    editor.putInt(Const.master_eq_slide_value_16,150);
                    editor.commit();
                }
            }
        });
        butMtg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MTG.class);
                startActivity(intent);
            }
        });
    }
    private void offSeekBar() {
        eqBar_20.setEnabled(false);
        eqBar_40.setEnabled(false);
        eqBar_63.setEnabled(false);
        eqBar_100.setEnabled(false);
        eqBar_160.setEnabled(false);
        eqBar_300.setEnabled(false);
        eqBar_500.setEnabled(false);
        eqBar_800.setEnabled(false);
        eqBar_1k.setEnabled(false);
        eqBar_1_2k.setEnabled(false);
        eqBar_2_2k.setEnabled(false);
        eqBar_5k.setEnabled(false);
        eqBar_10k.setEnabled(false);
        eqBar_12k.setEnabled(false);
        eqBar_16k.setEnabled(false);
        eqBar_20k.setEnabled(false);
    }

    private void onSeekBar() {
        eqBar_20.setEnabled(true);
        eqBar_40.setEnabled(true);
        eqBar_63.setEnabled(true);
        eqBar_100.setEnabled(true);
        eqBar_160.setEnabled(true);
        eqBar_300.setEnabled(true);
        eqBar_500.setEnabled(true);
        eqBar_800.setEnabled(true);
        eqBar_1k.setEnabled(true);
        eqBar_1_2k.setEnabled(true);
        eqBar_2_2k.setEnabled(true);
        eqBar_5k.setEnabled(true);
        eqBar_10k.setEnabled(true);
        eqBar_12k.setEnabled(true);
        eqBar_16k.setEnabled(true);
        eqBar_20k.setEnabled(true);
    }
    private void initEqBar() {
        eqBar_20.setMax(300);
        eqBar_40.setMax(300);
        eqBar_63.setMax(300);
        eqBar_100.setMax(300);
        eqBar_160.setMax(300);
        eqBar_300.setMax(300);
        eqBar_500.setMax(300);
        eqBar_800.setMax(300);
        eqBar_1k.setMax(300);
        eqBar_1_2k.setMax(300);
        eqBar_2_2k.setMax(300);
        eqBar_5k.setMax(300);
        eqBar_10k.setMax(300);
        eqBar_12k.setMax(300);
        eqBar_16k.setMax(300);
        eqBar_20k.setMax(300);
        eqBar_Master.setMax(80);


        eqBar_20.setProgress(sp.getInt(Const.master_eq_slide_value_1,150));
        eqBar_40.setProgress(sp.getInt(Const.master_eq_slide_value_2,150));
        eqBar_63.setProgress(sp.getInt(Const.master_eq_slide_value_3,150));
        eqBar_100.setProgress(sp.getInt(Const.master_eq_slide_value_4,150));
        eqBar_160.setProgress(sp.getInt(Const.master_eq_slide_value_5,150));
        eqBar_300.setProgress(sp.getInt(Const.master_eq_slide_value_6,150));
        eqBar_500.setProgress(sp.getInt(Const.master_eq_slide_value_7,150));
        eqBar_800.setProgress(sp.getInt(Const.master_eq_slide_value_8,150));
        eqBar_1k.setProgress(sp.getInt(Const.master_eq_slide_value_9,150));
        eqBar_1_2k.setProgress(sp.getInt(Const.master_eq_slide_value_10,150));
        eqBar_2_2k.setProgress(sp.getInt(Const.master_eq_slide_value_11,150));
        eqBar_5k.setProgress(sp.getInt(Const.master_eq_slide_value_12,150));
        eqBar_10k.setProgress(sp.getInt(Const.master_eq_slide_value_13,150));
        eqBar_12k.setProgress(sp.getInt(Const.master_eq_slide_value_14,150));
        eqBar_16k.setProgress(sp.getInt(Const.master_eq_slide_value_15,150));
        eqBar_20k.setProgress(sp.getInt(Const.master_eq_slide_value_16,150));
        eqBar_Master.setProgress(sp.getInt(Const.master_eq_slide,80));
    }
}
