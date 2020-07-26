package bam.billing.gst.controller;

import bam.billing.gst.alert.AlertMaker;
import bam.billing.gst.model.PurchaseBill;
import bam.billing.gst.utils.Preferences;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static bam.billing.gst.utils.ResourceConstants.Views;

public class SinglePurchaseBillController extends HBox {

    private static final Logger LOGGER = Logger.getLogger(SinglePurchaseBillController.class.getName());
    private static final String COLOR_CODE = "#ffcccc";
    @FXML
    public JFXDatePicker billDateCleared;
    @FXML
    public JFXComboBox<String> billStatus;
    @FXML
    public JFXTextField othersDetails;
    @FXML
    private JFXDatePicker invoiceDate;
    @FXML
    private JFXComboBox<String> companyNameCBOX;
    @FXML
    private JFXCheckBox is12Exists;
    @FXML
    private JFXCheckBox is18Exists;
    @FXML
    private JFXCheckBox is28Exists;
    @FXML
    private JFXCheckBox hasGoneToAuditorCheckBox;
    @FXML
    private JFXTextField invoiceTField;
    @FXML
    private JFXTextField amountBeforeTaxTField;
    @FXML
    private JFXTextField amountAfterTaxTField;
    @FXML
    private JFXTextField tax12TField;
    @FXML
    private JFXTextField tax18TField;
    @FXML
    private JFXTextField tax28TField;
    @FXML
    private JFXTextField extraAmountTextField;
    @FXML
    private Text slNoText;
    @FXML
    private HBox twelve;
    @FXML
    private HBox eighteen;
    @FXML
    private HBox twentyEight;

    private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator("*");
    private boolean ready = true;
    private PurchaseBill purchaseBill = null;
    private Set<String> companyNames = Preferences.getPreferences().getCompanyNames();


    public SinglePurchaseBillController() {
        FXMLLoader fxmlLoader = Views.getFXMLLoaderWithUrl(Views.SINGLE_PURCHASE_BILL);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        ObservableList<String> statusValues = FXCollections.observableArrayList();
        statusValues.addAll("YES", "NO", "PARTIAL");

        billStatus.setItems(statusValues);

        is12Exists.setSelected(false);
        is18Exists.setSelected(false);
        is28Exists.setSelected(false);

        tax12TField.setText("0");
        tax18TField.setText("0");
        tax28TField.setText("0");

        tax12TField.setDisable(true);
        tax18TField.setDisable(true);
        tax28TField.setDisable(true);

        is12Exists.setOnAction(e -> toggle(tax12TField, is12Exists));
        is18Exists.setOnAction(e -> toggle(tax18TField, is18Exists));
        is28Exists.setOnAction(e -> toggle(tax28TField, is28Exists));

        invoiceTField.getValidators().add(requiredFieldValidator);
        amountBeforeTaxTField.getValidators().add(requiredFieldValidator);
        amountAfterTaxTField.getValidators().add(requiredFieldValidator);

        invoiceTField.validate();
        amountBeforeTaxTField.validate();
        amountAfterTaxTField.validate();

        companyNameCBOX.getItems().addAll(companyNames);

        TextFields.bindAutoCompletion(companyNameCBOX.getEditor(), companyNames);

    }

    private void toggle(JFXTextField tField, JFXCheckBox tFieldExists) {
        tField.setDisable(!tFieldExists.isSelected());
        if (tFieldExists.isSelected()) {
            tField.getValidators().add(requiredFieldValidator);
            tField.validate();
        } else {
            tField.setText("0");
            tField.getValidators().clear();
        }
    }

    public boolean isReady() {
        ready = isValid();

        amountAfterTaxTField.setText(getTotalAmount());
        if (ready) {
            String date = "" + Date.from((invoiceDate.getValue()).atTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.MILLISECOND)
                    .atZone(ZoneId.systemDefault()).toInstant()).getTime();
            String dateCleared = "";
            if (billDateCleared.getValue() != null) {
                dateCleared = "" + Date.from((billDateCleared.getValue()).atTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.MILLISECOND)
                        .atZone(ZoneId.systemDefault()).toInstant()).getTime();
            }
            purchaseBill = new PurchaseBill(date
                    , companyNameCBOX.getValue()
                    , invoiceTField.getText()
                    , amountBeforeTaxTField.getText()
                    , tax12TField.getText()
                    , tax18TField.getText()
                    , tax28TField.getText()
                    , amountAfterTaxTField.getText()
                    , "" + hasGoneToAuditorCheckBox.isSelected()
                    , othersDetails.getText()
                    , dateCleared
                    , billStatus.getValue()
                    , "" + extraAmountTextField.getText());
        }
        return ready;
    }

    private String getTotalAmount() {
        return "" + (getFloatValue(amountBeforeTaxTField)
                + getFloatValue(tax12TField)
                + getFloatValue(tax18TField)
                + getFloatValue(tax28TField)
                + getFloatValue(extraAmountTextField));
    }

    private float getFloatValue(JFXTextField s) {
        if (s.getText() == null || s.getText().isEmpty() || s.getText().equalsIgnoreCase("0"))
            return 0;
        try {
            return Float.parseFloat(s.getText());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return 0;
    }

    private boolean isValid() {
        boolean s = true;
        if (invoiceDate.getValue() == null)
            s = addRedBackgroundToTField(invoiceDate);
        if (companyNameCBOX.getValue() == null)
            s = addRedBackgroundToTField(companyNameCBOX);
        if (invoiceTField.getText() == null || invoiceTField.getText().isEmpty())
            s = addRedBackgroundToTField(invoiceTField);
        if (amountBeforeTaxTField.getText() == null || amountBeforeTaxTField.getText().isEmpty() || isWrongFormat(amountBeforeTaxTField.getText()))
            s = addRedBackgroundToTField(amountBeforeTaxTField);
        if (is12Exists.isSelected() && (tax12TField.getText() == null || tax12TField.getText().isEmpty() || isWrongFormat(tax12TField.getText())))
            s = addRedBackgroundToTField(tax12TField);
        if (is18Exists.isSelected() && (tax18TField.getText() == null || tax18TField.getText().isEmpty() || isWrongFormat(tax18TField.getText())))
            s = addRedBackgroundToTField(tax18TField);
        if (is28Exists.isSelected() && (tax28TField.getText() == null || tax28TField.getText().isEmpty() || isWrongFormat(tax28TField.getText())))
            s = addRedBackgroundToTField(tax28TField);
        if (billStatus.getValue() == null || billStatus.getValue().isEmpty())
            s = addRedBackgroundToTField(billStatus);
        return s;
    }

    public PurchaseBill getPurchaseBill() {
        return purchaseBill;
    }

    public void setPurchaseBill(PurchaseBill toEdit) {
        if (toEdit == null) {
            invoiceDate.getEditor().clear();
            companyNameCBOX.getItems().clear();
            invoiceTField.clear();
            hasGoneToAuditorCheckBox.setSelected(false);
            amountBeforeTaxTField.clear();
            setTaxValues(is12Exists, tax12TField, "0");
            setTaxValues(is18Exists, tax18TField, "0");
            setTaxValues(is28Exists, tax28TField, "0");
            amountAfterTaxTField.clear();
            invoiceTField.clear();
            othersDetails.clear();
            billDateCleared.setValue(null);
            billStatus.getSelectionModel().clearSelection();
            return;
        }
        long l = Long.parseLong(toEdit.getDateInLong());
        Date d = new Date(l);
        invoiceDate.setValue(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        companyNameCBOX.setDisable(false);
        companyNameCBOX.getItems().addAll(companyNames);
        companyNameCBOX.getSelectionModel().select(toEdit.getCompanyName());

        invoiceTField.setText(toEdit.getInvoiceNo());
        invoiceTField.setEditable(false);
        invoiceTField.setDisable(true);

        hasGoneToAuditorCheckBox.setSelected(toEdit.hasGoneToAuditor());

        amountBeforeTaxTField.setText(toEdit.getAmountBeforeTax());
        extraAmountTextField.setText(toEdit.getExtraAmount());

        setTaxValues(is12Exists, tax12TField, toEdit.getTwelve());
        setTaxValues(is18Exists, tax18TField, toEdit.getEighteen());
        setTaxValues(is28Exists, tax28TField, toEdit.getTwentyEight());

        amountAfterTaxTField.setText(toEdit.getTotalAmount());
        invoiceTField.setEditable(false);
        othersDetails.setText(toEdit.getOthers());
        billStatus.getSelectionModel().select(toEdit.getStatus());
        if (toEdit.getDateClearedAsLong() != null && !toEdit.getDateClearedAsLong().isEmpty()) {
            l = Long.parseLong(toEdit.getDateClearedAsLong());
            d = new Date(l);
            billDateCleared.setValue(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

    private boolean isWrongFormat(String text) {
        try {
            Float.parseFloat(text);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    private boolean addRedBackgroundToTField(Region tField) {
        tField.setBackground(new Background(new BackgroundFill(Color.valueOf(COLOR_CODE)
                , new CornerRadii(0)
                , new Insets(0, 0, 0, 0))));
        ready = false;
        return false;
    }

    public void setSlNoText(String slNoText) {
        this.slNoText.setText(slNoText);
    }

    public VBox getVBox() {
        VBox vBox = new VBox();
        vBox.setSpacing(25);
        vBox.setAlignment(Pos.TOP_LEFT);
        twelve.setAlignment(Pos.CENTER_LEFT);
        eighteen.setAlignment(Pos.CENTER_LEFT);
        twentyEight.setAlignment(Pos.CENTER_LEFT);
        hasGoneToAuditorCheckBox.setText("Send To Auditor");
        vBox.getChildren().addAll(invoiceDate
                , companyNameCBOX
                , invoiceTField
                , amountBeforeTaxTField
                , twelve
                , eighteen
                , twentyEight
                , extraAmountTextField
                , amountAfterTaxTField
                , hasGoneToAuditorCheckBox
                , othersDetails
                , billDateCleared
                , billStatus);
        return vBox;
    }

    private void setTaxValues(JFXCheckBox isSelected, JFXTextField value, String text) {
        if (!text.equals("0.00")) {
            isSelected.setSelected(true);
            value.setText(text);
            value.setDisable(false);
        } else {
            isSelected.setSelected(false);
            value.setText(text);
            value.setDisable(true);
        }
    }

    public void markRed() {
        addRedBackgroundToTField(invoiceTField);
    }

    public void setAmountAfterText() {
        amountAfterTaxTField.setText(getTotalAmount());
    }
}
