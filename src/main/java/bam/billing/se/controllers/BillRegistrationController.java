package bam.billing.se.controllers;

import bam.billing.se.Main;
import bam.billing.se.dto.CustomerNameResult;
import bam.billing.se.helpers.BillService;
import bam.billing.se.helpers.CustomerService;
import bam.billing.se.models.Bill;
import bam.billing.se.models.Customer;
import bam.billing.se.models.Product;
import bam.billing.se.utils.AlertMaker;
import bam.billing.se.utils.Constants;
import bam.billing.se.utils.Message;
import bam.billing.se.utils.Preferences;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import static bam.billing.se.utils.Constants.*;
import static bam.billing.se.utils.Message.*;

public class BillRegistrationController {

    public JFXButton add, delete, submit;
    public JFXCheckBox checkBoxGST, isManual;
    public JFXComboBox<String> comboBoxCustomer;
    public JFXListView<SingleProductController> listView;
    public JFXDatePicker manualDate;
    public Text labelBillFor, totalAmount;
    public BorderPane borderPane, root;
    public HBox hBox, outerTopHBox;
    public VBox manualD;
    private Main mainApp;
    private Customer customer = null;
    private Bill bill = null, oldBill = null;
    private boolean isNewBill = true, ready = false;
    private Date date;
    private String billId, invoice;
    private ObservableList<String> gst = FXCollections.observableArrayList(), nonGst = FXCollections.observableArrayList();
    private Preferences preferences = Preferences.getPreferences();
    private int limit = Integer.parseInt(preferences.getProductMaxLimitPerBill());


    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        borderPane.setVisible(false);
        borderPane.setDisable(true);
        checkBoxGST.setSelected(true);
        manualDate.setDisable(true);
        CustomerNameResult customerNameResult = CustomerService.getCustomerNameResult();
        gst = customerNameResult.gstCustomerNames;
        nonGst = customerNameResult.nonGstCustomerNames;

        comboBoxCustomer.getItems().addAll(gst);
        TextFields.bindAutoCompletion(comboBoxCustomer.getEditor(), comboBoxCustomer.getItems());

        checkBoxGST.setOnAction(e -> toggleCustomer());
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
                SingleProductController product = new SingleProductController();
                product.setSlNO(listView.getItems().size() + 1);
                listView.getItems().add(product);
            } else {
                mainApp.snackBar(Message.LIMIT_EXCEEDED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        }
    }

    @FXML
    void handleCalculate() {

        if (isNewBill) {

            if (isManual.isSelected()) {
                if (manualDate.getValue() != null) {
                    date = Date.from((manualDate.getValue()).atTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.MILLISECOND)
                            .atZone(ZoneId.systemDefault()).toInstant());
                } else {
                    mainApp.snackBar(SELECT_DATE);
                    return;
                }
            } else {
                date = new Date();
            }
            getBillId();
        } else {
            if (isManual.isSelected()) {
                if (manualDate.getValue() == null) {
                    mainApp.snackBar(SELECT_DATE);
                    return;
                }
                date = Date.from((manualDate.getValue()).atTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.MILLISECOND)
                        .atZone(ZoneId.systemDefault()).toInstant());
                getBillId();
            } else {
                billId = oldBill.getBillId();
                invoice = oldBill.getInvoice();
                date = new Date(Long.parseLong(oldBill.getTime()));
            }
        }
        ready = false;
        ObservableList<SingleProductController> p = listView.getItems();

        if (p.size() == 0) {
            mainApp.snackBar(ADD_MIN_PRODUCT);
            return;
        }

        String errorMsg;
        boolean hasEror = false;
        for (SingleProductController s : p) {
            errorMsg = s.isReady();
            if (!errorMsg.equals("s")) {
                mainApp.snackBar(Constants.CHECK_FOR_ERROR, errorMsg, SnackBarColor.RED);
                hasEror = true;
            }

        }

        if (hasEror) {
            bill = null;
            return;
        }


        ObservableList<Product> products = FXCollections.observableArrayList();

        int slNo = 1;
        for (SingleProductController s : p) {
            Product product = s.getProduct();
            product.setSl(Integer.toString(slNo));
            products.add(product);
            slNo++;
        }


        bill = new Bill(
                billId,
                invoice,
                Long.toString(date.getTime()),
                customer,
                products,
                mainApp.getUser().getUserName()
        );

        totalAmount.setText(bill.getTotalAmount());
        ready = true;
    }

    private void getBillId() {
        int num = 1;
        invoice = preferences.getBillInvoiceCodePrefix() + "-" + new SimpleDateFormat("ddMMyy/").format(date) + String.format("%03d", num);
        while (BillService.ifInvoiceExist(invoice)) {
            invoice = preferences.getBillInvoiceCodePrefix() + "-" + new SimpleDateFormat("ddMMyy/").format(date) + String.format("%03d", num);
            num++;
        }
        billId = "Bill" + new SimpleDateFormat("yyyyMMddHHSSS").format(date) + num;
    }

    @FXML
    void handleBillSubmit() {

        handleCalculate();
        if (bill == null) return;

        if (ready && AlertMaker.showBill(bill, mainApp, false)) {
            boolean success = isNewBill ? BillService.insertNewBill(bill) :
                    BillService.deleteBill(oldBill.getBillId())
                            && BillService.insertNewBill(bill);
            if (success) {
                mainApp.snackBar(SUCCESS, bill.getInvoice() +
                        BILL_SAVE, SnackBarColor.GREEN);
                HashSet<String> desc = preferences.getDescriptions();
                HashSet<String> hsn = preferences.getHsn();

                for (Product p : bill.getProducts()) {
                    desc.add(p.getName());
                    hsn.add(p.getHsn());
                }

                preferences.setDescriptions(desc);
                preferences.setHsn(hsn);
                Preferences.setPreference(preferences);

                mainApp.handleRefresh();

            } else {
                mainApp.snackBar(FAILED, bill.getInvoice() + BILL_NOT_SAVED
                        , SnackBarColor.RED);
            }
        }

    }

    @FXML
    void handleCustomerSubmit() {
        if (comboBoxCustomer.getValue() != null && !comboBoxCustomer.getValue().isEmpty()) {
            customer = CustomerService.getCustomerInfo(comboBoxCustomer.getValue());
            if (customer == null) {
                mainApp.snackBar(CUSTOMER_NOT_FOUND);
                return;
            }
            labelBillFor.setText("" + customer.getName().toUpperCase());
            borderPane.setVisible(true);
            borderPane.setDisable(false);
            listView.setExpanded(true);
            listView.setVerticalGap(5.0);

            if (isNewBill) root.setTop(null);
        }
    }

    @FXML
    void handleDelete() {
        if (listView.getSelectionModel().getSelectedItems().size() != 0
                && listView.getSelectionModel().getSelectedItem() != null) {
            int i = 1;
            listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
            for (SingleProductController s : listView.getItems()) {
                s.setSlNO(i);
                i++;
            }
        } else {
            mainApp.snackBar(SELECT_ONE_ROW);
        }
    }

    public void setBill(Bill bill) {
        isNewBill = false;
        oldBill = bill;
        checkBoxGST.setSelected(!bill.getGSTNo().equalsIgnoreCase(Constants.FOR_OWN_USE));
        toggleCustomer();
        comboBoxCustomer.getSelectionModel().select(bill.getCustomerName());
        handleCustomerSubmit();
        billId = bill.getBillId();
        invoice = bill.getInvoice();
        date = new Date(Long.parseLong(bill.getTime()));
        totalAmount.setText(bill.getTotalAmount());
        int i = 1;
        for (Product product : bill.getProducts()) {
            SingleProductController product1 = new SingleProductController();
            product1.setProduct(product);
            product1.setSlNO(i);
            listView.getItems().addAll(product1);
        }
    }

    private void toggleCustomer() {
        comboBoxCustomer.getItems().clear();
        if (checkBoxGST.isSelected())
            comboBoxCustomer.getItems().addAll(gst);
        else
            comboBoxCustomer.getItems().addAll(nonGst);
        TextFields.bindAutoCompletion(comboBoxCustomer.getEditor(), comboBoxCustomer.getItems());
    }

}