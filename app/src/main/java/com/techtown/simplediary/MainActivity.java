package com.techtown.simplediary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    final String strSDPath = "/storage/emulated/0";
    final String directoryPath = strSDPath + "/myDiary";

    DatePicker dp;
    EditText edtDiary;
    Button button;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MODE_PRIVATE);

        dp = findViewById(R.id.datePicker);
        edtDiary = findViewById(R.id.editDiary);
        button = findViewById(R.id.button);

        dateInit();

        File myDir = new File(directoryPath);
        if (!myDir.exists()) {
            myDir.mkdir();
            button.setText("새로 저장");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeDiary();
            }
        });
    }

    private void dateInit() {
        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH);
        int cDay = cal.get(Calendar.DAY_OF_MONTH);

        fileName = Integer.toString(cYear) + "_" + Integer.toString(cMonth+1) + "_" + Integer.toString(cDay) + ".txt";
        readDiary(fileName);
        button.setEnabled(true);

        dp.init(cYear, cMonth, cDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fileName = Integer.toString(year) + "_" + Integer.toString(monthOfYear+1) + "_" + Integer.toString(dayOfMonth) + ".txt";
                readDiary(fileName);
                button.setEnabled(true);
            }
        });
    }

    private void writeDiary() {
        try {
            FileOutputStream outFs = new FileOutputStream(directoryPath + "/" + fileName);
            String str = edtDiary.getText().toString();
            outFs.write(str.getBytes());
            outFs.flush();
            outFs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readDiary(String fileName) {
        try {
            String filePath = directoryPath + "/" + fileName;
            Log.d("MainActivity", filePath);
            File file = new File(filePath);
            if (!file.exists()) {
                button.setText("새로 저장");
                edtDiary.setText("");
                return;
            }
            button.setText("수정하기");
            FileInputStream inFs = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(inFs, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            char[] txt = new char[inFs.available()];
            bufferedReader.read(txt);
            edtDiary.setText(new String(txt));

            bufferedReader.close();
            inputStreamReader.close();
            inFs.close();
            Log.d("MainActivity", new String(txt));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
