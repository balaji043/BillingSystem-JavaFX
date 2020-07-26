package bam.billing.gst;

import bam.billing.gst.alert.AlertMaker;
import bam.billing.gst.controller.BillingController;
import bam.billing.gst.controller.RootController;
import bam.billing.gst.model.Bill;
import bam.billing.gst.model.User;
import bam.billing.gst.service.DatabaseHelper;
import bam.billing.gst.utils.BillingSystemUtils;
import bam.billing.gst.utils.GenericController;
import bam.billing.gst.utils.ICON;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static bam.billing.gst.utils.ResourceConstants.CSS;
import static bam.billing.gst.utils.ResourceConstants.Views;
import static bam.billing.gst.utils.StringUtil.TITLE;

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

        FXMLLoader loader = Views.getFXMLLoaderWithUrl(Views.ROOT);

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
        primaryStage.getIcons().add(BillingSystemUtils.getImage(ICON.KRISH));

        scene = new Scene(decorator, 1080, 720);

        decorator.setTitle(TITLE);
        setNewStyle();

        primaryStage.setScene(scene);
        primaryStage.setTitle(TITLE);
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
        rootController.toggle();
        fxmlViewLoader(Views.LOGIN, true);
    }

    public void initMenuLayout() {
        isLoggedIn = true;
        rootLayout.getTop().setVisible(true);
        rootController.handleViewBill();
    }

    public void initNewBill(Bill bill, String isIGstBill) {
        GenericController controller = fxmlViewLoader(Views.BILLING_LAYOUT, false);
        if (bill != null && controller instanceof BillingController) {
            BillingController billingController = (BillingController) controller;
            billingController.setBill(bill, isIGstBill);
            this.rootController.window.setText("Edit bill");
        }
    }

    public void initViewBills() {
        fxmlViewLoader(Views.VIEW_BILL, false);
    }

    public void initCustomerPanel() {
        fxmlViewLoader(Views.CUSTOMER_PANEL, false);
    }

    public void initUserPanel() {
        fxmlViewLoader(Views.USER_PANEL, false);
    }

    public void initPurchaseBills() {
        fxmlViewLoader(Views.PURCHASE_BILL, false);
    }

    public void initNewPurchaseBills() {
        fxmlViewLoader(Views.NEW_PURCHASE_BILL, false);
    }

    public void handleLogout() {
        if (isLoggedIn && AlertMaker.showMCAlert("Confirm logout?"
                , "Are you sure you want to Logout?"
                , Main.this)) {
            isLoggedIn = false;
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

    private GenericController fxmlViewLoader(String path, boolean isLoginPage) {
        if (!isLoggedIn && !isLoginPage) {
            initLoginLayout();
        }

        FXMLLoader loader = Views.getFXMLLoaderWithUrl(path);
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

    public void setNewStyle() {
        //remove current style
        scene.getStylesheets().remove(theme);
        //getting new style
        theme = CSS.getCSS();
        //adding new style
        scene.getStylesheets().add(theme);
    }

    public File chooseFile() {
        this.snackBar("", "Choose File", "green");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll
                (new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        return fileChooser.showSaveDialog(this.getPrimaryStage());
    }
}
