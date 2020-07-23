package bam.billing.se.controllers;

import bam.billing.se.Main;
import bam.billing.se.helpers.DatabaseHelper_PurchaseBill;
import bam.billing.se.utils.BillingSystemUtils;
import bam.billing.se.utils.Preferences;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.util.HashSet;

import static bam.billing.se.utils.ResourceConstants.Icons;

public class PurchaseBillRegistration {

    public JFXButton addBTN, deleteBTN, submitBTN;
    public JFXListView<PurchaseBillController> listView;
    public JFXComboBox<String> typeComboBox;
    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        typeComboBox.getItems().addAll("Standard Enterprises", "Standard Equipments");

        BillingSystemUtils.setImageViewToButtons(Icons.ADD, addBTN);
        BillingSystemUtils.setImageViewToButtons(Icons.DELETE, deleteBTN);

    }

    public void handleAddBill() {
        if (typeComboBox.getValue() == null) {
            mainApp.snackBar("INFO", "SELECT TYPE FIRST", "green");
            return;
        }
        PurchaseBillController purchaseBill = new PurchaseBillController();
        listView.getItems().add(purchaseBill);
        setSlNo();
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
            setSlNo();
        }
    }

    public void handleSubmit() {

        if (typeComboBox.getValue() == null) {
            mainApp.snackBar("INFO", "SELECT TYPE FIRST", "green");
            return;
        }
        boolean ready = true;
        int noOfPurchaseBills = listView.getItems().size();
        for (PurchaseBillController b : listView.getItems()) {
            ready = ready && b.isReady();
        }
        HashSet<String> companyNames = Preferences.getPreferences().getCompanyNames();
        if (ready) {
            ObservableList<PurchaseBillController> s = listView.getItems();
            ObservableList<PurchaseBillController> toRemove = FXCollections.observableArrayList();
            boolean singleResult;
            for (PurchaseBillController bill : s) {
                singleResult = DatabaseHelper_PurchaseBill.insertNewPurchaseBill(bill.getPurchaseBill(), DatabaseHelper_PurchaseBill.getTableName(typeComboBox.getValue()));
                ready = ready && singleResult;
                if (singleResult) {
                    toRemove.add(bill);
                } else {
                    mainApp.snackBar("Error"
                            , "Another PurchaseBill exist with same invoice and Customer Name"
                            , "red");
                    bill.markRed();
                }
                companyNames.add(bill.getPurchaseBill().getCompanyName());
            }
            for (PurchaseBillController r : toRemove)
                listView.getItems().remove(r);
            if (ready) {
                mainApp.snackBar("SUCCESS", "Successfully Added " + noOfPurchaseBills + " Purchased Bills", "green");
                listView.getItems().clear();
                Preferences preferences = Preferences.getPreferences();
                preferences.setCompanyNames(companyNames);
                Preferences.setPreference(preferences);
            }
        } else
            mainApp.snackBar("INFO", "Check the Fields marked in Red", "red");
        if (listView.getItems().size() != 0) setSlNo();
    }

    public void handleBack() {
        mainApp.initPurchaseBills();
    }

    @FXML
    private void handleCalculate() {
        for (PurchaseBillController bill : listView.getItems()) {
            bill.setAmountAfterText();
        }
    }

    private void setSlNo() {
        int i = 1;
        for (PurchaseBillController b :
                listView.getItems())
            b.setSlNoText(String.format("%2d", i++));

    }
}
