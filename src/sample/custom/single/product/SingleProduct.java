package sample.custom.single.product;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;
import sample.Utils.FxmlPaths;
import sample.Utils.Preferences;
import sample.alert.AlertMaker;
import sample.model.Product;

public class SingleProduct extends HBox {
    @FXML
    private JFXTextField name, hsn, qty, rate, discount;
    @FXML
    private Label amount;
    @FXML
    private JFXComboBox<String> tax, per;
    @FXML
    private JFXCheckBox checkBoxDiscount, checkBoxAdd;
    @FXML
    private Text slNo;

    private Product product = null;

    private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator("*");


    public SingleProduct() {
        FXMLLoader fxmlLoader = new FXMLLoader(FxmlPaths.SINGLE_PRODUCT);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            checkBoxDiscount.setSelected(false);
            discount.setEditable(false);
            discount.setDisable(true);

            name.getValidators().add(requiredFieldValidator);
            hsn.getValidators().add(requiredFieldValidator);
            qty.getValidators().add(requiredFieldValidator);
            rate.getValidators().add(requiredFieldValidator);
            tax.getValidators().add(requiredFieldValidator);
            per.getValidators().add(requiredFieldValidator);
            discount.getValidators().add(requiredFieldValidator);

            checkBoxDiscount.setOnAction(e -> toggle());
            checkBoxAdd.setOnAction(e -> toggle());

            name.validate();
            hsn.validate();
            qty.validate();
            rate.validate();
            tax.validate();
            per.validate();

            String[] taxData = {"12", "18", "28"};
            tax.getItems().addAll(taxData);
            per.getItems().addAll(Preferences.getPreferences().getPerData());
            TextFields.bindAutoCompletion(name, Preferences.getPreferences().getDescriptions());
            TextFields.bindAutoCompletion(hsn, Preferences.getPreferences().getHsn());

        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
    }

    public String isReady() {

        if (checkForNullCondition()) {
            return "Check the fields Marked in Red";
        }

        if (name.getText().length() >= 30)
            return slNo.getText() + " Name cannot exceeds length 30 or higher";


        product = new Product(name.getText(), hsn.getText(), qty.getText(), tax.getValue()
                , getRate(), per.getValue());

        amount.setText("Amount : " + product.getTotalAmount());

        return "settingsButton";
    }

    private String getRate() {
        String rates = rate.getText();
        if (checkBoxDiscount.isSelected() || checkBoxAdd.isSelected()) {
            float r = Float.parseFloat(rate.getText()), d = Float.parseFloat(discount.getText());
            float val = (r * d) / 100;

            if (checkBoxAdd.isSelected() && !checkBoxDiscount.isSelected())
                rates = String.format("%.2f", r + val);
            else if (!checkBoxAdd.isSelected() && checkBoxDiscount.isSelected())
                rates = String.format("%.2f", r - val);
        }
        return rates;
    }

    private boolean checkForNullCondition() {
        boolean n = checkTextField(name, false);
        boolean h = checkTextField(hsn, false);
        boolean q = checkTextField(qty, true);
        boolean r = checkTextField(rate, true);

        boolean t = checkComboBox(tax);
        boolean p = checkComboBox(per);
        boolean d = false;

        if (checkBoxDiscount.isSelected() || checkBoxAdd.isSelected())
            d = checkTextField(discount, true);


        if (checkBoxDiscount.isSelected() && checkBoxAdd.isSelected()) {
            setBg(discount);
            return true;
        }

        return !(n && h && q && r && t && p && d);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        name.setText(product.getName());
        hsn.setText(product.getHsn());
        qty.setText(product.getQty());
        rate.setText(product.getRate());
        tax.setValue(product.getTax());
        per.setValue(product.getPer());
        amount.setText("Amount : " + product.getTotalAmount());
    }

    public void setSlNO(int s) {
        slNo.setText("" + s);
    }

    private void toggle() {
        if (checkBoxAdd.isSelected() && checkBoxDiscount.isSelected()) {
            discount.setEditable(false);
            discount.setDisable(true);
            discount.getValidators().clear();
            discount.getValidators().add(new RequiredFieldValidator("Select Only One Option"));
            discount.validate();
            discount.setText("");
        }
        if ((checkBoxDiscount.isSelected() && !checkBoxAdd.isSelected())
                || (!checkBoxDiscount.isSelected() && checkBoxAdd.isSelected())) {
            discount.setEditable(true);
            discount.setDisable(false);
            discount.getValidators().add(requiredFieldValidator);
            discount.validate();
        } else {
            discount.setEditable(false);
            discount.setDisable(true);
            discount.getValidators().clear();
            discount.resetValidation();
        }
    }

    private boolean checkTextField(JFXTextField textField, boolean isNumber) {
        if (textField.getText() == null || textField.getText().isEmpty()) {
            setBg(textField);
            return false;
        } else if (isNumber) {
            try {
                Float.parseFloat(qty.getText());
            } catch (Exception e) {
                setBg(qty);
                return false;
            }
        }
        return true;
    }

    private boolean checkComboBox(JFXComboBox jfxComboBox) {
        if (jfxComboBox.getValue() == null) {
            setBg(jfxComboBox);
            return false;
        }
        return true;
    }

    private void setBg(Region region) {
        region.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                , new CornerRadii(0)
                , new Insets(0, 0, 0, 0))));
    }

}
