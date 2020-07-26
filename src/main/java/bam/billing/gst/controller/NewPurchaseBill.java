package bam.billing.gst.controller;

import bam.billing.gst.Main;
import bam.billing.gst.service.PurchaseBillService;
import bam.billing.gst.utils.BillingSystemUtils;
import bam.billing.gst.utils.GenericController;
import bam.billing.gst.utils.ICON;
import bam.billing.gst.utils.Preferences;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.util.Set;

public class NewPurchaseBill implements GenericController {

    public JFXButton addBTN, deleteBTN, submitBTN;
    public JFXListView<SinglePurchaseBillController> listView;
    public JFXButton backBTN;
    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        BillingSystemUtils.setImageViewToButtons(ICON.ADD, addBTN);
        BillingSystemUtils.setImageViewToButtons(ICON.MINUS, deleteBTN);
        BillingSystemUtils.setImageViewToButtons(ICON.BACK, backBTN);
    }

    public void handleAddBill() {
        SinglePurchaseBillController purchaseBill = new SinglePurchaseBillController();
        listView.getItems().add(purchaseBill);
        setSlNo();
    }

    public void handleDeleteBill() {
        if (listView.getSelectionModel().getSelectedItem() == null)
            mainApp.snackBar("INFO", "Select a row First", "green");
        else
            listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
        setSlNo();
    }

    public void handleSubmit() {
        boolean ready = true;
        int noOfPurchaseBills = listView.getItems().size();
        for (SinglePurchaseBillController b : listView.getItems()) {
            ready = b.isReady() && ready;
        }
        Set<String> companyNames = Preferences.getPreferences().getCompanyNames();
        if (ready) {
            ObservableList<SinglePurchaseBillController> s = listView.getItems();
            ObservableList<SinglePurchaseBillController> toRemove = FXCollections.observableArrayList();
            boolean singleResult;
            for (SinglePurchaseBillController bill : s) {
                singleResult = PurchaseBillService.insertNewPurchaseBill(bill.getPurchaseBill());
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
            for (SinglePurchaseBillController r : toRemove)
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

    private void setSlNo() {
        int i = 1;
        for (SinglePurchaseBillController b :
                listView.getItems())
            b.setSlNoText(String.format("%2d", i++));

    }

    @FXML
    private void handleCalculate() {
        for (SinglePurchaseBillController bill : listView.getItems()) {
            bill.setAmountAfterText();
        }
    }
}
