package archylex.messenger.fx.Messenger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static archylex.messenger.fx.DB.Contacts.getContactByIDFromDB;
import static archylex.messenger.fx.Utils.Picture.PicToCircle;

public class MessageListCell extends ListCell<clMessage> {
    private HBox content;
    private Text name;
    private Label messageLabel;
    private VBox textBox;
    private int posElement;

    public MessageListCell() {
        super();

        name = new Text();

        posElement = 0;

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.JUSTIFY);

        textBox = new VBox(name, messageLabel);
        textBox.getStyleClass().add("message-box");
        textBox.setPadding(new Insets(10,10,10,10));

        content = new HBox(PicToCircle(null, 25), textBox);
        content.setAlignment(Pos.TOP_LEFT);
        content.setSpacing(10);
    }

    @Override
    protected void updateItem(clMessage item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null && !empty) {
            Person contact = getContactByIDFromDB(item.getSNName(), item.getSenderID());

            messageLabel.setText(item.getText());

            messageLabel.maxWidthProperty().bind(this.getListView().widthProperty().subtract(130));

            if (item.getSenderID().equals(item.getBranchID())) {
                name.setText(contact.getName() + " " + contact.getLastname());
                content.getChildren().set(posElement, PicToCircle(contact.getPhotoURL(), 25));

                textBox.toBack();
                content.setAlignment(Pos.TOP_RIGHT);
                posElement = 1;
            } else {
                MessengerAccount mAccount = MessengerAccount.getInstance();
                name.setText(mAccount.getName(item.getSNName()));
                content.getChildren().set(posElement, PicToCircle(mAccount.getPhotoURL(item.getSNName()), 25));

                textBox.toFront();
                content.setAlignment(Pos.TOP_LEFT);
                posElement = 0;
            }

            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }
}