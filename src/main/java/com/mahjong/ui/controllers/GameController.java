package com.mahjong.ui.controllers;

import com.mahjong.logic.GameEngine;
import com.mahjong.model.Board;
import com.mahjong.model.Tile;
import com.mahjong.ui.views.GameFieldView;
import com.mahjong.ui.views.GameResultView;
import com.mahjong.ui.utils.GameSave;
import com.mahjong.utils.LevelLoader;
import javafx.scene.layout.StackPane;

/**
 * Контроллер игры
 * Управляет игровым процессом, обработкой кликов, подсчётом очков и сохранением прогресса
 */
public class GameController {

    private GameFieldView gameFieldView; // представление игры
    private GameResultView gameResultView; // представление результата (победа/поражение)
    private String currentLevel; // текущий уровень
    private Runnable onBackCallback; // колбэк для возврата в меню
    private Board currentBoard; // текущая доска с плитками
    private Tile selectedTile = null; // выбранная плитка

    /**
     * Счётчики для системы очков и комбо
     */
    private int score = 0; // текущий счёт
    private int comboCounter = 0; // комбо-счётчик (пары подряд)
    private int maxCombo = 0; // максимальное комбо

    private static final int BASE_POINTS = 150; // очки за собранную пару
    private static final int BONUS_PER_COMBO = 50; // бонус за комбо

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
     * Кнопка "Назад" - возврат в меню выбора уровней с сохранением прогресса
     * Клик по плитке - обработка выбора/удаления плитки
     */
    private void setupButtons() {
        gameFieldView.getBackButton().setOnMouseClicked(event -> {
            System.out.println("Возврат в выбор уровней");

            // Сохраняем прогресс при выходе, если игра не завершена
            if (currentBoard != null && currentBoard.getActiveCount() > 0) {
                GameSave.save(currentBoard, currentLevel, score, comboCounter, maxCombo, LevelLoader.getCurrentSeed());
                System.out.println("Прогресс уровня '" + currentLevel + "' сохранён");
            }

            if (onBackCallback != null) {
                onBackCallback.run();
            }
        });

        gameFieldView.getRestButton().setOnMouseClicked(event -> {
            System.out.println("Перезагрузка уровня");

            if (currentBoard != null && currentBoard.getActiveCount() > 0) {
                GameSave.delete(currentLevel);
                System.out.println("Прогресс уровня '" + currentLevel + "сброшен");
            }
            restartLevel();

        });

        gameFieldView.setOnTileClick(this::handleTileClick);
    }

    /**
     * Настраивает плашку результата (победа/поражение)
     * Обработчики: перезапуск уровня и возврат в меню
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
     * Проверяет наличие сохранения для данного уровня.
     * Сбрасывает состояние выбранной плитки и границы доски.
     *
     * @param board доска с плитками (может быть null, тогда загрузится из JSON)
     * @param levelName имя уровня (для загрузки сохранения)
     */
    public void loadBoard(Board board, String levelName) {
        this.currentLevel = levelName;

        // Проверяем наличие сохранения для этого уровня
        GameSave.SaveData saveData = GameSave.load(levelName);

        if (saveData != null) {
            System.out.println("Найдено сохранение для уровня '" + levelName + "'! Восстанавливаем...");

            // Если доска не передана, загружаем свежий уровень из JSON
            if (board == null) {
                board = LevelLoader.loadLevel("levels/" + levelName + ".json");
                if (board == null) {
                    System.err.println("Ошибка: не удалось загрузить уровень " + levelName);
                    return;
                }
            }

            this.currentBoard = board;

            // Восстанавливаем состояние каждой плитки из сохранения
            for (int i = 0; i < board.getAllTiles().size() && i < saveData.tiles.size(); i++) {
                Tile tile = board.getAllTiles().get(i);
                GameSave.TileData savedTile = saveData.tiles.get(i);
                tile.setImageName(savedTile.imageName);
                tile.setRemoved(savedTile.isRemoved);
            }

            // Восстанавливаем счёт и комбо
            this.score = saveData.score;
            this.comboCounter = saveData.comboCounter;
            this.maxCombo = saveData.maxCombo;
            gameFieldView.updateScore(score);

            System.out.println("Восстановлено! Счёт: " + score + ", Комбо: " + comboCounter);
        } else {
            System.out.println("Новый уровень: " + levelName);

            // Загружаем новый уровень
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

        // Сбрасываем состояние и отображаем доску
        gameFieldView.resetBounds();
        this.selectedTile = null;
        gameFieldView.renderBoard(this.currentBoard);
        gameResultView.hide();

        System.out.println("Загружена доска: " + this.currentBoard.getActiveCount() + " активных плиток");
    }

    /**
     * Перезапускает текущий уровень
     * Удаляет сохранение и загружает новую доску
     */

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
     * Обновляет максимальное комбо при необходимости.
     */
    private void resetCombo() {
        if (comboCounter > 0) {
            System.out.println("Комбо прервано! Было: " + comboCounter + " пар подряд");
            if (comboCounter > maxCombo) {
                maxCombo = comboCounter;
            }
            System.out.println("Макс комбо: " + maxCombo);
            comboCounter = 0;
        }
    }

    /**
     * Основная логика обработки клика по плитке.
     * <p>
     * Алгоритм:
     * <ol>
     *   <li>Если плитка уже удалена — игнорируем клик</li>
     *   <li>Если плитка заблокирована — снимаем выделение и сбрасываем комбо</li>
     *   <li>Если нет выбранной плитки — выбираем текущую и затемняем её</li>
     *   <li>Если выбрана та же плитка — снимаем выделение</li>
     *   <li>Если выбрана другая плитка — проверяем, образуют ли они пару</li>
     *   <li>Если пара подходит — удаляем обе плитки, начисляем очки, сохраняем прогресс</li>
     *   <li>После удаления проверяем победу или наличие доступных ходов</li>
     *   <li>Если пара не подходит — снимаем выделение и сбрасываем комбо</li>
     * </ol>
     *
     * @param clickedTile плитка, по которой кликнул игрок
     */
    private void handleTileClick(Tile clickedTile) {
        // 1. Проверка: плитка уже удалена?
        if (clickedTile.isRemoved()) {
            System.out.println("Плитка уже удалена");
            return;
        }

        // 2. Проверка: плитка доступна для выбора?
        if (!GameEngine.isTileFree(currentBoard, clickedTile)) {
            System.out.println("Плитка заблокирована");
            clearSelection();
            resetCombo();
            return;
        }

        // 3. Нет выбранной плитки - выбираем текущую
        if (selectedTile == null) {
            selectedTile = clickedTile;
            gameFieldView.setTileDarkened(selectedTile, true);
            System.out.println("Выбрана плитка: " + selectedTile.getImageName());
            return;
        }

        // 4. Выбрана та же плитка - снимаем выделение
        if (selectedTile == clickedTile) {
            clearSelection();
            return;
        }

        // 5. Проверяем, образуют ли плитки пару
        if (GameEngine.canFormPair(currentBoard, selectedTile, clickedTile)) {
            System.out.println("🎉 Пара найдена! Удаляем:");
            System.out.println("  - " + selectedTile.getImageName());
            System.out.println("  - " + clickedTile.getImageName());

            // Удаляем пару через Board
            boolean pairRemoved = currentBoard.removePair(selectedTile.getId(), clickedTile.getId());

            if (pairRemoved) {
                addPointsForPair();
                clearSelection();
                gameFieldView.renderBoard(currentBoard);

                // АВТОСОХРАНЕНИЕ ПОСЛЕ КАЖДОГО ХОДА
                GameSave.save(currentBoard, currentLevel, score, comboCounter, maxCombo, LevelLoader.getCurrentSeed());

                // Проверка победы
                if (currentBoard.getActiveCount() == 0) {
                    System.out.println("ПОБЕДА! Все плитки удалены");
                    System.out.println("Итоговый счёт: " + score);
                    GameSave.delete(currentLevel);
                    showVictory();
                    return;
                }

                // Проверка поражения (нет доступных ходов)
                if (!GameEngine.hasAnyMove(currentBoard)) {
                    System.out.println("Игра окончена! Нет доступных ходов");
                    System.out.println("Итоговый счёт: " + score);
                    GameSave.delete(currentLevel);
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

    /**
     * Показывает экран победы
     */
    private void showVictory() {
        System.out.println("Победа");
        gameResultView.showVictory(score, maxCombo);
    }

    /**
     * Показывает экран поражения
     */
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
     * Показывает игровое поле (делает его видимым)
     */
    public void show() {
        gameFieldView.setVisible(true);
    }

    /**
     * Скрывает игровое поле
     */
    public void hide() {
        gameFieldView.setVisible(false);
    }

    /**
     * Геттеры для доступа к счёту и комбо извне
     */
    public int getScore() { return score; }
    public int getComboCounter() { return comboCounter; }
    public int getMaxCombo() { return maxCombo; }
}