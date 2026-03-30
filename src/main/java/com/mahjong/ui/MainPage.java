package com.mahjong.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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

    // Элементы главного меню
    private Text title;
    private Button playButton;
    private Button exitButton;

    // Кнопки выбора уровня
    private Button level1Button;
    private Button level2Button;
    private Button level3Button;
    private Button level4Button;

    // Контейнер
    private StackPane root;

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

        // 5. Создаем все элементы интерфейса
        createUI();

        // 6. Создаем контейнер и добавляем все элементы
        root = new StackPane();
        root.getChildren().addAll(backgroundView, title, playButton, exitButton,
                level1Button, level2Button, level3Button, level4Button);

        // 7. Позиционируем все элементы
        positionUI();

        // 8. Изначально показываем только главное меню
        showMainMenu();

        // 9. Создаем сцену на весь экран
        Scene scene = new Scene(root, screenWidth, screenHeight);

        // 10. Настраиваем окно
        primaryStage.setTitle("Mahjong Game");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
    }

    /**
     * Создает все элементы интерфейса
     */
    private void createUI() {
        // Заголовок
        title = new Text("PEAKME MAHJONG");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setFill(Color.ALICEBLUE);
        title.setStroke(Color.BLACK);
        title.setStrokeWidth(1);

        // Кнопки главного меню
        playButton = new Button("Играть");
        exitButton = new Button("Выход");

        playButton.setPrefWidth(300);
        playButton.setPrefHeight(40);
        exitButton.setPrefWidth(300);
        exitButton.setPrefHeight(40);

        // Кнопки выбора уровня
        level1Button = new Button("🐢 Черепашка");
        level2Button = new Button("🐍 Змея");
        level3Button = new Button("🐉 Дракон");
        level4Button = new Button("🦋 Бабочка");

        level1Button.setPrefWidth(250);
        level1Button.setPrefHeight(60);
        level2Button.setPrefWidth(250);
        level2Button.setPrefHeight(60);
        level3Button.setPrefWidth(250);
        level3Button.setPrefHeight(60);
        level4Button.setPrefWidth(250);
        level4Button.setPrefHeight(60);

        // Обработчики для кнопок главного меню
        playButton.setOnAction(event -> {
            System.out.println("Нажата кнопка ИГРАТЬ - показываем выбор уровня");
            showLevelSelection();
        });

        exitButton.setOnAction(event -> {
            System.out.println("Нажата кнопка ВЫХОД");
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        });

        // Обработчики для кнопок выбора уровня
        level1Button.setOnAction(event -> {
            System.out.println("Выбран уровень: Черепашка 🐢");
            startGame("turtle");
        });

        level2Button.setOnAction(event -> {
            System.out.println("Выбран уровень: Змея 🐍");
            startGame("snake");
        });

        level3Button.setOnAction(event -> {
            System.out.println("Выбран уровень: Дракон 🐉");
            startGame("dragon");
        });

        level4Button.setOnAction(event -> {
            System.out.println("Выбран уровень: Бабочка 🦋");
            startGame("butterfly");
        });
    }

    /**
     * Позиционирует все элементы на экране
     */
    private void positionUI() {
        // Заголовок - вверху по центру
        StackPane.setAlignment(title, javafx.geometry.Pos.TOP_CENTER);
        title.setTranslateY(screenHeight / 3);

        // Кнопки главного меню
        StackPane.setAlignment(playButton, javafx.geometry.Pos.TOP_CENTER);
        playButton.setTranslateY(screenHeight * 15 / 20);

        StackPane.setAlignment(exitButton, javafx.geometry.Pos.TOP_CENTER);
        exitButton.setTranslateY(screenHeight * 16 / 20);

        // Кнопки выбора уровня - располагаем в сетке 2x2
        double centerX = screenWidth / 2;
        double startY = screenHeight / 3;
        double offsetX = 280;
        double offsetY = 100;

        // Кнопка 1: левый верхний угол
        StackPane.setAlignment(level1Button, javafx.geometry.Pos.TOP_LEFT);
        level1Button.setTranslateX(centerX - offsetX);
        level1Button.setTranslateY(startY);

        // Кнопка 2: правый верхний угол
        StackPane.setAlignment(level2Button, javafx.geometry.Pos.TOP_LEFT);
        level2Button.setTranslateX(centerX + offsetX - level2Button.getPrefWidth());
        level2Button.setTranslateY(startY);

        // Кнопка 3: левый нижний угол
        StackPane.setAlignment(level3Button, javafx.geometry.Pos.TOP_LEFT);
        level3Button.setTranslateX(centerX - offsetX);
        level3Button.setTranslateY(startY + offsetY);

        // Кнопка 4: правый нижний угол
        StackPane.setAlignment(level4Button, javafx.geometry.Pos.TOP_LEFT);
        level4Button.setTranslateX(centerX + offsetX - level4Button.getPrefWidth());
        level4Button.setTranslateY(startY + offsetY);
    }

    /**
     * Показывает главное меню (скрывает выбор уровня)
     */
    private void showMainMenu() {
        title.setVisible(true);
        playButton.setVisible(true);
        exitButton.setVisible(true);

        level1Button.setVisible(false);
        level2Button.setVisible(false);
        level3Button.setVisible(false);
        level4Button.setVisible(false);
    }

    /**
     * Показывает выбор уровня (скрывает главное меню)
     */
    private void showLevelSelection() {
        title.setVisible(false);
        playButton.setVisible(false);
        exitButton.setVisible(false);

        level1Button.setVisible(true);
        level2Button.setVisible(true);
        level3Button.setVisible(true);
        level4Button.setVisible(true);
    }


    /**
     * Запускает игру с выбранным уровнем
     */
    private void startGame(String level) {
        System.out.println("Запуск игры на уровне: " + level);
        // Здесь будет переход на игровое поле
        // TODO: создать и показать игровое поле
    }

    /**
     * Позиционирует картинку так, чтобы точка (TARGET_X, TARGET_Y)
     * на уменьшенной картинке оказалась в центре экрана
     */
    private void positionImage() {
        double translateX = (screenWidth / 2) - TARGET_X;
        double translateY = (screenHeight / 2) - TARGET_Y;

        backgroundView.setTranslateX(translateX);
        backgroundView.setTranslateY(translateY);

        System.out.println("\n=== ПОЗИЦИОНИРОВАНИЕ ===");
        System.out.println("Центр экрана: (" + (int)(screenWidth/2) + ", " + (int)(screenHeight/2) + ")");
        System.out.println("Твоя точка на картинке: (" + TARGET_X + ", " + TARGET_Y + ")");
        System.out.println("Сдвиг картинки: (" + (int)translateX + ", " + (int)translateY + ")");
    }

    public static void main(String[] args) {
        launch(args);
    }
}