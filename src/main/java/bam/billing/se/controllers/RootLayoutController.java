package bam.billing.se.controllers;

import bam.billing.se.Main;
import bam.billing.se.utils.AlertMaker;
import bam.billing.se.utils.BillingSystemUtils;
import bam.billing.se.utils.ResourceConstants;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class RootLayoutController implements Initializable {

    @FXML
    public Label window;
    @FXML
    public VBox vBox;
    @FXML
    public BorderPane root;
    @FXML
    public StackPane home;
    @FXML
    public JFXButton addNewUser, r, s;
    @FXML
    private JFXHamburger ham;
    private int i = 0;
    private Main mainApp;
    private JFXDrawersStack stack = new JFXDrawersStack();
    private JFXDrawer drawer = new JFXDrawer();
    private HamburgerSlideCloseTransition hamTransition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        drawer.setSidePane(vBox);
        drawer.setDefaultDrawerSize(200);
        drawer.setOverLayVisible(false);
        drawer.setResizableOnDrag(false);
        drawer.setDirection(JFXDrawer.DrawerDirection.RIGHT);
        drawer.getSidePane().get(0).setStyle("-fx-background-color:-fx-base;");


        hamTransition = new HamburgerSlideCloseTransition(ham);
        hamTransition.setRate(-1);
        ham.addEventFilter(MouseEvent.MOUSE_PRESSED, (event ->
                toggle()
        ));
        int i = 25;
        BillingSystemUtils.setImageViewToButtons(ResourceConstants.Icons.REFRESH, r, i);
        BillingSystemUtils.setImageViewToButtons(ResourceConstants.Icons.GEAR, s, i);
    }

    private void setKeyShortCutAccelerators() {
        HashMap<KeyCodeCombination, Runnable> keyCodeCombinationRunnableHashMap = new HashMap<>();
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.TAB
                , KeyCombination.CONTROL_DOWN), this::toggle);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.N
                , KeyCombination.CONTROL_DOWN), this::handleNewBill1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.V
                , KeyCombination.CONTROL_DOWN), this::handleViewBill1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.C
                , KeyCombination.CONTROL_DOWN), this::handleCustomerPanel1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.U
                , KeyCombination.CONTROL_DOWN), this::handleUserPanel1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.L
                , KeyCombination.CONTROL_DOWN), this::handleLogout);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.H
                , KeyCombination.CONTROL_DOWN), this::handleHome);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.P
                , KeyCombination.CONTROL_DOWN), this::handlePurchaseBill);
        mainApp.getPrimaryStage().getScene().getAccelerators().putAll(keyCodeCombinationRunnableHashMap);
    }

    private void toggle() {
        hamTransition.setRate(hamTransition.getRate() * -1);
        hamTransition.play();
        stack.toggle(drawer);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        if (mainApp.getUser() != null &&
                !mainApp.isAdmin()) addNewUser.setDisable(true);
        setKeyShortCutAccelerators();
    }

    public void setContent(StackPane pane) {
        drawer.setContent(pane);
        root.setCenter(drawer);
    }

    public void handleHome() {
        setContent(home);
        i = 0;
        window.setText("HOME");
        toggle();
    }

    public void handleNewBill() {
        i = 1;
        window.setText("New Bill");
        toggle();
        mainApp.loadBillRegistrationView(null);
    }

    public void handleViewBill() {
        i = 2;
        window.setText("View Bill");
        toggle();
        mainApp.initViewBills();
    }

    public void handlePurchaseBill() {
        i = 6;
        window.setText("Purchase Bills");
        toggle();
        mainApp.initPurchaseBills();
    }

    public void handleCustomerPanel() {
        i = 3;
        window.setText("Customer Panel");
        toggle();
        mainApp.initCustomerPanel();
    }

    public void handleUserPanel() {
        i = 4;
        window.setText("User Panel");
        toggle();
        mainApp.initUserPanel();
    }

    public void handleLogout() {
        i = 5;
        mainApp.handleLogout();
    }

    private void handleNewBill1() {
        i = 1;
        window.setText("New Bill");
        mainApp.loadBillRegistrationView(null);
    }

    private void handleViewBill1() {
        i = 2;
        window.setText("View Bill");
        mainApp.initViewBills();
    }

    private void handleCustomerPanel1() {
        i = 3;
        window.setText("Customer Panel");
        mainApp.initCustomerPanel();
    }

    private void handleUserPanel1() {
        i = 4;
        window.setText("User Panel");
        mainApp.initUserPanel();
    }

    private void handlePurchaseBill1() {
        i = 6;
        window.setText("Purchase Bills");
        mainApp.initPurchaseBills();
    }

    @FXML
    public void handleRefresh() {
        switch (i) {
            case 0: {
                handleHome();
                return;
            }
            case 1: {
                handleNewBill1();
                return;
            }
            case 2: {
                handleViewBill1();
                return;
            }
            case 3: {
                handleCustomerPanel1();
                return;
            }
            case 4: {
                handleUserPanel1();
                return;
            }
            case 5: {
                handleLogout();
                return;
            }
            case 6: {
                handlePurchaseBill1();
                return;
            }
            default: {
                handleLogout();
            }
        }
    }

    @FXML
    public void handleSettings() {
        if (AlertMaker.showSettings(mainApp))
            mainApp.snackBar("Success", "Changes Saved Successfully", "green");
        else
            mainApp.snackBar("Cancelled", "Settings Change Cancelled", "green");

    }

    public void disableAddNewUserButton(boolean s) {
        addNewUser.setDisable(s);
    }

}
