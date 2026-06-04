package com.mahjong.ui.views;

import com.mahjong.model.Board;
import com.mahjong.model.Tile;
import com.mahjong.ui.utils.ScreenUtils;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class GameFieldView extends Pane {
    private ImageView backButton;
    private Rectangle gameBoardRect;
    private Board currentBoard;
    private Pane tilesLayer;
    private int tileHeight;
    private int tileWidth;

    private List<Tile> tiles;

    private java.util.function.Consumer<Tile> onTileClickHandler;

    public GameFieldView() {
        tileHeight = (int) Math.round(ScreenUtils.getScreenHeight() / 11);

        if (tileHeight < 40) {
            tileHeight = 40;
        }
        tileWidth = (int) Math.round(tileHeight * 300.0 / 420.0);
        tilesLayer = new Pane();
        tilesLayer.setStyle("-fx-background-color: transparent;");
        createUI();
        positionUI();
    }

    private void createUI() {

        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        // Кнопка "Назад"
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/back.png"));
            backButton = new ImageView(img);
            backButton.setFitWidth(screenWidth / 15);
            backButton.setPreserveRatio(true);
            backButton.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'Назад' не загружена: /images/back.png");
            backButton = new ImageView();
        }

        gameBoardRect = new Rectangle();
        gameBoardRect.setWidth(screenWidth * 0.85);
        gameBoardRect.setHeight(screenHeight * 0.9);
        gameBoardRect.setArcWidth(50);
        gameBoardRect.setArcHeight(50);
        gameBoardRect.setFill(Color.rgb(11, 77, 106, 0.9));
    }

    private void positionUI() {

        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        backButton.setTranslateX(screenWidth / 30);
        backButton.setTranslateY(screenHeight / 30);

        gameBoardRect.setTranslateX((screenWidth - gameBoardRect.getWidth()) / 2);
        gameBoardRect.setTranslateY((screenHeight - gameBoardRect.getHeight()) / 2);

        getChildren().add(gameBoardRect);   // 1. прямоугольник (внизу)
        getChildren().add(tilesLayer);      // 2. слой для плиток (посередине)
        getChildren().add(backButton);
    }

    public void renderBoard(Board board) {
        System.out.println(tileHeight);
        this.currentBoard = board;
        tilesLayer.getChildren().clear();

        List<Tile> activeTiles = board.getActiveTiles();
        if (activeTiles.isEmpty()) {
            System.out.println("Нет активных плиток для отображения");
            return;
        }

        // 1. Находим границы доски в координатах плиток
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Tile tile : activeTiles) {
            int x = tile.getX();
            int y = tile.getY();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        // 2. Сначала создаём все плитки (без позиционирования), чтобы получить их реальные размеры
        List<ImageView> tileViews = new ArrayList<>();
        for (Tile tile : activeTiles) {
            ImageView tileView = createTileImageView(tile);
            tileViews.add(tileView);
        }

        // 4. Пиксельные размеры всей доски
        int tilesInRow = maxX - minX + 1;
        int tilesInColumn = maxY - minY + 1;
        double boardPixelWidth = tilesInRow * tileWidth;
        double boardPixelHeight = tilesInColumn * tileHeight;

        // 5. Позиция прямоугольника (игровой области)
        double boardRectX = gameBoardRect.getTranslateX();
        double boardRectY = gameBoardRect.getTranslateY();
        double boardRectWidth = gameBoardRect.getWidth();
        double boardRectHeight = gameBoardRect.getHeight();

        // 6. Стартовая позиция для центрирования доски внутри прямоугольника
        double startX = boardRectX + (boardRectWidth - boardPixelWidth) / 2;
        double startY = boardRectY + (boardRectHeight - boardPixelHeight) / 2;

        System.out.println("Реальный размер плитки: " + (int)tileWidth + " x " + (int)tileHeight);
        System.out.println("Доска: " + tilesInRow + " x " + tilesInColumn + " плиток");
        System.out.println("Старт: (" + (int)startX + ", " + (int)startY + ")");

        // 7. Позиционируем и добавляем плитки
        for (int i = 0; i < activeTiles.size(); i++) {
            Tile tile = activeTiles.get(i);
            ImageView tileView = tileViews.get(i);

            // Вычисляем позицию с учётом центрирования
            double pixelX = startX + (tile.getX() - minX) * tileWidth;
            double pixelY = startY + (tile.getY() - minY) * tileHeight;

            // Учитываем смещение (shift)
            String shift = tile.getShift();
            if ("down".equals(shift)) {
                pixelY += tileHeight / 2;
            } else if ("right_down".equals(shift)) {
                pixelX += tileWidth / 2;
                pixelY += tileHeight / 2;
            } else if ("right".equals(shift)) {
                pixelX += tileWidth / 2;
            }

            // Учитываем слой (верхние плитки чуть выше)
            pixelY -= tile.getZ() * 4;
            pixelX -= tile.getZ() * 4;

            tileView.setTranslateX(pixelX);
            tileView.setTranslateY(pixelY);

            tilesLayer.getChildren().add(tileView);
        }

        System.out.println("Отображено плиток: " + activeTiles.size());
    }

    public void setOnTileClick(java.util.function.Consumer<Tile> handler) {
        this.onTileClickHandler = handler;
    }

    /**
     * Создаёт визуальное представление одной плитки
     */
    private ImageView createTileImageView(Tile tile) {
        String imagePath = buildImagePath(tile.getImageName());

        ImageView imageView = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(img);

            imageView.setFitHeight(tileHeight);
            imageView.setPreserveRatio(true);  // ширина подстроится автоматически


        } catch (Exception e) {
            System.err.println("Не загружена картинка: " + imagePath);
            // заглушка
            imageView.setStyle("-fx-background-color: #f5deb3; -fx-border-color: #8b4513; -fx-border-width: 2;");
            imageView.setFitHeight(70);
            imageView.setFitWidth(50);
        }

        // Эффект тени
        imageView.setEffect(new DropShadow(10, Color.BLACK));

        imageView.setOnMouseClicked(event -> {
            System.out.println("Клик по плитке: " + tile);
            if (onTileClickHandler != null) {
                onTileClickHandler.accept(tile);  // ← передаём плитку в контроллер
            }
        });

        return imageView;
    }

    /**
     * Строит путь к картинке по имени из JSON
     */
    private String buildImagePath(String imageName) {
        String basePath = "/tiles/";

        // Пример: "bamboos1" → "/images/tiles/bamboos/1.png"
        if (imageName.startsWith("bamboos")) {
            String num = imageName.substring(7);
            return basePath + "bamboos/" + num + ".png";
        }
        if (imageName.startsWith("characters")) {
            String num = imageName.substring(10);
            return basePath + "characters/" + num + ".png";
        }
        if (imageName.startsWith("circles")) {
            String num = imageName.substring(7);
            return basePath + "circles/" + num + ".png";
        }
        if (imageName.startsWith("dragon")) {
            String color = imageName.substring(6).toLowerCase();
            return basePath + "dragons/" + color + ".png";
        }
        if (imageName.equals("east") || imageName.equals("south") ||
                imageName.equals("west") || imageName.equals("north")) {
            return basePath + "winds/" + imageName + ".png";
        }

        return basePath + imageName + ".png";
    }

    public ImageView getBackButton() { return backButton; }
}

