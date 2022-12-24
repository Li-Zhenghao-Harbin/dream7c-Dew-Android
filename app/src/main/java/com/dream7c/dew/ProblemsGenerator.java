package com.dream7c.dew;

import java.io.File;
import java.io.RandomAccessFile;

public class ProblemsGenerator {
    public void initData() {
        String filePath = MainActivity.filePath;
        String fileName = MainActivity.targetProjectName + MainActivity.fileSuffix;
        writeTxtToFile(filePath, fileName);
    }

    //Generate file content
    private void writeTxtToFile(String filePath, String fileName) {
        //Transfer
        StringBuilder sb = new StringBuilder();
        int n = ProjectActivity.Questions.size();
        for (int i = 0; i < n; i++) {
            sb.append(ProjectActivity.Questions.get(i));
            sb.append("\n");
            sb.append(ProjectActivity.Answers.get(i));
            if (i != n - 1) {
                sb.append("\n");
            }
        }
        String strContent = sb.toString();
        //Fill
        makeFilePath(filePath, fileName);
        String strFilePath = filePath + fileName;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {

        }
    }

    //Create file
    private File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

        } catch (Exception e) {

        }
        return file;
    }

    //Create directory
    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }
}
