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
import java.util.List;

public class GameFieldView extends Pane {
    double screenWidth = ScreenUtils.getScreenWidth();
    double screenHeight = ScreenUtils.getScreenHeight();
    private ImageView backButton;
    private Rectangle gameBoardRect;
    private Board currentBoard;
    private Pane tilesLayer;
    private static final double TILE_HEIGHT = 80;

    private List<Tile> tiles;

    public GameFieldView() {
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
        this.currentBoard = board;
        tilesLayer.getChildren().clear();  // очищаем старые плитки

        // Получаем все активные плитки
        for (Tile tile : board.getActiveTiles()) {
            ImageView tileView = createTileImageView(tile);
            // Позиционируем по координатам из модели
            tileView.setTranslateX(tile.getX());
            tileView.setTranslateY(tile.getY());
            tilesLayer.getChildren().add(tileView);
        }

        System.out.println("Отображено плиток: " + board.getActiveCount());
    }

    /**
     * Создаёт визуальное представление одной плитки
     */
    private ImageView createTileImageView(Tile tile) {
        // 🔥 СТРОИМ ПУТЬ К КАРТИНКЕ
        String imagePath = buildImagePath(tile.getImageName());

        ImageView imageView = new ImageView();

        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(img);

            // 🔥 УСТАНАВЛИВАЕМ ВЫСОТУ, ШИРИНА ПОДСТРОИТСЯ АВТОМАТИЧЕСКИ
            imageView.setFitHeight(TILE_HEIGHT);
            imageView.setPreserveRatio(true);  // сохраняем пропорции

        } catch (Exception e) {
            // Если картинки нет — заглушка
            System.err.println("Не загружена картинка: " + imagePath);
            imageView.setStyle("-fx-background-color: #f5deb3; -fx-border-color: #8b4513; -fx-border-width: 2;");
            imageView.setFitHeight(TILE_HEIGHT);
            imageView.setFitWidth(60);  // примерная ширина для заглушки
        }

        // Эффект тени
        imageView.setEffect(new DropShadow(5, Color.BLACK));

        // Эффект при наведении
        imageView.setOnMouseEntered(e -> {
            imageView.setScaleX(1.05);
            imageView.setScaleY(1.05);
        });

        imageView.setOnMouseExited(e -> {
            imageView.setScaleX(1.0);
            imageView.setScaleY(1.0);
        });

        // Обработчик клика
        imageView.setOnMouseClicked(event -> {
            System.out.println("Клик по плитке: " + tile);
            tile.setRemoved(true);
            renderBoard(currentBoard);
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
        if (imageName.startsWith("cirles")) {
            String num = imageName.substring(6);
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

