package sample.UI.NewPurchaseBill;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
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
    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
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
            ready = ready && b.isReady();
        }
        HashSet<String> companyNames = Preferences.getPreferences().getCompanyNames();
        if (ready) {
            ObservableList<SinglePurchaseBill> s = listView.getItems();
            ObservableList<SinglePurchaseBill> toRemove = FXCollections.observableArrayList();
            boolean singleResult;
            for (SinglePurchaseBill bill : s) {
                singleResult = DatabaseHelper_PurchaseBill.insertNewPurchaseBill(bill.getPurchaseBill());
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
}
