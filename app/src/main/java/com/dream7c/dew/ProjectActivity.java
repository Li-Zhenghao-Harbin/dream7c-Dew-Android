package com.dream7c.dew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ProjectActivity extends AppCompatActivity {
    public static List<String> Questions = new ArrayList<>();
    public static List<String> Answers = new ArrayList<>();
    private int targetProblem = 0;
    private boolean contentSaved = true;
    private final String[] displayTypeList = new String[] {"顺序放映", "从当前放映", "随机乱序放映"};
    public static int displayStart = 0;
    public static boolean randomDisplay = false;
    public static List<String> randomQuestions = new ArrayList<>();
    public static List<String> randomAnswers = new ArrayList<>();

    private void showToast(String text) {
        Toast toast = Toast.makeText(ProjectActivity.this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void contentSaved(boolean status) {
        contentSaved = status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        setTitle(MainActivity.targetProjectName);
        String fileName = MainActivity.targetProjectName + MainActivity.fileSuffix;
        if (MainActivity.newProject) {
            resetProblems();
            addProblem();
            saveProject();
        } else {
            try {
                initProblems(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewProblems();
            refreshProblem();
        }
        final TextView tx_question = findViewById(R.id.tx_question);
        final TextView tx_answer = findViewById(R.id.tx_answer);
        final ListView listView = findViewById(R.id.view_problems);
        //onTextChanged
        tx_question.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Questions.set(targetProblem, tx_question.getText().toString());
                viewProblems();
                listView.setSelection(targetProblem);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        tx_answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Answers.set(targetProblem, tx_answer.getText().toString());
                viewProblems();
                listView.setSelection(targetProblem);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //Fix
        while (Answers.size() < Questions.size()) {
            Answers.add("");
        }
        //Save
        contentSaved(true);
        //Search
        loadProjectToActivity();
        //FloatActionButton
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (SettingActivity.displayType) {
                    case 1:
                        displayStart = targetProblem;
                    case 0:
                        randomDisplay = false;
                        displayProblems();
                        break;
                    case 2:
                        displayRandomProblems();
                        break;
                }
//                randomDisplay = false;
//                displayProblems();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProjectActivity.this);
                dialog.setItems(displayTypeList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 1:
                                displayStart = targetProblem;
                            case 0:
                                randomDisplay = false;
                                displayProblems();
                                break;
                            case 2:
                                displayRandomProblems();
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
    }

    public int[] randomSampling(int n) {
        Random rand = new Random();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        int[] res = new int[n];
        for (int a = 0, b; a < n; a++) {
            b = Math.abs(rand.nextInt()) % (n - a);
            res[a] = list.get(b);
            Collections.swap(list, n - a - 1, b);
        }
        return res;
    }

    private void displayRandomProblems() {
        ListView listView = findViewById(R.id.view_problems);
        int n = listView.getCount();
        int[] array = randomSampling(n);
        for (Integer e : array) {
            Log.d("MainActivity", e + " ");
        }
        randomQuestions.clear();
        randomAnswers.clear();
        for (int i = 0; i < n; i++) {
            randomQuestions.add(Questions.get(array[i]));
            randomAnswers.add(Answers.get(array[i]));
        }
        randomDisplay = true;
        displayProblems();
    }

    private void loadProjectToActivity() {
        File file = new File(MainActivity.filePath);
        if (!file.exists()) {
            return;
        }
        MainActivity.projectButtonNames.clear();
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (!f.isDirectory()) {
                String name = f.getName().substring(0, f.getName().length() - MainActivity.fileSuffix.length());
                MainActivity.projectButtonNames.add(name);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!contentSaved) {
            final AlertDialog.Builder saveDialog = new AlertDialog.Builder(ProjectActivity.this);
            saveDialog.setTitle("是否保存修改内容？");
            saveDialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveProject();
                    finish();
                }
            });
            saveDialog.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            saveDialog.show();
        } else {
            finish();
        }
    }

    private void resetProblems() {
        Questions.clear();
        Answers.clear();
        viewProblems();
    }

    private void addProblem() {
        Questions.add("");
        Answers.add("");
        targetProblem = Questions.size() - 1;
        TextView tx_question = findViewById(R.id.tx_question);
        TextView tx_answer = findViewById(R.id.tx_answer);
        tx_question.setText("");
        tx_answer.setText("");
        tx_question.requestFocus();
        refreshProblem();
        contentSaved(false);
        viewProblems();
    }

    private void removeProblem() {
        if (targetProblem >= Questions.size()) {
            return;
        }
        ListView listView = findViewById(R.id.view_problems);
        if (listView.getChildCount() == 0) {
            return;
        } else if (listView.getChildCount() == 1) {
          TextView tx_question = findViewById(R.id.tx_question);
          TextView tx_answer = findViewById(R.id.tx_answer);
          tx_question.setText("");
          tx_answer.setText("");
        }
        Questions.remove(targetProblem);
        Answers.remove(targetProblem);
        refreshProblem();
        contentSaved(false);
        viewProblems();
    }

    private void refreshProblem() {
        final ListView listView = findViewById(R.id.view_problems);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tx_question = findViewById(R.id.tx_question);
                TextView tx_answer = findViewById(R.id.tx_answer);
                tx_question.setText(Questions.get(targetProblem = i));
                tx_answer.setText(Answers.get(targetProblem));
                listView.setSelection(i);
            }
        });
    }

    private void viewProblems() {
        ListView listView = findViewById(R.id.view_problems);
        String[] title = Questions.toArray(new String[Questions.size()]);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, title));
    }

    private void initProblems(String fileName) throws Exception {
        //Parse Txt
        File file = new File(MainActivity.filePath + fileName);
        ProblemsParser.parserTxt(file);
        loadProblems();
    }

    private void loadProblems() {
        TextView tx_question = findViewById(R.id.tx_question);
        TextView tx_answer = findViewById(R.id.tx_answer);
        tx_question.setText(Questions.get(0));
        tx_answer.setText(Answers.get(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    private void displayProblems() {
        if (Questions.size() == 0) {
            return;
        }
        Intent newIntent = new Intent(ProjectActivity.this, DisplayActivity.class);
        startActivity(newIntent);
    }

    private void saveProject() {
        ProblemsGenerator problemsGenerator = new ProblemsGenerator();
        problemsGenerator.initData();
        showToast("已保存");
        contentSaved(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addProblem();
                break;
            case R.id.remove:
                removeProblem();
                break;
            case R.id.save:
                saveProject();
                break;
        }
        return false;
    }
}
