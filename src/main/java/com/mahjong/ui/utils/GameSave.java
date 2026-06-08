package com.mahjong.ui.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mahjong.model.Board;
import com.mahjong.model.Tile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для автоматического сохранения и загрузки прогресса игры.
 * <p>
 * Сохраняет:
 * - Текущий уровень
 * - Счёт и комбо
 * - Состояние каждой плитки (удалена или нет)
 * - Картинки на плитках
 * - Seed случайности (чтобы картинки не перемешивались)
 * <p>
 * Каждый уровень сохраняется в отдельный файл в папке saves/
 */
public class GameSave {

    /** Папка для хранения файлов сохранений */
    private static final String SAVE_FOLDER = "saves/";

    /** Gson с форматированием для читаемости JSON */
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Данные сохранения для одного уровня.
     * Содержит всю информацию для восстановления игры.
     */
    public static class SaveData {
        public String levelName;          // Название уровня (например, "dragon")
        public int score;                 // Текущий счёт игрока
        public int comboCounter;          // Количество пар подряд (комбо)
        public int maxCombo;              // Максимальное комбо за игру
        public int seed;                  // Зерно случайности для картинок
        public List<TileData> tiles = new ArrayList<>(); // Состояние всех плиток
    }

    /**
     * Данные одной плитки для сохранения.
     * Хранит всё, что нужно для восстановления.
     */
    public static class TileData {
        public int id;                    // Уникальный ID плитки
        public String imageName;          // Имя файла изображения
        public int x, y, z;               // Координаты на доске
        public String shift;              // Визуальное смещение
        public boolean isRemoved;         // Удалена ли плитка (САМОЕ ВАЖНОЕ!)
    }

    /**
     * Возвращает путь к файлу сохранения для указанного уровня.
     *
     * @param levelName название уровня
     * @return путь к файлу (например, "saves/dragon.json")
     */
    private static String getSaveFilePath(String levelName) {
        return SAVE_FOLDER + levelName + ".json";
    }

    /**
     * Создаёт папку для сохранений, если она не существует.
     * Вызывается автоматически при первом сохранении.
     */
    private static void ensureSaveFolder() {
        File folder = new File(SAVE_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("Создана папка для сохранений: " + SAVE_FOLDER);
        }
    }

    /**
     * Сохраняет текущий прогресс игры в файл.
     * <p>
     * Сохраняется:
     * - Название уровня
     * - Счёт и комбо
     * - Состояние всех плиток (какие удалены, какие картинки)
     * - Seed для воспроизведения случайности
     *
     * @param board        текущая доска с плитками
     * @param levelName    название уровня
     * @param score        текущий счёт
     * @param comboCounter текущее комбо
     * @param maxCombo     максимальное комбо
     * @param seed         зерно случайности (для картинок)
     */
    public static void save(Board board, String levelName, int score, int comboCounter, int maxCombo, int seed) {
        ensureSaveFolder();

        // Создаём объект для сохранения
        SaveData data = new SaveData();
        data.levelName = levelName;
        data.score = score;
        data.comboCounter = comboCounter;
        data.maxCombo = maxCombo;
        data.seed = seed;

        // Сохраняем состояние каждой плитки
        for (Tile tile : board.getAllTiles()) {
            TileData td = new TileData();
            td.id = tile.getId();
            td.imageName = tile.getImageName();
            td.x = tile.getX();
            td.y = tile.getY();
            td.z = tile.getZ();
            td.shift = tile.getShift();
            td.isRemoved = tile.isRemoved();
            data.tiles.add(td);
        }

        // Записываем в файл
        String filePath = getSaveFilePath(levelName);
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
            System.out.println("Сохранение уровня '" + levelName + "' в файл: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    /**
     * Загружает сохранённый прогресс для указанного уровня.
     *
     * @param levelName название уровня
     * @return SaveData с сохранёнными данными, или null если сохранения нет
     */
    public static SaveData load(String levelName) {
        String filePath = getSaveFilePath(levelName);
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Нет сохранения для уровня: " + levelName);
            return null;
        }

        try (FileReader reader = new FileReader(filePath)) {
            SaveData data = gson.fromJson(reader, SaveData.class);
            System.out.println("Загружено сохранение для уровня: " + levelName);
            return data;
        } catch (IOException e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
            return null;
        }
    }

    /**
     * Проверяет, есть ли сохранение для указанного уровня.
     *
     * @param levelName название уровня
     * @return true если сохранение существует, false если нет
     */
    public static boolean hasSave(String levelName) {
        return new File(getSaveFilePath(levelName)).exists();
    }

    /**
     * Удаляет сохранение для указанного уровня.
     * Вызывается при победе, поражении или перезапуске уровня.
     *
     * @param levelName название уровня
     */
    public static void delete(String levelName) {
        String filePath = getSaveFilePath(levelName);
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            System.out.println("Удалено сохранение для уровня: " + levelName);
        }
    }

    /**
     * Возвращает список всех уровней, для которых есть сохранения.
     *
     * @return список названий уровней с сохранениями
     */
    public static List<String> getSavedLevels() {
        ensureSaveFolder();
        List<String> levels = new ArrayList<>();
        File folder = new File(SAVE_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                levels.add(name.substring(0, name.length() - 5)); // убираем ".json"
            }
        }
        return levels;
    }
}