package com.mahjong.model;

import java.util.Objects;

/**
 * Класс для одной плитки. Данные о положение, типе и состояние памяти
 */

public class Tile {
    private final int id;   // Индификатор плитки
    private final String imageName; // Имя файла изображения
    private final int x; // коорд x
    private final int y; // коорд y
    private final int z; // коорд z - слой
    private boolean isRemoved; // удалена ли плитка

    public Tile(int id, String imageName, int x, int y, int z){
        this.id = id;
        this.imageName = imageName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.isRemoved = false; // так как в самом начале плитка на месте
    }

    public int getId() {
        return id;
    }

    public String getImageName() {
        return imageName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    // переопределение equals, чтобы плитки с одинаковыми id считались равными
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return id == tile.id;
    }

    // Переопределяем hashCode вместе с equals
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // представление плитки
    @Override
    public String toString() {
        return "Tile{" +
                "id=" + id +
                ", name='" + imageName + '\'' +
                ", pos=(" + x + "," + y + "," + z + ")" +
                ", removed=" + isRemoved +
                '}';
    }

    // TECT
    public static void main(String[] args) {
        System.out.println("Тестируем класс Tile:");
        Tile tile1 = new Tile(1, "bamboo1", 100, 100, 0);
        Tile tile2 = new Tile(2, "bamboo2", 180, 100, 0);

        System.out.println(tile1);
        System.out.println(tile2);

        // Проверяем удаление
        tile1.setRemoved(true);
        System.out.println("После удаления tile1: " + tile1.isRemoved());

        // Проверяем сравнение
        Tile tile1copy = new Tile(1, "bamboo1", 100, 100, 0);
        System.out.println("tile1 равен tile1copy? " + tile1.equals(tile1copy)); // Должно быть true, т.к. id одинаковый
    }
}
