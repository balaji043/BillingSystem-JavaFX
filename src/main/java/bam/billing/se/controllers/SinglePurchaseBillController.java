package bam.billing.se.controllers;

import bam.billing.se.models.PurchaseBill;
import bam.billing.se.utils.AlertMaker;
import bam.billing.se.utils.BillingSystemUtils;
import bam.billing.se.utils.Preferences;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
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
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static bam.billing.se.utils.ResourceConstants.Views;

public class SinglePurchaseBillController extends HBox {

    private static final Logger LOGGER = Logger.getLogger(SinglePurchaseBillController.class.getName());
    @FXML
    private JFXDatePicker fromDate;
    @FXML
    private JFXComboBox<String> companyNameCBOX;
    @FXML
    private JFXCheckBox is_12Exists, is_18Exists, is_28Exists, hasGoneToAuditorCheckBox;
    @FXML
    private JFXTextField invoiceTField, amountBeforeTaxTField, amountAfterTaxTField;
    @FXML
    private JFXTextField _12TField, _18TField, _28TField;
    @FXML
    private Text slNoText;
    @FXML
    private HBox twelve, eighteen, twentyEight;

    private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator("*");
    private boolean ready = true;
    private PurchaseBill purchaseBill = null;
    private HashSet<String> companyNames = Preferences.getPreferences().getCompanyNames();


    public SinglePurchaseBillController() {
        FXMLLoader loader = BillingSystemUtils.getFXMLLoader(Views.SINGLE_PURCHASE_BILL);

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }

        is_12Exists.setSelected(false);
        is_18Exists.setSelected(false);
        is_28Exists.setSelected(false);

        _12TField.setText("0");
        _18TField.setText("0");
        _28TField.setText("0");

        _12TField.setDisable(true);
        _18TField.setDisable(true);
        _28TField.setDisable(true);

        is_12Exists.setOnAction(e -> toggle(_12TField, is_12Exists));
        is_18Exists.setOnAction(e -> toggle(_18TField, is_18Exists));
        is_28Exists.setOnAction(e -> toggle(_28TField, is_28Exists));

        invoiceTField.getValidators().add(requiredFieldValidator);
        amountBeforeTaxTField.getValidators().add(requiredFieldValidator);
        amountAfterTaxTField.getValidators().add(requiredFieldValidator);

        invoiceTField.validate();
        amountBeforeTaxTField.validate();
        amountAfterTaxTField.validate();
        amountAfterTaxTField.setDisable(true);

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
        ready = true;
        if (fromDate.getValue() == null) {
            fromDate.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                    , new CornerRadii(0)
                    , new Insets(0, 0, 0, 0))));
            ready = false;
        }
        if (companyNameCBOX.getValue() == null) {
            ready = false;
            companyNameCBOX.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                    , new CornerRadii(0)
                    , new Insets(0, 0, 0, 0))));
        }


        if (invoiceTField.getText() == null || invoiceTField.getText().isEmpty())
            addRedBackgroundToTField(invoiceTField);
        if (amountBeforeTaxTField.getText() == null || amountBeforeTaxTField.getText().isEmpty() || isWrongFormat(amountBeforeTaxTField.getText()))
            addRedBackgroundToTField(amountBeforeTaxTField);
        if (is_12Exists.isSelected() && (_12TField.getText() == null || _12TField.getText().isEmpty() || isWrongFormat(_12TField.getText())))
            addRedBackgroundToTField(_12TField);
        if (is_18Exists.isSelected() && (_18TField.getText() == null || _18TField.getText().isEmpty() || isWrongFormat(_18TField.getText())))
            addRedBackgroundToTField(_18TField);
        if (is_28Exists.isSelected() && (_28TField.getText() == null || _28TField.getText().isEmpty() || isWrongFormat(_28TField.getText())))
            addRedBackgroundToTField(_28TField);

        if (ready) {
            amountAfterTaxTField.setText(getTotalAmount());
            String date = "" + Date.from((fromDate.getValue()).atTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.MILLISECOND)
                    .atZone(ZoneId.systemDefault()).toInstant()).getTime();
            purchaseBill = new PurchaseBill(date
                    , "" + companyNameCBOX.getValue()
                    , "" + invoiceTField.getText()
                    , "" + amountBeforeTaxTField.getText()
                    , "" + _12TField.getText()
                    , "" + _18TField.getText()
                    , "" + _28TField.getText()
                    , "" + amountAfterTaxTField.getText()
                    , "" + hasGoneToAuditorCheckBox.isSelected());
        }
        return ready;
    }

    public PurchaseBill getPurchaseBill() {
        return purchaseBill;
    }

    public void setPurchaseBill(PurchaseBill toEdit) {
        if (toEdit == null) {
            fromDate.getEditor().clear();
            companyNameCBOX.getItems().clear();
            invoiceTField.clear();
            hasGoneToAuditorCheckBox.setSelected(false);
            amountBeforeTaxTField.clear();
            setTaxValues(is_12Exists, _12TField, "0");
            setTaxValues(is_18Exists, _18TField, "0");
            setTaxValues(is_28Exists, _28TField, "0");
            amountAfterTaxTField.clear();
            invoiceTField.clear();
            return;
        }
        long l = Long.parseLong(toEdit.getDateInLong());
        Date d = new Date(l);
        fromDate.setValue(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        companyNameCBOX.setDisable(true);
        companyNameCBOX.getItems().addAll(companyNames);
        companyNameCBOX.getSelectionModel().select(toEdit.getCompanyName());

        invoiceTField.setText(toEdit.getInvoiceNo());
        invoiceTField.setEditable(false);
        invoiceTField.setDisable(true);

        amountBeforeTaxTField.setText(toEdit.getAmountBeforeTax());

        setTaxValues(is_12Exists, _12TField, toEdit.getTwelve());
        setTaxValues(is_18Exists, _18TField, toEdit.getEighteen());
        setTaxValues(is_28Exists, _28TField, toEdit.getTwentyEight());

        hasGoneToAuditorCheckBox.setSelected(toEdit.hasGoneToAuditor());

        amountAfterTaxTField.setText(toEdit.getTotalAmount());
        invoiceTField.setEditable(false);
    }

    private boolean isWrongFormat(String text) {
        try {
            Float.parseFloat(text);
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return true;
        }
    }

    private void addRedBackgroundToTField(JFXTextField tField) {
        tField.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                , new CornerRadii(0)
                , new Insets(0, 0, 0, 0))));
        ready = false;
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
        vBox.getChildren().addAll(fromDate
                , companyNameCBOX
                , invoiceTField
                , amountBeforeTaxTField
                , twelve, eighteen
                , twentyEight
                , amountAfterTaxTField
                , hasGoneToAuditorCheckBox);
        return vBox;
    }

    private void setTaxValues(JFXCheckBox isSelected, JFXTextField value, String text) {
        if (!text.equals("0")) {
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

    private String getTotalAmount() {
        return "" + (getFloatValue(amountBeforeTaxTField)
                + getFloatValue(_12TField)
                + getFloatValue(_18TField)
                + getFloatValue(_28TField));
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

    public void setAmountAfterText() {
        amountAfterTaxTField.setText(getTotalAmount());
    }
}
