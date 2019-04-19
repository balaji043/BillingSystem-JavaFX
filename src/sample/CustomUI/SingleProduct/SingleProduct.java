package sample.CustomUI.SingleProduct;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
    private JFXCheckBox checkBoxDiscount, checkBoxAdd;
    @FXML
    private Text slNo;

    private Product product = null;

    private RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator("*");


    public SingleProduct() {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource
                ("CustomUI/SingleProduct/singleProductLayout.fxml"));
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

        String errorMsg = " ";

        if ((name.getText() == null || name.getText().isEmpty()
                || hsn.getText() == null || hsn.getText().isEmpty()
                || qty.getText() == null || qty.getText().isEmpty()
                || rate.getText() == null || rate.getText().isEmpty()
                || tax.getValue() == null || per.getValue() == null)
                || (checkBoxDiscount.isSelected() && checkBoxAdd.isSelected())
                || (
                (checkBoxDiscount.isSelected() || checkBoxAdd.isSelected()) && (discount.getText() == null
                        || discount.getText().isEmpty()))) {

            if (name.getText() == null || name.getText().isEmpty()) {
                errorMsg = " Name ";
                name.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
            }
            if (hsn.getText() == null || hsn.getText().isEmpty()) {
                errorMsg += " HSN ";
                hsn.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
            }
            if (qty.getText() == null || qty.getText().isEmpty()) {
                errorMsg += " QTY ";
                qty.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
            }
            if (rate.getText() == null || rate.getText().isEmpty()) {
                errorMsg += " Rate ";
                rate.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
            }
            if (tax.getValue() == null) {
                errorMsg += " Tax ";
                tax.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
            }
            if (per.getValue() == null) {
                errorMsg += " PerData ";
                per.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
            }

            if ((checkBoxDiscount.isSelected() || checkBoxAdd.isSelected()) && (discount.getText() == null
                    || discount.getText().isEmpty())) {
                errorMsg += " Discount ";
                discount.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
            }
            if (errorMsg.length() != 0)
                errorMsg = slNo.getText() + errorMsg + " are mandatory";
            if (checkBoxDiscount.isSelected() && checkBoxAdd.isSelected())
                errorMsg += ". Only choose one Option for Discount.";
            return errorMsg.toUpperCase();

        } else {
            try {
                Integer.parseInt(qty.getText());
            } catch (Exception e) {
                qty.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
                return slNo.getText() + " Qty Rate Discount should be in numeral value";
            }
            try {
                Float.parseFloat(rate.getText());
            } catch (Exception e) {
                rate.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                        , new CornerRadii(0)
                        , new Insets(0, 0, 0, 0))));
                return slNo.getText() + " Rate should be in number or decimal value";
            }
            if (checkBoxDiscount.isSelected() || checkBoxAdd.isSelected()) {
                try {
                    Float.parseFloat(discount.getText());
                } catch (Exception e) {
                    discount.setBackground(new Background(new BackgroundFill(Color.valueOf("#ffcccc")
                            , new CornerRadii(0)
                            , new Insets(0, 0, 0, 0))));
                    return slNo.getText() + " Discount should be in number or decimal value";
                }
            }
        }

        if (name.getText().length() >= 50)
            return slNo.getText() + " Name cannot exceeds length 30 or higher";

        String rates = rate.getText();

        if (checkBoxDiscount.isSelected() || checkBoxAdd.isSelected()) {
            float r = Float.parseFloat(rate.getText()), d = Float.parseFloat(discount.getText());
            float val = (r * d) / 100;

            if (checkBoxAdd.isSelected() && !checkBoxDiscount.isSelected())
                rates = String.format("%.2f", r + val);
            else if (!checkBoxAdd.isSelected() && checkBoxDiscount.isSelected())
                rates = String.format("%.2f", r - val);
            else
                return slNo.getText() + "Check Discount Section";
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

    public void setProduct(Product product) {
        name.setText(product.getName());
        hsn.setText(product.getHsn());
        qty.setText(product.getQty());
        rate.setText(product.getRate());
        tax.setValue(product.getTax());
        per.setValue(product.getPer());
        amount.setText("Amount : " + product.getTotalAmount());
    }

    private void toggle() {
        if (checkBoxAdd.isSelected() && checkBoxDiscount.isSelected()) {
            discount.setEditable(false);
            discount.setDisable(true);
            discount.getValidators().clear();
            discount.getValidators().add(new RequiredFieldValidator("Select Only One Option"));
            discount.validate();
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

}
