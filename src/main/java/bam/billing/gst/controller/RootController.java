package bam.billing.gst.controller;

import bam.billing.gst.Main;
import bam.billing.gst.alert.AlertMaker;
import bam.billing.gst.utils.BillingSystemUtils;
import bam.billing.gst.utils.GenericController;
import bam.billing.gst.utils.ICON;
import bam.billing.gst.utils.StringUtil;
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

public class RootController implements Initializable, GenericController {

    @FXML
    public Label window;
    @FXML
    public VBox vBox;
    @FXML
    public BorderPane root;
    @FXML
    public StackPane home;
    @FXML
    public JFXButton addNewUser, refreshButton, settingsButton;
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


        BillingSystemUtils.setImageViewToButtons(ICON.REFRESH, refreshButton);
        BillingSystemUtils.setImageViewToButtons(ICON.SETTING, settingsButton);
    }

    private void setAccelerators() {
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

    public void toggle() {

        if (mainApp.isLoggedIn || drawer.isOpened()) {
            hamTransition.setRate(hamTransition.getRate() * -1);
            hamTransition.play();
            stack.toggle(drawer);
        }
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        if (mainApp.getUser() != null &&
                !mainApp.getUser().getAccess().equals("admin")) addNewUser.setDisable(true);
        setAccelerators();
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
        window.setText("New bill");
        toggle();
        mainApp.initNewBill(null, "");
    }

    public void handlePurchaseBill() {
        i = 6;
        window.setText("Purchase Bills");
        toggle();
        mainApp.initPurchaseBills();
    }

    public void handleViewBill() {
        i = 2;
        window.setText("View bill");
        toggle();
        mainApp.initViewBills();
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
        window.setText("New bill");
        mainApp.initNewBill(null, "");
    }

    private void handleViewBill1() {
        i = 2;
        window.setText("View bill");
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
        mainApp.snackBar("Refreshed", "page refreshed successfully", StringUtil.GREEN);
    }

    @FXML
    public void handleSettings() {
        if (AlertMaker.showSettings(mainApp))
            mainApp.snackBar(StringUtil.SUCCESS, "Changes Saved Successfully", StringUtil.GREEN);
        else
            mainApp.snackBar(StringUtil.CANCELLED, "Settings Change Cancelled", StringUtil.GREEN);

    }

    public void disableAddNewUserButton(boolean s) {
        addNewUser.setDisable(s);
    }


}
