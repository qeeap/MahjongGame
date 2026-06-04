package com.mahjong.ui.controllers;

import com.mahjong.logic.GameEngine;
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
    private Tile selectedTile = null;

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

        gameFieldView.setOnTileClick(this::handleTileClick);
    }


    public void loadBoard(Board board) {
        this.currentBoard = board;
        this.selectedTile = null;
        gameFieldView.renderBoard(board);

        // Выводим информацию о загруженной доске
        System.out.println("Загружена доска: " + board.getActiveCount() + " активных плиток");
    }

    /**
     * 🔥 ПРОСТАЯ ОБРАБОТКА: удаляем только доступные плитки
     */
    private void handleTileClick(Tile clickedTile) {
        if (clickedTile.isRemoved()) {
            System.out.println("Плитка уже удалена");
            return;
        }

        // 🔥 ПРОВЕРЯЕМ, МОЖНО ЛИ УДАЛИТЬ ЭТУ ПЛИТКУ
        if (!GameEngine.isTileFree(currentBoard, clickedTile)) {
            System.out.println("Плитка заблокирована! Нельзя удалить.");
            return;
        }

        // Если плитка доступна — удаляем
        System.out.println("Удаляем плитку: " + clickedTile.getImageName());
        clickedTile.setRemoved(true);

        // Обновляем отображение
        gameFieldView.renderBoard(currentBoard);

        // Проверяем победу
        if (currentBoard.getActiveCount() == 0) {
            System.out.println("🎉 ПОБЕДА! Все плитки удалены!");
        }
    }

    public void show() {
        gameFieldView.setVisible(true);
    }

    public void hide() {
        gameFieldView.setVisible(false);
    }
}

