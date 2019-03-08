package sample.CustomUI.SingleProduct;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;
import sample.Alert.AlertMaker;
import sample.Main;
import sample.Model.Product;
import sample.Utils.Preferences;

public class SingleProduct extends HBox {
    @FXML
    private JFXTextField name, hsn, qty, rate, discount;
    @FXML
    private Label amount;
    @FXML
    private JFXComboBox<String> tax, per;
    @FXML
    private JFXCheckBox checkBox;
    @FXML
    private Text slNo;

    private Product product = null;

    public SingleProduct() {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource
                ("CustomUI/SingleProduct/singleProductLayout.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            checkBox.setSelected(false);
            discount.setEditable(false);
            discount.setDisable(true);

            RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator("*");
            name.getValidators().add(requiredFieldValidator);
            hsn.getValidators().add(requiredFieldValidator);
            qty.getValidators().add(requiredFieldValidator);
            rate.getValidators().add(requiredFieldValidator);
            tax.getValidators().add(requiredFieldValidator);
            per.getValidators().add(requiredFieldValidator);
            discount.getValidators().add(requiredFieldValidator);

            checkBox.setOnAction(e -> {
                if (checkBox.isSelected()) {
                    discount.setEditable(true);
                    discount.setDisable(false);
                    discount.validate();
                } else {
                    discount.setEditable(false);
                    discount.setDisable(true);
                    discount.getValidators().clear();
                    discount.resetValidation();
                }
            });
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

        String errorMsg = " ";

        if ((name.getText() == null || name.getText().isEmpty()
                || hsn.getText() == null || hsn.getText().isEmpty()
                || qty.getText() == null || qty.getText().isEmpty()
                || rate.getText() == null || rate.getText().isEmpty()
                || tax.getValue() == null || per.getValue() == null)
                || (checkBox.isSelected() && (discount.getText() == null
                || discount.getText().isEmpty()))) {

            if (name.getText() == null || name.getText().isEmpty()) errorMsg = " Name ";
            if (hsn.getText() == null || hsn.getText().isEmpty()) errorMsg += " HSN ";
            if (qty.getText() == null || qty.getText().isEmpty()) errorMsg += " QTY ";
            if (rate.getText() == null || rate.getText().isEmpty()) errorMsg += " Rate ";
            if (tax.getValue() == null) errorMsg += " Tax ";
            if (per.getValue() == null) errorMsg += " PerData ";
            if ((checkBox.isSelected() && (discount.getText() == null
                    || discount.getText().isEmpty()))) errorMsg += " Discount ";
            return slNo.getText() + errorMsg + " are mandatory";
        } else {
            try {
                Integer.parseInt(qty.getText());
            } catch (Exception e) {
                return slNo.getText() + " Qty Rate Discount should be in numeral value";
            }
            try {
                Integer.parseInt(rate.getText());
            } catch (Exception e) {
                return slNo.getText() + " Rate should be in number value";
            }
            if (checkBox.isSelected()) {
                try {
                    Float.parseFloat(discount.getText());
                } catch (Exception e) {
                    return slNo.getText() + " Discount should be in number or decimal value";
                }
            }
        }

        if (name.getText().length() >= 30)
            return slNo.getText() + " Name cannot exceeds length 30 or higher";

        String rates = rate.getText();

        if (checkBox.isSelected()) {

            float r = Integer.parseInt(rate.getText()), d = Float.parseFloat(discount.getText());
            float val = (r * d) / 100;
            rates = String.format("%.2f", r - val);

        }
        product = new Product(name.getText(), hsn.getText(), qty.getText(), tax.getValue()
                , rates, per.getValue());

        amount.setText("Amount : " + product.getTotalAmount());

        return "s";
    }

    public Product getProduct() {
        return product;
    }

    public void setSlNO(int s) {
        slNo.setText("" + s);
    }

    public void setProduct(Product product){
        name.setText(product.getName());
        hsn.setText(product.getHsn());
        qty.setText(product.getQty());
        rate.setText(product.getRate());
        amount.setText(product.getTotalAmount());
        tax.setValue(product.getTax());
        per.setValue(product.getPer());
    }
}
