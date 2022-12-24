package com.dream7c.dew;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProblemsParser {
    public static List<Problems> parserXml(InputStream in) throws Exception {
        List<Problems> problemsLists = null;
        Problems problems = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, "utf-8");
        int type = parser.getEventType();
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_DOCUMENT:
                    problemsLists = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    if ("Problem".equals(parser.getName())) {
                        problems = new Problems();
                    } else if ("Question".equals(parser.getName())) {
                        String city = parser.nextText();
                        problems.setQuestions(city);
                    } else if ("Answer".equals(parser.getName())) {
                        String temp = parser.nextText();
                        problems.setAnswers(temp);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("Problem".equals(parser.getName())) {
                        problemsLists.add(problems);
                    }
                    break;
            }
            type = parser.next();
        }
        return problemsLists;
    }

    public static void parserTxt(File file) throws IOException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        //test 计算机系统
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt;
        boolean target = true;
        ProjectActivity.Questions.clear();
        ProjectActivity.Answers.clear();
        while ((lineTxt = bufferedReader.readLine()) != null) {
            if (target) {
                ProjectActivity.Questions.add(lineTxt);
            } else {
                ProjectActivity.Answers.add(lineTxt);
            }
            target = !target;
        }
    }
}
