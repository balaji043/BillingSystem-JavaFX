package sample.UI.NewPurchaseBill;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sample.Alert.AlertMaker;
import sample.CustomUI.SinglePurchaseBill.SinglePurchaseBill;
import sample.Database.DatabaseHelper_PurchaseBill;
import sample.Main;
import sample.Utils.Preferences;

import java.util.HashSet;

public class NewPurchaseBill {

    public JFXButton addBTN, deleteBTN, submitBTN;
    public JFXListView<SinglePurchaseBill> listView;
    public JFXComboBox<String> typeComboBox;
    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        typeComboBox.getItems().addAll("Standard Enterprises", "Standard Equipments");
        int i = 35;
        ImageView view1 = null, view2 = null;
        try {
            view1 = new ImageView(new Image(Main.class.
                    getResourceAsStream("Resources/icons/add.png")
                    , i, i, true, true));
            view2 = new ImageView(new Image(Main.class.
                    getResourceAsStream("Resources/icons/delete.png")
                    , i, i, true, true));

        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        if (view1 != null)
            addBTN.setGraphic(view1);
        if (view2 != null)
            deleteBTN.setGraphic(view2);
    }

    public void handleAddBill() {
        if (typeComboBox.getValue() == null) {
            mainApp.snackBar("INFO", "SELECT TYPE FIRST", "green");
            return;
        }
        SinglePurchaseBill purchaseBill = new SinglePurchaseBill();
        purchaseBill.setSlNoText(String.format("%2d", listView.getItems().size() + 1));
        listView.getItems().add(purchaseBill);
    }

    public void handleDeleteBill() {
        if (typeComboBox.getValue() == null) {
            mainApp.snackBar("INFO", "SELECT TYPE FIRST", "green");
            return;
        }
        if (listView.getSelectionModel().getSelectedItem() == null) {
            mainApp.snackBar("INFO", "Select a row First", "green");
        } else {
            listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
            int i = 1;
            for (SinglePurchaseBill bill : listView.getItems())
                bill.setSlNoText("" + (i++));
        }
    }

    public void handleSubmit() {

        if (typeComboBox.getValue() == null) {
            mainApp.snackBar("INFO", "SELECT TYPE FIRST", "green");
            return;
        }
        boolean ready = true;
        int noOfPurchaseBills = listView.getItems().size();
        for (SinglePurchaseBill b : listView.getItems()) {
            ready = ready && b.isReady();
        }
        HashSet<String> companyNames = Preferences.getPreferences().getCompanyNames();
        if (ready) {
            ObservableList<SinglePurchaseBill> s = listView.getItems();
            for (SinglePurchaseBill bill : s) {
                ready = ready && DatabaseHelper_PurchaseBill.insertNewPurchaseBill(bill.getPurchaseBill(), DatabaseHelper_PurchaseBill.getTableName(typeComboBox.getValue()));
                companyNames.add(bill.getPurchaseBill().getCompanyName());
            }
            if (ready) {
                mainApp.snackBar("SUCCESS", "Successfully Added " + noOfPurchaseBills + " Purchased Bills", "green");
                listView.getItems().clear();
                Preferences preferences = Preferences.getPreferences();
                preferences.setCompanyNames(companyNames);
                Preferences.setPreference(preferences);
            }
        } else
            mainApp.snackBar("INFO", "Check the Fields marked in Red", "red");
    }

    public void handleBack() {
        mainApp.initPurchaseBills();
    }


}
