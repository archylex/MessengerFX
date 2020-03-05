package archylex.messenger.fx.Messenger;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

import static archylex.messenger.fx.Utils.Picture.PicToCircle;

public class ContactListCell extends ListCell<Person> {
    private HBox content;
    private String photo_url = null;
    private Label nameLabel;

    public ContactListCell() {
        super();

        nameLabel = new Label();
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.JUSTIFY);

        content = new HBox(PicToCircle(photo_url, 10), nameLabel);
        content.setSpacing(10);
        content.setFillHeight(true);
        content.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void updateItem(Person item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null && !empty) {
            nameLabel.setText(item.getName() + " " + item.getLastname());
            content.getChildren().set(0, PicToCircle(item.getPhotoURL(), 12));
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }
}
