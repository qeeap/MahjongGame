package com.mahjong.ui.controllers;

import com.mahjong.logic.GameEngine;
import com.mahjong.model.Board;
import com.mahjong.model.Tile;
import com.mahjong.ui.views.GameFieldView;
import com.mahjong.ui.views.GameResultView;
import com.mahjong.ui.controllers.MainController;
import com.mahjong.utils.LevelLoader;
import javafx.scene.layout.StackPane;

/**
 * Контроллер игры
 */
public class GameController {

    private GameFieldView gameFieldView; //представление игры
    private GameResultView gameResultView; //представление результата
    private String currentLevel; //текущий уровень
    private Runnable onBackCallback;  // колбэк для возврата в меню
    private Board currentBoard; //текущая доская с плитками
    private Tile selectedTile = null; //выбранная плитка

    /**
     * ВАЛЕРА ЭТИ СЧЕТЧИКИ ТЕБЕ
     */
    private int score = 0; //текущий счет
    private int comboCounter = 0; //комбо-счетчик
    private int maxCombo = 0; //максимальное комбо


    private static final int BASE_POINTS = 150; //очки за собранную пару
    private static final int BONUS_PER_COMBO = 50; //бонус за комбуху еееее


    /**
     * Конструктор контроллера.
     * Создаёт игровое поле и настраивает обработчики кнопок.
     *
     * @param root - корневой контейнер, в который будет добавлено игровое поле
     * @param onBackCallback - действие, выполняемое при возврате в меню выбора уровней
     */
    public GameController(StackPane root, Runnable onBackCallback) {
        this.gameFieldView = new GameFieldView();
        this.gameResultView = new GameResultView();
        this.onBackCallback = onBackCallback;

        root.getChildren().addAll(gameFieldView, gameResultView);
        setupButtons();
        setupResultView();
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
     * Настраивает плашку результата
     */
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


    /**
     * Загружает игровую доску и отображает её на экране.
     * Сбрасывает состояние выбранной плитки и границы доски.
     *
     * @param board доска с плитками (загруженная из JSON)
     */
    public void loadBoard(Board board, String levelName) {
        this.currentBoard = board;
        this.currentLevel = levelName;
        gameFieldView.resetBounds();
        this.selectedTile = null;

        this.score = 0;
        this.comboCounter = 0;
        gameFieldView.updateScore(0);

        gameFieldView.renderBoard(board);
        gameResultView.hide();

        System.out.println("Загружена доска: " + board.getActiveCount() + " активных плиток");
    }


    /**
     * Перезапускает текущий уровень
     */
    public void restartLevel() {
        System.out.println("Перезапуск уровня: " + currentLevel);

        // Загружаем доску заново
        Board newBoard = LevelLoader.loadLevel("levels/" + currentLevel + ".json");
        if (newBoard != null) {
            loadBoard(newBoard, currentLevel);
        } else {
            System.err.println("Ошибка перезагрузки уровня: " + currentLevel);
        }
    }


    /**
     * Начисляет очки за собранную пару.
     * Формула: BASE_POINTS + (comboCounter * BONUS_PER_COMBO)
     * После начисления comboCounter увеличивается на 1.
     */
    private void addPointsForPair() {
        int pointsEarned = BASE_POINTS + (comboCounter * BONUS_PER_COMBO);
        score += pointsEarned;
        comboCounter++;

        System.out.println("+ " + pointsEarned + " очков");
        System.out.println("Всего очков: " + score);
        System.out.println("Комбо: " + comboCounter + " пар подряд");

        gameFieldView.updateScore(score);
    }


    /**
     * Сбрасывает комбо-счётчик при неправильной паре или ошибке.
     */
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

                // победа
                if (currentBoard.getActiveCount() == 0) {
                    System.out.println("Все плитки удалены");
                    System.out.println("Итоговый счёт: " + score);
                    showVictory();
                    return;
                }

                // проигрыш
                if (!GameEngine.hasAnyMove(currentBoard)) {
                    System.out.println("Игра окончена");
                    System.out.println("Итоговый счёт: " + score);
                    showGameOver();
                    return;
                }
            } else {
                System.out.println("Не удалось удалить пару (возможно, одна из плиток уже удалена)");
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

    /**
    * Геттеры
     */
    public int getScore() {return score; }
    public int getComboCounter() {return comboCounter; }
    public int getMaxCombo() {return maxCombo; }
}

