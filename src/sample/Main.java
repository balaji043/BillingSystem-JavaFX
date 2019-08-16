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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.UI.Billing.BillingController;
import sample.UI.NewPurchaseBill.NewPurchaseBill;
import sample.UI.Root.RootController;
import sample.Utils.FxmlPaths;
import sample.Utils.GenericController;
import sample.Utils.Preferences;
import sample.alert.AlertMaker;
import sample.database.DatabaseHelper;
import sample.model.Bill;
import sample.model.User;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private final Logger logger = Logger.getLogger(Main.class.getName());
    public boolean isLoggedIn = false;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootController rootController = null;

    private User user = null;

    private JFXSpinner spinner = new JFXSpinner();
    private Scene scene;
    private String theme;

    public static void main(String[] args) {
        launch(args);
    }

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
        loader.setLocation(FxmlPaths.ROOT);

        try {
            loader.load();
            rootController = loader.getController();
            rootLayout = rootController.root;
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
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
            fxmlViewLoader(FxmlPaths.LOGIN);
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
            BillingController billingController = (BillingController) fxmlViewLoader(FxmlPaths.BILLING_CONTROLLER);
            if (bill != null && billingController != null) {
                billingController.setBill(bill, isIGstBill);
                this.rootController.window.setText("Edit bill");
            }
        }
    }

    public void initViewBills() {
        fxmlViewLoader(FxmlPaths.VIEW_BILL);
    }

    public void initCustomerPanel() {
        if (isLoggedIn) {
            fxmlViewLoader(FxmlPaths.CUSTOMER_PANEL);
        }
    }

    public void initUserPanel() {
        if (isLoggedIn) {
            fxmlViewLoader(FxmlPaths.USER_PANEL);
        }
    }

    public void initPurchaseBills() {
        if (isLoggedIn) {
            fxmlViewLoader(FxmlPaths.PURCHASE_BILL);
        }
    }

    public void initNewPurchaseBills() {
        if (isLoggedIn) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FxmlPaths.NEW_PURCHASE_BILL);
            try {
                StackPane root = loader.load();
                rootController.setContent(root);
                NewPurchaseBill newPurchaseBill = loader.getController();
                newPurchaseBill.setMainApp(Main.this);
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
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

    private GenericController fxmlViewLoader(URL url) {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        StackPane root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        GenericController controller = loader.getController();
        controller.setMainApp(Main.this);
        rootController.setContent(root);
        return controller;
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
                Preferences.getPreferences().getTheme() + "Theme.css").toExternalForm();
        scene.getStylesheets().add(theme);
    }

    public void addSpinner() {
        rootLayout.getChildren().add(spinner);
    }

    public void removeSpinner() {
        rootLayout.getChildren().remove(spinner);
    }

    public File chooseFile() {
        this.snackBar("", "Choose File", "green");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll
                (new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        return fileChooser.showSaveDialog(this.getPrimaryStage());
    }
}
