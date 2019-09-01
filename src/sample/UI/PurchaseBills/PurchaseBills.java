package sample.UI.PurchaseBills;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;
import sample.Main;
import sample.Utils.*;
import sample.alert.AlertMaker;
import sample.custom.single.purchasebill.SinglePurchaseBill;
import sample.database.DatabaseHelperPurchaseBill;
import sample.database.ExcelDatabaseHelper;
import sample.database.ExcelHelper;
import sample.model.PurchaseBill;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class PurchaseBills implements GenericController {

    @FXML
    private JFXCheckBox taxTotalCheckBox;
    @FXML
    private Text totalAmount;
    @FXML
    private JFXButton editSubmitButton;
    @FXML
    private JFXButton searchSubmitButton;
    @FXML
    private JFXTextField searchBox;
    @FXML
    private JFXTextField searchBox2;
    @FXML
    private StackPane main;
    @FXML
    private TableView<PurchaseBill> tableView;
    @FXML
    private JFXDatePicker fromDate, toDate;
    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXCheckBox editPanelCheckBox;
    @FXML
    private JFXButton deleteBTN;
    @FXML
    private JFXButton addBTN;
    @FXML
    private JFXButton downloadButton;
    @FXML
    private JFXButton importButton;
    @FXML
    private JFXComboBox<String> companyNameCBOX;
    @FXML
    private JFXComboBox<String> sendToAuditorComboBox;
    @FXML
    private JFXComboBox<String> billStatusComboBox;

    private SinglePurchaseBill singlePurchaseBill = new SinglePurchaseBill();
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

        List<String> billStatusCodes = Arrays.asList("ALL", "YES", "PARTIAL", "NO");

        billStatusComboBox.getItems().addAll(billStatusCodes);

        sendToAuditorComboBox.getSelectionModel().selectFirst();
        billStatusComboBox.getSelectionModel().selectFirst();

        borderPane.setRight(null);
        companyNameCBOX.getItems().addAll(Preferences.getPreferences().getCompanyNames());
        TextFields.bindAutoCompletion(companyNameCBOX.getEditor(), companyNameCBOX.getItems());

        sendToAuditorComboBox.getItems().addAll("All", "Sent To Auditor", "Not Send to Auditor");
        sendToAuditorComboBox.getSelectionModel().selectFirst();
        taxTotalCheckBox.setOnAction(event -> setTotalAmount());

        initTable();
        tableView.setOnMouseClicked(e -> onPurchaseBillSelected());
        TextFields.bindAutoCompletion(searchBox, getInvoiceList());
        TextFields.bindAutoCompletion(searchBox2, getTotalNetAmountList());
    }

    private ObservableList<String> getTotalNetAmountList() {
        ObservableList<String> invoices = FXCollections.observableArrayList();
        for (PurchaseBill bill : tableView.getItems())
            invoices.add(bill.getTotalAmount());
        return invoices;
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
            mainApp.snackBar(StringUtil.INFO, "Nothing to Export", StringUtil.RED);
            return;
        }

        File dest = mainApp.chooseFile();
        if (dest == null) {
            mainApp.snackBar(StringUtil.INFO, "Operation Cancelled", StringUtil.GREEN);
        } else {
            if (ExcelHelper.writeExcelPurchaseBills(dest, tableView.getItems()))
                mainApp.snackBar(StringUtil.SUCCESS
                        , "Customer Data Written to Excel"
                        , StringUtil.GREEN);
            else
                mainApp.snackBar(StringUtil.FAILED
                        , "Customer Data is NOT written to Excel"
                        , StringUtil.RED);
        }
    }

    public void handleDeletePurchaseBill() {
        if (mainApp.getUser().getAccess().equals(StringUtil.ADMIN)) {
            if (tableView.getSelectionModel().getSelectedItems().size() == 1) {
                PurchaseBill toDeletePurchaseBill = tableView.getSelectionModel().getSelectedItem();
                if (toDeletePurchaseBill != null &&
                        AlertMaker.showMCAlert("Confirm ? "
                                , "Are you sure you want to delete this Purchase bill from "
                                        + toDeletePurchaseBill.getCompanyName() + "?"
                                , mainApp)) {
                    if (DatabaseHelperPurchaseBill.deletePurchaseBill(toDeletePurchaseBill)) {
                        mainApp.snackBar(StringUtil.SUCCESS, "Purchase bill Deleted Successfully", StringUtil.GREEN);
                        loadTable();
                    }
                } else
                    mainApp.snackBar(StringUtil.INFO, "No bill Selected", StringUtil.GREEN);
            } else {
                ObservableList<PurchaseBill> toDelete = FXCollections.observableArrayList();
                toDelete.addAll(tableView.getSelectionModel().getSelectedItems());

                if (AlertMaker.showMCAlert("Confirm ? "
                        , "Are you sure you want to delete " + toDelete.size() + " Purchase Bills from ?"
                        , mainApp)) {
                    for (PurchaseBill toDeleteBill : toDelete)
                        DatabaseHelperPurchaseBill.deletePurchaseBill(toDeleteBill);
                    loadTable();
                }
            }
        }
    }

    public void handleEditPurchaseBill() {
        if (mainApp.getUser().getAccess().equals(StringUtil.ADMIN)) {
            if (editPanelCheckBox.isSelected()) {

                VBox vBox = new VBox();
                vBox.setPrefWidth(240);

                VBox vBox1 = singlePurchaseBill.getVBox();

                vBox1.setMaxWidth(200);
                vBox1.setPrefWidth(200);
                ScrollPane pane = new ScrollPane(vBox1);
                pane.setPrefViewportWidth(260);
                pane.setPrefWidth(220);
                pane.setPadding(new Insets(10, 5, 1, 5));
                vBox.getChildren().addAll(pane, editPanel);
                vBox.setAlignment(Pos.CENTER);
                vBox.setSpacing(5);

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
                    if (DatabaseHelperPurchaseBill.updatePurchaseBill(singlePurchaseBill.getPurchaseBill())) {
                        mainApp.snackBar(StringUtil.SUCCESS, "Purchase bill Updated Successfully", StringUtil.GREEN);
                        singlePurchaseBill.setPurchaseBill(null);
                        loadTable();
                    } else mainApp.snackBar(StringUtil.ERROR, "Operation Failed", StringUtil.RED);
                else mainApp.snackBar(StringUtil.INFO, "Operation cancelled", StringUtil.GREEN);
            else
                mainApp.snackBar(StringUtil.INFO, "Check the details once again", StringUtil.GREEN);

    }

    public void handleImportPurchaseBill() {
        File dest = mainApp.chooseFile();
        if (dest == null) {
            mainApp.snackBar(StringUtil.INFO, "Operation Cancelled", StringUtil.GREEN);
        } else {
            if (ExcelDatabaseHelper.writeDBPurchaseBill(dest))
                mainApp.snackBar(StringUtil.SUCCESS
                        , "Purchase bill Data Written to DB"
                        , StringUtil.GREEN);
            else
                mainApp.snackBar(StringUtil.SUCCESS
                        , "Purchase bill Data is NOT written to DB"
                        , StringUtil.RED);
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
        addTableColumn("Postage ", "extraAmount");
        addTableColumn("Total Net Amount", "totalAmount");
        addTableColumn("Send To Auditor", "hasGoneToAuditorString");
        addTableColumn("Remarks", "others");
        addTableColumn("Date Cleared", "dateCleared");
        addTableColumn("Bill Status", "status");

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
        if ((searchBox.getText() != null && !searchBox.getText().isEmpty()) || (searchBox2.getText() != null && !searchBox2.getText().isEmpty())) {
            if (searchBox.getText() != null && !searchBox.getText().isEmpty())
                purchaseBills = DatabaseHelperPurchaseBill.getPurchaseBillList(searchBox.getText());
            else purchaseBills = DatabaseHelperPurchaseBill.getPurchaseBillListByTotalNetAmount(searchBox2.getText());

        } else if (companyNameCBOX.getValue() != null)
            if (fromDate.getValue() != null && toDate.getValue() != null)
                purchaseBills = DatabaseHelperPurchaseBill.getPurchaseBillList(companyNameCBOX.getValue(), fromDate.getValue(), toDate.getValue());
            else
                purchaseBills = DatabaseHelperPurchaseBill.getPurchaseBillListByCompanyName(companyNameCBOX.getValue());
        else if (fromDate.getValue() != null && toDate.getValue() != null)
            purchaseBills = DatabaseHelperPurchaseBill.getPurchaseBillList(fromDate.getValue(), toDate.getValue());
        else
            purchaseBills = DatabaseHelperPurchaseBill.getAllPurchaseBillList();

        if (!billStatusComboBox.getValue().equals("ALL")) {
            ObservableList<PurchaseBill> temp = FXCollections.observableArrayList();
            for (PurchaseBill p : purchaseBills) {
                if (p.getStatus().equalsIgnoreCase(billStatusComboBox.getValue())) {
                    temp.add(p);
                }
            }
            purchaseBills = temp;
        }

        tableView.getItems().addAll(getFilterListBySendToAuditor(purchaseBills));
        tableView.sort();
        setTotalAmount();

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
            result = result && DatabaseHelperPurchaseBill.markBillAsGoneToAuditor(purchaseBill);
        loadTable();
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
