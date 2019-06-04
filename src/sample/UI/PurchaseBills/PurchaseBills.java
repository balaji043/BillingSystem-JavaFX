package sample.UI.PurchaseBills;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
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
import org.controlsfx.control.textfield.TextFields;
import sample.Alert.AlertMaker;
import sample.CustomUI.SinglePurchaseBill.SinglePurchaseBill;
import sample.Database.DatabaseHelper_PurchaseBill;
import sample.Database.ExcelHelper;
import sample.Main;
import sample.Model.PurchaseBill;
import sample.Utils.Preferences;

import java.io.File;
import java.time.LocalDate;

public class PurchaseBills {
    public JFXComboBox<String> companyNameCBOX, sendToAuditorComboBox;
    public JFXTextField searchBox;
    public StackPane main;
    public TableView<PurchaseBill> tableView;
    public JFXDatePicker fromDate, toDate;
    public BorderPane borderPane;
    public JFXCheckBox editPanelCheckBox;
    private SinglePurchaseBill singlePurchaseBill = new SinglePurchaseBill();

    @FXML
    private VBox editPanel;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        borderPane.setRight(null);
        companyNameCBOX.getItems().addAll(Preferences.getPreferences().getCompanyNames());
        TextFields.bindAutoCompletion(companyNameCBOX.getEditor(), companyNameCBOX.getItems());

        sendToAuditorComboBox.getItems().addAll("All", "Sent To Auditor", "Not Send to Auditor");
        sendToAuditorComboBox.getSelectionModel().selectFirst();

        initTable();
        tableView.setOnMouseClicked(e -> onPurchaseBillSelected());
        TextFields.bindAutoCompletion(searchBox, getInvoiceList());
    }

    private ObservableList<String> getInvoiceList() {
        ObservableList<String> invoices = FXCollections.observableArrayList();
        for (PurchaseBill bill :
                tableView.getItems())
            invoices.add(bill.getInvoiceNo());
        return invoices;
    }

    public void handleAddNewPurchaseBill() {
        mainApp.initNewPurchaseBills();
    }

    public void handleSubmit() {
        loadTable();
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
            if (ExcelHelper.writeExcelPurchaseBills(dest, tableView.getItems()))
                mainApp.snackBar("Success"
                        , "Customer Data Written to Excel"
                        , "green");
            else
                mainApp.snackBar("Failed"
                        , "Customer Data is NOT written to Excel"
                        , "red");
        }
    }

    public void handleDeletePurchaseBill() {
        if (mainApp.getUser().getAccess().equals("admin")) {
            PurchaseBill toDeletePurchaseBill = tableView.getSelectionModel().getSelectedItem();
            if (toDeletePurchaseBill != null &&
                    AlertMaker.showMCAlert("Confirm ? "
                            , "Are you sure you want to delete this Purchase Bill from "
                                    + toDeletePurchaseBill.getCompanyName() + "?"
                            , mainApp)) {
                if (DatabaseHelper_PurchaseBill.deletePurchaseBill(toDeletePurchaseBill)) {
                    mainApp.snackBar("Success", "Purchase Bill Deleted Successfully", "green");
                    loadTable();
                }
            } else
                mainApp.snackBar("Info", "No Bill Selected", "green");
        }
    }

    public void handleEditPurchaseBill() {
        if (mainApp.getUser().getAccess().equals("admin")) {
            if (editPanelCheckBox.isSelected()) {

                VBox vBox = new VBox();
                vBox.getChildren().addAll(singlePurchaseBill.getVBox(), editPanel);
                vBox.setAlignment(Pos.CENTER);
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
                    if (DatabaseHelper_PurchaseBill.updatePurchaseBill(singlePurchaseBill.getPurchaseBill())) {
                        mainApp.snackBar("Success", "Purchase Bill Updated Successfully", "green");
                        loadTable();
                    } else mainApp.snackBar("ERROR", "Operation Failed", "red");
                else mainApp.snackBar("INFO", "Operation cancelled", "green");
            else
                mainApp.snackBar("INFO", "Check the details once again", "green");

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

        ObservableList<PurchaseBill> purchaseBills;
        if (searchBox.getText() != null && !searchBox.getText().isEmpty())
            purchaseBills = DatabaseHelper_PurchaseBill.getPurchaseBillList(searchBox.getText());
        else if (companyNameCBOX.getValue() != null)
            if (fromDate.getValue() != null && toDate.getValue() != null)
                purchaseBills = DatabaseHelper_PurchaseBill.getPurchaseBillList(companyNameCBOX.getValue(), fromDate.getValue(), toDate.getValue());
            else
                purchaseBills = DatabaseHelper_PurchaseBill.getPurchaseBillListByCompanyName(companyNameCBOX.getValue());
        else if (fromDate.getValue() != null && toDate.getValue() != null)
            purchaseBills = DatabaseHelper_PurchaseBill.getPurchaseBillList(fromDate.getValue(), toDate.getValue());
        else
            purchaseBills = DatabaseHelper_PurchaseBill.getAllPurchaseBillList();

        tableView.getItems().addAll(getFilterListBySendToAuditor(purchaseBills));
        tableView.sort();

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

    public void handleSendToAuditorSubmit() {
        ObservableList<PurchaseBill> selectedBills = tableView.getSelectionModel().getSelectedItems();
        if (!mainApp.getUser().getAccess().equals("admin") || selectedBills.size() == 0 || !AlertMaker.showMCAlert("Confirm?", "Are you sure you want to mark these items as send to auditor?", mainApp))
            return;
        boolean result = true;
        for (PurchaseBill purchaseBill : selectedBills)
            result = result && DatabaseHelper_PurchaseBill.markBillAsGoneToAuditor(purchaseBill);
        loadTable();
    }
}
