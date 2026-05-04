package com.mahjong.ui.controllers;

import com.mahjong.ui.views.MainView;
import com.mahjong.ui.views.LevelSelectionView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController {

    private StackPane root;
    private MainView mainView;
    private LevelSelectionView levelSelectView;

    public MainController(StackPane root) {
        this.root = root;
        this.mainView = new MainView();
        this.levelSelectView = new LevelSelectionView();

        init();
    }

    private void init() {
        root.getChildren().addAll(mainView, levelSelectView);
        showMainMenu();
        setupButtons();
    }

    private void setupButtons() {
        mainView.getPlayButton().setOnMouseClicked(event -> {
            System.out.println("Показываем выбор уровня");
            showLevelSelection();
        });

        mainView.getExitButton().setOnMouseClicked(event -> {
            System.out.println("Выход из игры");
            Stage stage = (Stage) mainView.getScene().getWindow();
            stage.close();
        });

        levelSelectView.getLevelDragon().setOnMouseClicked(event -> {
            System.out.println("Выбран уровень: Черепашка 🐢");
            startGame("turtle");
        });

        levelSelectView.getLevelPyramid().setOnMouseClicked(event -> {
            System.out.println("Выбран уровень: Змея 🐍");
            startGame("snake");
        });

        levelSelectView.getLevelButterfly().setOnMouseClicked(event -> {
            System.out.println("Выбран уровень: Дракон 🐉");
            startGame("dragon");
        });

        levelSelectView.getLevelSpider().setOnMouseClicked(event -> {
            System.out.println("Выбран уровень: Бабочка 🦋");
            startGame("butterfly");
        });

        levelSelectView.getBackButton().setOnMouseClicked(event -> {
            System.out.println("Возврат в главное меню");
            showMainMenu();
        });
    }

    private void showMainMenu() {
        mainView.setVisible(true);
        levelSelectView.setVisible(false);
    }

    private void showLevelSelection() {
        mainView.setVisible(false);
        levelSelectView.setVisible(true);
    }

    private void startGame(String level) {
        System.out.println("Запуск игры на уровне: " + level);
        // TODO: переход на игровое поле
    }
}