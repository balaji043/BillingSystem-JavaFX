package bam.billing.nongst.controllers;

import bam.billing.nongst.Main;
import bam.billing.nongst.helpers.DatabaseHelper_User;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

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
                if (DatabaseHelper_User.valid(user, password)) {
                    mainApp.snackBar("", "Welcome " + user, "green");
                    mainApp.setUser(DatabaseHelper_User.getUserInfo(user));
                    mainApp.loadMenuView();
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
            e.printStackTrace();
        }
    }

    public void setMainApp(Main mainApp) {
        textFieldUserName.requestFocus();
        this.mainApp = mainApp;
        validator.setMessage("*");
        mainApp.getPrimaryStage().getScene().setOnKeyPressed(e -> {
            if (!mainApp.isLoggedIn() && e.getCode() == KeyCode.ENTER) {
                handleSignIn();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
