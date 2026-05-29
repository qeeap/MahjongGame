package com.mahjong.ui;

import com.mahjong.ui.controllers.MainController;
import com.mahjong.ui.utils.ScreenUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainPage extends Application {

    private static final double TARGET_X = 950;
    private static final double TARGET_Y = 350;
    private static final double IMAGE_WIDTH = 2300;

    @Override
    public void start(Stage primaryStage) {

        Image mainPage = new Image(
                getClass().getResourceAsStream("/images/mainpage_back.jpg")
        );

        if (mainPage == null) {
            System.err.println("Ошибка: фон не найден!");
            return;
        }

        ScreenUtils.updateScreenSize();
        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        ImageView backgroundView = new ImageView(mainPage);
        backgroundView.setFitWidth(IMAGE_WIDTH);
        backgroundView.setPreserveRatio(true);

        double translateX = (screenWidth / 2) - TARGET_X;
        double translateY = (screenHeight / 2) - TARGET_Y;
        backgroundView.setTranslateX(translateX);
        backgroundView.setTranslateY(translateY);

        StackPane root = new StackPane();
        root.getChildren().add(backgroundView);

        MainController controller = new MainController(root);

        Scene scene = new Scene(root, screenWidth, screenHeight);

        System.out.println("Ширина: " + screenWidth + ", Высота: " + screenHeight);

        primaryStage.setTitle("Mahjong Game");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
