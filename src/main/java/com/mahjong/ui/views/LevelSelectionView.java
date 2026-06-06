package com.mahjong.ui.views;

import com.mahjong.ui.utils.ScreenUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
/**
 * LevelSelectionView - меню выбора уровня
 */
public class LevelSelectionView extends StackPane {

    private ImageView levelDragon; //картинка уровня
    private ImageView levelPyramid; //картинка уровня
    private ImageView levelTurtle; //картинка уровня
    private ImageView levelSpider; //картинка уровня
    private ImageView backButton; //картинка кнопки "Вернуться"


    /**
     * Генератор
     */
    public LevelSelectionView() {
        createUI();
        positionUI();
    }


    /**
     * Загрузка картинок уровней и кнопок
     */
    private void createUI() {

        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        //кнопка "Назад"
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

        //кнопка "Дракон"
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

        //кнопка "Пирамида"
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

        //кнопка "Черепаха"
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/turtle.png"));
            levelTurtle = new ImageView(img);
            levelTurtle.setFitWidth(screenHeight / 2.5);
            levelTurtle.setPreserveRatio(true);
            levelTurtle.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'Черепаха' не загружена: /images/turtle.png");
            levelTurtle = new ImageView();
        }
        //кнопка "Паук"
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


    /**
     * Расстановка позиции кнопок
     */
    private void positionUI() {
        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        double centerX = screenWidth / 2;
        double centerY = screenHeight / 2;
        double offset = levelPyramid.getBoundsInLocal().getHeight() / 6;
        double offsetY = offset / 2;


        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        backButton.setTranslateX(screenWidth / 30);
        backButton.setTranslateY(screenHeight / 30);

        StackPane.setAlignment(levelDragon, Pos.TOP_LEFT);
        levelDragon.setTranslateX(centerX - levelDragon.getBoundsInLocal().getWidth() - offset);
        levelDragon.setTranslateY(centerY - levelDragon.getBoundsInLocal().getHeight() - offsetY);

        System.out.println(centerX - levelDragon.getBoundsInLocal().getWidth() - offset);

        //ряд 1, колонка 2 (правый верхний угол)
        StackPane.setAlignment(levelPyramid, Pos.TOP_LEFT);
        levelPyramid.setTranslateX(centerX + offset);
        levelPyramid.setTranslateY(centerY - levelPyramid.getBoundsInLocal().getHeight() - offsetY);

        System.out.println(centerX + offset);

        //ряд 2, колонка 1 (левый нижний угол)
        StackPane.setAlignment(levelTurtle, Pos.TOP_LEFT);
        levelTurtle.setTranslateX(centerX - levelTurtle.getBoundsInLocal().getWidth() - offset);
        levelTurtle.setTranslateY(centerY + offsetY);

        //ряд 2, колонка 2 (правый нижний угол)
        StackPane.setAlignment(levelSpider, Pos.TOP_LEFT);
        levelSpider.setTranslateX(centerX + offset);
        levelSpider.setTranslateY(centerY + offsetY);

        getChildren().addAll(levelDragon, levelPyramid, levelTurtle, levelSpider, backButton);
    }


    /**
     * Геттеры для контроллера
     */
    public ImageView getLevelDragon() { return levelDragon; }
    public ImageView getLevelPyramid() { return levelPyramid; }
    public ImageView getLevelTurtle() { return levelTurtle; }
    public ImageView getLevelSpider() { return levelSpider; }
    public ImageView getBackButton() { return backButton; }
}

