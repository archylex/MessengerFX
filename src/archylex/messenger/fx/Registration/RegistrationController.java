package archylex.messenger.fx.Registration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import archylex.messenger.fx.DB.Connetion;
import static archylex.messenger.fx.Utils.Encrypt.encodeData;

public class RegistrationController {

    @FXML
    private TextField loginText;

    @FXML
    private PasswordField passField;

    @FXML
    private PasswordField repassField;

    @FXML
    private TextField emailText;

    @FXML
    private Label errorLabel;

    @FXML
    private AnchorPane errorPane;

    private Connetion connetion;

    public void initialize(URL url, ResourceBundle rb) {
        this.connetion = new Connetion();
    }

    @FXML
    private void addUser(ActionEvent actionEvent) throws SQLException {

        if (this.loginText.getText().isEmpty()) {
            showError("User field is empty.");
        } else {
            boolean userExists = false;
            String sql = "SELECT * FROM users_data";
            Connection conn = Connetion.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                if(this.loginText.getText().equals(rs.getString(2)))
                    userExists = true;
            }

            if (!userExists) {

                if (repassField.getText().equals(passField.getText())) {
                    sql = "INSERT INTO users_data(USER,PASSWORD,EMAIL) VALUES(?,?,?)";

                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, this.loginText.getText());
                    ps.setString(2, encodeData(this.loginText.getText(), this.passField.getText()));
                    ps.setString(3, this.emailText.getText());
                    ps.execute();
                    conn.close();

                    Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
                    stage.close();
                    loadLogin();

                } else {
                    showError("Passwords not equals.");
                }
            } else {
                showError("User exists.");
            }
        }
    }

    @FXML
    public void backButton(ActionEvent actionEvent) {
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
        loadLogin();
    }

    @FXML
    public void errorClose() {
        loginText.setText("");
        passField.setText("");
        errorPane.setDisable(true);
        errorPane.setVisible(false);
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

    private void showError(String txt) {
        errorLabel.setText(txt);
        errorPane.setDisable(false);
        errorPane.setVisible(true);
    }

}
