package bam.billing.se;

import bam.billing.se.controllers.*;
import bam.billing.se.helpers.DatabaseHelper;
import bam.billing.se.models.Bill;
import bam.billing.se.models.User;
import bam.billing.se.utils.AlertMaker;
import bam.billing.se.utils.BillingSystemUtils;
import bam.billing.se.utils.Message;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static bam.billing.se.utils.Constants.*;
import static bam.billing.se.utils.ResourceConstants.CSS;
import static bam.billing.se.utils.ResourceConstants.Icons.STD_ENT;
import static bam.billing.se.utils.ResourceConstants.Views;


public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;


    private boolean isLoggedIn = false;
    private RootLayoutController rootLayoutController = null;
    private User user = null;
    private Scene scene;
    private String theme;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        DatabaseHelper.create();
        initializeRootLayoutWithMainAppInstance();
        initializeSceneWithDecoratorAndCSS();
        initializeStageWithAttributes();
        rootLayoutController.setMainApp(Main.this);
        loadLoginView();
    }

    public void initializeStageWithAttributes() {
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(BillingSystemUtils.getImage(STD_ENT));
        this.primaryStage.setOnCloseRequest(event -> {
            event.consume();
            handleClose();
        });
        primaryStage.show();
    }

    private JFXDecorator getJfxDecoratorWithRootLayoutAndPrimaryStage() {
        JFXDecorator decorator = new JFXDecorator(primaryStage, rootLayout);
        decorator.setCustomMaximize(true);
        decorator.setMaximized(true);
        return decorator;
    }

    private void initializeSceneWithDecoratorAndCSS() {
        JFXDecorator decorator = getJfxDecoratorWithRootLayoutAndPrimaryStage();
        scene = new Scene(decorator, 1080, 720);
        scene.getStylesheets().add(CSS.getCSS());
    }

    private void initializeRootLayoutWithMainAppInstance() {
        FXMLLoader loader = BillingSystemUtils.getFXMLLoader(Views.ROOT_PAGE);
        try {
            loader.load();
            rootLayoutController = loader.getController();
            rootLayout = rootLayoutController.root;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLoginView() {
        user = null;
        isLoggedIn = false;
        rootLayout.getTop().setVisible(false);
        if (!isLoggedIn) {
            FXMLLoader loader = BillingSystemUtils.getFXMLLoader(Views.LOGIN_PAGE);
            try {
                StackPane root = loader.load();
                rootLayout.setCenter(root);
                LoginViewController rootController = loader.getController();
                rootController.setMainApp(Main.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadMenuView() {
        isLoggedIn = true;
        rootLayout.getTop().setVisible(true);
        if (isLoggedIn)
            rootLayoutController.handleViewBill();
    }

    public void loadBillRegistrationView(Bill bill) {
        if (isLoggedIn) {
            FXMLLoader loader = BillingSystemUtils.getFXMLLoader(Views.BILLING);
            try {
                StackPane root = loader.load();
                rootLayoutController.setContent(root);
                BillRegistrationController rootController = loader.getController();
                rootController.setMainApp(Main.this);
                if (bill != null) {
                    rootController.setBill(bill);
                    this.rootLayoutController.window.setText(MenuNames.MODIFY_BILL);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initViewBills() {
        if (isLoggedIn()) {
            FXMLLoader loader = BillingSystemUtils.getFXMLLoader(Views.BILLS_PANEL);
            try {
                StackPane root = loader.load();
                rootLayoutController.setContent(root);
                BillPanelController rootController = loader.getController();
                rootController.setMainApp(Main.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initCustomerPanel() {
        if (isLoggedIn) {
            FXMLLoader loader = BillingSystemUtils.getFXMLLoader(Views.CUSTOMER_PANEL);
            try {
                StackPane root = loader.load();
                rootLayoutController.setContent(root);
                CustomerPanelController rootController = loader.getController();
                rootController.setMainApp(Main.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initUserPanel() {
        if (isLoggedIn) {
            FXMLLoader loader = BillingSystemUtils.getFXMLLoader(Views.USER_PANEL);
            try {
                StackPane root = loader.load();
                rootLayoutController.setContent(root);
                UserPanelController rootController = loader.getController();
                rootController.setMainApp(Main.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initPurchaseBills() {
        if (isLoggedIn) {
            FXMLLoader loader = BillingSystemUtils.getFXMLLoader(Views.PURCHASE_BILLS_PANEL);
            try {
                StackPane root = loader.load();
                rootLayoutController.setContent(root);
                PurchaseBillsPanelController rootController = loader.getController();
                rootController.setMainApp(Main.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initNewPurchaseBills() {
        if (isLoggedIn) {
            FXMLLoader loader = BillingSystemUtils.getFXMLLoader(Views.PURCHASE_BILL_REGISTRATION);
            try {
                StackPane root = loader.load();
                rootLayoutController.setContent(root);
                PurchaseBillRegistration rootController = loader.getController();
                rootController.setMainApp(Main.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleLogout() {
        if (isLoggedIn && AlertMaker.showMCAlert(Message.LOG_OUT, Main.this)) {
            rootLayout.setLeft(null);
            loadLoginView();
        }
    }

    private void handleClose() {
        if (AlertMaker.showMCAlert(Message.CLOSE_WINDOW, Main.this)) {
            primaryStage.close();
        }
    }

    public void handleRefresh() {
        rootLayoutController.handleRefresh();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        rootLayoutController.disableAddNewUserButton(!isAdmin());
    }

    public boolean isAdmin() {
        return user != null && user.getAccess().equalsIgnoreCase(UserRole.ADMIN);
    }

    public void snackBar(Message message) {
        snackBar(message.title, message.message, message.color.style);
    }

    public void snackBar(String title, String msg, SnackBarColor color) {
        snackBar(title, msg, color.style);
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

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

}
