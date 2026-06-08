package com.mahjong.utils;

import com.google.gson.Gson;
import com.mahjong.model.Board;
import com.mahjong.model.Tile;
import com.mahjong.ui.utils.GameSave;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LevelLoader {

    private static final String[] ALL_TILES = {
            "bamboos1", "bamboos2", "bamboos3", "bamboos4", "bamboos5", "bamboos6", "bamboos7", "bamboos8", "bamboos9",
            "characters1", "characters2", "characters3", "characters4", "characters5", "characters6", "characters7", "characters8", "characters9",
            "circles1", "circles2", "circles3", "circles4", "circles5", "circles6", "circles7", "circles8", "circles9",
            "east", "south", "west", "north",
            "dragonRed", "dragonGreen", "dragonWhite"
    };

    // Храним seed для текущего уровня
    private static int currentSeed = 0;
    private static Random random = new Random();

    public static Board loadLevel(String fileName) {
        try {
            LevelData levelData = readJsonFile(fileName);
            if (levelData == null) {
                return createTestBoard();
            }

            List<TileData> allTileData = new ArrayList<>();
            for (LayerData layer : levelData.layers) {
                for (TileData tileData : layer.tiles) {
                    tileData.z = layer.z;
                    allTileData.add(tileData);
                }
            }

            int totalTiles = allTileData.size();
            if (totalTiles % 2 != 0) {
                System.err.println("Ошибка: нечётное количество плиток! (" + totalTiles + ")");
                return createTestBoard();
            }

            // Получаем имя уровня из fileName
            String levelName = fileName.replace("levels/", "").replace(".json", "");

            // Проверяем, есть ли сохранение для этого уровня
            GameSave.SaveData saveData = GameSave.load(levelName);

            List<String> imageNames;

            if (saveData != null) {
                // Есть сохранение - используем сохранённый seed
                currentSeed = saveData.seed;
                random.setSeed(currentSeed);
                System.out.println("Восстанавливаем seed из сохранения: " + currentSeed);

                // Генерируем картинки с сохранённым seed
                imageNames = generateImageList(totalTiles);
                Collections.shuffle(imageNames, random);
            } else {
                // Новый уровень - генерируем случайный seed
                currentSeed = random.nextInt(1000000);
                random.setSeed(currentSeed);
                System.out.println("Новый уровень! Случайный seed: " + currentSeed);

                // Генерируем картинки с новым seed
                imageNames = generateImageList(totalTiles);
                Collections.shuffle(imageNames, random);
            }

            Board board = new Board();
            int id = 1;

            for (int i = 0; i < totalTiles; i++) {
                TileData tileData = allTileData.get(i);
                String imageName = imageNames.get(i);

                Tile tile = new Tile(id++, imageName, tileData.x, tileData.y, tileData.z, tileData.shift);
                board.addTile(tile);
            }

            System.out.println(" Загружен уровень: " + levelData.name);
            System.out.println("  Всего плиток: " + board.getActiveCount());
            System.out.println("  Пар: " + board.getActiveCount() / 2);

            return board;

        } catch (Exception e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
            e.printStackTrace();
            return createTestBoard();
        }
    }

    /**
     * Возвращает текущий seed (для сохранения)
     */
    public static int getCurrentSeed() {
        return currentSeed;
    }

    private static LevelData readJsonFile(String fileName) {
        try {
            InputStream inputStream = LevelLoader.class.getClassLoader()
                    .getResourceAsStream(fileName);

            if (inputStream == null) {
                System.err.println("Файл не найден: " + fileName);
                return null;
            }

            Gson gson = new Gson();
            return gson.fromJson(new InputStreamReader(inputStream), LevelData.class);

        } catch (Exception e) {
            System.err.println("Ошибка чтения JSON: " + e.getMessage());
            return null;
        }
    }

    private static List<String> generateImageList(int totalTiles) {
        List<String> images = new ArrayList<>();
        int pairsNeeded = totalTiles / 2;

        for (int i = 0; i < pairsNeeded; i++) {
            String image = ALL_TILES[i % ALL_TILES.length];
            images.add(image);
            images.add(image);
        }

        return images;
    }

    private static Board createTestBoard() {
        System.err.println("Используется тестовая доска (4 плитки)");
        Board board = new Board();
        board.addTile(new Tile(1, "bamboos1", 0, 0, 0, "none"));
        board.addTile(new Tile(2, "bamboos1", 1, 0, 0, "none"));
        board.addTile(new Tile(3, "bamboos2", 0, 1, 0, "none"));
        board.addTile(new Tile(4, "bamboos2", 1, 1, 0, "none"));
        return board;
    }

    private static class LevelData {
        String name;
        List<LayerData> layers;
    }

    private static class LayerData {
        int z;
        List<TileData> tiles;
    }

    private static class TileData {
        int x;
        int y;
        String shift;
        transient int z;
    }
}