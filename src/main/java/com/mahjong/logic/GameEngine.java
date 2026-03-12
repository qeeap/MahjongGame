package com.mahjong.logic;

import com.mahjong.model.Board;
import com.mahjong.model.Tile;

import java.util.List;

/**
 * GameEngine содержит всю логику правил игры Маджонг.
 * Не хранит состояние, только проверяет условия.
 */

public class GameEngine {

    /**
     * Проверяет, свободна ли плитка (можно ли её выбрать).
     * Плитка свободна, если:
     * 1. Она не удалена
     * 2. Сверху нет другой плитки (на слое z+1)
     * 3. Слева ИЛИ справа нет соседа на том же слое
     *
     * @param board доска с плитками
     * @param tile проверяемая плитка
     * @return true, если плитку можно выбрать
     */

    public static boolean isTileFree(Board board, Tile tile){
        //Если плитка удалена - она недоступна
        if (tile.isRemoved()){
            return false;
        }
        int x = tile.getX();
        int y = tile.getY();
        int z = tile.getZ();

        List<Tile> above = board.findTilesAtPosition(x, y, z+1);
        if (!above.isEmpty()){
            return false; //заблокирована плиткой сверху
        }

        List<Tile> left = board.findTilesAtPosition(x-1, y, z);
        List<Tile> right = board.findTilesAtPosition(x+1, y, z);

        boolean hasLeft = !left.isEmpty();
        boolean hasRight = !right.isEmpty();

        if (hasLeft && hasRight){
            return false; //если слева справа занято, то плитка недоступна
        }

        return true; // если хотя бы с одной стороны свободно - плитку вытаскиваем
    }

    /**
     * Проверяет, можно ли составить пару из двух плиток.
     * Условия:
     * 1. Это разные плитки (разные ID)
     * 2. Обе не удалены
     * 3. Обе свободны (isTileFree)
     * 4. У них одинаковые имена картинок
     *
     * @param board доска
     * @param first первая плитка
     * @param second вторая плитка
     * @return true, если пара подходит
     */

    public static boolean canFormPair(Board board, Tile first, Tile second){
        // с самой собой пару не создает
        if (first.getId() == second.getId()){
            return false;
        }

        //обе плитки должны быть не удалены
        if (first.isRemoved() || second.isRemoved()){
            return false;
        }

        //обе свободны
        if (!isTileFree(board, first) || !isTileFree(board, second)){
            return false;
        }

        return first.getImageName().equals(second.getImageName()); // если картики совпадают, то true вернет

    }

    /**
     * Возвращает подсказку — первую найденную возможную пару.
     *
     * @param board доска
     * @return массив из двух плиток, или null, если ходов нет
     */

    public static Tile[] getHint(Board board){
        List<Tile> activeTile = board.getActiveTiles();


    }

}