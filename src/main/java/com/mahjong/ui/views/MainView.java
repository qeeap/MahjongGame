package com.mahjong.ui.views;

import com.mahjong.ui.utils.ScreenUtils;

import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * MainView - главный экран входа
 */
public class MainView extends StackPane{
    private ImageView logoView; //картинка лого
    private ImageView playButton; //картинка кнопки "Играть"
    private ImageView exitButton; //картинка кнопки "Выход"


    /**
     * Генератор
     */
    public MainView() {
        createUI();
        positionUI();
    }


    /**
     * Загрузка картинок и их обработка при нажатии
     */
    private void createUI() {

        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        //логотип
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/gamename.png"));
            logoView = new ImageView(logo);
            logoView.setFitWidth(screenWidth * 3 / 5);
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Логотип не загружен: /images/gamename.png");
            logoView = new ImageView();
        }

        //кнопка "Играть"
        try {
            Image playImg = new Image(getClass().getResourceAsStream("/images/start.png"));
            playButton = new ImageView(playImg);
            playButton.setFitWidth(screenWidth / 5);
            playButton.setPreserveRatio(true);
            playButton.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'Играть' не загружена: /images/start.png");
            playButton = new ImageView();
        }

        //кнопка "Выход"
        try {
            Image exitImg = new Image(getClass().getResourceAsStream("/images/exit.png"));
            exitButton = new ImageView(exitImg);
            exitButton.setFitWidth(screenWidth / 5);
            exitButton.setPreserveRatio(true);
            exitButton.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'Выход' не загружена: /images/exit.png");
            exitButton = new ImageView();
        }

        playButton.setOnMouseClicked(event -> {
            System.out.println("Нажата кнопка ИГРАТЬ");
        });

        exitButton.setOnMouseClicked(event -> {
            System.out.println("Нажата кнопка ВЫХОД");
            javafx.application.Platform.exit();
        });
    }


    /**
     * Расстановка позиций
     */
    private void positionUI() {
        double screenHeight = ScreenUtils.getScreenHeight();

        StackPane.setAlignment(logoView, javafx.geometry.Pos.TOP_CENTER);
        logoView.setTranslateY(screenHeight / 4);

        //кнопка "Играть"
        StackPane.setAlignment(playButton, javafx.geometry.Pos.TOP_CENTER);
        playButton.setTranslateY(screenHeight * 14 / 20);

        //кнопка "Выход"
        StackPane.setAlignment(exitButton, javafx.geometry.Pos.TOP_CENTER);
        exitButton.setTranslateY(screenHeight * 16 / 20);

        getChildren().addAll(logoView, playButton, exitButton);
    }


    /**
     * Геттеры для контроллера
     */
    public ImageView getPlayButton() { return playButton; }
    public ImageView getExitButton() { return exitButton; }
}
