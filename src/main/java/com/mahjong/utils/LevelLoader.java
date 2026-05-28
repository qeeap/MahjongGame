package com.mahjong.utils;

import com.google.gson.Gson;
import com.mahjong.model.Board;
import com.mahjong.model.Tile;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Загрузчик уровней для игры Маджонг.
 * Поддерживает:
 * - Чтение уровней из JSON
 * - Случайное распределение картинок
 * - Автоматический подсчёт плиток
 */

public  class LevelLoader {
    //список картинок
    private static final String[] ALL_TILES = {
            "bamboos1", "bamboos2", "bamboos3", "bamboos4", "bamboos5", "bamboos6", "bamboos7", "bamboos8", "bamboos9",
            "characters1", "characters2", "characters3", "characters4", "characters5", "characters6", "characters7", "characters8", "characters9",
            "circles1", "circles2", "circles3", "circles4", "circles5", "circles6", "circles7", "circles8", "circles9",
            "east", "south", "west", "north",
            "dragonRed", "dragonGreen", "dragonWhite"
    };

    /**
     * Загружает уровень со случайным распределением картинок
     *
     * @param fileName путь к файлу (например, "levels/pyramid.json")
     * @return Board с готовыми плитками
     */

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

            List<String> imageNames = generateImageList(totalTiles);
            Collections.shuffle(imageNames);

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
     * чтение из Json
     */

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


    /**
     * Генерирует список картинок нужной длины
     * Каждая картинка встречается ровно 2 раза
     */
    private static List<String> generateImageList(int totalTiles) {
        List<String> images = new ArrayList<>();
        int pairsNeeded = totalTiles / 2;

        for (int i = 0; i < pairsNeeded; i++) {
            String image = ALL_TILES[i % ALL_TILES.length];
            images.add(image);  // первая плитка
            images.add(image);  // вторая плитка
        }

        return images;
    }

    /**
     * Тестовая доска на случай ошибки
     */
    private static Board createTestBoard() {
        System.err.println("Используется тестовая доска (4 плитки)");
        Board board = new Board();
        board.addTile(new Tile(1, "bamboo1", 0, 0, 0, "none"));
        board.addTile(new Tile(2, "bamboo1", 1, 0, 0, "none"));
        board.addTile(new Tile(3, "bamboo2", 0, 1, 0, "none"));
        board.addTile(new Tile(4, "bamboo2", 1, 1, 0, "none"));
        return board;
    }

    private static class LevelData {
        String name;
        List<LayerData> layers;
    }

    private static class LayerData {
        int z;
        List<TileData> tiles;  // 1 = плитка, 0 = пусто
    }

    private static class TileData {
        int x;
        int y;
        String shift;
        transient int z;
    }

    private static class TilePosition {
        int x, y, z;
        String shift;

        TilePosition(int x, int y, int z, String shift) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.shift = shift; // смещение
        }
    }

    // Временный метод для тестирования
    public static void main(String[] args) {
        System.out.println("=== ТЕСТ LEVELLOADER ===\n");

        Board board = loadLevel("levels/turtle.json");

        System.out.println("\n--- Результат ---");
        System.out.println("Активных плиток: " + board.getActiveCount());
        System.out.println("Всего плиток: " + board.getAllTiles().size());

        System.out.println("\n--- Первые 5 плиток ---");
        List<Tile> tiles = board.getAllTiles();
        for (int i = 0; i < Math.min(5, tiles.size()); i++) {
            Tile tile = tiles.get(i);
            System.out.println("ID: " + tile.getId() +
                    ", Картинка: " + tile.getImageName() +
                    ", Координаты: (" + tile.getX() + ", " + tile.getY() + ", " + tile.getZ() + ")" +
                    ", Shift: " + tile.getShift());
        }

        if (board.getActiveCount() >= 2) {
            System.out.println("\n--- Проверка удаления пары ---");
            Tile first = board.getActiveTiles().get(0);
            Tile second = board.getActiveTiles().get(1);
            System.out.println("Удаляем плитки: " + first.getId() + " и " + second.getId());
            board.removePair(first.getId(), second.getId());
            System.out.println("Осталось активных: " + board.getActiveCount());
        }

        System.out.println("\n=== ТЕСТ ЗАВЕРШЕН ===");
    }
}

