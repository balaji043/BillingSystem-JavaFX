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
import javafx.stage.FileChooser;
import org.controlsfx.control.textfield.TextFields;
import sample.Alert.AlertMaker;
import sample.Database.DatabaseHelper;
import sample.Database.ExcelHelper;
import sample.Main;
import sample.Model.Bill;
import sample.Utils.BillingSystemUtils;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static sample.Alert.AlertMaker.showBill;

@SuppressWarnings("Duplicates")
public class ViewBillsController implements Initializable {
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
        gst = DatabaseHelper.getCustomerNameList(0);
        nonGst = DatabaseHelper.getCustomerNameList(1);
        all = DatabaseHelper.getCustomerNameList(3);
        customerName.setItems(gst);
        TextFields.bindAutoCompletion(customerName.getEditor(), customerName.getItems());

        comboBills.getItems().addAll("GST", "I-GST");
        comboBills.getSelectionModel().selectFirst();
        checkGST.setOnAction(e -> setCustomerName());
        checkNonGst.setOnAction(e -> setCustomerName());
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
        if (!mainApp.getUser().getAccess().equals("admin")) {
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
            mainApp.snackBar("INFO", "Select a bill", "green");
            return;
        }
        showBill(tableView.getSelectionModel().getSelectedItem(), mainApp
                , true, BillingSystemUtils.getN(comboBills.getValue()));
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
            if (ExcelHelper.writeExcelBills(dest, bills)) {
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
        if (!mainApp.getUser().getAccess().equals("admin")) {
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
                b = DatabaseHelper.deleteBill(tableView.getSelectionModel().getSelectedItem().getBillId()
                        , billTableName);
                if (b)
                    mainApp.snackBar("Success", "Bill is Successfully Deleted", "green");
                else
                    mainApp.snackBar("Failed", "Bill is Not Deleted", "red");
                loadTable();
            } else {
                mainApp.snackBar("INFO", "Select a bill to delete", "green");
            }
        } else {
            mainApp.snackBar("INFO", "Requires admin access", "green");
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
        if (DatabaseHelper.getCustomerInfo(bill.getCustomerName()) == null) {
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
            bills = DatabaseHelper.getBillLists("%" + searchBox.getText() + "%", billTableName);
        else if (customerName.getValue() != null && all.contains(customerName.getValue()))
            if (fromDate.getValue() != null && toDate != null)
                bills = DatabaseHelper.getBillList(customerName.getValue()
                        , fromDate.getValue(), toDate.getValue(), billTableName);
            else
                bills = DatabaseHelper.getBillList(customerName.getValue(), billTableName);
        else if (fromDate.getValue() != null && toDate != null)
            if (checkGST.isSelected() && !checkNonGst.isSelected())
                bills = DatabaseHelper.getBillList(fromDate.getValue(), toDate.getValue(), 1
                        , billTableName);
            else if (!checkGST.isSelected() && checkNonGst.isSelected())
                bills = DatabaseHelper.getBillList(fromDate.getValue(), toDate.getValue(), 2
                        , billTableName);
            else
                bills = DatabaseHelper.getBillList(fromDate.getValue(), toDate.getValue(), 3
                        , billTableName);
        else if (checkGST.isSelected() && !checkNonGst.isSelected())
            bills = DatabaseHelper.getBillList(true, billTableName);
        else if (!checkGST.isSelected() && checkNonGst.isSelected())
            bills = DatabaseHelper.getBillList(false, billTableName);
        else
            bills = DatabaseHelper.getBillList(billTableName);
        tableView.getItems().addAll(bills);
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
