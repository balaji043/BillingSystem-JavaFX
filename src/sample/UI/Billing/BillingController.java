package sample.UI.Billing;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;
import sample.Alert.AlertMaker;
import sample.CustomUI.SingleProduct.SingleProduct;
import sample.Database.DatabaseHelper;
import sample.Main;
import sample.Model.Bill;
import sample.Model.Customer;
import sample.Model.Product;
import sample.Utils.BillingSystemUtils;
import sample.Utils.Preferences;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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

    private ObservableList<String> gst = FXCollections.observableArrayList();
    private ObservableList<String> nonGst = FXCollections.observableArrayList();
    private Preferences preferences = Preferences.getPreferences();
    private int limit = Integer.parseInt(preferences.getLimit());
    private int num;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        borderPane.setVisible(false);
        borderPane.setDisable(true);
        checkBoxGST.setSelected(true);
        gst = DatabaseHelper.getCustomerNameList(0);
        nonGst = DatabaseHelper.getCustomerNameList(1);
        comboBoxCustomer.setItems(gst);

        checkIGST.setSelected(preferences.isIGSTBill());

        checkBoxGST.setOnAction(e -> {
            comboBoxCustomer.getItems().clear();
            if (checkBoxGST.isSelected())
                comboBoxCustomer.setItems(gst);
            else
                comboBoxCustomer.setItems(nonGst);
        });
        TextFields.bindAutoCompletion(comboBoxCustomer.getEditor(), comboBoxCustomer.getItems());

        isManual.setOnAction(e -> {
            if (isManual.isSelected())
                manualDate.setDisable(false);
            else
                manualDate.setDisable(true);
        });
    }

    @FXML
    void handleAdd() {
        try {
            if (listView.getItems().size() < limit) {
                SingleProduct product = new SingleProduct();
                product.setSlNO(listView.getItems().size() + 1);
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
        String st;

        if (isManual.isSelected()) {
            if (manualDate.getValue() == null) {
                mainApp.snackBar("INFO", "Select a date", "red");
                return;
            } else {
                num = Integer.parseInt(preferences.getAllBillNO());
                date = Date.from((manualDate.getValue())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
        } else {
            date = new Date();
            if (!LocalDate.now().equals(BillingSystemUtils.convertToLocalDateViaInstant(
                    new Date(Long.parseLong(preferences.getDate()))))) {
                preferences.setDate("" + date.getTime());
                preferences.setInvoice("0");
            }
            num = Integer.parseInt(preferences.getInvoice());
        }
        num++;
        if (num < 10) st = "00" + num;
        else if (num < 100) st = "0" + num;
        else st = "" + num;
        ObservableList<SingleProduct> p = listView.getItems();
        ObservableList<Product> products = FXCollections.observableArrayList();
        if (p.size() == 0) {
            mainApp.snackBar("Info", "Fill at least One Product", "red");
            return;
        }
        int i = 1;
        String errorMsg;
        boolean t = true;
        for (SingleProduct s : p) {
            errorMsg = s.isReady();
            if (!errorMsg.equals("s")) {
                mainApp.snackBar("Check the Following"
                        , errorMsg
                        , "red");
                s.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
                t = false;
            } else {
                s.setBackground(new Background(new BackgroundFill(Color.valueOf("white")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
                Product ps = s.getProduct();
                ps.setSl("" + i);
                i++;
                products.add(ps);
            }
        }
        if (!t) {
            bill = null;
            return;
        }
        String invoice = "J-" + new SimpleDateFormat("ddMMyy/")
                .format(date) + st;
        bill = new Bill("Bill" +
                new SimpleDateFormat("yyyyMMddHHSSS").format(date)
                , invoice
                , "" + date.getTime()
                , "" + customer.getName()
                , "" + customer.getId()
                , "" + customer.getStreetAddress() + "\n"
                + customer.getCity() + "\n" + customer.getState()
                , "" + customer.getPhone()
                , "" + customer.getGstIn()
                , products, mainApp.getUser().getUserName());
        ready = true;
        totalAmount.setText("Total Amount : " + bill.getTotalAmount());
    }

    @FXML
    void handleBillSubmit() {
        handleCalculate();
        if (bill == null) return;
        if (ready && AlertMaker.showBill(bill, mainApp, false, checkIGST.isSelected())) {
            HashSet<String> desc = preferences.getDescriptions();
            HashSet<String> hsn = preferences.getHsn();
            if (!isManual.isSelected())
                preferences.setInvoice("" + (num++));
            else
                preferences.setAllBillNO("" + (num++));

            for (Product p : bill.getProducts()) {
                desc.add(p.getName());
                hsn.add(p.getHsn());
            }
            preferences.setDescriptions(desc);
            preferences.setHsn(hsn);
            preferences.setIGSTBill(checkIGST.isSelected());
            Preferences.setPreference(preferences);

            boolean okay = checkIGST.isSelected()
                    ? DatabaseHelper.insertNewBill(bill, "IBills")
                    : DatabaseHelper.insertNewBill(bill, "Bills");

            if (okay) {
                mainApp.snackBar("Success", bill.getInvoice() +
                        " Bill is saved successfully!", "green");
                mainApp.handleRefresh();
            } else
                mainApp.snackBar("Failed", bill.getInvoice() +
                        " Bill item is not saved !", "red");
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
            labelBillFor.setText("Bill For " + comboBoxCustomer.getValue().toUpperCase());
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