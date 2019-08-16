package sample.UI.UserPanel;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import sample.Main;
import sample.Utils.GenericController;
import sample.Utils.Preferences;
import sample.Utils.StringUtil;
import sample.alert.AlertMaker;
import sample.database.DatabaseHelperUser;
import sample.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserPanelController implements GenericController {
    private static final Logger LOGGER = Logger.getLogger(UserPanelController.class.getName());
    @FXML
    private JFXTextField name, username;
    @FXML
    private JFXPasswordField passwordField, passwordField1;
    @FXML
    private JFXComboBox<String> accessComboBox;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TableView<User> userTableView;

    private Main mainApp;
    private User user = null;
    private boolean isNewUser = true;
    private String[] access = {"admin", "employee"};
    private String emp = null;
    private RequiredFieldValidator validator = new RequiredFieldValidator();

    public void setMainApp(Main main) {
        accessComboBox.getItems().addAll(access);
        this.mainApp = main;
        initTable();
        validator.setMessage("Cannot Delete Admin");
        accessComboBox.getValidators().add(validator);
        userTableView.setOnMouseClicked(e -> {
            user = userTableView.getSelectionModel().getSelectedItem();
            clearAll();
            if (user == null) return;
            isNewUser = false;
            name.setText(user.getName());
            emp = user.getId();
            accessComboBox.getSelectionModel().select(user.getAccess());
            username.setText(user.getUserName());
            passwordField.setText(user.getPassword());
        });
    }

    private void initTable() {

        userTableView.getItems().clear();
        userTableView.getColumns().clear();

        addTableColumn("Name", "name", 100);
        addTableColumn("Employee ID", "id", 101);
        addTableColumn("User Name", "userName", 100);
        addTableColumn("Access", "Access", 100);

        userTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        loadTable();
        borderPane.setCenter(userTableView);
    }

    private void loadTable() {
        userTableView.getItems().clear();
        userTableView.getItems().addAll(DatabaseHelperUser.getUserList());
    }

    @FXML
    void handleAddNow() {
        if (isNewUser) {
            if (name.getText() == null || name.getText().isEmpty()
                    || username.getText() == null || username.getText().isEmpty()
                    || passwordField.getText() == null || passwordField.getText().isEmpty()
                    || accessComboBox.getValue() == null || accessComboBox.getValue().isEmpty()) {
                if (name.getText() == null || name.getText().isEmpty()) {
                    name.validate();
                }
                if (username.getText() == null || username.getText().isEmpty()) {
                    username.validate();
                }
                if (passwordField.getText() == null || passwordField.getText().isEmpty()) {
                    passwordField.validate();
                }
                if (accessComboBox.getValue() == null || accessComboBox.getValue().isEmpty()) {
                    accessComboBox.validate();
                }
            } else {
                user = new User("" + name.getText()
                        , "" + username.getText()
                        , "EMP" + new SimpleDateFormat("yyyyMMddHHSSS").format(new Date())
                        , "" + passwordField.getText()
                        , "" + accessComboBox.getValue());
                if (DatabaseHelperUser.insertNewUser(user)) {
                    mainApp.snackBar(StringUtil.SUCCESS, "User Added Successfully", StringUtil.GREEN);
                    clearAll();
                } else {
                    mainApp.snackBar(StringUtil.FAILED, "User Not Added", StringUtil.RED);
                }
            }
        } else {
            if (name.getText() == null
                    || username.getText() == null
                    || passwordField.getText() == null
                    || accessComboBox.getValue() == null) {
                if (name.getText() == null || name.getText().isEmpty()) {
                    name.validate();
                }
                if (username.getText() == null || username.getText().isEmpty()) {
                    username.validate();
                }
                if (passwordField.getText() == null || passwordField.getText().isEmpty()) {
                    passwordField.validate();
                }
                if (accessComboBox.getValue() == null || accessComboBox.getValue().isEmpty()) {
                    accessComboBox.validate();
                }
            } else {
                user = new User("" + name.getText()
                        , "" + username.getText()
                        , emp
                        , "" + passwordField.getText()
                        , "" + accessComboBox.getValue());
                if (DatabaseHelperUser.updateUser(user)) {
                    clearAll();
                    mainApp.snackBar(StringUtil.SUCCESS, "User Data Updated Successfully", StringUtil.GREEN);
                } else {
                    mainApp.snackBar(StringUtil.FAILED
                            , "User Data Not Updated Successfully", StringUtil.RED);

                }
            }
        }
        loadTable();
    }

    @FXML
    public void handleBack() {
        try {
            User users = userTableView.getSelectionModel().getSelectedItem();
            boolean okay;
            if (users == null) return;
            if (user != null && users.getAccess().equals("admin")) {
                mainApp.snackBar("Failed", "Cannot Delete Admin User", "red");
                return;
            }
            okay = AlertMaker.showMCAlert("Confirm delete?"
                    , "Are you sure you want to delete" + users.getName() + "'settingsButton data"
                    , mainApp);

            if (okay) {
                if (DatabaseHelperUser.deleteUser(users)) {
                    mainApp.snackBar(StringUtil.SUCCESS
                            , "Selected User'settingsButton data is deleted"
                            , StringUtil.GREEN);
                    clearAll();
                } else {
                    mainApp.snackBar(StringUtil.FAILED
                            , "Selected User'settingsButton data is not deleted"
                            , StringUtil.RED);
                    clearAll();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        loadTable();
    }

    @FXML
    public void handleSpecial() {
        if (!passwordField1.getText().isEmpty()) {
            Preferences preferences = Preferences.getPreferences();
            Preferences.setPreference(preferences);
            mainApp.snackBar(StringUtil.SUCCESS, "You special password is changed now.", StringUtil.GREEN);
        }
    }

    private void addTableColumn(String name, String msg, int width) {
        TableColumn<User, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(msg));
        column.setPrefWidth(width);
        userTableView.getColumns().add(column);
    }

    private void clearAll() {
        name.clear();
        username.clear();
        passwordField.clear();
        name.setPromptText("Name");
        passwordField.setPromptText("Password");
        accessComboBox.getSelectionModel().clearSelection();
        accessComboBox.setPromptText("Grant Access");
        isNewUser = true;
    }
}
