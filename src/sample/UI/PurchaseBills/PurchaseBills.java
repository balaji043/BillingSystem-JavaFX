package sample.UI.PurchaseBills;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;
import sample.Alert.AlertMaker;
import sample.CustomUI.SinglePurchaseBill.SinglePurchaseBill;
import sample.Database.DatabaseHelper_PurchaseBill;
import sample.Database.ExcelDatabaseHelper;
import sample.Database.ExcelHelper;
import sample.Main;
import sample.Model.PurchaseBill;
import sample.Utils.BillingSystemUtils;
import sample.Utils.ICON;
import sample.Utils.Preferences;

import java.io.File;
import java.time.LocalDate;

public class PurchaseBills {
    public JFXComboBox<String> companyNameCBOX, billTypeCBOX, sendToAuditorComboBox;
    public JFXTextField searchBox, searchBox2;
    public StackPane main;
    public TableView<PurchaseBill> tableView;
    public JFXDatePicker fromDate, toDate;
    public BorderPane borderPane;
    public JFXCheckBox editPanelCheckBox;
    public JFXButton downloadButton;
    public JFXButton searchSubmitButton;
    public JFXButton deleteBTN;
    public JFXButton downloadAllButton;
    public JFXButton importButton;
    public JFXButton editSubmitButton;
    public JFXButton addBTN;

    private SinglePurchaseBill singlePurchaseBill = new SinglePurchaseBill();
    @FXML
    private JFXCheckBox taxTotalCheckBox;
    @FXML
    private Text totalAmount;
    @FXML
    private VBox editPanel;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        BillingSystemUtils.setImageViewToButtons(ICON.ADD, addBTN);
        BillingSystemUtils.setImageViewToButtons(ICON.DELETE, deleteBTN);
        BillingSystemUtils.setImageViewToButtons(ICON.DOWNLOAD, downloadButton);
        BillingSystemUtils.setImageViewToButtons(ICON.UPLOAD, importButton);
        BillingSystemUtils.setImageViewToButtons(ICON.SEARCH, searchSubmitButton);
        BillingSystemUtils.setImageViewToButtons(ICON.SAVE, editSubmitButton);
        BillingSystemUtils.setImageViewToButtons(ICON.SAVE, downloadAllButton);
        downloadAllButton.setPrefSize(80, 40);
        downloadAllButton.setText("All");


        borderPane.setRight(null);
        billTypeCBOX.getItems().addAll("Standard Enterprises", "Standard Equipments");
        billTypeCBOX.getSelectionModel().selectFirst();

        sendToAuditorComboBox.getItems().addAll("All", "Sent To Auditor", "Not Send to Auditor");
        sendToAuditorComboBox.getSelectionModel().selectFirst();

        companyNameCBOX.getItems().addAll(Preferences.getPreferences().getCompanyNames());
        TextFields.bindAutoCompletion(companyNameCBOX.getEditor(), companyNameCBOX.getItems());


        initTable();
        tableView.setOnMouseClicked(e -> onPurchaseBillSelected());
        TextFields.bindAutoCompletion(searchBox2, getTotalNetAmountList());
        getInvoiceList();
        taxTotalCheckBox.setOnAction(event -> setTotalAmount());

    }

    public void handleAddNewPurchaseBill() {
        mainApp.initNewPurchaseBills();
    }

    public void handleSubmit() {
        loadTable();
    }

    public void handleDownloadAll() {
        if (tableView.getItems().size() == 0) {
            mainApp.snackBar("", "Nothing to Export", "red");
            return;
        }

        File dest = mainApp.chooseFile();
        if (dest == null) {
            mainApp.snackBar("INFO", "Operation Cancelled", "green");
        } else {
            if (ExcelHelper.writeExcelPurchaseBills(dest))
                mainApp.snackBar("Success"
                        , "Purchase Bill Data Written to Excel"
                        , "green");
            else
                mainApp.snackBar("Failed"
                        , "Purchase Bill Data is NOT written to Excel"
                        , "red");
        }
    }

    public void handleDeletePurchaseBill() {
        if (mainApp.getUser().getAccess().equals("admin")) {
            if (tableView.getSelectionModel().getSelectedItems().size() == 1) {
                PurchaseBill toDeletePurchaseBill = tableView.getSelectionModel().getSelectedItem();
                if (toDeletePurchaseBill != null &&
                        AlertMaker.showMCAlert("Confirm ? "
                                , "Are you sure you want to delete this Purchase Bill from "
                                        + toDeletePurchaseBill.getCompanyName() + "?"
                                , mainApp)) {
                    if (DatabaseHelper_PurchaseBill.deletePurchaseBill(toDeletePurchaseBill, DatabaseHelper_PurchaseBill.getTableName(billTypeCBOX.getValue()))) {
                        mainApp.snackBar("Success", "Purchase Bill Deleted Successfully", "green");
                        loadTable();
                    }
                } else
                    mainApp.snackBar("Info", "No Bill Selected", "green");
            } else {
                ObservableList<PurchaseBill> toDelete = FXCollections.observableArrayList();
                toDelete.addAll(tableView.getSelectionModel().getSelectedItems());

                if (AlertMaker.showMCAlert("Confirm ? "
                        , "Are you sure you want to delete " + toDelete.size() + " Purchase Bills from ?"
                        , mainApp)) {
                    for (PurchaseBill toDeleteBill : toDelete)
                        DatabaseHelper_PurchaseBill.deletePurchaseBill(toDeleteBill, DatabaseHelper_PurchaseBill.getTableName(billTypeCBOX.getValue()));
                    loadTable();
                }
            }
        }
    }

    public void handleEditPurchaseBill() {
        if (mainApp.getUser().getAccess().equals("admin")) {
            if (editPanelCheckBox.isSelected()) {

                VBox vBox = new VBox();
                vBox.getChildren().addAll(singlePurchaseBill.getVBox(), editPanel);
                vBox.setAlignment(Pos.TOP_LEFT);
                vBox.setSpacing(20);
                vBox.setPadding(new Insets(20, 20, 20, 20));

                borderPane.setRight(vBox);
                onPurchaseBillSelected();
            } else {
                borderPane.setRight(null);
            }
        }
    }

    public void handleEditSubmit() {
        if (mainApp.getUser().getAccess().equals("admin"))
            if (singlePurchaseBill.isReady())
                if (AlertMaker.showMCAlert("Confirm?", "Are you sure you want to update these changes?", mainApp))
                    if (DatabaseHelper_PurchaseBill.updatePurchaseBill(singlePurchaseBill.getPurchaseBill(), DatabaseHelper_PurchaseBill.getTableName(billTypeCBOX.getValue()))) {
                        mainApp.snackBar("Success", "Purchase Bill Updated Successfully", "green");
                        loadTable();
                    } else mainApp.snackBar("ERROR", "Operation Failed", "red");
                else mainApp.snackBar("INFO", "Operation cancelled", "green");
            else
                mainApp.snackBar("INFO", "Check the details once again", "green");

    }

    public void handleSendToAuditorSubmit() {
        ObservableList<PurchaseBill> selectedBills = tableView.getSelectionModel().getSelectedItems();
        if (!mainApp.getUser().getAccess().equals("admin") || selectedBills.size() == 0 || !AlertMaker.showMCAlert("Confirm?", "Are you sure you want to mark these items as send to auditor?", mainApp))
            return;
        boolean result = true;
        for (PurchaseBill purchaseBill : selectedBills)
            result = result && DatabaseHelper_PurchaseBill.updatePurchaseBillAsGoneToAuditor(purchaseBill, DatabaseHelper_PurchaseBill.getTableName(billTypeCBOX.getValue()));
        loadTable();
    }

    private void initTable() {

        tableView.getColumns().clear();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<PurchaseBill, LocalDate> column = new TableColumn<>("Date");
        column.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableView.getColumns().add(column);

        addTableColumn("Company Name", "companyName");
        addTableColumn("Invoice No", "invoiceNo");
        addTableColumn("Amount Before Tax", "amountBeforeTax");
        addTableColumn("12%", "twelve");
        addTableColumn("18%", "eighteen");
        addTableColumn("28%", "twentyEight");
        addTableColumn("Total Net Amount", "totalAmount");
        addTableColumn("Send To Auditor", "hasGoneToAuditorString");

        tableView.getSortOrder().clear();
        tableView.getSortOrder().add(column);
        tableView.sort();
        loadTable();
    }

    private void addTableColumn(String name, String msg) {
        TableColumn<PurchaseBill, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(msg));
        tableView.getColumns().add(column);
    }

    private void loadTable() {
        tableView.getItems().clear();
        String tableName = DatabaseHelper_PurchaseBill.getTableName(billTypeCBOX.getValue());

        ObservableList<PurchaseBill> purchaseBills;
        if ((searchBox.getText() != null && !searchBox.getText().isEmpty()) || (searchBox2.getText() != null && !searchBox2.getText().isEmpty())) {
            if (searchBox.getText() != null && !searchBox.getText().isEmpty())
                purchaseBills = DatabaseHelper_PurchaseBill.getPurchaseBillList(searchBox.getText(), tableName);
            else
                purchaseBills = DatabaseHelper_PurchaseBill.getPurchaseBillListByTotalNetAmount(searchBox2.getText(), tableName);
        } else if (companyNameCBOX.getValue() != null)
            if (fromDate.getValue() != null && toDate.getValue() != null)
                purchaseBills = DatabaseHelper_PurchaseBill.getPurchaseBillList(companyNameCBOX.getValue(), fromDate.getValue(), toDate.getValue(), tableName);
            else
                purchaseBills = DatabaseHelper_PurchaseBill.getPurchaseBillListByCompanyName(companyNameCBOX.getValue(), tableName);
        else if (fromDate.getValue() != null && toDate.getValue() != null)
            purchaseBills = DatabaseHelper_PurchaseBill.getPurchaseBillList(fromDate.getValue(), toDate.getValue(), tableName);
        else
            purchaseBills = DatabaseHelper_PurchaseBill.getAllPurchaseBillList(tableName);

        tableView.getItems().addAll(getFilterListBySendToAuditor(purchaseBills));
        tableView.sort();

    }

    private void onPurchaseBillSelected() {
        if (tableView.getSelectionModel().getSelectedItems().size() != 1) {
            singlePurchaseBill.setPurchaseBill(null);
            return;
        }
        if (editPanelCheckBox.isSelected()) {
            PurchaseBill toEdit = tableView.getSelectionModel().getSelectedItem();
            singlePurchaseBill.setPurchaseBill(toEdit);
        }
    }

    private ObservableList<PurchaseBill> getFilterListBySendToAuditor(ObservableList<PurchaseBill> purchaseBills) {
        ObservableList<PurchaseBill> newBills = FXCollections.observableArrayList();

        if (sendToAuditorComboBox.getValue().equals("All"))
            return purchaseBills;
        else if (sendToAuditorComboBox.getValue().equals("Sent To Auditor")) {
            for (PurchaseBill p : purchaseBills)
                if (p.hasGoneToAuditor())
                    newBills.add(p);
        } else {
            for (PurchaseBill p : purchaseBills)
                if (!p.hasGoneToAuditor())
                    newBills.add(p);
        }
        return newBills;
    }

    private void getInvoiceList() {
        ObservableList<String> s = FXCollections.observableArrayList();
        String[] strings = {"StdEnt", "StdEqm"};
        for (String string : strings)
            for (PurchaseBill v : DatabaseHelper_PurchaseBill.getAllPurchaseBillList(string))
                s.add(v.getInvoiceNo());
        TextFields.bindAutoCompletion(searchBox, s);
    }

    private ObservableList<String> getTotalNetAmountList() {
        ObservableList<String> invoices = FXCollections.observableArrayList();
        String[] strings = {"StdEnt", "StdEqm"};
        for (String string : strings)
            for (PurchaseBill v : DatabaseHelper_PurchaseBill.getAllPurchaseBillList(string))
                invoices.add(v.getTotalAmount());
        return invoices;
    }


    public void handleDownloadSubmit() {
        if (tableView.getItems().size() == 0) {
            mainApp.snackBar("", "Nothing to Export", "red");
            return;
        }

        File dest = mainApp.chooseFile();
        if (dest == null) {
            mainApp.snackBar("INFO", "Operation Cancelled", "green");
        } else {
            if (ExcelHelper.writeExcelPurchaseBills(dest, companyNameCBOX.getValue(), tableView.getItems()))
                mainApp.snackBar("Success"
                        , "Purchase Bill Data Written to Excel"
                        , "green");
            else
                mainApp.snackBar("Failed"
                        , "Purchase Bill Data is NOT written to Excel"
                        , "red");
        }
    }

    public void handleImportPurchaseBill() {
        File dest = mainApp.chooseFile();
        if (dest == null) {
            mainApp.snackBar("INFO", "Operation Cancelled", "green");
        } else {
            if (ExcelDatabaseHelper.writeDBPurchaseBill(dest))
                mainApp.snackBar("Success"
                        , "Purchase Bill Data Written to DB"
                        , "green");
            else
                mainApp.snackBar("Failed"
                        , "Purchase Bill Data is NOT written to DB"
                        , "red");
        }
    }

    private void setTotalAmount() {
        float total = 0;
        if (taxTotalCheckBox.isSelected()) {
            for (PurchaseBill p : tableView.getItems())
                total = total + Float.parseFloat(p.getTotalAmount());
        } else {
            for (PurchaseBill p : tableView.getItems())
                total = total + Float.parseFloat(p.getAmountBeforeTax());
        }
        totalAmount.setText("Rs. " + String.format("%.2f", total));
    }
}
