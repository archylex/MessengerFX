package archylex.messenger.fx.Animation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import java.util.Random;

import static java.lang.Math.random;

public class ColorfulSquares {

    public Group ColorfulSquares(Pane winBoxSize) {
        Group squares = new Group();
        Random rand = new Random();
        double maxCount = 50;
        double minCount = 30;

        for (int i = 0; i < 50; i++) {
            double rectSize = (Math.random() * ((maxCount - minCount) + 1)) + minCount;
            float r = rand.nextFloat() / 2f + 0.5f;
            float g = rand.nextFloat() / 2f + 0.5f;
            float b = rand.nextFloat() / 2f + 0.5f;
            Color color = new Color(r, g, b, 1.0);
            Rectangle square = new Rectangle(rectSize, rectSize, Color.TRANSPARENT);
            square.setStrokeType(StrokeType.OUTSIDE);
            square.setStroke(color);
            square.setStrokeWidth(1);
            squares.getChildren().add(square);
        }

        squares.setEffect(new BoxBlur(3, 3, 3));

        Rectangle canvas = new Rectangle(winBoxSize.getWidth(), winBoxSize.getHeight(), Color.TRANSPARENT);
        Group root = new Group(canvas, squares);

        Timeline timeline = new Timeline();
        for (Node square : squares.getChildren()) {
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(square.translateXProperty(), random() * 540),
                            new KeyValue(square.translateYProperty(), random() * 660),
                            new KeyValue(square.opacityProperty(), rand.nextFloat())),
                    new KeyFrame(new Duration(10000),
                            new KeyValue(square.translateXProperty(), random() * 540),
                            new KeyValue(square.translateYProperty(), random() * 660),
                            new KeyValue(square.opacityProperty(), rand.nextFloat())));
        }
        timeline.setAutoReverse(true);
        timeline.setCycleCount(-1);
        timeline.play();

        return root;
    }
}