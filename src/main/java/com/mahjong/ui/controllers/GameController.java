package com.mahjong.ui.controllers;

import com.mahjong.logic.GameEngine;
import com.mahjong.model.Board;
import com.mahjong.model.Tile;
import com.mahjong.ui.views.GameFieldView;
import com.mahjong.ui.views.GameResultView;
import com.mahjong.ui.utils.GameSave;
import com.mahjong.utils.LevelLoader;
import javafx.scene.layout.StackPane;

public class GameController {

    private GameFieldView gameFieldView;
    private GameResultView gameResultView;
    private String currentLevel;
    private Runnable onBackCallback;
    private Board currentBoard;
    private Tile selectedTile = null;

    private int score = 0;
    private int comboCounter = 0;
    private int maxCombo = 0;

    private static final int BASE_POINTS = 150;
    private static final int BONUS_PER_COMBO = 50;

    public GameController(StackPane root, Runnable onBackCallback) {
        this.gameFieldView = new GameFieldView();
        this.gameResultView = new GameResultView();
        this.onBackCallback = onBackCallback;

        root.getChildren().addAll(gameFieldView, gameResultView);
        setupButtons();
        setupResultView();
    }

    private void setupButtons() {
        gameFieldView.getBackButton().setOnMouseClicked(event -> {
            System.out.println("Возврат в выбор уровней");

            if (currentBoard != null && currentBoard.getActiveCount() > 0) {
                GameSave.save(currentBoard, currentLevel, score, comboCounter, maxCombo, LevelLoader.getCurrentSeed());
                System.out.println("Прогресс уровня '" + currentLevel + "' сохранён");
            }

            if (onBackCallback != null) {
                onBackCallback.run();
            }
        });

        gameFieldView.setOnTileClick(this::handleTileClick);
    }

    private void setupResultView() {
        gameResultView.setOnRetry(() -> {
            System.out.println("Перезапуск уровня");
            gameResultView.hide();
            restartLevel();
        });

        gameResultView.setOnMenu(() -> {
            System.out.println("Возврат в меню выбора уровней");
            gameResultView.hide();
            if (onBackCallback != null) {
                onBackCallback.run();
            }
        });
    }

    public void loadBoard(Board board, String levelName) {
        this.currentLevel = levelName;

        GameSave.SaveData saveData = GameSave.load(levelName);

        if (saveData != null) {
            System.out.println("Найдено сохранение для уровня '" + levelName + "'! Восстанавливаем...");

            if (board == null) {
                board = LevelLoader.loadLevel("levels/" + levelName + ".json");
                if (board == null) {
                    System.err.println("Ошибка: не удалось загрузить уровень " + levelName);
                    return;
                }
            }

            this.currentBoard = board;

            for (int i = 0; i < board.getAllTiles().size() && i < saveData.tiles.size(); i++) {
                Tile tile = board.getAllTiles().get(i);
                GameSave.TileData savedTile = saveData.tiles.get(i);
                tile.setImageName(savedTile.imageName);
                tile.setRemoved(savedTile.isRemoved);
            }

            this.score = saveData.score;
            this.comboCounter = saveData.comboCounter;
            this.maxCombo = saveData.maxCombo;
            gameFieldView.updateScore(score);

            System.out.println("✅ Восстановлено! Счёт: " + score + ", Комбо: " + comboCounter);
        } else {
            System.out.println("📀 Новый уровень: " + levelName);

            if (board == null) {
                board = LevelLoader.loadLevel("levels/" + levelName + ".json");
                if (board == null) {
                    System.err.println("Ошибка: не удалось загрузить уровень " + levelName);
                    return;
                }
            }

            this.currentBoard = board;
            this.score = 0;
            this.comboCounter = 0;
            this.maxCombo = 0;
            gameFieldView.updateScore(0);
        }

        gameFieldView.resetBounds();
        this.selectedTile = null;
        gameFieldView.renderBoard(this.currentBoard);
        gameResultView.hide();

        System.out.println("Загружена доска: " + this.currentBoard.getActiveCount() + " активных плиток");
    }

    public void restartLevel() {
        System.out.println("Перезапуск уровня: " + currentLevel);
        GameSave.delete(currentLevel);

        Board newBoard = LevelLoader.loadLevel("levels/" + currentLevel + ".json");
        if (newBoard != null) {
            this.currentBoard = newBoard;
            loadBoard(newBoard, currentLevel);
        } else {
            System.err.println("Ошибка перезагрузки уровня: " + currentLevel);
        }
    }

    private void addPointsForPair() {
        int pointsEarned = BASE_POINTS + (comboCounter * BONUS_PER_COMBO);
        score += pointsEarned;
        comboCounter++;

        System.out.println("+ " + pointsEarned + " очков");
        System.out.println("Всего очков: " + score);
        System.out.println("Комбо: " + comboCounter + " пар подряд");

        gameFieldView.updateScore(score);
    }

    private void resetCombo() {
        if (comboCounter > 0) {
            System.out.println("Комбо прервано. Было: " + comboCounter + " пар подряд");
            if (comboCounter > maxCombo) {
                maxCombo = comboCounter;
            }
            System.out.println("Макс комбо:" + maxCombo);
            comboCounter = 0;
        }
    }

    private void handleTileClick(Tile clickedTile) {
        if (clickedTile.isRemoved()) {
            System.out.println("Плитка уже удалена");
            return;
        }

        if (!GameEngine.isTileFree(currentBoard, clickedTile)) {
            System.out.println("Плитка заблокирована");
            clearSelection();
            resetCombo();
            return;
        }

        if (selectedTile == null) {
            selectedTile = clickedTile;
            gameFieldView.setTileDarkened(selectedTile, true);
            System.out.println("Выбрана плитка: " + selectedTile.getImageName());
            return;
        }

        if (selectedTile == clickedTile) {
            clearSelection();
            return;
        }

        if (GameEngine.canFormPair(currentBoard, selectedTile, clickedTile)) {
            System.out.println("Пара найдена. Удаляем:");
            System.out.println("  - " + selectedTile.getImageName());
            System.out.println("  - " + clickedTile.getImageName());

            boolean pairRemoved = currentBoard.removePair(selectedTile.getId(), clickedTile.getId());

            if (pairRemoved) {
                addPointsForPair();
                clearSelection();
                gameFieldView.renderBoard(currentBoard);

                GameSave.save(currentBoard, currentLevel, score, comboCounter, maxCombo, LevelLoader.getCurrentSeed());

                if (currentBoard.getActiveCount() == 0) {
                    System.out.println("Все плитки удалены");
                    System.out.println("Итоговый счёт: " + score);
                    GameSave.delete(currentLevel);
                    showVictory();
                    return;
                }

                if (!GameEngine.hasAnyMove(currentBoard)) {
                    System.out.println("Игра окончена");
                    System.out.println("Итоговый счёт: " + score);
                    GameSave.delete(currentLevel);
                    showGameOver();
                    return;
                }
            } else {
                System.out.println("Не удалось удалить пару");
                resetCombo();
                clearSelection();
            }
        } else {
            System.out.println("Плитки не совпадают или недоступны");
            resetCombo();
            clearSelection();
        }
    }

    private void showVictory() {
        System.out.println("Победа");
        gameResultView.showVictory(score, maxCombo);
    }

    private void showGameOver() {
        System.out.println("Поражение");
        gameResultView.showGameOver(score, maxCombo);
    }

    private void clearSelection() {
        if (selectedTile != null) {
            gameFieldView.setTileDarkened(selectedTile, false);
            selectedTile = null;
        }
    }

    public void show() {
        gameFieldView.setVisible(true);
    }

    public void hide() {
        gameFieldView.setVisible(false);
    }

    public int getScore() { return score; }
    public int getComboCounter() { return comboCounter; }
    public int getMaxCombo() { return maxCombo; }
}