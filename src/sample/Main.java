package sample;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.Alert.AlertMaker;
import sample.Database.DatabaseHelper;
import sample.Model.Bill;
import sample.Model.User;
import sample.UI.Billing.BillingController;
import sample.UI.CustomerPanel.CustomerPanelController;
import sample.UI.Login.LoginController;
import sample.UI.Root.RootController;
import sample.UI.UserPanel.UserPanelController;
import sample.UI.ViewBills.ViewBillsController;
import sample.Utils.Preferences;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    public boolean isLoggedIn = false;

    private RootController rootController = null;

    private User user = null;

    private JFXSpinner spinner = new JFXSpinner();

    public static void main(String[] args) {
        launch(args);
    }

    private Scene scene;

    private String theme;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setOnCloseRequest(event -> {
            event.consume();
            handleClose();
        });
        double d = 50;
        spinner.setMaxSize(d, d);
        spinner.setPrefSize(d, d);
        DatabaseHelper.create();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("UI/Root/Root.fxml"));

        try {
            loader.load();
            rootController = loader.getController();
            rootLayout = rootController.root;
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFXDecorator decorator = new JFXDecorator(primaryStage, rootLayout);
        decorator.setCustomMaximize(true);
        decorator.setMaximized(true);
        primaryStage.getIcons().add(new Image(Main.class.
                getResourceAsStream("Resources/icons/KrisEnt.png")));

        scene = new Scene(decorator, 1080, 720);

        decorator.setTitle("Krishna Enterprises");
        scene.getStylesheets().add(Main.class.getResource("Resources/CSS/" +
                Preferences.getPreferences().getTheme() + "Theme.css")
                .toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

        rootController.setMainApp(Main.this);

        try {
            initLoginLayout();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
    }

    public void initLoginLayout() {
        user = null;
        isLoggedIn = false;
        rootLayout.getTop().setVisible(false);
        if (!isLoggedIn) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("UI/Login/Login.fxml"));
            try {
                StackPane root = loader.load();
                rootLayout.setCenter(root);
                LoginController rootController = loader.getController();
                rootController.setMainApp(Main.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initMenuLayout() {
        isLoggedIn = true;
        rootLayout.getTop().setVisible(true);
        if (isLoggedIn)
            rootController.handleViewBill();
    }

    public void initNewBill(Bill bill, String isIGstBill) {
        if (isLoggedIn) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("UI/Billing/Billing.fxml"));
            try {
                StackPane root = loader.load();
                rootController.setContent(root);
                BillingController rootController = loader.getController();
                rootController.setMainApp(Main.this);
                if (bill != null) {
                    rootController.setBill(bill, isIGstBill);
                    this.rootController.window.setText("Edit Bill");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initViewBills() {
        if (isLoggedIn) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("UI/ViewBills/ViewBills.fxml"));
            try {
                StackPane root = loader.load();
                rootController.setContent(root);
                ViewBillsController rootController = loader.getController();
                rootController.setMainApp(Main.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initCustomerPanel() {
        if (isLoggedIn) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("UI/CustomerPanel/CustomerPanel.fxml"));
            try {
                StackPane root = loader.load();
                rootController.setContent(root);
                CustomerPanelController rootController = loader.getController();
                rootController.setMainApp(Main.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initUserPanel() {
        if (isLoggedIn) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("UI/UserPanel/UserPanel.fxml"));
            try {
                StackPane root = loader.load();
                rootController.setContent(root);
                UserPanelController rootController = loader.getController();
                rootController.setMainApp(Main.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleLogout() {
        if (isLoggedIn && AlertMaker.showMCAlert("Confirm logout?"
                , "Are you sure you want to Logout?"
                , Main.this)) {
            rootLayout.setLeft(null);
            initLoginLayout();
        }
    }

    private void handleClose() {
        if (AlertMaker.showMCAlert("Confirm exit?"
                , "Are you sure you want to exit?"
                , Main.this)) {
            primaryStage.close();
        }
    }

    public void handleRefresh() {
        rootController.handleRefresh();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        rootController.disableAddNewUserButton(!user.getAccess().equalsIgnoreCase("admin"));
        this.user = user;
    }

    public void snackBar(String title, String msg, String color) {
        Label header = new Label(title);
        Text body = new Text(msg);
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        if (!title.isEmpty())
            dialogLayout.setHeading(header);
        dialogLayout.setBody(body);
        StackPane pop = new StackPane(dialogLayout);
        if (color.equals("red")) color = "#ffcccc";
        else color = "#ccffcc";
        pop.setStyle("-fx-background-color:" + color + ";");
        pop.setPrefWidth(1080);
        pop.setPrefHeight(30);
        JFXSnackbar bar = new JFXSnackbar(rootLayout);

        bar.enqueue(new JFXSnackbar.SnackbarEvent(pop));
    }

    public void setStyle() {
        scene.getStylesheets().remove(theme);
        theme = Main.class.getResource("Resources/CSS/" +
                Preferences.getPreferences().getTheme() + "Theme.css")
                .toExternalForm();
        scene.getStylesheets().add(theme);
    }

    public void addSpinner() {
        rootLayout.getChildren().add(spinner);
    }

    public void removeSpinner() {
        rootLayout.getChildren().remove(spinner);
    }
}
