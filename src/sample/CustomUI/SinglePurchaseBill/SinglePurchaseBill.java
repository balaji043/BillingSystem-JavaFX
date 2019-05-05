package sample.CustomUI.SinglePurchaseBill;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sample.Alert.AlertMaker;
import sample.Main;
import sample.Model.PurchaseBill;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class SinglePurchaseBill extends HBox {

    @FXML
    private JFXDatePicker fromDate;
    @FXML
    private JFXComboBox<String> companyNameCBOX;
    @FXML
    private JFXCheckBox is_12Exists, is_18Exists, is_28Exists;
    @FXML
    private JFXTextField invoiceTField, amountBeforeTaxTField, amountAfterTaxTField;
    @FXML
    private JFXTextField _12TField, _18TField, _28TField;
    @FXML
    private Text slNoText;

    private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator("*");
    private boolean ready = true;
    private PurchaseBill purchaseBill = null;


    public SinglePurchaseBill() {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource
                ("CustomUI/SinglePurchaseBill/SinglePurchaseBill.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

            is_12Exists.setSelected(false);
            is_18Exists.setSelected(false);
            is_28Exists.setSelected(false);

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

        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
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
                    , getTaxAmounts()
                    , amountAfterTaxTField.getText());
        }
        return ready;
    }

    public PurchaseBill getPurchaseBill() {
        return purchaseBill;
    }

    private String[] getTaxAmounts() {
        String[] taxAmounts = {"", "", ""};
        if (is_12Exists.isSelected()) taxAmounts[0] = _12TField.getText();
        if (is_18Exists.isSelected()) taxAmounts[0] = _18TField.getText();
        if (is_28Exists.isSelected()) taxAmounts[0] = _28TField.getText();
        return taxAmounts;
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
}
