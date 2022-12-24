package com.dream7c.dew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String filePath = "/sdcard/dream7c/dream7c Dew/";
    public static final String fileSuffix = ".txt";

    private int projectIndex = 1;
    public static boolean newProject = true;
    public static String targetProjectName = "";
    public static List<String> projectButtonNames = new ArrayList<>();
    public String[] projectsTools = new String[] {"重命名", "删除"};

    private void showToast(String text) {
        Toast toast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void checkNeedPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission
                (this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
        }
    }

    private void setPreferences() {
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SettingActivity.fontSize = sharedPreferences.getInt("fontSize", 0);
        SettingActivity.displayType = sharedPreferences.getInt("displayType", 0);
        SettingActivity.showProcess = sharedPreferences.getBoolean("showProcess", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Permission
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent getPermission = new Intent();
                getPermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getPermission);
            }
        } else {
            checkNeedPermissions();
        }
        //Reload Settings
        setPreferences();
        //FloatActionButton
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(MainActivity.this, ProjectActivity.class);
                String tempName = "新建项目" + projectIndex;
                while (projectButtonNames.contains(tempName)) {
                    tempName = "新建项目" + (++projectIndex);
                }
                targetProjectName = tempName;
                newProject = true;
                startActivity(newIntent);
                addProjectToMainMenu("新建项目" + projectIndex++);
            }
        });
        //Load projects
        loadProjectToActivity(false);
        //Search
        final TextView tx_search = findViewById(R.id.tx_search);
        tx_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadSearchResultList(tx_search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadSearchResultList(String searchText) {
        final ListView listView = findViewById(R.id.view_search);
        if (searchText.equals("")) {
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
            List<String> list = new ArrayList<>();
            for (String str : projectButtonNames) {
                if (str.indexOf(searchText) != -1) {
                    list.add(str);
                }
            }
            String[] title = list.toArray(new String[list.size()]);
            listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, title));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView v = (TextView)view;
                    Intent newIntent = new Intent(MainActivity.this, ProjectActivity.class);
                    targetProjectName = v.getText().toString();
                    newProject = false;
                    showToast("打开 " + targetProjectName);
                    startActivity(newIntent);
                }
            });
        }
    }

    private void addProjectToMainMenu(final String projectName) {
        final Button projectBtn = new Button(this);
        projectBtn.setText(projectName);
        projectBtn.setAllCaps(false);
        projectBtn.setBackgroundColor(Color.parseColor("#e9e9e9"));
        final LinearLayout layout = findViewById(R.id.linearLayout);
        layout.addView(projectBtn);
        projectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(MainActivity.this, ProjectActivity.class);
                targetProjectName = projectBtn.getText().toString();
                newProject = false;
                showToast("打开 " + targetProjectName);
                startActivity(newIntent);
            }
        });
        projectBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                targetProjectName = projectBtn.getText().toString();
                dialog.setTitle("操作 " + targetProjectName);
                final String pathName = getFullPath(targetProjectName);
                dialog.setItems(projectsTools, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //Rename project
                                final EditText editText = new EditText(MainActivity.this);
                                AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this).setView(editText);
                                editText.setText(targetProjectName);
                                editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                editText.selectAll();
                                inputDialog.setTitle("重命名 " + projectName);
                                inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String newName = editText.getText().toString();
                                        String newPath = getFullPath(newName);
                                        File file = new File(pathName);
                                        if (file.renameTo(new File(newPath))) {
                                            showToast("重命名 " + newName);
                                            projectBtn.setText(newName);
                                            loadProjectToActivity(true);
                                        } else {
                                            showToast("重命名失败，名称包含非法字符");
                                        }
                                    }
                                }).show();
                                break;
                            case 1: //Delete project
                                layout.removeView(projectBtn);
                                File file = new File(pathName);
                                if (file.exists() && file.isFile()) {
                                    file.delete();
                                    loadProjectToActivity(true);
                                }
                                showToast("删除 " + projectName);
                                break;
                        }
                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    private String getFullPath(String fileName) {
        return filePath + fileName + fileSuffix;
    }

    private void loadProjectToActivity(boolean onlyLoadList) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        projectButtonNames.clear();
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (!f.isDirectory()) {
                String name = f.getName().substring(0, f.getName().length() - 4);
                if (!onlyLoadList) {
                    addProjectToMainMenu(name);
                }
                projectButtonNames.add(name);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent newIntent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(newIntent);
                break;
            case R.id.about:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setCancelable(false)
                        .setTitle("关于本软件")
                        .setMessage("柒幻 寒露 Android\nv 1.1.6\n\n作者：李正浩\n版权所有 © 2022 柒幻工作室\nwww.dream7c.com")
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
