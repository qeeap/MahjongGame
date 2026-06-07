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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GameFieldView - экран игры
 */
public class GameFieldView extends Pane {

    private ImageView backButton; //каринка кнопки "Вернуться"
    private ImageView scoreImg; //каринка счета
    private Text scoreText; //текущий счет
    private Rectangle gameBoardRect; //прямоугольник на фоне
    private Board currentBoard; //текущая доска
    private Pane tilesLayer; //все плитки на доске (поле плиток)
    private int tileHeight; //высота плитки (размер)
    private int tileWidth; //ширина плитки(размер)

    /**
     * Обработчик клика по плитке.
     * Устанавливается контроллером через {@link #setOnTileClick}.
     */
    private java.util.function.Consumer<Tile> onTileClickHandler; //коллбек клика

    private ImageView highlightedTileView = null;  // запоминаем какую плитку подсветили

    private int originalMinX; //максимально левая плитка на поле
    private int originalMaxX; //максимально правая плитка на поле
    private int originalMinY;  //максимально верхняя плитка на поле
    private int originalMaxY;  //максимально нижняя плитка на поле
    private boolean boundsInitialized = false;  //флаг для расположения


    /**
    * Генератор
     * Задает размер плитки
     * Задает область плиток
     */
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


    /**
     * Загрузка всех изображений
     * Создание фона для поля
     */
    private void createUI() {

        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        //кнопка "Назад"
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

        //кнопка счета
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/score.png"));
            scoreImg = new ImageView(img);
            scoreImg.setFitWidth(screenWidth / 12);
            scoreImg.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Картинка счета не загружена: /images/score.png");
            scoreImg = new ImageView();
        }

        //текст счета
        scoreText = new Text("0");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        scoreText.setFill(Color.rgb(11, 77, 106, 0.9));
        scoreText.setStrokeWidth(1.5);
        scoreText.setStyle("-fx-font-smoothing-type: gray;");

        //фон для поля
        gameBoardRect = new Rectangle();
        gameBoardRect.setWidth(screenWidth * 0.85);
        gameBoardRect.setHeight(screenHeight * 0.9);
        gameBoardRect.setArcWidth(50);
        gameBoardRect.setArcHeight(50);
        gameBoardRect.setFill(Color.rgb(11, 77, 106, 0.9));
    }


    /**
     * Позиции всех изображений
     */
    private void positionUI() {

        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        backButton.setTranslateX(screenWidth / 30);
        backButton.setTranslateY(screenHeight / 30);

        positionScoreElements();

        //прямоугольник
        gameBoardRect.setTranslateX((screenWidth - gameBoardRect.getWidth()) / 2);
        gameBoardRect.setTranslateY((screenHeight - gameBoardRect.getHeight()) / 2);

        getChildren().add(gameBoardRect);   // 1. прямоугольник
        getChildren().add(tilesLayer);      // 2. слой для плиток
        getChildren().add(scoreImg);        // 3. картинка счета
        getChildren().add(scoreText);       // 4. текст счёта
        getChildren().add(backButton);      // 5. кнопка
    }


    /**
     * Позиционирует картинку и текст счёта
     */
    private void positionScoreElements() {
        double screenWidth = ScreenUtils.getScreenWidth();
        double screenHeight = ScreenUtils.getScreenHeight();

        StackPane.setAlignment(scoreImg, Pos.TOP_LEFT);
        scoreImg.setTranslateX(screenHeight / 30);
        scoreImg.setTranslateY(screenHeight / 7);

        StackPane.setAlignment(scoreText, Pos.TOP_LEFT);

        double imgX = scoreImg.getTranslateX();
        double imgY = scoreImg.getTranslateY();
        double imgWidth = scoreImg.getFitWidth();
        double imgHeight = scoreImg.getFitHeight();

        double textWidth = scoreText.getBoundsInLocal().getWidth();
        double textHeight = scoreText.getBoundsInLocal().getHeight();

        scoreText.setTranslateX(imgX + (imgWidth - textWidth) / 2);
        scoreText.setTranslateY(imgY + (imgHeight  + textHeight) / 2);
    }

    /**
     * Обновляет отображение счёта
     */
    public void updateScore(int score) {
        if (scoreText != null) {
            scoreText.setText(String.valueOf(score));

            double imgX = scoreImg.getTranslateX();
            double imgY = scoreImg.getTranslateY();
            double imgWidth = scoreImg.getBoundsInLocal().getWidth();
            double imgHeight = scoreImg.getBoundsInLocal().getHeight();

            double textWidth = scoreText.getBoundsInLocal().getWidth();
            double textHeight = scoreText.getBoundsInLocal().getHeight();

            scoreText.setTranslateX(imgX + (imgWidth - textWidth) / 2);
            scoreText.setTranslateY(imgY + (imgHeight  + textHeight) / 2);
        }
    }

    /**
     * Рендер доски
     * Находим центр уровня (по плиткам)
     * Помечаем флаг отображения
     * Располагаем плитки
     * Располагаем фон (gameBoardRect)
     */
    public void renderBoard(Board board) {
        System.out.println(tileHeight);
        this.currentBoard = board;
        tilesLayer.getChildren().clear(); //очищаем старые плитки

        List<Tile> activeTiles = board.getActiveTiles();
        if (activeTiles.isEmpty()) {
            System.out.println("Нет активных плиток для отображения");
            return;
        }

        if (!boundsInitialized) {
            //границы доски в координатах плиток
            originalMinX = Integer.MAX_VALUE;
            originalMaxX = Integer.MIN_VALUE;
            originalMinY = Integer.MAX_VALUE;
            originalMaxY = Integer.MIN_VALUE;

            for (Tile tile : activeTiles) {
                int x = tile.getX();
                int y = tile.getY();
                if (x < originalMinX) originalMinX = x;
                if (x > originalMaxX) originalMaxX = x;
                if (y < originalMinY) originalMinY = y;
                if (y > originalMaxY) originalMaxY = y;
            }
            boundsInitialized = true;

            System.out.println("Исходные границы: minX=" + originalMinX + ", maxX=" + originalMaxX +
                    ", minY=" + originalMinY + ", maxY=" + originalMaxY);
        }

        //просто задаем все плитки
        List<ImageView> tileViews = new ArrayList<>();
        for (Tile tile : activeTiles) {
            ImageView tileView = createTileImageView(tile);
            tileViews.add(tileView);
        }

        //размеры поля
        int tilesInRow = originalMaxX - originalMinX + 1;
        int tilesInColumn = originalMaxY - originalMinY + 1;
        double boardPixelWidth = tilesInRow * tileWidth;
        double boardPixelHeight = tilesInColumn * tileHeight;

        //располагаем прямоугольник
        double boardRectX = gameBoardRect.getTranslateX();
        double boardRectY = gameBoardRect.getTranslateY();
        double boardRectWidth = gameBoardRect.getWidth();
        double boardRectHeight = gameBoardRect.getHeight();

        //центрирование прямоугольника и плиток
        double startX = boardRectX + (boardRectWidth - boardPixelWidth) / 2;
        double startY = boardRectY + (boardRectHeight - boardPixelHeight) / 2;

        System.out.println("Реальный размер плитки: " + (int)tileWidth + " x " + (int)tileHeight);
        System.out.println("Доска: " + tilesInRow + " x " + tilesInColumn + " плиток");
        System.out.println("Старт: (" + (int)startX + ", " + (int)startY + ")");

        //непосредственно добавление
        for (int i = 0; i < activeTiles.size(); i++) {
            Tile tile = activeTiles.get(i);
            ImageView tileView = tileViews.get(i);
            double pixelX = startX + (tile.getX() - originalMinX) * tileWidth;
            double pixelY = startY + (tile.getY() - originalMinY) * tileHeight;

            //реальзация смещения
            String shift = tile.getShift();
            if ("down".equals(shift)) {
                pixelY += tileHeight / 2;
            } else if ("right_down".equals(shift)) {
                pixelX += tileWidth / 2;
                pixelY += tileHeight / 2;
            } else if ("right".equals(shift)) {
                pixelX += tileWidth / 2;
            }

            //учитываем слой (верхние плитки чуть выше и левее)
            pixelY -= tile.getZ() * 4;
            pixelX -= tile.getZ() * 4;

            tileView.setTranslateX(pixelX);
            tileView.setTranslateY(pixelY);

            tilesLayer.getChildren().add(tileView);
        }

        System.out.println("Отображено плиток: " + activeTiles.size());
    }


    /**
     * Коллбек клика по кнопке для контроллера
     */
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

        //вот тут тень
        imageView.setEffect(new DropShadow(15, Color.BLACK));

        imageView.setOnMouseClicked(event -> {
            System.out.println("Клик по плитке: " + tile);
            if (onTileClickHandler != null) {
                onTileClickHandler.accept(tile);  //передаём плитку в контроллер
            }
        });

        imageView.setUserData(tile);  // запоминаем для какой плитки этот ImageView

        return imageView;
    }

    /***
     * Затемняет плитку так пышпышпыш
     */
    public void setTileDarkened(Tile tile, boolean dark) {
        for (javafx.scene.Node node : tilesLayer.getChildren()) {
            if (node instanceof ImageView) {
                ImageView iv = (ImageView) node;
                if (iv.getUserData() == tile) {
                    if (dark) {
                        // Только затемнение
                        javafx.scene.effect.ColorAdjust darken = new javafx.scene.effect.ColorAdjust();
                        darken.setBrightness(-0.4);  // затемнение на 40%
                        iv.setEffect(darken);
                    } else {
                        // Возвращаем обычную тень
                        iv.setEffect(new DropShadow(15, Color.BLACK));
                    }
                    break;
                }
            }
        }
    }

    /**
     * Убираем флаг при выходе из уровня
     */
    public void resetBounds() {
        boundsInitialized = false;
    }


    /**
     * Строит путь к картинке по имени из JSON
     */
    private String buildImagePath(String imageName) {
        String basePath = "/tiles/";

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
    /**
     * Геттеры для контроллера
     */
    public ImageView getBackButton() { return backButton; }
}

