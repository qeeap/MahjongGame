package com.mahjong.ui.views;

import com.mahjong.model.Tile;
import com.mahjong.ui.utils.ScreenUtils;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class GameFieldView extends Pane {

    private ImageView backButton;
    private static final double TILE_WIDTH = 60;
    private static final double TILE_HEIGHT = 80;

    private List<Tile> tiles;

    public GameFieldView() {
        createUI();
        positionUI();
    }

    private void createUI() {

        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        // Кнопка "Назад"
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/back.png"));
            backButton = new ImageView(img);
            backButton.setFitWidth(screenWidth / 15);
            backButton.setPreserveRatio(true);
            backButton.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'Назад' не загружена: /images/back.png");
            backButton = new ImageView();
        }
    }

    private void positionUI() {

        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        backButton.setTranslateX(screenWidth / 30);
        backButton.setTranslateY(screenHeight / 30);

        getChildren().add(backButton);
    }

    public ImageView getBackButton() { return backButton; }
}