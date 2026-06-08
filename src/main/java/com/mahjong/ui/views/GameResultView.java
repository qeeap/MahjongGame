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

    public HBox buttonPanel;
    public VBox textContainer;

    public GameResultView() {
        setVisible(false);

        darkBackground = new Rectangle();
        darkBackground.setFill(Color.rgb(5, 37, 46, 0.7));

        //текст
        scoreText = new Text("Score: 0");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        scoreText.setFill(Color.rgb(11, 77, 106, 0.9));
        scoreText.setStrokeWidth(1.5);
        scoreText.setStyle("-fx-font-smoothing-type: gray;");

        maxComboText = new Text("MAX combo: 0");
        maxComboText.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        maxComboText.setFill(Color.rgb(11, 77, 106, 0.9));
        maxComboText.setStrokeWidth(1.5);
        maxComboText.setStyle("-fx-font-smoothing-type: gray;");

        //кнопки
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

        //контейнеры
        buttonPanel = new HBox(ScreenUtils.getScreenWidth() / 50);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.getChildren().addAll(retryButton, menuButton);

        textContainer = new VBox(ScreenUtils.getScreenWidth() / 100);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.getChildren().addAll(scoreText, maxComboText);

        resultImage = new ImageView();
        resultImage.setPreserveRatio(true);

        StackPane.setAlignment(buttonPanel, Pos.CENTER);
        StackPane.setAlignment(textContainer, Pos.CENTER);
        StackPane.setAlignment(resultImage, Pos.CENTER);

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
        //обновление
        scoreText.setText("Score: " + score);
        maxComboText.setText("MAX combo: " + maxCombo);

        //фон
        String imagePath = isVictory ? "/images/victory.png" : "/images/lose.png";
        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            resultImage.setImage(img);
            resultImage.setFitHeight(ScreenUtils.getScreenHeight() / 3);
        } catch (Exception e) {
            System.err.println("Не загружена картинка результата: " + imagePath);
            resultImage = new ImageView();
        }
        buttonPanel.setTranslateY(resultImage.getFitHeight()/3);
        textContainer.setTranslateY(resultImage.getFitHeight()/10);
        System.out.println(resultImage.getTranslateY() + resultImage.getFitWidth()*1.75);

        getChildren().clear();
        getChildren().addAll(darkBackground, resultImage, buttonPanel, textContainer);

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