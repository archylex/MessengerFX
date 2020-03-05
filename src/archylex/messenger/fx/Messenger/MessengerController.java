package archylex.messenger.fx.Messenger;

import archylex.messenger.fx.GlobalVariables.Variables;
import archylex.messenger.fx.Skype.SkypeWebPlugin;
import archylex.messenger.fx.VK.VKPlugin;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

import static archylex.messenger.fx.DB.Contacts.*;
import static archylex.messenger.fx.DB.Messages.*;
import static archylex.messenger.fx.Utils.Picture.PicToCircle;
import static archylex.messenger.fx.Utils.Timestamp.getTimeStamp;


public class MessengerController implements Initializable {
    @FXML
    private Label nicknameLabel;
    @FXML
    private Label nameInfoLabel;
    @FXML
    private Button dehaze_button;
    @FXML
    private Button infoMenuButton;
    @FXML
    private Button vkMenuButton;
    @FXML
    private Button skypeMenuButton;
    @FXML
    private Button fbMenuButton;
    @FXML
    private Button tgMenuButton;
    @FXML
    private Button settingMenuButton;
    @FXML
    private Button contactsMenuButton;
    @FXML
    private Button logoutMenuButton;
    @FXML
    public VBox vbSidebarMain;
    @FXML
    private VBox menuSidebar;
    @FXML
    private VBox shadowBox;
    @FXML
    private VBox chatLogs;
    @FXML
    private HBox avaCircle;
    @FXML
    public AnchorPane pnlRoot;
    @FXML
    private VBox contactListBox;
    @FXML
    private TextField messageField;
    @FXML
    private Button vkHeaderButton;
    @FXML
    private Button skypeHeaderButton;

    public static ListView<clMessage> listView;
    public ObservableList<clMessage> messages;
    public static ListView<Person> contactsListView;
    private ObservableList<Person> contactsObservableList;

    private FadeTransition ft;
    private TranslateTransition openNav;
    private TranslateTransition closeNav;
    private TranslateTransition closeFastNav;

    VKPlugin vkp;

    SkypeWebPlugin skype;

    Recipient recipient;

    MessengerAccount mAccount;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        recipient = new Recipient();

        mAccount = MessengerAccount.getInstance();

        openNav = new TranslateTransition(Duration.millis(100), menuSidebar);
        openNav.setToX(menuSidebar.getTranslateX()-menuSidebar.getWidth());

        closeNav = new TranslateTransition(Duration.millis(100), menuSidebar);
        closeFastNav = new TranslateTransition(Duration.millis(.1), menuSidebar);

        shadowBox.setDisable(true);

        ft = new FadeTransition(Duration.millis(100), shadowBox);
        ft.setFromValue(0.6);
        ft.setToValue(0.0);
        ft.play();

        dehaze_button.getStyleClass().add("dehaze");
        dehaze_button.setPickOnBounds(true);
        Region icon = new Region();
        icon.getStyleClass().add("icon");
        dehaze_button.setGraphic(icon);

        dehaze_button.setOnAction((ActionEvent evt) -> {
            sideBarShow();
        });


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                closeFastNav.setToX(-(menuSidebar.getWidth()));
                closeFastNav.play();
                menuSidebar.setVisible(false);
            }
        });

        avaCircle.getChildren().add(PicToCircle("", 25));
        avaCircle.setPadding(new Insets(10,0,0,25));

        nicknameLabel.setText(Variables.user);
        nicknameLabel.setTranslateX(25);
        nicknameLabel.setTranslateY(20);

        nameInfoLabel.setText(Variables.email);
        nameInfoLabel.setTranslateX(25);
        nameInfoLabel.setTranslateY(24);

        setMenuButton("Settings","settings-icon", settingMenuButton);
        setMenuButton("Info","info-icon", infoMenuButton);
        setMenuButton("VK","vk-icon", vkMenuButton);
        setMenuButton("Skype","skype-icon", skypeMenuButton);
        setMenuButton("Facebook","fb-icon", fbMenuButton);
        setMenuButton("Telegram","tg-icon", tgMenuButton);
        setMenuButton("Contacts", "edit-icon", contactsMenuButton);
        setMenuButton("Logout", "logout-icon", logoutMenuButton);

        // Contact List
        setMenuButton("", "vk-icon", vkHeaderButton);
        setMenuButton("", "skype-icon", skypeHeaderButton);

        contactsObservableList = FXCollections.observableArrayList();
        contactsListView = new ListView<Person>(contactsObservableList);
        contactsListView.setCellFactory(new Callback<ListView<Person>, ListCell<Person>>() {
            @Override
            public ListCell<Person> call(ListView<Person> list) {
                return new ContactListCell();
            }
        });

        contactsListView.getStyleClass().add("chat-list");

        VBox.setVgrow(contactsListView, Priority.ALWAYS);

        contactsListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Person> observableValue, Person customVKContact, Person t1) -> {
            recipient.setRecipient(observableValue.getValue().getSNID(), observableValue.getValue().getSNName());

            messages.clear();

            Person person = getContactByIDFromDB(recipient.getSocialNetworkName(), recipient.getPersonID());

            for (clMessage message : getMessagesByBranch(person.getSNName(), person.getSNID())) {
                messages.add(message);
            }

            if (recipient.getSocialNetworkName().equals("Skype")) {
                try {
                    List<clMessage> msgs = skype.getMessagesFromUserID(person.getSNID());
                    for (clMessage msg : msgs) {
                        messages.add(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        contactListBox.getChildren().addAll(contactsListView);


        vkHeaderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        skypeHeaderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        messages = FXCollections.observableArrayList();

        listView = new ListView<clMessage>(messages);

        listView.setCellFactory(new Callback<ListView<clMessage>, ListCell<clMessage>>() {
            @Override
            public ListCell<clMessage> call(ListView<clMessage> listView) {
                return new MessageListCell();
            }
        });

        listView.getStyleClass().add("chat-list");

        chatLogs.getChildren().addAll(listView);

        VBox.setVgrow(listView, Priority.ALWAYS);

        messageField = new TextField();
        messageField.setPromptText("Write something...");
        messageField.getStyleClass().add("write-message-field");

        Button sendButton = new Button();
        sendButton.getStyleClass().add("write-icon-button");
        sendButton.setPickOnBounds(true);
        Region icon_region = new Region();
        icon_region.getStyleClass().add("send-icon");
        sendButton.setGraphic(icon_region);

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!messageField.getText().isEmpty()) {
                    clMessage new_msg = new clMessage(recipient.getSocialNetworkName(), recipient.getPersonID(), mAccount.getID(recipient.getSocialNetworkName()), messageField.getText(), getTimeStamp());

                    messages.add(new_msg);

                    addMessageToDB(new_msg);

                    if (recipient.getSocialNetworkName().equalsIgnoreCase("VK")) {
                        vkp.sendMessage(Integer.valueOf(recipient.getPersonID()), messageField.getText());
                    } else if (recipient.getSocialNetworkName().equalsIgnoreCase("Skype")) {
                        try {
                            skype.sendMessage(messageField.getText(), recipient.getPersonID());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    messageField.setText("");
                }
            }
        });

        HBox writeFieldBox = new HBox(messageField, sendButton);
        HBox.setHgrow(messageField,Priority.ALWAYS);
        writeFieldBox.getStyleClass().add("write-box");
        writeFieldBox.prefHeight(50);

        writeFieldBox.setAlignment(Pos.CENTER_LEFT);

        chatLogs.getChildren().add(writeFieldBox);

        closeNav.statusProperty().addListener(new ChangeListener<Animation.Status>() {
            @Override
            public void changed(ObservableValue<? extends Animation.Status> observableValue,
                                Animation.Status oldValue, Animation.Status newValue) {
                if(newValue == Animation.Status.STOPPED){
                    if(closeNav.getToX() == -(menuSidebar.getWidth())) {
                        menuSidebar.setVisible(false);
                        shadowBox.setDisable(true);
                    }
                }
            }
        });


        logoutMenuButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.close();
                loadLogin();
            }
        });

        vkMenuButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (vkp == null) {
                    vkp = new VKPlugin("login", "password");

                    if (vkp.isAuthorized()) {
                        List<Person> friends = vkp.getFriends();

                        // Check contact list
                        for (Person friend : friends) {
                            if (!isContactFromDB(friend.getSNName(), friend.getSNID()))
                                addContactToDB(friend);
                        }

                        // Load contact list from database
                        // Chat can exist with a person who is not in friends
                        String snname = "VK";

                        for (Person contact : getContactListFromDB(snname)) {
                            contactsObservableList.add(contact);
                        }

                        Person myprofile = vkp.getUser();
                        mAccount.setID(snname, myprofile.getSNID().toString());
                        mAccount.setName(snname, myprofile.getName() + " " + myprofile.getLastname());
                        mAccount.setPhotoURL(snname, myprofile.getPhotoURL());

                        recipient.setRecipient(mAccount.getID(snname), snname);

                        avaCircle.getChildren().set(0, PicToCircle(mAccount.getPhotoURL(snname), 25));
                        nicknameLabel.setText(mAccount.getName(snname));

                        startTask();
                    }
                }
            }
        });

        skypeMenuButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (skype == null) {
                    String login = "login@hotmail.com";
                    String password = "password";

                    skype = new SkypeWebPlugin(login, password);

                    try {
                        // Check contact list
                        for (Person contact : skype.getContactList()) {
                            if (!isContactFromDB(contact.getSNName(), contact.getSNID()))
                                addContactToDB(contact);
                        }

                        // Load contact list from database
                        String snname = "Skype";

                        for (Person contact : getContactListFromDB(snname)) {
                            contactsObservableList.add(contact);
                        }

                        Person myprofile = skype.getMyProfile();
                        mAccount.setID(snname, myprofile.getSNID().toString());
                        mAccount.setName(snname, myprofile.getName() + " " + myprofile.getLastname());
                        mAccount.setPhotoURL(snname, myprofile.getPhotoURL());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static Map<String, String> splitQuery(String url) throws UnsupportedEncodingException {
        url = url.substring(url.indexOf("#")+1);
        url.trim();
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = url.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    private void loadLogin() {
        try {
            Stage stage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("/archylex/messenger/fx/Login/Login.fxml"));
            stage.setScene(new Scene(pane));
            stage.setTitle("Login");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMenuButton(String text, String icon, Button button) {
        button.getStyleClass().add("sidebar-icon-button");
        button.setPickOnBounds(true);
        Region icon_region = new Region();
        icon_region.getStyleClass().add(icon);
        button.setGraphic(icon_region);
        button.setText(text);
    }

    @FXML
    public void sideBarHide() {
        ft.setFromValue(0.6);
        ft.setToValue(0.0);
        ft.play();

        dehaze_button.getStyleClass().remove("sidebar-button-active");
        dehaze_button.getStyleClass().add("sidebar-button");

        closeNav.setToX(-(menuSidebar.getWidth()));
        closeNav.play();
    }

    @FXML
    public void sideBarShow() {
        if ((menuSidebar.getTranslateX()) == -(menuSidebar.getWidth()) ) {
            shadowBox.setDisable(false);
            menuSidebar.setVisible(true);

            ft.setFromValue(0.0);
            ft.setToValue(0.6);
            ft.play();

            dehaze_button.getStyleClass().remove("sidebar-button");
            dehaze_button.getStyleClass().add("sidebar-button-active");

            openNav.play();
        } else {
            sideBarHide();
        }
    }

    public void startTask()
    {
        Runnable task = new Runnable()
        {
            public void run()
            {
                longPollMonitor();
            }
        };

        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public void longPollMonitor() {
        while (true) {
            try {
                Message msg = vkp.getMessage();

                if (msg != null) {
                    Person person = vkp.getPersonByID(msg.getFromId());
                    final Boolean contactExist = isContactFromDB(person.getSNName(), msg.getFromId().toString());

                    if (!contactExist)
                        addContactToDB(person);

                    final clMessage new_message = new clMessage(person.getSNName(), msg.getFromId().toString(), msg.getFromId().toString(), msg.getText(), new java.sql.Timestamp(msg.getDate()));

                    addMessageToDB(new_message);

                    Platform.runLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // Check contact
                            if (!contactExist)
                                contactsObservableList.add(person);

                            messages.add(new_message);
                        }
                    });
                }

                Thread.sleep(10000);
            } catch (ClientException e) {
                e.printStackTrace();
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
