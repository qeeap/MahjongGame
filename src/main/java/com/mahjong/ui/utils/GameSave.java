package com.mahjong.ui.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mahjong.model.Board;
import com.mahjong.model.Tile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameSave {

    private static final String SAVE_FOLDER = "saves/";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static class SaveData {
        public String levelName;
        public int score;
        public int comboCounter;
        public int maxCombo;
        public int seed;
        public List<TileData> tiles = new ArrayList<>();
    }

    public static class TileData {
        public int id;
        public String imageName;
        public int x, y, z;
        public String shift;
        public boolean isRemoved;
    }

    // Получить путь к файлу сохранения для уровня
    private static String getSaveFilePath(String levelName) {
        return SAVE_FOLDER + levelName + ".json";
    }

    // Создать папку для сохранений, если её нет
    private static void ensureSaveFolder() {
        File folder = new File(SAVE_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("Создана папка для сохранений: " + SAVE_FOLDER);
        }
    }

    public static void save(Board board, String levelName, int score, int comboCounter, int maxCombo, int seed) {
        ensureSaveFolder();

        SaveData data = new SaveData();
        data.levelName = levelName;
        data.score = score;
        data.comboCounter = comboCounter;
        data.maxCombo = maxCombo;
        data.seed = seed;

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

        String filePath = getSaveFilePath(levelName);
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
            System.out.println("Сохранение уровня '" + levelName + "' в файл: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    public static SaveData load(String levelName) {
        String filePath = getSaveFilePath(levelName);
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Нет сохранения для уровня: " + levelName);
            return null;
        }

        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, SaveData.class);
        } catch (IOException e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
            return null;
        }
    }

    public static boolean hasSave(String levelName) {
        return new File(getSaveFilePath(levelName)).exists();
    }

    public static void delete(String levelName) {
        String filePath = getSaveFilePath(levelName);
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            System.out.println("Удалено сохранение для уровня: " + levelName);
        }
    }

    // Получить список всех сохранённых уровней
    public static List<String> getSavedLevels() {
        ensureSaveFolder();
        List<String> levels = new ArrayList<>();
        File folder = new File(SAVE_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                levels.add(name.substring(0, name.length() - 5));
            }
        }
        return levels;
    }
}