package com.dream7c.dew;

public class Settings {
    public float convertFontSize(int fontSize) {
        switch (fontSize) {
            case 1:
                return 16;
            case 2:
                return 20;
            case 3:
                return 24;
            case 4:
                return 28;
        }
        return 12;
    }
}
