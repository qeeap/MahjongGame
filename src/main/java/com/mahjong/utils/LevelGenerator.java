package com.mahjong.utils;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LevelGenerator {

    // Таблица соответствия цифр и смещений
    private static final Map<Integer, String> SHIFT_MAP = new HashMap<>();
    static {
        SHIFT_MAP.put(1, "none"); // без смещения
        SHIFT_MAP.put(2, "down"); // смещение вниз
        SHIFT_MAP.put(3, "right");// смещение вправо
        SHIFT_MAP.put(5, "right_down");// смещение вправо и вниз
    }

    /**
     * Генерирует уровень из матрицы с цифрами (1-6 = разные смещения)
     * @param name название уровня
     * @param matrix матрица (0=пусто, 1-6=плитка со смещением)
     */
    public static void generateLevel(String name, int[][] matrix) {
        generateLevel(name, new int[][][]{matrix});
    }

    /**
     * Генерирует уровень с несколькими слоями
     * @param name название уровня
     * @param layers матрицы слоёв
     */
    public static void generateLevel(String name, int[][][] layers) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"name\": \"").append(name).append("\",\n");
        json.append("  \"layers\": [\n");

        for (int layerIdx = 0; layerIdx < layers.length; layerIdx++) {
            int[][] matrix = layers[layerIdx];

            json.append("    {\n");
            json.append("      \"z\": ").append(layerIdx).append(",\n");
            json.append("      \"tiles\": [\n");

            StringBuilder tilesBuilder = new StringBuilder();
            for (int row = 0; row < matrix.length; row++) {
                for (int col = 0; col < matrix[row].length; col++) {
                    int value = matrix[row][col];
                    if (value != 0) {
                        String shift = SHIFT_MAP.getOrDefault(value, "none");
                        tilesBuilder.append("        {\"x\": ").append(col)
                                .append(", \"y\": ").append(row)
                                .append(", \"shift\": \"").append(shift).append("\"},\n");
                    }
                }
            }

            // Удаляем последнюю запятую
            String tilesStr = tilesBuilder.toString();
            if (tilesStr.endsWith(",\n")) {
                tilesStr = tilesStr.substring(0, tilesStr.length() - 2) + "\n";
            }
            json.append(tilesStr);
            json.append("      ]\n");
            json.append("    }");

            if (layerIdx < layers.length - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}");

        System.out.println(json.toString());
        saveToFile(name, json.toString());
    }

    private static void saveToFile(String name, String json) {
        try {
            String fileName = "src/main/resources/levels/" + name.toLowerCase() + ".json";
            PrintWriter out = new PrintWriter(new FileWriter(fileName));
            out.print(json);
            out.close();
            System.out.println("\n Файл сохранён: " + fileName);
        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    // ==================== СОЗДАНИЕ УРОВНЯ ====================

    public static void main(String[] args) {
        // 1 = ровно, 2 = вниз, 3 = вправо, 5 = вправо-вниз
        int[][] layer0 = {
                //  2  3  4  5  6  7  8  9 10  11 12 13 14 15 16
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0}, //1
                {0, 2, 1, 2, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0}, //2
                {1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0}, //3
                {0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3}, //4
                {0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0}, //5
                {0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3}, //6
                {0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1}  //7
        };

        int[][] layer1 = {
                //  2  3  4  5  6  7  8  9 10  11 12 13 14 15 16
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0}, //1
                {0, 2, 1, 2, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0}, //2
                {1, 2, 1, 2, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0}, //3
                {0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 3}, //4
                {0, 0, 0, 3, 3, 3, 0, 3, 3, 3, 0, 3, 0, 3, 3, 0}, //5
                {0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3}, //6
                {0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1}  //7
        };

        int[][][] turtle = {layer0, layer1};
        generateLevel("turtle", turtle);
    }
}