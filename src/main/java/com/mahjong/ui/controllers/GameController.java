package com.mahjong.ui.controllers;

import com.mahjong.logic.GameEngine;
import com.mahjong.model.Board;
import com.mahjong.model.Tile;
import com.mahjong.ui.views.GameFieldView;
import javafx.scene.layout.StackPane;

/**
 * Контроллер игры
 */
public class GameController {

    private GameFieldView gameFieldView; //представление игры
    private String currentLevel; //текущий уровень
    private Runnable onBackCallback;  // колбэк для возврата в меню
    private Board currentBoard; //текущая доская с плитками
    private Tile selectedTile = null; //выбранная плитка


    /**
     * Конструктор контроллера.
     * Создаёт игровое поле и настраивает обработчики кнопок.
     *
     * @param root - корневой контейнер, в который будет добавлено игровое поле
     * @param onBackCallback - действие, выполняемое при возврате в меню выбора уровней
     */
    public GameController(StackPane root, Runnable onBackCallback) {
        this.gameFieldView = new GameFieldView();
        this.onBackCallback = onBackCallback;

        root.getChildren().add(gameFieldView);
        setupButtons();
    }

    /**
     * Настраивает обработчики для кнопок:
     * Кнопка "Назад" - возврат в меню выбора уровней
     * Клик по плитке - обработка выбора/удаления плитки
     */
    private void setupButtons() {
        gameFieldView.getBackButton().setOnMouseClicked(event -> {
            System.out.println("Возврат в выбор уровней");
            if (onBackCallback != null) {
                onBackCallback.run();
            }
        });

        gameFieldView.setOnTileClick(this::handleTileClick);
    }


    /**
     * Загружает игровую доску и отображает её на экране.
     * Сбрасывает состояние выбранной плитки и границы доски.
     *
     * @param board доска с плитками (загруженная из JSON)
     */
    public void loadBoard(Board board) {
        this.currentBoard = board;
        gameFieldView.resetBounds();
        this.selectedTile = null;
        gameFieldView.renderBoard(board);

        System.out.println("Загружена доска: " + board.getActiveCount() + " активных плиток");
    }


    /**
     * Основная логика обработки клика по плитке.
     * <p>
     * Алгоритм:
     * <ol>
     *   <li>Если плитка уже удалена — игнорируем клик</li>
     *   <li>Если плитка заблокирована  — снимаем выделение</li>
     *   <li>Если нет выбранной плитки — выбираем текущую и затемняем её</li>
     *   <li>Если выбрана та же плитка — снимаем выделение</li>
     *   <li>Если выбрана другая плитка — проверяем, образуют ли они пару</li>
     *   <li>Если пара подходит — удаляем обе плитки</li>
     *   <li>После удаления проверяем победу или наличие доступных ходов</li>
     *   <li>Если пара не подходит — просто снимаем выделение</li>
     * </ol>
     *
     * @param clickedTile плитка, по которой кликнул игрок
     */
    private void handleTileClick(Tile clickedTile) {
        if (clickedTile.isRemoved()) {
            System.out.println("Плитка уже удалена");
            return;
        }

        if (!GameEngine.isTileFree(currentBoard, clickedTile)) {
            System.out.println("Плитка заблокирована! Нельзя выбрать.");
            clearSelection();
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
            System.out.println("Пара найдена! Удаляем:");
            System.out.println("  - " + selectedTile.getImageName());
            System.out.println("  - " + clickedTile.getImageName());

            selectedTile.setRemoved(true);
            clickedTile.setRemoved(true);

            clearSelection();
            gameFieldView.renderBoard(currentBoard);

            //победа
            if (currentBoard.getActiveCount() == 0) {
                System.out.println("Все плитки удалены!");
                showVictoryAndExit();
                return;
            }

            //проигрыш
            if (!GameEngine.hasAnyMove(currentBoard)) {
                System.out.println("Игра окончена");
                showGameOverAndExit();
                return;
            }
        } else {
            System.out.println("Плитки не совпадают или недоступны");
            clearSelection();
        }
    }


    /**
     * Обрабатывает победу:
     * Выводит сообщение в консоль
     * Возвращает игрока в меню выбора уровней
     * <p>
     * TODO: в будущем добавить отображение графического экрана победы
     */
    private void showVictoryAndExit() {
        System.out.println("Возврат в выбор уровней...");
        // TODO: позже добавить картинку с победой
        if (onBackCallback != null) {
            onBackCallback.run();
        }
    }


    /**
     * Обрабатывает поражение:
     * Выводит сообщение в консоль
     * Возвращает игрока в меню выбора уровней
     * <p>
     * TODO: в будущем добавить отображение графического экрана поражения
     */
    private void showGameOverAndExit() {
        System.out.println("Нет доступных ходов. Возврат в выбор уровней...");
        // TODO: позже добавишь картинку с проигрышем
        if (onBackCallback != null) {
            onBackCallback.run();
        }
    }


    /**
     * Снимает выделение с текущей выбранной плитки.
     * Убирает затемнение и обнуляет переменную selectedTile.
     */
    private void clearSelection() {
        if (selectedTile != null) {
            gameFieldView.setTileDarkened(selectedTile, false);
            selectedTile = null;
        }
    }


    /**
     * Показывает игровое поле (делает его видимым).
     * Используется при переключении между экранами.
     */
    public void show() {
        gameFieldView.setVisible(true);
    }


    /**
     * Скрывает игровое поле.
     * Используется при переключении между экранами.
     */
    public void hide() {
        gameFieldView.setVisible(false);
    }
}

