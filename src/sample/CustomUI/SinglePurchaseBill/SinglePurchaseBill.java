package sample.CustomUI.SinglePurchaseBill;

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
import sample.Alert.AlertMaker;
import sample.Main;
import sample.Model.PurchaseBill;
import sample.Utils.Preferences;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class SinglePurchaseBill extends HBox {

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


    public SinglePurchaseBill() {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource
                ("CustomUI/SinglePurchaseBill/SinglePurchaseBill.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
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

        companyNameCBOX.getItems().addAll(companyNames);


    }

    private void toggle(JFXTextField tField, JFXCheckBox tFieldExists) {
        tField.setDisable(!tFieldExists.isSelected());
        if (tFieldExists.isSelected()) {
            tField.getValidators().add(requiredFieldValidator);
            tField.validate();
        } else tField.getValidators().clear();
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
        if (amountAfterTaxTField.getText() == null || amountAfterTaxTField.getText().isEmpty() || isWrongFormat(amountAfterTaxTField.getText()))
            addRedBackgroundToTField(amountAfterTaxTField);
        if (is_12Exists.isSelected() && (_12TField.getText() == null || _12TField.getText().isEmpty() || isWrongFormat(_12TField.getText())))
            addRedBackgroundToTField(_12TField);
        if (is_18Exists.isSelected() && (_18TField.getText() == null || _18TField.getText().isEmpty() || isWrongFormat(_18TField.getText())))
            addRedBackgroundToTField(_18TField);
        if (is_28Exists.isSelected() && (_28TField.getText() == null || _28TField.getText().isEmpty() || isWrongFormat(_28TField.getText())))
            addRedBackgroundToTField(_28TField);

        if (ready) {
            String date = "" + Date.from((fromDate.getValue()).atTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.MILLISECOND)
                    .atZone(ZoneId.systemDefault()).toInstant()).getTime();

            purchaseBill = new PurchaseBill(date
                    , companyNameCBOX.getValue()
                    , invoiceTField.getText()
                    , amountBeforeTaxTField.getText()
                    , _12TField.getText()
                    , _18TField.getText()
                    , _28TField.getText()
                    , amountAfterTaxTField.getText()
                    , "" + hasGoneToAuditorCheckBox.isSelected());
        }
        return ready;
    }

    public PurchaseBill getPurchaseBill() {
        return purchaseBill;
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

    public void setPurchaseBill(PurchaseBill toEdit) {
        long l = Long.parseLong(toEdit.getDateInLong());
        Date d = new Date(l);
        fromDate.setValue(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        companyNameCBOX.getItems().addAll(companyNames);
        companyNameCBOX.getSelectionModel().select(toEdit.getCompanyName());

        invoiceTField.setText(toEdit.getInvoiceNo());
        invoiceTField.setEditable(false);
        invoiceTField.setDisable(true);

        hasGoneToAuditorCheckBox.setSelected(toEdit.hasGoneToAuditor());

        amountBeforeTaxTField.setText(toEdit.getAmountBeforeTax());

        if (!toEdit.getTwelve().equals("0")) {
            is_12Exists.setSelected(true);
            _12TField.setText(toEdit.getTwelve());
            _12TField.setDisable(false);
        }
        if (!toEdit.getEighteen().equals("0")) {
            is_18Exists.setSelected(true);
            _18TField.setText(toEdit.getEighteen());
            _18TField.setDisable(false);
        }
        if (!toEdit.getTwentyEight().equals("0")) {
            is_28Exists.setSelected(true);
            _28TField.setText(toEdit.getTwentyEight());
            _28TField.setDisable(false);
        }

        amountAfterTaxTField.setText(toEdit.getTotalAmount());
        invoiceTField.setEditable(false);

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
}
