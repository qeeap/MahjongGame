package com.mahjong.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Screen;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainPage extends Application {

    private ImageView backgroundView;
    private double windowWidth;
    private double windowHeight;

    @Override
    public void start(Stage primaryStage) {

        System.out.println("=== ДИАГНОСТИКА ЗАГРУЗКИ КАРТИНКИ ===");

        // 1. Показываем, где мы находимся
        String currentDir = System.getProperty("user.dir");
        System.out.println("📁 Текущая директория: " + currentDir);

        // 2. Пробуем найти файл в файловой системе (для проверки)
        Path possiblePath = Paths.get(currentDir, "src", "main", "resources", "images", "mainpage_back.jpg");
        System.out.println("📁 Ожидаемый путь в файловой системе: " + possiblePath);
        System.out.println("📁 Файл существует: " + Files.exists(possiblePath));

        // 3. Пробуем загрузить разными способами
        Image backgroundImage = null;

        // Способ 1: твой текущий путь
        System.out.println("\n🔍 Способ 1: /images/mainpage_back.jpg");
        try (InputStream is = getClass().getResourceAsStream("/images/mainpage_back.jpg")) {
            if (is != null) {
                System.out.println("   InputStream найден! Размер: " + is.available() + " байт");
                backgroundImage = new Image(is);
                System.out.println("   ✅ Загружено!");
            } else {
                System.out.println("   ❌ InputStream = null (файл не найден)");
            }
        } catch (Exception e) {
            System.out.println("   ❌ Ошибка: " + e.getMessage());
        }

        // Способ 2: без слеша в начале
        if (backgroundImage == null || backgroundImage.getWidth() == 0) {
            System.out.println("\n🔍 Способ 2: images/mainpage_back.jpg");
            try (InputStream is = getClass().getResourceAsStream("images/mainpage_back.jpg")) {
                if (is != null) {
                    System.out.println("   InputStream найден!");
                    backgroundImage = new Image(is);
                    System.out.println("   ✅ Загружено!");
                } else {
                    System.out.println("   ❌ InputStream = null");
                }
            } catch (Exception e) {
                System.out.println("   ❌ Ошибка: " + e.getMessage());
            }
        }

        // Способ 3: проверяем, что вообще есть в resources
        if (backgroundImage == null || backgroundImage.getWidth() == 0) {
            System.out.println("\n🔍 Способ 3: Смотрим, что есть в resources");
            // Пробуем найти корень resources
            URL rootUrl = getClass().getResource("/");
            if (rootUrl != null) {
                System.out.println("   Корень resources: " + rootUrl.getPath());
            }

            // Пробуем найти папку images
            URL imagesUrl = getClass().getResource("/images/");
            if (imagesUrl != null) {
                System.out.println("   Папка /images/ найдена: " + imagesUrl.getPath());
            } else {
                System.out.println("   ❌ Папка /images/ не найдена!");
            }

            // Пробуем найти любой файл в папке images
            try {
                URL anyFile = getClass().getResource("/images/");
                if (anyFile != null) {
                    Path imagesPath = Paths.get(anyFile.toURI());
                    System.out.println("   Содержимое папки images:");
                    Files.list(imagesPath).forEach(path -> {
                        System.out.println("      - " + path.getFileName());
                    });
                }
            } catch (Exception e) {
                System.out.println("   Не удалось прочитать папку: " + e.getMessage());
            }
        }

        // Проверка результата
        if (backgroundImage == null || backgroundImage.getWidth() == 0) {
            System.err.println("\n❌ НЕ УДАЛОСЬ ЗАГРУЗИТЬ КАРТИНКУ!");
            System.err.println("\nПроверь:");
            System.err.println("1. Точное имя файла (с учетом регистра)");
            System.err.println("2. Файл должен лежать в: src/main/resources/images/mainpage_back.jpg");
            System.err.println("3. После добавления файла нужно пересобрать проект: Maven → Reload Project");
            System.err.println("4. Или: Build → Rebuild Project");
            return;
        }

        System.out.println("\n✅ КАРТИНКА УСПЕШНО ЗАГРУЖЕНА!");
        System.out.println("📷 Размер: " + (int)backgroundImage.getWidth() + " x " + (int)backgroundImage.getHeight());

        backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(true);

        StackPane root = new StackPane();
        root.getChildren().add(backgroundView);

        Scene scene = new Scene(root);

        windowWidth = Screen.getPrimary().getVisualBounds().getWidth();
        windowHeight = Screen.getPrimary().getVisualBounds().getHeight();
        System.out.println("🖥️ Размер экрана: " + (int)windowWidth + " x " + (int)windowHeight);

        primaryStage.setTitle("Mahjong Game");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();

        setViewportForMainScreen();

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            windowWidth = newVal.doubleValue();
            updateViewport();
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            windowHeight = newVal.doubleValue();
            updateViewport();
        });
    }

    private void setViewportForMainScreen() {
        backgroundView.setViewport(new javafx.geometry.Rectangle2D(
                0, 0, windowWidth, windowHeight
        ));
        System.out.println("📐 Viewport установлен");
    }

    private void updateViewport() {
        javafx.geometry.Rectangle2D current = backgroundView.getViewport();
        if (current != null) {
            backgroundView.setViewport(new javafx.geometry.Rectangle2D(
                    current.getMinX(), current.getMinY(), windowWidth, windowHeight
            ));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}