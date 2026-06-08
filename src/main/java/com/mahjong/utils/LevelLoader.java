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

/**
 * Загрузчик уровней для игры Маджонг.
 * Поддерживает:
 * - Чтение уровней из JSON
 * - Случайное распределение картинок с сохранением seed
 * - Автоматический подсчёт плиток
 * - Восстановление сохранённых уровней
 */
public class LevelLoader {

    // Список всех возможных картинок для плиток
    private static final String[] ALL_TILES = {
            "bamboos1", "bamboos2", "bamboos3", "bamboos4", "bamboos5", "bamboos6", "bamboos7", "bamboos8", "bamboos9",
            "characters1", "characters2", "characters3", "characters4", "characters5", "characters6", "characters7", "characters8", "characters9",
            "circles1", "circles2", "circles3", "circles4", "circles5", "circles6", "circles7", "circles8", "circles9",
            "east", "south", "west", "north",
            "dragonRed", "dragonGreen", "dragonWhite"
    };

    // Храним seed для текущего уровня (для воспроизведения того же распределения картинок)
    private static int currentSeed = 0;
    private static Random random = new Random();

    /**
     * Загружает уровень из JSON файла.
     * Если есть сохранение для этого уровня, восстанавливает картинки с сохранённым seed.
     *
     * @param fileName путь к файлу (например, "levels/pyramid.json")
     * @return Board с готовыми плитками
     */
    public static Board loadLevel(String fileName) {
        try {
            // Читаем JSON файл
            LevelData levelData = readJsonFile(fileName);
            if (levelData == null) {
                return createTestBoard();
            }

            // Собираем все плитки из всех слоёв
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

            // Создаём доску и добавляем плитки
            Board board = new Board();
            int id = 1;

            for (int i = 0; i < totalTiles; i++) {
                TileData tileData = allTileData.get(i);
                String imageName = imageNames.get(i);

                Tile tile = new Tile(id++, imageName, tileData.x, tileData.y, tileData.z, tileData.shift);
                board.addTile(tile);
            }

            System.out.println("   Загружен уровень: " + levelData.name);
            System.out.println("   Всего плиток: " + board.getActiveCount());
            System.out.println("   Пар: " + board.getActiveCount() / 2);

            return board;

        } catch (Exception e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
            e.printStackTrace();
            return createTestBoard();
        }
    }

    /**
     * Возвращает текущий seed (для сохранения прогресса)
     *
     * @return текущий seed
     */
    public static int getCurrentSeed() {
        return currentSeed;
    }

    /**
     * Читает JSON файл уровня
     *
     * @param fileName путь к файлу
     * @return LevelData объект с данными уровня
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
     * Генерирует список картинок нужной длины.
     * Каждая картинка встречается ровно 2 раза (для составления пар).
     *
     * @param totalTiles общее количество плиток
     * @return список имён картинок
     */
    private static List<String> generateImageList(int totalTiles) {
        List<String> images = new ArrayList<>();
        int pairsNeeded = totalTiles / 2;

        for (int i = 0; i < pairsNeeded; i++) {
            String image = ALL_TILES[i % ALL_TILES.length];
            images.add(image);  // первая плитка пары
            images.add(image);  // вторая плитка пары
        }

        return images;
    }

    /**
     * Создаёт тестовую доску на случай ошибки загрузки уровня.
     * Содержит 4 плитки (2 пары) для базового тестирования.
     *
     * @return Board с тестовыми плитками
     */
    private static Board createTestBoard() {
        System.err.println("⚠️ Используется тестовая доска (4 плитки)");
        Board board = new Board();
        board.addTile(new Tile(1, "bamboos1", 0, 0, 0, "none"));
        board.addTile(new Tile(2, "bamboos1", 1, 0, 0, "none"));
        board.addTile(new Tile(3, "bamboos2", 0, 1, 0, "none"));
        board.addTile(new Tile(4, "bamboos2", 1, 1, 0, "none"));
        return board;
    }

    // ==================== ВНУТРЕННИЕ КЛАССЫ ДЛЯ JSON ====================

    /**
     * Внутренний класс для парсинга корневого объекта JSON
     */
    private static class LevelData {
        String name;           // Название уровня
        List<LayerData> layers; // Список слоёв
    }

    /**
     * Внутренний класс для парсинга слоя JSON
     */
    private static class LayerData {
        int z;                 // Номер слоя (высота)
        List<TileData> tiles; // Список плиток в слое
    }

    /**
     * Внутренний класс для парсинга плитки в JSON
     */
    private static class TileData {
        int x;                 // Координата X
        int y;                 // Координата Y
        String shift;          // Смещение плитки (none, down, right, right_down)
        transient int z;       // Координата Z (заполняется из LayerData)
    }

    // ==================== ТЕСТИРОВАНИЕ ====================

    /**
     * Временный метод для тестирования загрузчика уровней
     */
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

    }
}