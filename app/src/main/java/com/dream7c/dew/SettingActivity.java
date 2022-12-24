package com.dream7c.dew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {

    public static int fontSize = 0;
    public static int displayType = 0;
    private final String[] displayTypeList = new String[] {"顺序放映", "从当前放映", "随机乱序放映"};
    public static boolean showProcess = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        this.setTitle("放映设置");
        //Reload
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(fontSize);
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, displayTypeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(displayType);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                displayType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Switch switch_process = findViewById(R.id.switch_process);
        switch_process.setChecked(showProcess);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SeekBar seekBar = findViewById(R.id.seekBar);
        fontSize = seekBar.getProgress();
        Switch switch_process = findViewById(R.id.switch_process);
        showProcess = switch_process.isChecked();
        savePreferences();
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("fontSize", fontSize);
        edit.putInt("displayType", displayType);
        edit.putBoolean("showProcess", showProcess);
        edit.commit();
    }
}
