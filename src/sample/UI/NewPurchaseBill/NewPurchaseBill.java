package sample.UI.NewPurchaseBill;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import sample.Main;
import sample.Utils.BillingSystemUtils;
import sample.Utils.GenericController;
import sample.Utils.ICON;
import sample.Utils.Preferences;
import sample.custom.single.purchasebill.SinglePurchaseBill;
import sample.database.DatabaseHelperPurchaseBill;

import java.util.Set;

public class NewPurchaseBill implements GenericController {

    public JFXButton addBTN, deleteBTN, submitBTN;
    public JFXListView<SinglePurchaseBill> listView;
    public JFXButton backBTN;
    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        BillingSystemUtils.setImageViewToButtons(ICON.ADD, addBTN);
        BillingSystemUtils.setImageViewToButtons(ICON.MINUS, deleteBTN);
        BillingSystemUtils.setImageViewToButtons(ICON.BACK, backBTN);
    }

    public void handleAddBill() {
        SinglePurchaseBill purchaseBill = new SinglePurchaseBill();
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
        for (SinglePurchaseBill b : listView.getItems()) {
            ready = b.isReady() && ready;
        }
        Set<String> companyNames = Preferences.getPreferences().getCompanyNames();
        if (ready) {
            ObservableList<SinglePurchaseBill> s = listView.getItems();
            ObservableList<SinglePurchaseBill> toRemove = FXCollections.observableArrayList();
            boolean singleResult;
            for (SinglePurchaseBill bill : s) {
                singleResult = DatabaseHelperPurchaseBill.insertNewPurchaseBill(bill.getPurchaseBill());
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
            for (SinglePurchaseBill r : toRemove)
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
        for (SinglePurchaseBill b :
                listView.getItems())
            b.setSlNoText(String.format("%2d", i++));

    }

    @FXML
    private void handleCalculate() {
        for (SinglePurchaseBill bill : listView.getItems()) {
            bill.setAmountAfterText();
        }
    }
}
