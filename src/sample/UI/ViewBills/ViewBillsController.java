package sample.UI.ViewBills;

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
import sample.Main;
import sample.Utils.BillingSystemUtils;
import sample.Utils.GenericController;
import sample.Utils.StringUtil;
import sample.alert.AlertMaker;
import sample.database.DatabaseHelperBills;
import sample.database.DatabaseHelperCustomer;
import sample.database.ExcelHelper;
import sample.model.Bill;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static sample.alert.AlertMaker.showBill;

@SuppressWarnings("Duplicates")
public class ViewBillsController implements Initializable, GenericController {

    @FXML
    private JFXCheckBox taxTotalCheckBox;
    @FXML
    private Text totalAmount;
    @FXML
    private JFXButton edit;
    @FXML
    private JFXCheckBox checkGST, checkNonGst;
    @FXML
    private JFXDatePicker fromDate, toDate;
    @FXML
    private JFXComboBox<String> customerName, comboBills;
    @FXML
    private TableView<Bill> tableView;
    @FXML
    private JFXTextField searchBox;
    @FXML
    private StackPane main;
    private Main mainApp;
    private ObservableList<Bill> bills = FXCollections.observableArrayList();

    private String billTableName = "BILLS";
    private ObservableList<String> gst, nonGst, all;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gst = DatabaseHelperCustomer.getCustomerNameList(0);
        nonGst = DatabaseHelperCustomer.getCustomerNameList(1);
        all = DatabaseHelperCustomer.getCustomerNameList(3);
        customerName.setItems(gst);
        TextFields.bindAutoCompletion(customerName.getEditor(), customerName.getItems());

        comboBills.getItems().addAll("GST", "I-GST");
        comboBills.getSelectionModel().selectFirst();
        checkGST.setOnAction(e -> setCustomerName());
        checkNonGst.setOnAction(e -> setCustomerName());
        taxTotalCheckBox.setOnAction(e -> setTotalAmount());
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        checkNonGst.setSelected(true);
        checkGST.setSelected(true);
        toDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (fromDate.getValue() != null)
                    setDisable(empty || date.compareTo(fromDate.getValue()) < 0);
            }
        });
        if (!mainApp.getUser().getAccess().equals(StringUtil.ADMIN)) {
            edit.setDisable(true);
        }
        initTable();
        ObservableList<String> invoices = FXCollections.observableArrayList();
        for (Bill b : bills) {
            invoices.add(b.getInvoice());
        }
        TextFields.bindAutoCompletion(searchBox, invoices);
    }

    @FXML
    void handleSubmit() {
        loadTable();
    }

    @FXML
    void handleViewBill() {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            mainApp.snackBar(StringUtil.INFO, "Select a bill", StringUtil.GREEN);
            return;
        }
        showBill(tableView.getSelectionModel().getSelectedItem(), mainApp
                , true, BillingSystemUtils.getN(comboBills.getValue()));
    }

    @FXML
    void handleDownload() {
        if (bills.size() == 0) {
            mainApp.snackBar(StringUtil.INFO, "Nothing to Import", "red");
            return;
        }
        mainApp.snackBar(StringUtil.INFO, "Choose File", StringUtil.GREEN);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll
                (new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        File dest = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (dest == null) {
            mainApp.snackBar(StringUtil.INFO, "Operation cancelled", StringUtil.GREEN);
        } else {
            JFXSpinner spinner = new JFXSpinner();
            double d = 50;
            spinner.setMaxSize(d, d);
            spinner.setPrefSize(d, d);
            main.getChildren().add(spinner);
            if (ExcelHelper.writeExcelBills(dest, bills)) {
                mainApp.snackBar(StringUtil.SUCCESS
                        , "bill History Data Written to Excel"
                        , StringUtil.GREEN);
            } else {
                mainApp.snackBar(StringUtil.FAILED
                        , "bill History Data is NOT written to Excel"
                        , "red");
            }
            main.getChildren().remove(spinner);
        }
    }

    @FXML
    void handleDeleteBill() {
        if (!mainApp.getUser().getAccess().equals(StringUtil.ADMIN)) {
            mainApp.snackBar("INFO", "Requires admin Access", "red");
            return;
        }
        boolean b;
        billTableName = BillingSystemUtils.getTableName(comboBills.getValue());
        if (mainApp.getUser().getAccess().equals("admin")) {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                if (!AlertMaker.showMCAlert("Confirm delete?"
                        , "Are sure you want to delete?", mainApp))
                    return;
                b = DatabaseHelperBills.deleteBill(tableView.getSelectionModel().getSelectedItem().getBillId()
                        , billTableName);
                if (b)
                    mainApp.snackBar(StringUtil.SUCCESS, "bill is Successfully Deleted", StringUtil.GREEN);
                else
                    mainApp.snackBar(StringUtil.FAILED, "bill is Not Deleted", StringUtil.RED);
                loadTable();
            } else {
                mainApp.snackBar(StringUtil.INFO, "Select a bill to delete", StringUtil.GREEN);
            }
        } else {
            mainApp.snackBar(StringUtil.INFO, "Requires admin access", "StringUtil.GREEN");
        }
    }

    @FXML
    void handleModifyBill() {
        if (!mainApp.getUser().getAccess().equals("admin")) {
            mainApp.snackBar("INFO", "Requires admin Access", "red");
            return;
        }
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            mainApp.snackBar("INFO", "Select a bill", "green");
            return;
        }
        Bill bill = tableView.getSelectionModel().getSelectedItem();
        if (DatabaseHelperCustomer.getCustomerInfo(bill.getCustomerName()) == null) {
            mainApp.snackBar("Info", " Customer Data Doesn't Exists.\n Cannot Modify the bill for now", "red");
            return;
        }

        mainApp.initNewBill(bill, comboBills.getValue());

    }

    private void initTable() {
        if (!mainApp.isLoggedIn) mainApp.initLoginLayout();

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
        billTableName = BillingSystemUtils.getTableName(comboBills.getValue());
        if (searchBox.getText() != null && !searchBox.getText().isEmpty())
            bills = DatabaseHelperBills.getBillLists("%" + searchBox.getText() + "%", billTableName);
        else if (customerName.getValue() != null && all.contains(customerName.getValue()))
            if (fromDate.getValue() != null && toDate != null)
                bills = DatabaseHelperBills.getBillList(customerName.getValue()
                        , fromDate.getValue(), toDate.getValue(), billTableName);
            else
                bills = DatabaseHelperBills.getBillList(customerName.getValue(), billTableName);
        else if (fromDate.getValue() != null && toDate != null)
            if (checkGST.isSelected() && !checkNonGst.isSelected())
                bills = DatabaseHelperBills.getBillList(fromDate.getValue(), toDate.getValue(), 1
                        , billTableName);
            else if (!checkGST.isSelected() && checkNonGst.isSelected())
                bills = DatabaseHelperBills.getBillList(fromDate.getValue(), toDate.getValue(), 2
                        , billTableName);
            else
                bills = DatabaseHelperBills.getBillList(fromDate.getValue(), toDate.getValue(), 3
                        , billTableName);
        else if (checkGST.isSelected() && !checkNonGst.isSelected())
            bills = DatabaseHelperBills.getBillList(true, billTableName);
        else if (!checkGST.isSelected() && checkNonGst.isSelected())
            bills = DatabaseHelperBills.getBillList(false, billTableName);
        else
            bills = DatabaseHelperBills.getBillList(billTableName);

        tableView.getItems().addAll(bills);
        setTotalAmount();
    }

    private void setTotalAmount() {
        float total = 0;
        if (taxTotalCheckBox.isSelected()) {
            for (Bill b : bills)
                total = total + Float.parseFloat(b.getTotalAmount());
        } else {
            for (Bill b : bills)
                total = total + Float.parseFloat(b.getGross());
        }
        totalAmount.setText("Rs. " + String.format("%.2f", total));
    }

    private void addTableColumn(String name, String msg) {
        TableColumn<Bill, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(msg));
        tableView.getColumns().add(column);
    }

    private void setCustomerName() {
        if (checkGST.isSelected() && !checkNonGst.isSelected())
            customerName.setItems(gst);
        else if (!checkGST.isSelected() && checkNonGst.isSelected())
            customerName.setItems(nonGst);
        else
            customerName.setItems(all);
    }

}
