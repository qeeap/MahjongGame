package com.mahjong.ui.utils;

import javafx.stage.Screen;

public class ScreenUtils {
    private static double screenWidth;
    private static double screenHeight;

    public static void updateScreenSize() {
        screenWidth = Screen.getPrimary().getBounds().getWidth();
        screenHeight = Screen.getPrimary().getBounds().getHeight();
    }

    public static double getScreenWidth() {
        if (screenWidth == 0) updateScreenSize();
        return screenWidth;
    }

    public static double getScreenHeight() {
        if (screenHeight == 0) updateScreenSize();
        return screenHeight;
    }

    public static double getCenterX() {
        return getScreenWidth() / 2;
    }

    public static double getCenterY() {
        return getScreenHeight() / 2;
    }
}


