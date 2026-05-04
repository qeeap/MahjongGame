package com.mahjong.ui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mahjong.model.Tile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    private static final double CELL_WIDTH = 65;   // ширина ячейки в пикселях
    private static final double CELL_HEIGHT = 85;  // высота ячейки в пикселях
    private static final double START_X = 50;      // начальный отступ по X
    private static final double START_Y = 50;      // начальный отступ по Y

    private static final Gson gson = new Gson();

    /**
     * Загружает уровень из JSON файла
     * @param levelFileName имя файла (например, "dragon.json")
     * @return список плиток с вычисленными пиксельными координатами
     */
    public static List<Tile> loadLevel(String levelFileName) {
        List<Tile> tiles = new ArrayList<>();

        try {
            // Читаем JSON файл из resources
            InputStream inputStream = LevelLoader.class.getResourceAsStream("/levels/" + levelFileName);
            if (inputStream == null) {
                System.err.println("Файл уровня не найден: /levels/" + levelFileName);
                return tiles;
            }

            // Парсим JSON с помощью Gson
            JsonObject root = gson.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);

            String levelName = root.get("name").getAsString();
            System.out.println("Загрузка уровня: " + levelName);

            JsonArray layers = root.getAsJsonArray("layers");
            int tileId = 1;

            // Проходим по всем слоям
            for (int layerIdx = 0; layerIdx < layers.size(); layerIdx++) {
                JsonObject layer = layers.get(layerIdx).getAsJsonObject();
                int z = layer.get("z").getAsInt();
                JsonArray tilesArray = layer.getAsJsonArray("tiles");

                System.out.println("  Слой " + z + ": " + tilesArray.size() + " плиток");

                // Проходим по всем плиткам в слое
                for (int i = 0; i < tilesArray.size(); i++) {
                    JsonObject tileData = tilesArray.get(i).getAsJsonObject();
                    int gridX = tileData.get("x").getAsInt();
                    int gridY = tileData.get("y").getAsInt();
                    String shift = tileData.has("shift") ? tileData.get("shift").getAsString() : "none";

                    // Вычисляем пиксельные координаты с учётом смещения
                    double pixelX = calculateX(gridX, gridY, shift, z);
                    double pixelY = calculateY(gridX, gridY, shift, z);

                    // Определяем тип плитки
                    String imageName = getDefaultImageName(tileId);

                    Tile tile = new Tile(tileId++, imageName, (int) pixelX, (int) pixelY, z);
                    tiles.add(tile);
                }
            }

            System.out.println("Всего загружено плиток: " + tiles.size());

        } catch (Exception e) {
            System.err.println("Ошибка загрузки уровня: " + e.getMessage());
            e.printStackTrace();
        }

        return tiles;
    }

    /**
     * Вычисляет X координату плитки с учётом смещения
     */
    private static double calculateX(int gridX, int gridY, String shift, int z) {
        double x = START_X + gridX * CELL_WIDTH;

        // Корректировка в зависимости от смещения
        switch (shift) {
            case "down":
                x += CELL_WIDTH / 2;
                break;
            case "right_down":
                x += CELL_WIDTH;
                break;
            case "up":
                x += CELL_WIDTH / 2;
                break;
            case "left":
                x -= CELL_WIDTH / 2;
                break;
            default:
                // none — без изменений
                break;
        }

        // Добавляем небольшой сдвиг для верхних слоёв (создаём глубину)
        x += z * 2;

        return x;
    }

    /**
     * Вычисляет Y координату плитки с учётом смещения
     */
    private static double calculateY(int gridX, int gridY, String shift, int z) {
        double y = START_Y + gridY * CELL_HEIGHT;

        // Корректировка в зависимости от смещения
        switch (shift) {
            case "down":
                y += CELL_HEIGHT / 2;
                break;
            case "right_down":
                y += CELL_HEIGHT / 2;
                break;
            case "up":
                y -= CELL_HEIGHT / 2;
                break;
            default:
                // none — без изменений
                break;
        }

        // Верхние слои чуть выше (создаём иллюзию глубины)
        y -= z * 4;

        return y;
    }

    /**
     * Временный метод — позже нужно будет брать тип плитки из JSON
     */
    private static String getDefaultImageName(int id) {
        // Добавим разнообразие типов плиток
        String[] types = {
                "bamboo_1", "bamboo_2", "bamboo_3", "bamboo_4", "bamboo_5",
                "circle_1", "circle_2", "circle_3", "circle_4", "circle_5",
                "character_1", "character_2", "character_3",
                "dragon_red", "dragon_green", "dragon_white",
                "wind_east", "wind_south", "wind_west", "wind_north"
        };

        return types[id % types.length];
    }
}
