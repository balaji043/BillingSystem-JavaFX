package sample.UI.Login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import sample.Main;
import sample.Utils.BillingSystemUtils;
import sample.Utils.GenericController;
import sample.Utils.ICON;
import sample.database.DatabaseHelperUser;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LoginController implements Initializable, GenericController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    @FXML
    private ImageView mainImageView;

    @FXML
    private ImageView userImageView;

    @FXML
    private ImageView passwordImageView;

    @FXML
    private JFXTextField textFieldUserName;

    @FXML
    private JFXPasswordField textFieldPassword;


    private RequiredFieldValidator validator = new RequiredFieldValidator();

    private Main mainApp;


    @FXML
    private void handleSignIn() {
        try {
            String user = textFieldUserName.getText(), password = textFieldPassword.getText();
            if (!user.isEmpty() && !password.isEmpty()) {
                if (DatabaseHelperUser.valid(user, password)) {
                    mainApp.snackBar("", "Welcome " + user, "green");
                    mainApp.setUser(DatabaseHelperUser.getUserInfo(user));
                    mainApp.initMenuLayout();
                    textFieldUserName.setText("");
                    textFieldPassword.setText("");
                } else {
                    mainApp.snackBar("", "Wrong!\nTry Again!", "red");
                }
            } else {
                if (textFieldUserName.getText().isEmpty() &&
                        textFieldPassword.getText().isEmpty()) {
                    mainApp.snackBar("", "Enter Both Fields", "red");
                    textFieldPassword.validate();
                    textFieldPassword.validate();
                } else if (textFieldPassword.getText().isEmpty()) {
                    mainApp.snackBar("", "Enter Password", "red");
                    textFieldPassword.validate();
                } else {
                    mainApp.snackBar("", "Enter UserName", "red");
                    textFieldUserName.validate();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public void setMainApp(Main mainApp) {

        setImages();
        validator.setMessage("*");
        this.mainApp = mainApp;
        mainApp.getPrimaryStage().getScene().setOnKeyPressed(e -> {
            if (!mainApp.isLoggedIn && e.getCode() == KeyCode.ENTER) {
                handleSignIn();
            }
        });
    }

    private void setImages() {
        BillingSystemUtils.setImageToImageViews(ICON.KRISH, mainImageView);
        BillingSystemUtils.setImageToImageViews(ICON.USER, userImageView);
        BillingSystemUtils.setImageToImageViews(ICON.PASSWORD, passwordImageView);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldUserName.requestFocus();
        textFieldUserName.getValidators().add(validator);
        textFieldUserName.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) textFieldUserName.validate();
        });

        textFieldPassword.getValidators().add(validator);
        textFieldPassword.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) textFieldPassword.validate();
        });
    }

}
