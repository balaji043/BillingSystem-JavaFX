package bam.billing.nongst.controllers;

import bam.billing.nongst.Main;
import bam.billing.nongst.dto.CustomerNameResult;
import bam.billing.nongst.helpers.BillService;
import bam.billing.nongst.helpers.CustomerService;
import bam.billing.nongst.helpers.ExcelHelper;
import bam.billing.nongst.models.Bill;
import bam.billing.nongst.utils.AlertMaker;
import bam.billing.nongst.utils.CustomerType;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static bam.billing.nongst.utils.AlertMaker.showBill;

@SuppressWarnings("Duplicates")
public class BillPanelController implements Initializable {

    @FXML
    private Text totalAmount;
    @FXML
    private JFXCheckBox taxTotalCheckBox;
    @FXML
    private JFXCheckBox checkGST, checkNonGst;
    @FXML
    private JFXDatePicker fromDate, toDate;
    @FXML
    private JFXComboBox<String> customerName;
    @FXML
    private TableView<Bill> tableView;
    @FXML
    private JFXTextField searchBox;
    @FXML
    private StackPane main;

    private Main mainApp;
    private ObservableList<Bill> bills = FXCollections.observableArrayList();
    CustomerNameResult customerNameResult = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        customerNameResult = CustomerService.getCustomerNameResult();

        TextFields.bindAutoCompletion(customerName.getEditor(), customerName.getItems());
        setCustomerName();
        checkGST.setOnAction(e -> setCustomerName());
        checkNonGst.setOnAction(e -> setCustomerName());
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        checkNonGst.setSelected(true);
        checkGST.setSelected(true);
        TextFields.bindAutoCompletion(customerName.getEditor(), customerName.getItems());
        toDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (fromDate.getValue() != null)
                    setDisable(empty || date.compareTo(fromDate.getValue()) < 0);
            }
        });
        initTable();
        ObservableList<String> invoices = FXCollections.observableArrayList();
        for (Bill b : bills) {
            invoices.add(b.getInvoice());
        }
        TextFields.bindAutoCompletion(searchBox, invoices);
        taxTotalCheckBox.setOnAction(e -> setTotalAmount());
    }

    @FXML
    void handleSubmit() {
        loadTable();
    }

    @FXML
    void handleViewBill() {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            mainApp.snackBar("INFO", "Select a bill", "green");
            return;
        }
        showBill(tableView.getSelectionModel().getSelectedItem(), mainApp
                , true);
    }

    @FXML
    void handleDownload() {
        if (bills.size() == 0) {
            mainApp.snackBar("", "Nothing to Import", "red");
            return;
        }
        mainApp.snackBar("", "Choose File", "green");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll
                (new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        File dest = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (dest == null) {
            mainApp.snackBar("INFO", "Operation cancelled", "green");
        } else {
            JFXSpinner spinner = new JFXSpinner();
            double d = 50;
            spinner.setMaxSize(d, d);
            spinner.setPrefSize(d, d);
            main.getChildren().add(spinner);
            if (ExcelHelper.writeExcelBills(dest, tableView.getItems())) {
                mainApp.snackBar("Success"
                        , "Bill History Data Written to Excel"
                        , "green");
            } else {
                mainApp.snackBar("Failed"
                        , "Bill History Data is NOT written to Excel"
                        , "red");
            }
            main.getChildren().remove(spinner);
        }
    }

    @FXML
    void handleDeleteBill() {
        if (!mainApp.isAdmin()) {
            mainApp.snackBar("INFO", "Requires admin Access", "red");
            return;
        }
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            if (!AlertMaker.showMCAlert("Confirm delete?"
                    , "Are sure you want to delete?", mainApp))
                return;
            boolean b = BillService.deleteBill(tableView.getSelectionModel().getSelectedItem().getBillId());
            if (b)
                mainApp.snackBar("Success", "Bill is Successfully Deleted", "green");
            else
                mainApp.snackBar("Failed", "Bill is Not Deleted", "red");
            loadTable();
        } else {
            mainApp.snackBar("INFO", "Select a bill to delete", "green");
        }
    }

    @FXML
    void handleModifyBill() {
        if (!mainApp.isAdmin()) {
            mainApp.snackBar("INFO", "Requires admin Access", "red");
            return;
        }
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            mainApp.snackBar("INFO", "Select a bill", "green");
            return;
        }
        Bill bill = tableView.getSelectionModel().getSelectedItem();
        if (CustomerService.getCustomerInfo(bill.getCustomerName()) == null) {
            mainApp.snackBar("Info", " Customer Data Doesn't Exists.\n Cannot Modify the bill for now", "red");
            return;
        }
        mainApp.loadBillRegistrationView(bill);

    }

    private void initTable() {
        if (!mainApp.isLoggedIn()) mainApp.loadLoginView();

        tableView.getColumns().clear();
        tableView.getItems().clear();

        addTableColumn("Customer", "customerName");
        addTableColumn("Place", "place");
        addTableColumn("Invoice", "invoice");

        TableColumn<Bill, LocalDate> column = new TableColumn<>("Date");
        column.setCellValueFactory(new PropertyValueFactory<>("localDate"));
        tableView.getColumns().add(column);
        tableView.getSortOrder().add(column);

        TableColumn<Bill, Double> column1 = new TableColumn<>("Invoice Amount");
        column1.setCellValueFactory(new PropertyValueFactory<>("total"));
        tableView.getColumns().add(column1);

        addTableColumn("User Name", "userName");
        tableView.sort();
        loadTable();
    }

    private void loadTable() {
        tableView.getItems().clear();

        if (searchBox.getText() != null && !searchBox.getText().isEmpty())
            bills = BillService.getBillLists("%" + searchBox.getText() + "%");
        else if (customerName.getValue() != null && customerNameResult.allCustomerNames.contains(customerName.getValue()))
            if (fromDate.getValue() != null && toDate != null)
                bills = BillService.getBillList(customerName.getValue()
                        , fromDate.getValue(), toDate.getValue());
            else
                bills = BillService.getBillList(customerName.getValue());
        else if (fromDate.getValue() != null && toDate != null)
            if (checkGST.isSelected() && !checkNonGst.isSelected())
                bills = BillService.getBillList(fromDate.getValue(), toDate.getValue(), CustomerType.GST);
            else if (!checkGST.isSelected() && checkNonGst.isSelected())
                bills = BillService.getBillList(fromDate.getValue(), toDate.getValue(), CustomerType.NON_GST);
            else
                bills = BillService.getBillList(fromDate.getValue(), toDate.getValue(), CustomerType.BOTH);
        else if (checkGST.isSelected() && !checkNonGst.isSelected())
            bills = BillService.getBillList(true);
        else if (!checkGST.isSelected() && checkNonGst.isSelected())
            bills = BillService.getBillList(false);
        else
            bills = BillService.getBillList();
        tableView.getItems().addAll(bills);
        setTotalAmount();
    }

    private void addTableColumn(String name, String msg) {
        TableColumn<Bill, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(msg));
        tableView.getColumns().add(column);
    }

    private void setCustomerName() {
        if (checkGST.isSelected() && !checkNonGst.isSelected())
            customerName.setItems(customerNameResult.gstCustomerNames);
        else if (!checkGST.isSelected() && checkNonGst.isSelected())
            customerName.setItems(customerNameResult.nonGstCustomerNames);
        else
            customerName.setItems(customerNameResult.allCustomerNames);
    }

    private void setTotalAmount() {
        float total = 0;
        if (taxTotalCheckBox.isSelected()) {
            for (Bill b : tableView.getItems())
                total = total + Float.parseFloat(b.getTotalAmount());
        } else {
            for (Bill b : tableView.getItems())
                total = total + Float.parseFloat(b.getTotalAmountBeforeRoundOff());
        }
        totalAmount.setText("Rs. " + String.format("%.2f", total));
    }

}