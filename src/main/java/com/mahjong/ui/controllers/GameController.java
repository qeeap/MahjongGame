package com.mahjong.ui.controllers;

import com.mahjong.model.Board;
import com.mahjong.model.Tile;
import com.mahjong.ui.views.GameFieldView;
import javafx.scene.layout.StackPane;

import java.util.List;

public class GameController {

    private GameFieldView gameFieldView;
    private String currentLevel;
    private Runnable onBackCallback;  // колбэк для возврата в меню
    private Board currentBoard;

    public GameController(StackPane root, Runnable onBackCallback) {
        this.gameFieldView = new GameFieldView();
        this.onBackCallback = onBackCallback;

        root.getChildren().add(gameFieldView);
        setupButtons();
    }

    private void setupButtons() {
        gameFieldView.getBackButton().setOnMouseClicked(event -> {
            System.out.println("Возврат в выбор уровней");
            if (onBackCallback != null) {
                onBackCallback.run();
            }
        });
    }

    public void loadBoard(Board board) {
        this.currentBoard = board;
        gameFieldView.renderBoard(board);

        // Выводим информацию о загруженной доске
        System.out.println("Загружена доска: " + board.getActiveCount() + " активных плиток");
    }

    public void show() {
        gameFieldView.setVisible(true);
    }

    public void hide() {
        gameFieldView.setVisible(false);
    }
}

