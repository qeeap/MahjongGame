package com.mahjong.model;

import java.util.Objects;

/**
 * Класс для одной плитки.
 * Содержит данные о положении, типе, состоянии и визуальном смещении.
 *
 */
public class Tile {

    // ==================== ПОЛЯ КЛАССА ====================

    private final int id;           // Уникальный идентификатор плитки
    private String imageName;       // Имя файла изображения (изменяемое для восстановления из сохранения)
    private final int x;            // Координата X (столбец)
    private final int y;            // Координата Y (строка)
    private final int z;            // Координата Z (слой/высота)
    private final String shift;     // Визуальное смещение плитки (none, down, right, right_down)
    private boolean isRemoved;      // Флаг: удалена ли плитка с доски

    // ==================== КОНСТРУКТОР ====================

    /**
     * Создаёт новую плитку с заданными параметрами.
     * По умолчанию плитка не удалена (isRemoved = false).
     *
     * @param id        уникальный идентификатор плитки
     * @param imageName имя файла изображения (например, "bamboos1")
     * @param x         координата X (столбец)
     * @param y         координата Y (строка)
     * @param z         координата Z (слой/высота)
     * @param shift     визуальное смещение (none, down, right, right_down)
     */
    public Tile(int id, String imageName, int x, int y, int z, String shift) {
        this.id = id;
        this.imageName = imageName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.shift = shift;
        this.isRemoved = false;
    }

    // ==================== ГЕТТЕРЫ ====================

    /**
     * Возвращает уникальный идентификатор плитки.
     *
     * @return id плитки
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает имя файла изображения плитки.
     *
     * @return имя изображения (например, "bamboos1")
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Возвращает координату X (столбец).
     *
     * @return координата X
     */
    public int getX() {
        return x;
    }

    /**
     * Возвращает координату Y (строка).
     *
     * @return координата Y
     */
    public int getY() {
        return y;
    }

    /**
     * Возвращает координату Z (слой/высота).
     *
     * @return координата Z
     */
    public int getZ() {
        return z;
    }

    /**
     * Возвращает визуальное смещение плитки.
     *
     * @return смещение (none, down, right, right_down)
     */
    public String getShift() {
        return shift;
    }

    /**
     * Проверяет, удалена ли плитка с доски.
     *
     * @return true если плитка удалена, false если активна
     */
    public boolean isRemoved() {
        return isRemoved;
    }

    // ==================== СЕТТЕРЫ ====================

    /**
     * Устанавливает состояние удаления плитки.
     *
     * @param removed true если плитку нужно удалить, false если восстановить
     */
    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    /**
     * Устанавливает новое имя файла изображения.
     * Используется при восстановлении сохранённой игры.
     *
     * @param imageName новое имя изображения
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    // ==================== ПЕРЕОПРЕДЕЛЁННЫЕ МЕТОДЫ ====================

    /**
     * Сравнивает плитки по идентификатору.
     * Две плитки считаются равными, если у них одинаковый id.
     *
     * @param o объект для сравнения
     * @return true если плитки имеют одинаковый id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return id == tile.id;
    }

    /**
     * Возвращает хэш-код на основе идентификатора плитки.
     *
     * @return хэш-код плитки
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Возвращает строковое представление плитки для отладки.
     *
     * @return строка с информацией о плитке
     */
    @Override
    public String toString() {
        return "Tile{" +
                "id=" + id +
                ", name='" + imageName + '\'' +
                ", pos=(" + x + "," + y + "," + z + "," + shift + ")" +
                ", removed=" + isRemoved +
                '}';
    }

    // ==================== ТЕСТИРОВАНИЕ ====================

    /**
     * Временный метод для тестирования класса Tile.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        System.out.println("=== ТЕСТИРОВАНИЕ КЛАССА TILE ===\n");

        // Создание плиток
        System.out.println("1. Создание плиток:");
        Tile tile1 = new Tile(1, "bamboos1", 100, 100, 0, "right");
        Tile tile2 = new Tile(2, "bamboos2", 180, 100, 0, "right");
        System.out.println("  " + tile1);
        System.out.println("  " + tile2);

        // Проверка удаления
        System.out.println("\n2. Проверка удаления:");
        tile1.setRemoved(true);
        System.out.println("  После удаления tile1.isRemoved() = " + tile1.isRemoved());

        // Проверка сравнения
        System.out.println("\n3. Проверка сравнения:");
        Tile tile1copy = new Tile(1, "bamboos1", 100, 100, 0, "right");
        System.out.println("  tile1.equals(tile1copy) = " + tile1.equals(tile1copy));
        System.out.println("  (ожидается true, так как id одинаковый)");

        // Проверка изменения картинки
        System.out.println("\n4. Проверка изменения картинки:");
        System.out.println("  До изменения: " + tile1.getImageName());
        tile1.setImageName("new_bamboos");
        System.out.println("  После изменения: " + tile1.getImageName());

        System.out.println("\n=== ТЕСТ ЗАВЕРШЕН ===");
    }
}