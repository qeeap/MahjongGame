package com.mahjong.ui.controllers;

import com.mahjong.model.Board;
import com.mahjong.ui.views.GameFieldView;
import com.mahjong.ui.views.MainView;
import com.mahjong.ui.views.LevelSelectionView;
import com.mahjong.ui.utils.GameSave;
import com.mahjong.utils.LevelLoader;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController {

    private StackPane root;
    private MainView mainView;
    private LevelSelectionView levelSelectView;
    private GameFieldView gameFieldMenu;
    private GameController gameController;

    public MainController(StackPane root) {
        this.root = root;
        this.mainView = new MainView();
        this.levelSelectView = new LevelSelectionView();
        this.gameFieldMenu = new GameFieldView();
        this.gameController = new GameController(root, this::showLevelSelection);

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
            System.out.println("Выбран уровень: Дракон");
            startGame("dragon");  // ИСПРАВЛЕНО: было "test"
        });

        levelSelectView.getLevelPyramid().setOnMouseClicked(event -> {
            System.out.println("Выбран уровень: Пирамида");
            startGame("pyramid");
        });

        levelSelectView.getLevelTurtle().setOnMouseClicked(event -> {
            System.out.println("Выбран уровень: Черепаха");
            startGame("turtle");
        });

        levelSelectView.getLevelSpider().setOnMouseClicked(event -> {
            System.out.println("Выбран уровень: Паук");
            startGame("spider");
        });

        levelSelectView.getBackButton().setOnMouseClicked(event -> {
            System.out.println("Возврат в главное меню");
            showMainMenu();
        });

        gameFieldMenu.getBackButton().setOnMouseClicked(event -> {
            System.out.println("Возврат в выбор уровней");
            showLevelSelection();
        });
    }

    private void showMainMenu() {
        mainView.setVisible(true);
        levelSelectView.setVisible(false);
        gameController.hide();
    }

    private void showLevelSelection() {
        mainView.setVisible(false);
        levelSelectView.setVisible(true);
        gameController.hide();
    }

    private void startGame(String levelFileName) {
        String jsonPath = "levels/" + levelFileName + ".json";
        System.out.println("Загрузка уровня: " + jsonPath);

        // ПРОВЕРЯЕМ, ЕСТЬ ЛИ СОХРАНЕНИЕ ДЛЯ ЭТОГО УРОВНЯ
        if (GameSave.hasSave(levelFileName)) {
            System.out.println("Найдено сохранение для уровня " + levelFileName);
            // Загружаем без board (будет восстановлено из сохранения в GameController)
            gameController.loadBoard(null, levelFileName);
        } else {
            // Новый уровень - загружаем из JSON
            Board board = LevelLoader.loadLevel(jsonPath);
            if (board == null || board.getActiveCount() == 0) {
                System.err.println("Ошибка: уровень не загружен!");
                return;
            }
            gameController.loadBoard(board, levelFileName);
        }

        mainView.setVisible(false);
        levelSelectView.setVisible(false);
        gameController.show();
    }
}