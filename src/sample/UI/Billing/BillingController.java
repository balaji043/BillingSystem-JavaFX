package sample.UI.Billing;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;
import sample.Alert.AlertMaker;
import sample.CustomUI.SingleProduct.SingleProduct;
import sample.Database.DatabaseHelper;
import sample.Main;
import sample.Model.Bill;
import sample.Model.Customer;
import sample.Model.Product;
import sample.Utils.Preferences;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class BillingController {

    private Main mainApp;

    private Customer customer = null;

    private Bill bill = null;

    private boolean ready = false;

    @FXML
    private JFXCheckBox checkBoxGST, checkIGST, isManual;

    @FXML
    private JFXComboBox<String> comboBoxCustomer;

    @FXML
    private JFXListView<SingleProduct> listView;

    @FXML
    private Text labelBillFor, totalAmount;

    @FXML
    private BorderPane borderPane, root;

    @FXML
    private JFXDatePicker manualDate;
    private String tableName = "Bills";

    private ObservableList<String> gst = FXCollections.observableArrayList();
    private ObservableList<String> nonGst = FXCollections.observableArrayList();
    private Preferences preferences = Preferences.getPreferences();
    private int limit = Integer.parseInt(preferences.getLimit());


    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        borderPane.setVisible(false);
        borderPane.setDisable(true);

        checkBoxGST.setSelected(true);
        manualDate.setDisable(true);

        gst = DatabaseHelper.getCustomerNameList(0);
        nonGst = DatabaseHelper.getCustomerNameList(1);

        comboBoxCustomer.setItems(gst);
        TextFields.bindAutoCompletion(comboBoxCustomer.getEditor(), comboBoxCustomer.getItems());


        checkBoxGST.setOnAction(e -> {
            comboBoxCustomer.getItems().clear();
            if (checkBoxGST.isSelected())
                comboBoxCustomer.setItems(gst);
            else
                comboBoxCustomer.setItems(nonGst);
            TextFields.bindAutoCompletion(comboBoxCustomer.getEditor(), comboBoxCustomer.getItems());

        });
        isManual.setOnAction(e -> {
            if (isManual.isSelected())
                manualDate.setDisable(false);
            else
                manualDate.setDisable(true);

        });
        checkIGST.setOnAction(e -> {
            if (checkIGST.isSelected())
                tableName = "IBills";
            else
                tableName = "Bills";

        });

    }

    @FXML
    void handleAdd() {
        try {
            if (listView.getItems().size() < limit) {
                SingleProduct product = new SingleProduct();
                product.setSlNO(listView.getItems().size() + 1);
                product.isDiscountAdd(!isManual.isSelected());
                listView.getItems().add(product);
            } else {
                mainApp.snackBar("INFO", "Can not add more than "
                        + limit + " items", "green");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        }
    }

    @FXML
    void handleCalculate() {

        Date date;
        String billId, invoice;
        int num = 1;
        String start;
        if (isManual.isSelected()) {
            start = "K-";
            if (manualDate.getValue() == null) {
                mainApp.snackBar("INFO", "Select a date", "red");
                return;
            }
            date = Date.from((manualDate.getValue()).atTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.MILLISECOND)
                    .atZone(ZoneId.systemDefault()).toInstant());
        } else {
            start = "J-";
            date = new Date();
        }
        invoice = start + new SimpleDateFormat("ddMMyy/").format(date) + String.format("%03d", num);
        while (DatabaseHelper.ifInvoiceExist(invoice, tableName)) {
            invoice = start + new SimpleDateFormat("ddMMyy/").format(date) + String.format("%03d", num);
            num++;
        }
        billId = "Bill" + new SimpleDateFormat("yyyyMMddHHSSS").format(date) + num;

        ready = false;
        ObservableList<SingleProduct> p = listView.getItems();

        if (p.size() == 0) {
            mainApp.snackBar("Info", "Fill at least One Product", "red");
            return;
        }

        String errorMsg;
        boolean errored = false;
        for (SingleProduct s : p) {
            s.isDiscountAdd(isManual.isSelected());
            errorMsg = s.isReady();
            if (!errorMsg.equals("s")) {
                mainApp.snackBar("Check the Following", errorMsg, "red");
                errored = true;
            }
        }

        if (errored) {
            bill = null;
            return;
        }


        ObservableList<Product> products = FXCollections.observableArrayList();

        int slNo = 1;
        for (SingleProduct s : p) {
            Product product = s.getProduct();
            product.setSl("" + slNo);
            products.add(product);
            slNo++;
        }


        bill = new Bill("" + billId
                , "" + invoice
                , "" + date.getTime()
                , "" + customer.getName()
                , "" + customer.getId()
                , "" + customer.getStreetAddress() + "\n" + customer.getCity() + "\n" + customer.getState()
                , "" + customer.getPhone()
                , "" + customer.getGstIn()
                , products
                , mainApp.getUser().getUserName());

        totalAmount.setText("Total Amount : " + bill.getTotalAmount());
        ready = true;
    }

    @FXML
    void handleBillSubmit() {

        handleCalculate();
        if (bill == null) return;

        if (ready && AlertMaker.showBill(bill, mainApp, false, checkIGST.isSelected())) {

            mainApp.addSpinner();
            if (DatabaseHelper.insertNewBill(bill, tableName)) {

                mainApp.snackBar("Success", bill.getInvoice() +
                        " Bill is saved successfully!", "green");

                HashSet<String> desc = preferences.getDescriptions();
                HashSet<String> hsn = preferences.getHsn();

                for (Product p : bill.getProducts()) {
                    desc.add(p.getName());
                    hsn.add(p.getHsn());
                }
                preferences.setDescriptions(desc);
                preferences.setHsn(hsn);
                Preferences.setPreference(preferences);
                mainApp.removeSpinner();
                mainApp.handleRefresh();

            } else {
                mainApp.snackBar("Failed", bill.getInvoice() +
                        " Bill item is not saved !", "red");
            }

        }

    }

    @FXML
    void handleCustomerSubmit() {
        if (comboBoxCustomer.getValue() != null && !comboBoxCustomer.getValue().isEmpty()) {
            try {
                customer = DatabaseHelper.getCustomerInfo(DatabaseHelper.getCustomerNameList()
                        .get(comboBoxCustomer.getValue()));
            } catch (Exception e) {
                return;
            }
            if (customer == null) return;
            borderPane.setVisible(true);
            borderPane.setDisable(false);
            labelBillFor.setText(comboBoxCustomer.getValue().toUpperCase());
            root.setTop(null);
            listView.setExpanded(true);
            listView.setVerticalGap(5.0);
        }
    }

    @FXML
    void handleDelete() {
        if (listView.getSelectionModel().getSelectedItems().size() != 0
                && listView.getSelectionModel().getSelectedItem() != null) {
            int i = 1;
            listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
            for (SingleProduct s : listView.getItems()) {
                s.setSlNO(i);
                i++;
            }
        } else {
            mainApp.snackBar("", "Select a row to Delete", "red");
        }
    }

}