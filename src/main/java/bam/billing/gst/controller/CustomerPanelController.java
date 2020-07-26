package bam.billing.gst.controller;

import bam.billing.gst.Main;
import bam.billing.gst.alert.AlertMaker;
import bam.billing.gst.model.Customer;
import bam.billing.gst.service.CustomerService;
import bam.billing.gst.service.ExcelDatabaseHelper;
import bam.billing.gst.service.ExcelHelper;
import bam.billing.gst.utils.BillingSystemUtils;
import bam.billing.gst.utils.GenericController;
import bam.billing.gst.utils.ICON;
import bam.billing.gst.utils.StringUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerPanelController implements Initializable, GenericController {

    private static final Logger LOGGER = Logger.getLogger(CustomerPanelController.class.getName());

    @FXML
    private JFXCheckBox checkGST, checkGST1, checkGST2;
    @FXML
    private JFXTextField cnNameSearchTField;
    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXTextField cnName, phone, gstIn, address1, address2, state, zip;
    @FXML
    private JFXButton searchBTN;
    @FXML
    private JFXButton exportButton;
    @FXML
    private JFXButton importButton;
    @FXML
    private JFXButton deleteBTN;
    @FXML
    private JFXButton submitBTN;

    @FXML
    private TableView<Customer> userTableView;

    private Main mainApp;

    private RequiredFieldValidator validator = new RequiredFieldValidator("*");
    private boolean isNewCustomer = true;

    private Customer customer = null;

    private ObservableList<Customer> all = FXCollections.observableArrayList();

    private ObservableList<Customer> gst = FXCollections.observableArrayList();

    private ObservableList<Customer> nonGST = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        BillingSystemUtils.setImageViewToButtons(ICON.UPLOAD, importButton);
        BillingSystemUtils.setImageViewToButtons(ICON.DOWNLOAD, exportButton);
        BillingSystemUtils.setImageViewToButtons(ICON.SEARCH, searchBTN);
        BillingSystemUtils.setImageViewToButtons(ICON.DELETE, deleteBTN);
        BillingSystemUtils.setImageViewToButtons(ICON.SAVE, submitBTN);


        cnName.getValidators().add(validator);
        phone.getValidators().add(validator);
        gstIn.getValidators().add(validator);
        address1.getValidators().add(validator);
        address2.getValidators().add(validator);
        state.getValidators().add(validator);
        zip.getValidators().add(validator);
        checkGST1.setSelected(true);
        checkGST2.setSelected(true);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        init();
        gstIn.setDisable(true);
        userTableView.setOnMouseClicked(e -> {
            clearAll();
            customer = userTableView.getSelectionModel().getSelectedItem();
            if (customer == null) return;
            isNewCustomer = false;
            setAll(customer);
        });
        checkGST.setOnAction(e -> toggle(checkGST.isSelected()));
        checkGST1.setOnAction(e -> setCustomer());
        checkGST2.setOnAction(e -> setCustomer());
    }

    private void setAll(Customer customer) {

        cnName.setText(customer.getName());
        checkGST.setSelected(!customer.getGstIn().equals(StringUtil.getForOwnUse()));
        toggle(checkGST.isSelected());
        gstIn.setText(customer.getGstIn());
        address1.setText(customer.getStreetAddress());
        address2.setText(customer.getCity());
        state.setText(customer.getState());
        zip.setText(customer.getZipCode());
        phone.setText(customer.getPhone());
    }

    private void init() {
        userTableView.getColumns().clear();
        userTableView.getItems().clear();

        addTableColumn("Name", "name");
        addTableColumn("Phone", "phone");
        addTableColumn("GST IN", "gstIn");
        addTableColumn("Street", "streetAddress");
        addTableColumn("City", "city");
        addTableColumn("State", "state");
        addTableColumn("Zip", "zipCode");
        userTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setCustomer();
        borderPane.setCenter(userTableView);
    }

    @FXML
    void handleDelete() {
        try {
            Customer customerInner = userTableView.getSelectionModel().getSelectedItem();
            boolean okay;
            okay = AlertMaker.showMCAlert("Confirm delete?"
                    , "If you delete now, then the Data shown in the bills of this customer will be the ones saved at" +
                            " the time of bill submission.\n" + customerInner.getName() + "'settingsButton data"
                    , mainApp);
            if (okay) {
                if (CustomerService.deleteCustomer(customerInner)) {
                    mainApp.snackBar(StringUtil.SUCCESS
                            , "Selected Customer'settingsButton data is deleted"
                            , StringUtil.GREEN);
                    clearAll();
                } else {
                    mainApp.snackBar(StringUtil.FAILED
                            , "Selected User'settingsButton data is not deleted"
                            , StringUtil.RED);
                    clearAll();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        setCustomer();
    }

    @FXML
    void handleSubmit() {
        boolean submitted = false;
        if (isNewCustomer) {
            if (check()) {

                Customer customerInner = new Customer(cnName.getText()
                        , phone.getText(), gstIn.getText()
                        , "" + address1.getText()
                        , "" + address2.getText()
                        , state.getText()
                        , zip.getText()
                        , "CUS" + new SimpleDateFormat("yyyyMMddHHSSS").format(new Date()));
                submitted = CustomerService.insertNewCustomer(customerInner);
                if (submitted) {
                    mainApp.snackBar(StringUtil.SUCCESS
                            , "New Customer Added Successfully"
                            , StringUtil.GREEN);
                } else {
                    mainApp.snackBar(StringUtil.FAILED
                            , "New Customer Not Added"
                            , StringUtil.RED);
                }
            }
        } else {
            if (check()) {

                Customer newCustomer = new Customer(cnName.getText()
                        , "" + phone.getText(), "" + gstIn.getText()
                        , "" + address1.getText(), "" + address2.getText()
                        , "" + state.getText(), "" + zip.getText()
                        , customer.getId());
                submitted = CustomerService.updateCustomer(newCustomer);
                if (submitted) {
                    mainApp.snackBar(StringUtil.SUCCESS
                            , "Customer Data Updated Successfully"
                            , StringUtil.RED);
                } else {
                    mainApp.snackBar(StringUtil.FAILED
                            , "Customer Data Not Updated Successfully"
                            , StringUtil.RED);
                }
            }
        }
        if (submitted) {
            setCustomer();
            clearAll();
        }
    }


    @FXML
    void handleImport() {

        File dest = mainApp.chooseFile();
        if (dest == null) {
            mainApp.snackBar(StringUtil.INFO
                    , "Operation Cancelled"
                    , StringUtil.GREEN);
        } else {
            if (ExcelDatabaseHelper.writeDBCustomer(dest)) {
                mainApp.snackBar(StringUtil.SUCCESS
                        , "Stock History Data Written to Excel"
                        , StringUtil.GREEN);
            } else {
                mainApp.snackBar(StringUtil.FAILED
                        , "Stock History Data is NOT written to Excel"
                        , StringUtil.RED);
            }
        }
        init();
    }

    @FXML
    void handleExport() {
        if (all.size() == 0) {
            mainApp.snackBar(StringUtil.INFO
                    , "Nothing to Import"
                    , StringUtil.GREEN);
            return;
        }

        File dest = mainApp.chooseFile();
        if (dest == null) {
            mainApp.snackBar(StringUtil.INFO
                    , "Operation Cancelled"
                    , StringUtil.GREEN);
        } else {
            if (ExcelHelper.writeExcelCustomer(dest, all))
                mainApp.snackBar(StringUtil.SUCCESS
                        , "Customer Data Written to Excel"
                        , StringUtil.GREEN);
            else
                mainApp.snackBar(StringUtil.FAILED
                        , "Customer Data is NOT written to Excel"
                        , StringUtil.RED);
        }
    }

    private void addTableColumn(String name, String msg) {
        TableColumn<Customer, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(msg));
        column.setPrefWidth(100);
        userTableView.getColumns().add(column);
    }

    private void clearAll() {
        isNewCustomer = true;
        cnName.setText("");
        phone.setText("");
        address1.setText("");
        gstIn.setText("");
        address2.setText("");
        state.setText("");
        zip.setText("");
        checkGST.setSelected(false);
        gstIn.setDisable(true);
        customer = null;
    }

    private boolean check() {

        if (cnName.getText() == null || cnName.getText().isEmpty()) {
            cnName.validate();
            return false;
        }

        if (checkGST.isSelected()) {
            if (phone.getText() == null || phone.getText().isEmpty()) {
                phone.validate();
            }
            if (gstIn.getText().length() != 15) {
                mainApp.snackBar(StringUtil.INFO
                        , "Enter Correct length of GSTIN NO Entered : " + gstIn.getText().length()
                        , StringUtil.RED);
                return false;
            }
        } else {
            gstIn.setText(StringUtil.getForOwnUse());
        }
        if ((phone.getText() != null
                && !phone.getText().isEmpty())
                && (phone.getText().length() != 10
                || !checkNum(phone.getText()))) {
            mainApp.snackBar(StringUtil.INFO
                    , "Enter Correct Phone"
                    , StringUtil.RED);
            return false;

        }
        return true;
    }

    private boolean checkNum(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void toggle(boolean b) {
        if (b) {
            gstIn.setDisable(false);
            gstIn.setEditable(true);
            gstIn.getValidators().add(validator);
            gstIn.validate();
            if (gstIn.getText().equalsIgnoreCase(StringUtil.getForOwnUse())) {
                gstIn.setText("");
            }
        } else {
            gstIn.setDisable(true);
            gstIn.setEditable(false);
            gstIn.resetValidation();
            if (!gstIn.getText().equalsIgnoreCase(StringUtil.getForOwnUse())) {
                gstIn.setText(StringUtil.getForOwnUse());
            }
        }
    }

    private void setCustomer() {
        getCustomers();
        userTableView.getItems().clear();
        if ((checkGST1.isSelected() && checkGST2.isSelected())
                || (!checkGST1.isSelected() && !checkGST2.isSelected())) {

            userTableView.getItems().addAll(all);
            TextFields.bindAutoCompletion(cnNameSearchTField, CustomerService.getCustomerNameList(2));

        } else if (checkGST1.isSelected()) {

            userTableView.getItems().addAll(gst);
            TextFields.bindAutoCompletion(cnNameSearchTField, CustomerService.getCustomerNameList(1));

        } else {

            userTableView.getItems().addAll(nonGST);
            TextFields.bindAutoCompletion(cnNameSearchTField, CustomerService.getCustomerNameList(0));

        }
    }

    private void getCustomers() {
        all = CustomerService.getCustomerList();
        nonGST.clear();
        gst.clear();
        for (Customer c : all) {
            if (c.getGstIn().equals(StringUtil.getForOwnUse()))
                nonGST.add(c);
            else gst.add(c);
        }
    }

    public void handleSearch() {
        if (cnNameSearchTField.getText() != null && !cnNameSearchTField.getText().isEmpty()) {
            userTableView.getItems().clear();
            userTableView.getItems().addAll(CustomerService.getCustomerList(cnNameSearchTField.getText()));
        }
    }
}
