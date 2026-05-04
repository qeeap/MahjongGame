package com.mahjong.ui.views;

import com.mahjong.ui.utils.ScreenUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;

public class LevelSelectionView extends StackPane {

    private ImageView levelDragon;
    private ImageView levelPyramid;
    private ImageView levelButterfly;
    private ImageView levelSpider;
    private ImageView backButton;

    public LevelSelectionView() {
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

        // Кнопка "Дракон"
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/dragon.png"));
            levelDragon = new ImageView(img);
            levelDragon.setFitWidth(screenHeight / 2.5);
            levelDragon.setPreserveRatio(true);
            levelDragon.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'Дракон' не загружена: /images/dragon.png");
            levelDragon = new ImageView();
        }

        // Кнопка "Пирамида"
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/pyramid.png"));
            levelPyramid = new ImageView(img);
            levelPyramid.setFitWidth(screenHeight / 2.5);
            levelPyramid.setPreserveRatio(true);
            levelPyramid.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'Пирамида' не загружена: /images/pyramid.png");
            levelPyramid = new ImageView();
        }

        // Кнопка "Бабочка"
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/butterfly.png"));
            levelButterfly = new ImageView(img);
            levelButterfly.setFitWidth(screenHeight / 2.5);
            levelButterfly.setPreserveRatio(true);
            levelButterfly.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'Бабочка' не загружена: /images/butterfly.png");
            levelButterfly = new ImageView();
        }

        try {
            Image img = new Image(getClass().getResourceAsStream("/images/spider.png"));
            levelSpider = new ImageView(img);
            levelSpider.setFitWidth(screenHeight / 2.5);
            levelSpider.setPreserveRatio(true);
            levelSpider.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'Паук' не загружена: /images/spider.png");
            levelSpider = new ImageView();
        }
    }

    private void positionUI() {
        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        double centerX = screenWidth / 2;
        double startY = screenHeight / 3;
        double offsetX = 280;
        double offsetY = 100;

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        backButton.setTranslateX(screenWidth / 30);
        backButton.setTranslateY(screenHeight / 30);

        StackPane.setAlignment(levelDragon, Pos.TOP_LEFT);
        levelDragon.setTranslateX(centerX - offsetX);
        levelDragon.setTranslateY(startY);

        // Ряд 1, колонка 2 (правый верхний угол)
        StackPane.setAlignment(levelPyramid, Pos.TOP_LEFT);
        levelPyramid.setTranslateX(centerX + offsetX - levelPyramid.getFitWidth());
        levelPyramid.setTranslateY(startY);

        // Ряд 2, колонка 1 (левый нижний угол)
        StackPane.setAlignment(levelButterfly, Pos.TOP_LEFT);
        levelButterfly.setTranslateX(centerX - offsetX);
        levelButterfly.setTranslateY(startY + offsetY);

        // Ряд 2, колонка 2 (правый нижний угол)
        StackPane.setAlignment(levelSpider, Pos.TOP_LEFT);
        levelSpider.setTranslateX(centerX + offsetX - levelSpider.getFitWidth());
        levelSpider.setTranslateY(startY + offsetY);

        // Добавляем все элементы
        getChildren().addAll(levelDragon, levelPyramid, levelButterfly, levelSpider, backButton);
    }

    // Геттеры для контроллера
    public ImageView getLevelDragon() { return levelDragon; }
    public ImageView getLevelPyramid() { return levelPyramid; }
    public ImageView getLevelButterfly() { return levelButterfly; }
    public ImageView getLevelSpider() { return levelSpider; }
    public ImageView getBackButton() { return backButton; }
}