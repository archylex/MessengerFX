package archylex.messenger.fx.Utils;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class Picture {
    public static Circle PicToCircle(String url, int radius) {
        Image im = null;
        Circle result = new Circle(50,50, radius);
        result.setStroke(Color.WHITE);

        try {
            im = new Image(url, false);
        } catch (Exception e){
            im = new Image("/resources/images/noavatar.png", false);
        }

        result.setFill(new ImagePattern(im));

        return result;
    }
}
