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

    public static boolean isTileFree(Board board, Tile tile) {
        //Если плитка удалена - она недоступна
        if (tile.isRemoved()) {
            return false;
        }
        int x = tile.getX();
        int y = tile.getY();
        int z = tile.getZ();
        String shift = tile.getShift();

        boolean blockedLeft = false;
        boolean blockedRight = false;

        List<Tile> above = board.findTilesAtPosition(x, y, z + 1);
        if (!above.isEmpty()) {
            return false; //заблокирована плиткой сверху
        }

        if (!tile.getShift().equals("right")){
            List<Tile> aboveLeft = board.findTilesAtPosition(x - 1, y, z + 1);
            for (Tile neighborUp : aboveLeft) {
                if (neighborUp.getShift().equals("none")){
                    System.out.println("Слева мешает на слое выше");
                    blockedLeft = true;
                }

                if (neighborUp.getShift().equals("right") || neighborUp.getShift().equals("right_down")){
                    System.out.println("мешает плитка слой выше слева со смещением вправо");
                    return false;
                }
            }
        }

        List<Tile> aboveDown = board.findTilesAtPosition(x, y - 1, z + 1);
        for (Tile neighborUp : aboveDown) {
            if (neighborUp.getShift().equals("down") || neighborUp.getShift().equals("right_down")) {
                System.out.println("на слое выше плитка над мешает");
                return false;
            }
        }

        List<Tile> aboveDiagonal = board.findTilesAtPosition(x - 1, y - 1, z + 1);
        for (Tile neighborUp : aboveDiagonal) {
            if (neighborUp.getShift().equals("right_down")) {
                System.out.println("мешает плитка диагональ лево вверх слой выше");
                return false;
            }
        }

        List<Tile> aboveRight = board.findTilesAtPosition(x + 1, y, z + 1);
        for (Tile neighborUp : aboveRight) {
            if (neighborUp.getShift().equals("right") || neighborUp.getShift().equals("right_down") || neighborUp.getShift().equals("none")) {
                System.out.println("мешает плитка справа на слой выше");
                blockedRight = true;
            }
        }


        if (tile.getShift().equals("right")){
            List<Tile> aboveRightUp = board.findTilesAtPosition(x+1, y-1, z+1);
            for (Tile neighborUp : aboveRightUp){
                if (neighborUp.getShift().equals("down")){
                    System.out.println("плитка со смещением вправо мешает плитка слой выше диагональ вверх право со смещением вниз");
                    return false;
                }
            }
        }

        if (tile.getShift().equals("right") || tile.getShift().equals("right_down")){
            List<Tile> aboveShiftRight = board.findTilesAtPosition(x+1, y, z+1);
            for (Tile neighbourUp : aboveShiftRight){
                if (neighbourUp.getShift().equals("down") | neighbourUp.getShift().equals("none")){
                    System.out.println("плитка со смещением вправо мешает плитка слой выше справа со смещением вниз и просто");
                    return false;
                }
            }
        }

        if (tile.getShift().equals("down") || tile.getShift().equals("right_down")){
            List<Tile> bottomAbove = board.findTilesAtPosition(x, y+1, z+1);
            for (Tile neighbour : bottomAbove) {
                if (neighbour.getShift().equals("right") || neighbour.getShift().equals("none")) {
                    System.out.println("плитка со смещением вниз мешает плитка сверху на y+1");
                    return false;
                }
            }
        }

        if (tile.getShift().equals("down")){
            List<Tile> bottomAboveLeft = board.findTilesAtPosition(x-1, y+1, z+1);
            for (Tile neighbour : bottomAboveLeft){
                if (neighbour.getShift().equals("right")){
                    System.out.println("плитка со смещением вниз мешает плитка сверху на y+1 со смещением вправо");
                    return false;
                }
            }
        }


        if (tile.getShift().equals("down")) {
            List<Tile> bottomRight = board.findTilesAtPosition(x + 1, y + 1, z);
            for (Tile neighbor : bottomRight){
                if (neighbor.getShift().equals("none")){
                    System.out.println("плитка со смещением вниз, мешает диагональ право ");
                    blockedRight = true;
                }
            }
        }

        if (tile.getShift().equals("down")) {
            List<Tile> bottomLeft = board.findTilesAtPosition(x - 1, y + 1, z);
            for (Tile neighbor : bottomLeft){
                if (neighbor.getShift().equals("none")){
                    System.out.println("плитка со смещением вниз мешает диагональ лево");
                    blockedLeft = true;
                }
            }
        }

        List<Tile> diagonalLeftDown = board.findTilesAtPosition(x - 1, y - 1, z);
        for (Tile neighbor : diagonalLeftDown) {
            if (neighbor.getShift().equals("down") || neighbor.getShift().equals("right_down")) {
                System.out.println("мешает диагональ вверх лево смещением вниз, тот же слой");
                blockedLeft = true;
            }
        }

        List<Tile> diagonalRightDown = board.findTilesAtPosition(x + 1, y - 1, z);
        for (Tile neighbor : diagonalRightDown) {
            if (neighbor.getShift().equals("down") || neighbor.getShift().equals("right_down")) {
                System.out.println("мешает диагональ право вверх, тот же слой");
                blockedRight = true;
            }
        }

        List<Tile> left = board.findTilesAtPosition(x-1, y, z);
        if (!left.isEmpty()){
            blockedLeft = true;
            System.out.println("слева блокирует");
        }

        List<Tile> right = board.findTilesAtPosition(x+1, y, z);
        if (!right.isEmpty()){
            blockedRight = true;
            System.out.println("справа блокирует");
        }

        //плитка свободна, если не заблокирована с обеих сторон
        if (blockedLeft && blockedRight){
            System.out.println("заблокирована с двух сторон");
            return false;
        }

        return true;
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
     * Проверяет, есть ли на доске хотя бы одна возможная пара.
     *
     * @param board доска
     * @return true если есть хотя бы один ход
     */

    public static boolean hasAnyMove(Board board){
        List<Tile> activeTiles = board.getActiveTiles();

        for (int i = 0; i < activeTiles.size(); i++){
            Tile first = activeTiles.get(i);
            for (int j = i + 1; j < activeTiles.size(); j++){
                Tile second = activeTiles.get(j);
                if (canFormPair(board, first, second)){
                    return true; // есть ход
                }
            }
        }
        return false; // нет хода
    }

    /**
     * Возвращает подсказку — первую найденную возможную пару.
     *
     * @param board доска
     * @return массив из двух плиток, или null, если ходов нет
     */

    public static Tile[] getHint(Board board){
        List<Tile> activeTile = board.getActiveTiles();

        for (int i = 0; i < activeTile.size(); i++) {
            Tile first = activeTile.get(i);
            for (int j = i + 1; j < activeTile.size(); j++) {
                Tile second = activeTile.get(j);
                if (canFormPair(board, first, second)) {
                    return new Tile[]{first, second}; // свободная пара
                }
            }
        }
        return null; // нет подсказок
    }
}
