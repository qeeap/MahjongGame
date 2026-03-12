package com.mahjong.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Создаем текст
        Text welcomeText = new Text("Добро пожаловать в Маджонг!");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        welcomeText.setFill(Color.WHITE); // Белый текст

        // Создаем контейнер с фоном
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #2C3E50;"); // Темно-синий фон
        root.getChildren().add(welcomeText);

        // Создаем сцену
        Scene scene = new Scene(root);

        // Настраиваем окно
        primaryStage.setTitle("Маджонг");
        primaryStage.setScene(scene);

        // На весь экран
        primaryStage.setFullScreen(true);
        // Или если хочешь именно максимизированное окно (не fullscreen):
        // primaryStage.setMaximized(true);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}