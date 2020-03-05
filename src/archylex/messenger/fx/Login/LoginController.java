package archylex.messenger.fx.Login;

import archylex.messenger.fx.GlobalVariables.Variables;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import archylex.messenger.fx.Animation.ColorfulSquares;


public class LoginController implements Initializable {
    LoginModel loginModel = new LoginModel();

    @FXML
    private TextField loginText;

    @FXML
    private PasswordField passField;

    @FXML
    private Label errorLabel;

    @FXML
    private AnchorPane errorPane;

    @FXML
    private Label createButton;

    @FXML
    private Pane aniPane;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {

        setAnimatedBackground();

        if(!this.loginModel.isDBConnected()) {
            errorLabel.setText("DB file not found.");
            errorPane.setDisable(false);
            errorPane.setVisible(true);
        }
    }

    @FXML
    public void loginButton(ActionEvent actionEvent) {
        try {
            if(this.loginModel.isLogin(loginText.getText(), passField.getText())) {
                Variables.user = loginText.getText();
                int id = this.loginModel.getID(loginText.getText(), passField.getText());
                Variables.id = id;
                Variables.email = this.loginModel.getEmail(id);
                Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
                loadMessenger();
            }
            else {
                errorLabel.setText("Login or password is incorrect");
                errorPane.setDisable(false);
                errorPane.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("DB file is incorrect.");
            errorPane.setDisable(false);
            errorPane.setVisible(true);
        }
    }

    @FXML
    public void createButton() {
        try {
            Stage stage = (Stage) this.createButton.getScene().getWindow();
            stage.close();
          loadRegistration();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void errorClose() {
        loginText.setText("");
        passField.setText("");
        errorPane.setDisable(true);
        errorPane.setVisible(false);
    }

    private void loadMessenger() {
        try {
            Stage stage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("/archylex/messenger/fx/Messenger/Messenger.fxml"));
            stage.setScene(new Scene(pane));
            stage.setTitle("MessengerFX");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRegistration() {
        try {
            Stage stage = new Stage();
            Pane pane = FXMLLoader.load(getClass().getResource("/archylex/messenger/fx/Registration/Registration.fxml"));
            stage.setScene(new Scene(pane));
            stage.setTitle("Registration");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAnimatedBackground() {
        ColorfulSquares cs =  new ColorfulSquares();
        aniPane.getChildren().add(cs.ColorfulSquares(aniPane));
    }

}
