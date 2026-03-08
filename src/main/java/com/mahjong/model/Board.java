package com.mahjong.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для представления игровой доски.
 */
public class Board {
    private final List<Tile> tiles;  // Список всех плиток на доске

    public Board() {
        this.tiles = new ArrayList<>(); // Создание пустой доски
    }

    /**
     * Добавить одну плитку на доску.
     * @param tile плитка для добавления
     */
    public void addTile(Tile tile) {
        if (tile != null) {
            tiles.add(tile);
        }
    }

    /**
     * Добавить список плиток на доску.
     * @param newTiles список плиток для добавления
     */
    public void addAllTiles(List<Tile> newTiles) {
        if (newTiles != null) {
            tiles.addAll(newTiles);
        }
    }

    /**
     * Получить все плитки (включая удаленные).
     * @return копия списка всех плиток
     */
    public List<Tile> getAllTiles() {
        return new ArrayList<>(tiles); // Возвращаем копию, чтобы внешний код не мог испортить наш список
    }

    /**
     * Получить только неудаленные плитки (активные).
     * @return список активных плиток
     */
    public List<Tile> getActiveTiles() {
        List<Tile> active = new ArrayList<>();
        for (Tile tile : tiles) {
            if (!tile.isRemoved()) {
                active.add(tile);
            }
        }
        return active;
    }

    /**
     * Удалить плитку с доски (пометить как удаленную).
     * @param tileId ID плитки для удаления
     * @return true если плитка найдена и удалена
     */
    public boolean removeTile(int tileId) {
        for (Tile tile : tiles) {
            if (tile.getId() == tileId && !tile.isRemoved()) {
                tile.setRemoved(true);
                return true;
            }
        }
        return false;
    }

    /**
     * Удалить две плитки (пара).
     * @param tileId1 ID первой плитки
     * @param tileId2 ID второй плитки
     * @return true если обе плитки найдены и удалены
     */
    public boolean removePair(int tileId1, int tileId2) {
        // Нельзя удалить пару из одинаковых ID
        if (tileId1 == tileId2) {
            return false;
        }

        boolean removed1 = removeTile(tileId1);
        boolean removed2 = removeTile(tileId2);
        return removed1 && removed2;
    }

    /**
     * Найти плитку по ID.
     * @param id ID плитки
     * @return плитка или null, если не найдена
     */
    public Tile findTileById(int id) {
        for (Tile tile : tiles) {
            if (tile.getId() == id) {
                return tile;
            }
        }
        return null;
    }

    /**
     * Найти плитки по координатам.
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     * @return список плиток на указанной позиции (обычно 0 или 1)
     */
    public List<Tile> findTilesAtPosition(int x, int y, int z) {
        List<Tile> result = new ArrayList<>();
        for (Tile tile : tiles) {
            if (!tile.isRemoved() &&
                    tile.getX() == x &&
                    tile.getY() == y &&
                    tile.getZ() == z) {
                result.add(tile);
            }
        }
        return result;
    }

    /**
     * Получить количество активных плиток.
     * @return количество неудаленных плиток
     */
    public int getActiveCount() {
        int count = 0;
        for (Tile tile : tiles) {
            if (!tile.isRemoved()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Проверить, есть ли на доске активные плитки.
     * @return true если есть хотя бы одна активная плитка
     */
    public boolean hasActiveTiles() {
        return getActiveCount() > 0;
    }

    /**
     * Очистить доску (удалить все плитки физически).
     * Используется при загрузке нового уровня.
     */
    public void clear() {
        tiles.clear();
    }

    /**
     * Сбросить доску (все плитки становятся активными).
     * Используется при перезапуске уровня.
     */
    public void reset() {
        for (Tile tile : tiles) {
            tile.setRemoved(false);
        }
    }

    @Override
    public String toString() {
        return "Board{" +
                "total tiles=" + tiles.size() +
                ", active tiles=" + getActiveCount() +
                '}';
    }

    /**
     * Временный метод для тестирования класса Board.
     */
    public static void main(String[] args) {
        System.out.println("=== ТЕСТИРОВАНИЕ BOARD ===\n");

        // Создаем доску
        Board board = new Board();
        System.out.println("Создана пустая доска: " + board);

        // Добавляем плитки
        board.addTile(new Tile(1, "bamboo1", 0, 0, 0));
        board.addTile(new Tile(2, "bamboo2", 1, 0, 0));
        board.addTile(new Tile(3, "bamboo1", 2, 0, 0));
        board.addTile(new Tile(4, "dragonRed", 0, 1, 0));
        board.addTile(new Tile(5, "dragonRed", 1, 1, 0));

        System.out.println("После добавления 5 плиток: " + board);
        System.out.println("Активные плитки: " + board.getActiveTiles().size());

        // Удаляем одну плитку
        System.out.println("\n--- Удаление плитки 2 ---");
        boolean removed = board.removeTile(2);
        System.out.println("Удалена? " + removed);
        System.out.println("Теперь активных: " + board.getActiveCount());

        // Пытаемся удалить уже удаленную
        System.out.println("\n--- Попытка удалить плитку 2 снова ---");
        removed = board.removeTile(2);
        System.out.println("Удалена? " + removed);

        // Удаляем пару
        System.out.println("\n--- Удаление пары (4 и 5) ---");
        boolean pairRemoved = board.removePair(4, 5);
        System.out.println("Пара удалена? " + pairRemoved);
        System.out.println("Осталось активных: " + board.getActiveCount());

        // Поиск плитки
        System.out.println("\n--- Поиск плитки по ID ---");
        Tile found = board.findTileById(1);
        System.out.println("Найдена плитка с ID 1: " + found);

        found = board.findTileById(999);
        System.out.println("Поиск несуществующей: " + found);

        // Поиск по координатам
        System.out.println("\n--- Поиск по координатам (0,0,0) ---");
        List<Tile> atPosition = board.findTilesAtPosition(0, 0, 0);
        System.out.println("Найдено плиток: " + atPosition.size());

        // Сброс доски
        System.out.println("\n--- Сброс доски (reset) ---");
        board.reset();
        System.out.println("После сброса: " + board);

        System.out.println("\n=== ТЕСТ ЗАВЕРШЕН ===");
    }
}