package com.mahjong.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Screen;

public class MainPage extends Application {

    // 🎯 ТВОЯ ТОЧКА на УМЕНЬШЕННОЙ картинке (координаты от левого верхнего угла)
    private static final double TARGET_X = 950;   // X координата на уменьшенной картинке
    private static final double TARGET_Y = 350;   // Y координата на уменьшенной картинке

    // Размер, до которого ты уменьшаешь картинку
    private static final double IMAGE_WIDTH = 2300;   // ширина уменьшенной картинки

    private ImageView backgroundView;
    private double imageHeight;     // высота уменьшенной картинки (вычисляется)
    private double screenWidth;     // ширина текущего экрана
    private double screenHeight;    // высота текущего экрана

    @Override
    public void start(Stage primaryStage) {

        // 1. Загружаем оригинальную картинку
        Image originalImage = new Image(
                getClass().getResourceAsStream("/images/mainpage_back.jpg")
        );

        if (originalImage == null) {
            System.err.println("Ошибка: картинка не найдена!");
            return;
        }

        // 2. Получаем размеры текущего экрана
        screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        // 3. Уменьшаем картинку до заданной ширины (2300), высота подстроится пропорционально
        backgroundView = new ImageView(originalImage);
        backgroundView.setFitWidth(IMAGE_WIDTH);
        backgroundView.setPreserveRatio(true);

        // Получаем реальную высоту после уменьшения
        imageHeight = backgroundView.getFitHeight();

        System.out.println("=== ИНФОРМАЦИЯ ===");
        System.out.println("Уменьшенная картинка: " + (int)IMAGE_WIDTH + " x " + (int)imageHeight);
        System.out.println("Твоя точка на уменьшенной картинке: (" + TARGET_X + ", " + TARGET_Y + ")");
        System.out.println("Текущий экран: " + (int)screenWidth + " x " + (int)screenHeight);

        // 4. Позиционируем картинку так, чтобы точка (TARGET_X, TARGET_Y) была в центре экрана
        positionImage();

        // 5. Контейнер
        StackPane root = new StackPane();
        root.getChildren().add(backgroundView);

        // 6. Создаем сцену на весь экран
        Scene scene = new Scene(root, screenWidth, screenHeight);

        // 7. Настраиваем окно (полноэкранный режим)
        primaryStage.setTitle("Mahjong Game");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
    }

    /**
     * Позиционирует картинку так, чтобы точка (TARGET_X, TARGET_Y)
     * на уменьшенной картинке оказалась в центре экрана
     */
    private void positionImage() {
        // Точка (TARGET_X, TARGET_Y) на уменьшенной картинке
        // Мы хотим, чтобы она оказалась в центре экрана

        // Вычисляем, на сколько нужно сдвинуть картинку
        // Центр экрана = (screenWidth/2, screenHeight/2)
        // Нам нужно, чтобы точка картинки (TARGET_X, TARGET_Y) совпала с центром экрана

        double translateX = (screenWidth / 2) - TARGET_X;
        double translateY = (screenHeight / 2) - TARGET_Y;

        backgroundView.setTranslateX(translateX);
        backgroundView.setTranslateY(translateY);

        System.out.println("\n=== ПОЗИЦИОНИРОВАНИЕ ===");
        System.out.println("Центр экрана: (" + (int)(screenWidth/2) + ", " + (int)(screenHeight/2) + ")");
        System.out.println("Твоя точка на картинке: (" + TARGET_X + ", " + TARGET_Y + ")");
        System.out.println("Сдвиг картинки: (" + (int)translateX + ", " + (int)translateY + ")");

        // Проверка: точка картинки + сдвиг = центр экрана
        System.out.println("Проверка: " + TARGET_X + " + " + (int)translateX + " = " +
                (int)(TARGET_X + translateX) + " (должно быть " + (int)(screenWidth/2) + ")");
        System.out.println("Проверка: " + TARGET_Y + " + " + (int)translateY + " = " +
                (int)(TARGET_Y + translateY) + " (должно быть " + (int)(screenHeight/2) + ")");
    }

    public static void main(String[] args) {
        launch(args);
    }
}