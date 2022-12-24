package com.dream7c.dew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    private List<String> Question;
    private List<String> Answer;
    private int problemLength;
    private int targetProblem = 0;
    private boolean showAnswer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setTitle(MainActivity.targetProjectName);
        TextView tx_question = findViewById(R.id.tx_question);
        TextView tx_answer = findViewById(R.id.tx_answer);
        //Initial
            //displayType
            Question = ProjectActivity.randomDisplay ? ProjectActivity.randomQuestions : ProjectActivity.Questions;
            Answer = ProjectActivity.randomDisplay ? ProjectActivity.randomAnswers : ProjectActivity.Answers;
            problemLength = Question.size();
            //showProcess
            TextView tx_process = findViewById(R.id.tx_process);
            tx_process.setVisibility(SettingActivity.showProcess ? View.VISIBLE : View.GONE);
            //init
            tx_question.setText(Question.get(targetProblem = ProjectActivity.displayStart));
            tx_answer.setText("");
            refreshProcess();
        //Display settings
        setSettings();
        //OnClick
        tx_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousPart();
            }
        });
        tx_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPart();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void setSettings() {
        Settings settings = new Settings();
        TextView tx_question = findViewById(R.id.tx_question);
        TextView tx_answer = findViewById(R.id.tx_answer);
        tx_question.setTextSize(settings.convertFontSize(SettingActivity.fontSize));
        tx_answer.setTextSize(settings.convertFontSize(SettingActivity.fontSize));
    }

    private void previousPart() {
        TextView tx_question = findViewById(R.id.tx_question);
        TextView tx_answer = findViewById(R.id.tx_answer);
        if (!showAnswer) {
            if (targetProblem == 0) {
                return;
            }
            tx_question.setText(Question.get(--targetProblem));
            tx_answer.setText(Answer.get(targetProblem));
        } else {
            tx_answer.setText("");
        }
        showAnswer = !showAnswer;
        refreshProcess();
    }

    private void nextPart() {
        TextView tx_question = findViewById(R.id.tx_question);
        TextView tx_answer = findViewById(R.id.tx_answer);
        if (!showAnswer) {
            tx_answer.setText(Answer.get(targetProblem));
        } else {
            if (targetProblem == problemLength - 1) {
                return;
            }
            tx_question.setText(Question.get(++targetProblem));
            tx_answer.setText("");
        }
        showAnswer = !showAnswer;
        refreshProcess();
    }

    private void refreshProcess() {
        TextView tx_process = findViewById(R.id.tx_process);
        tx_process.setText((targetProblem + 1) + " / " + problemLength);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                AlertDialog.Builder dialog = new AlertDialog.Builder(DisplayActivity.this);
                dialog.setCancelable(false)
                        .setTitle("放映帮助")
                        .setMessage("上一页：点击屏幕上方区域\n下一页：点击屏幕下方区域")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
        }
        return false;
    }
}
