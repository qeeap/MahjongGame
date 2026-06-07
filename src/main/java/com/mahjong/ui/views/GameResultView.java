package com.mahjong.ui.views;

import com.mahjong.ui.utils.ScreenUtils;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Плашка результата игры (победа/поражение)
 */
public class GameResultView extends StackPane {

    private ImageView resultImage;
    private ImageView retryButton;
    private ImageView menuButton;
    private Runnable onRetryAction;
    private Runnable onMenuAction;
    private Rectangle darkBackground;
    private Text scoreText;
    private Text maxComboText;
    private VBox container;

    public GameResultView() {
        setAlignment(Pos.CENTER);
        setVisible(false);

        darkBackground = new Rectangle();
        darkBackground.setFill(Color.rgb(5, 37, 46, 0.7));

        // ========== ТЕКСТЫ ==========
        scoreText = new Text("Счёт: 0");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        scoreText.setFill(Color.rgb(11, 77, 106, 0.9));
        scoreText.setStrokeWidth(1.5);
        scoreText.setStyle("-fx-font-smoothing-type: gray;");

        maxComboText = new Text("Макс комбо: 0");
        maxComboText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        maxComboText.setFill(Color.rgb(11, 77, 106, 0.9));
        maxComboText.setStrokeWidth(1.5);
        maxComboText.setStyle("-fx-font-smoothing-type: gray;");

        // ========== КНОПКИ ==========
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/tryagain.png"));
            retryButton = new ImageView(img);
            retryButton.setFitWidth(ScreenUtils.getScreenWidth() / 5);
            retryButton.setPreserveRatio(true);
            retryButton.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'Заново' не загружена: /images/tryagain.png");
            retryButton = new ImageView();
        }

        try {
            Image img = new Image(getClass().getResourceAsStream("/images/return.png"));
            menuButton = new ImageView(img);
            menuButton.setFitWidth(ScreenUtils.getScreenWidth() / 5);
            menuButton.setPreserveRatio(true);
            menuButton.setStyle("-fx-cursor: hand;");
        } catch (Exception e) {
            System.err.println("Кнопка 'В меню' не загружена: /images/return.png");
            menuButton = new ImageView();
        }

        // ========== КОНТЕЙНЕРЫ ==========
        HBox buttonPanel = new HBox(ScreenUtils.getScreenWidth() / 50);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.getChildren().addAll(retryButton, menuButton);

        VBox textContainer = new VBox(15);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.getChildren().addAll(scoreText, maxComboText);

        container = new VBox(25);
        container.setAlignment(Pos.CENTER);

        getChildren().addAll(darkBackground, container);
    }

    /**
     * Обновляет размеры при изменении окна
     */
    public void resize(double width, double height) {
        darkBackground.setWidth(width);
        darkBackground.setHeight(height);
    }

    /**
     * Показывает плашку с результатом
     */
    public void showResult(int score, int maxCombo, boolean isVictory) {
        // Обновляем текст
        scoreText.setText("Счёт: " + score);
        maxComboText.setText("Макс комбо: " + maxCombo);

        // Загружаем нужную картинку
        String imagePath = isVictory ? "/images/victory.png" : "/images/lose.png";
        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            resultImage = new ImageView(img);
            resultImage.setPreserveRatio(true);
            resultImage.setFitWidth(ScreenUtils.getScreenWidth() * 0.6);
        } catch (Exception e) {
            System.err.println("Не загружена картинка результата: " + imagePath);
            resultImage = new ImageView();
        }

        // Пересобираем контейнер
        HBox buttonPanel = new HBox(ScreenUtils.getScreenWidth() / 50);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.getChildren().addAll(retryButton, menuButton);

        VBox textContainer = new VBox(15);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.getChildren().addAll(scoreText, maxComboText);

        container.getChildren().clear();
        container.getChildren().addAll(resultImage, textContainer, buttonPanel);

        setVisible(true);
    }

    /**
     * Показывает победу
     */
    public void showVictory(int score, int maxCombo) {
        showResult(score, maxCombo, true);
    }

    /**
     * Показывает поражение
     */
    public void showGameOver(int score, int maxCombo) {
        showResult(score, maxCombo, false);
    }

    /**
     * Скрывает плашку
     */
    public void hide() {
        setVisible(false);
    }

    /**
     * Устанавливает действие для кнопки "Заново"
     */
    public void setOnRetry(Runnable action) {
        this.onRetryAction = action;
        if (retryButton != null) {
            retryButton.setOnMouseClicked(event -> {
                if (onRetryAction != null) onRetryAction.run();
            });
        }
    }

    /**
     * Устанавливает действие для кнопки "В меню"
     */
    public void setOnMenu(Runnable action) {
        this.onMenuAction = action;
        if (menuButton != null) {
            menuButton.setOnMouseClicked(event -> {
                if (onMenuAction != null) onMenuAction.run();
            });
        }
    }
}