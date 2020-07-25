package bam.billing.se.controllers;

import bam.billing.se.Main;
import bam.billing.se.helpers.ExcelDatabaseHelper;
import bam.billing.se.helpers.ExcelHelper;
import bam.billing.se.helpers.PurchaseBillService;
import bam.billing.se.models.PurchaseBill;
import bam.billing.se.utils.AlertMaker;
import bam.billing.se.utils.BillingSystemUtils;
import bam.billing.se.utils.Preferences;
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

import java.io.File;
import java.time.LocalDate;

import static bam.billing.se.utils.ResourceConstants.Icons;

public class PurchaseBillsPanelController {
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

    private SinglePurchaseBillController singlePurchaseBillController = new SinglePurchaseBillController();
    @FXML
    private JFXCheckBox taxTotalCheckBox;
    @FXML
    private Text totalAmount;
    @FXML
    private VBox editPanel;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        BillingSystemUtils.setImageViewToButtons(Icons.ADD, addBTN);
        BillingSystemUtils.setImageViewToButtons(Icons.DELETE, deleteBTN);
        BillingSystemUtils.setImageViewToButtons(Icons.DOWNLOAD, downloadButton);
        BillingSystemUtils.setImageViewToButtons(Icons.IMPORT, importButton);
        BillingSystemUtils.setImageViewToButtons(Icons.SEARCH, searchSubmitButton);
        BillingSystemUtils.setImageViewToButtons(Icons.SAVE, editSubmitButton);
        BillingSystemUtils.setImageViewToButtons(Icons.SAVE, downloadAllButton);
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
        if (tableView.getItems().isEmpty()) {
            mainApp.snackBar("", "Nothing to Export", "red");
            return;
        }

        File dest = BillingSystemUtils.chooseFile(mainApp);
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
        if (mainApp.getUser().isAdmin()) {
            if (tableView.getSelectionModel().getSelectedItems().size() == 1) {
                PurchaseBill toDeletePurchaseBill = tableView.getSelectionModel().getSelectedItem();
                if (toDeletePurchaseBill != null &&
                        AlertMaker.showMCAlert("Confirm ? "
                                , "Are you sure you want to delete this Purchase Bill from "
                                        + toDeletePurchaseBill.getCompanyName() + "?"
                                , mainApp)) {
                    if (PurchaseBillService.deletePurchaseBill(toDeletePurchaseBill, PurchaseBillService.getTableName(billTypeCBOX.getValue()))) {
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
                        PurchaseBillService.deletePurchaseBill(toDeleteBill, PurchaseBillService.getTableName(billTypeCBOX.getValue()));
                    loadTable();
                }
            }
        }
    }

    public void handleEditPurchaseBill() {
        if (mainApp.isAdmin()) {
            if (editPanelCheckBox.isSelected()) {

                VBox vBox = new VBox();
                vBox.getChildren().addAll(singlePurchaseBillController.getVBox(), editPanel);
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
        if (mainApp.isAdmin()) {
            if (singlePurchaseBillController.isReady()) {
                if (AlertMaker.showMCAlert("Confirm?", "Are you sure you want to update these changes?", mainApp)) {
                    if (PurchaseBillService.updatePurchaseBill(singlePurchaseBillController.getPurchaseBill(), PurchaseBillService.getTableName(billTypeCBOX.getValue()))) {
                        mainApp.snackBar("Success", "Purchase Bill Updated Successfully", "green");
                        loadTable();
                    } else {
                        mainApp.snackBar("ERROR", "Operation Failed", "red");
                    }
                } else {
                    mainApp.snackBar("INFO", "Operation cancelled", "green");
                }
            } else {
                mainApp.snackBar("INFO", "Check the details once again", "green");
            }
        }
    }

    public void handleSendToAuditorSubmit() {
        ObservableList<PurchaseBill> selectedBills = tableView.getSelectionModel().getSelectedItems();
        if (!mainApp.isAdmin() || selectedBills.isEmpty() || !AlertMaker.showMCAlert("Confirm?", "Are you sure you want to mark these items as send to auditor?", mainApp)) {
            return;
        }
        boolean result = true;
        for (PurchaseBill purchaseBill : selectedBills) {
            result = result && PurchaseBillService.updatePurchaseBillAsGoneToAuditor(purchaseBill, PurchaseBillService.getTableName(billTypeCBOX.getValue()));
        }
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
        String tableName = PurchaseBillService.getTableName(billTypeCBOX.getValue());

        ObservableList<PurchaseBill> purchaseBills;
        if ((searchBox.getText() != null && !searchBox.getText().isEmpty()) || (searchBox2.getText() != null && !searchBox2.getText().isEmpty())) {
            if (searchBox.getText() != null && !searchBox.getText().isEmpty()) {
                purchaseBills = PurchaseBillService.getPurchaseBillList(searchBox.getText(), tableName);
            } else {
                purchaseBills = PurchaseBillService.getPurchaseBillListByTotalNetAmount(searchBox2.getText(), tableName);
            }
        } else if (companyNameCBOX.getValue() != null) {
            if (fromDate.getValue() != null && toDate.getValue() != null) {
                purchaseBills = PurchaseBillService.getPurchaseBillList(companyNameCBOX.getValue(), fromDate.getValue(), toDate.getValue(), tableName);
            } else {
                purchaseBills = PurchaseBillService.getPurchaseBillListByCompanyName(companyNameCBOX.getValue(), tableName);
            }
        } else if (fromDate.getValue() != null && toDate.getValue() != null) {
            purchaseBills = PurchaseBillService.getPurchaseBillList(fromDate.getValue(), toDate.getValue(), tableName);
        } else {
            purchaseBills = PurchaseBillService.getAllPurchaseBillList(tableName);
        }

        tableView.getItems().addAll(getFilterListBySendToAuditor(purchaseBills));
        tableView.sort();

    }

    private void onPurchaseBillSelected() {
        if (tableView.getSelectionModel().getSelectedItems().size() != 1) {
            singlePurchaseBillController.setPurchaseBill(null);
            return;
        }
        if (editPanelCheckBox.isSelected()) {
            PurchaseBill toEdit = tableView.getSelectionModel().getSelectedItem();
            singlePurchaseBillController.setPurchaseBill(toEdit);
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
            for (PurchaseBill v : PurchaseBillService.getAllPurchaseBillList(string))
                s.add(v.getInvoiceNo());
        TextFields.bindAutoCompletion(searchBox, s);
    }

    private ObservableList<String> getTotalNetAmountList() {
        ObservableList<String> invoices = FXCollections.observableArrayList();
        String[] strings = {"StdEnt", "StdEqm"};
        for (String string : strings)
            for (PurchaseBill v : PurchaseBillService.getAllPurchaseBillList(string))
                invoices.add(v.getTotalAmount());
        return invoices;
    }


    public void handleDownloadSubmit() {
        if (tableView.getItems().isEmpty()) {
            mainApp.snackBar("", "Nothing to Export", "red");
            return;
        }

        File dest = BillingSystemUtils.chooseFile(mainApp);
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
        File dest = BillingSystemUtils.chooseFile(mainApp);
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
