package bam.billing.se.controllers;

import bam.billing.se.utils.Preferences;
import bam.billing.se.utils.ResourceConstants;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class SettingsController implements Initializable {
    @FXML
    VBox main;
    @FXML
    JFXTextField storeNameTF;
    @FXML
    JFXTextField addressLineOneTF;
    @FXML
    JFXTextField addressLineTwoTF;
    @FXML
    JFXTextField gstNumberTF;
    @FXML
    JFXTextField bankNameTF;
    @FXML
    JFXTextField branchNameTF;
    @FXML
    JFXTextField bankAccountNoTF;
    @FXML
    JFXTextField ifscCodeTF;
    @FXML
    JFXTextField phoneNumberTF;
    @FXML
    JFXTextField productMaxLimitPerBillTF;
    @FXML
    JFXTextField billInvoiceCodePrefixTF;
    @FXML
    JFXComboBox<String> descriptionStringJFXComboBox;
    @FXML
    JFXComboBox<String> perDataStringJFXComboBox;
    @FXML
    JFXComboBox<String> cssThemeStringJFXComboBox;
    @FXML
    JFXComboBox<String> hsnDataStringJFXComboBox;
    private Preferences preferences;
    private HashSet<String> descriptionsDataSet, perDataSet, hsnDataSet;
    private String descriptionOld, descriptionNew, perDataOld, perDataNew, hsnOld, hsnNew;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preferences = Preferences.getPreferences();
        descriptionsDataSet = preferences.getDescriptions();
        perDataSet = preferences.getPerData();
        hsnDataSet = preferences.getHsn();
        storeNameTF.setText(preferences.getStoreName());
        addressLineOneTF.setText(preferences.getAddressLineOne());
        addressLineTwoTF.setText(preferences.getAddressLineTwo());
        gstNumberTF.setText(preferences.getGstInNumber());
        bankNameTF.setText(preferences.getBankName());
        branchNameTF.setText(preferences.getBankBranchName());
        bankAccountNoTF.setText(preferences.getBankAccountNumber());
        ifscCodeTF.setText(preferences.getIfscCode());
        phoneNumberTF.setText(preferences.getPhoneNumber());
        productMaxLimitPerBillTF.setText(preferences.getProductMaxLimitPerBill());
        billInvoiceCodePrefixTF.setText(preferences.getBillInvoiceCodePrefix());

        setDescription();
        setPerData();
        setHsnData();

        cssThemeStringJFXComboBox.getItems().clear();
        cssThemeStringJFXComboBox.getItems().addAll(ResourceConstants.CSS.CSS_NAME_LIST);
        cssThemeStringJFXComboBox.setValue(preferences.getCssThemeName());

        descriptionStringJFXComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            descriptionOld = oldValue;
            descriptionNew = newValue;
        });
        perDataStringJFXComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            perDataOld = oldValue;
            perDataNew = newValue;
        });
        hsnDataStringJFXComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            hsnOld = oldValue;
            hsnNew = newValue;
        });
    }

    private void setDescription() {
        descriptionStringJFXComboBox.getItems().clear();
        descriptionStringJFXComboBox.getEditor().clear();
        descriptionStringJFXComboBox.getItems().addAll(descriptionsDataSet);
    }

    private void setPerData() {
        perDataStringJFXComboBox.getItems().clear();
        perDataStringJFXComboBox.getEditor().clear();
        perDataStringJFXComboBox.getItems().addAll(perDataSet);
    }

    private void setHsnData() {
        hsnDataStringJFXComboBox.getItems().clear();
        hsnDataStringJFXComboBox.getEditor().clear();
        hsnDataStringJFXComboBox.getItems().addAll(hsnDataSet);
    }

    public void saveAll() {
        preferences.setStoreName(storeNameTF.getText());
        preferences.setAddressLineOne(addressLineOneTF.getText());
        preferences.setAddressLineTwo(addressLineTwoTF.getText());
        preferences.setGstInNumber(gstNumberTF.getText());
        preferences.setBankName(bankNameTF.getText());
        preferences.setBankBranchName(branchNameTF.getText());
        preferences.setBankAccountNumber(bankAccountNoTF.getText());
        preferences.setIfscCode(ifscCodeTF.getText());
        preferences.setPhoneNumber(phoneNumberTF.getText());
        preferences.setProductMaxLimitPerBill(productMaxLimitPerBillTF.getText());
        preferences.setDescriptions(descriptionsDataSet);
        preferences.setPerData(perDataSet);
        preferences.setCssThemeName(cssThemeStringJFXComboBox.getValue());
        preferences.setBillInvoiceCodePrefix(billInvoiceCodePrefixTF.getText());
        Preferences.setPreference(preferences);
    }

    @FXML
    public void handleDAdd() {
        if (descriptionStringJFXComboBox.getValue() != null && !descriptionStringJFXComboBox.getValue().isEmpty()) {
            descriptionsDataSet.add(descriptionStringJFXComboBox.getValue());
            preferences.setDescriptions(descriptionsDataSet);
            Preferences.setPreference(preferences);
            setDescription();
        }
    }

    @FXML
    public void handleDDelete() {
        descriptionsDataSet.remove(descriptionStringJFXComboBox.getValue());
        preferences.setDescriptions(descriptionsDataSet);
        Preferences.setPreference(preferences);
        setDescription();
    }

    @FXML
    public void handleDSubmit() {
        descriptionsDataSet.remove(descriptionOld);
        descriptionsDataSet.add(descriptionNew);
        preferences.setDescriptions(descriptionsDataSet);
        Preferences.setPreference(preferences);
        setDescription();
    }

    @FXML
    public void handlePAdd() {
        if (perDataStringJFXComboBox.getValue() != null && !perDataStringJFXComboBox.getValue().isEmpty()) {
            perDataSet.add(perDataStringJFXComboBox.getValue());
            preferences.setDescriptions(perDataSet);
            Preferences.setPreference(preferences);
            setPerData();
        }
    }

    @FXML
    public void handlePDelete() {
        perDataSet.remove(perDataStringJFXComboBox.getValue());
        preferences.setDescriptions(perDataSet);
        Preferences.setPreference(preferences);
        setPerData();
    }

    @FXML
    public void handlePSubmit() {
        perDataSet.remove(perDataOld);
        perDataSet.add(perDataNew);
        preferences.setDescriptions(perDataSet);
        Preferences.setPreference(preferences);
        setPerData();
    }

    @FXML
    public void handleHAdd() {
        if (hsnDataStringJFXComboBox.getValue() != null && !hsnDataStringJFXComboBox.getValue().isEmpty()) {
            System.out.println(hsnDataStringJFXComboBox.getValue());
            hsnDataSet.add(hsnDataStringJFXComboBox.getValue());
            System.out.println(hsnDataSet);
            preferences.setHsn(hsnDataSet);
            System.out.println(preferences);
            Preferences.setPreference(preferences);
            setHsnData();
        }
    }

    @FXML
    public void handleHDelete() {
        hsnDataSet.remove(hsnDataStringJFXComboBox.getValue());
        preferences.setDescriptions(hsnDataSet);
        Preferences.setPreference(preferences);
        setHsnData();
    }

    @FXML
    public void handleHSubmit() {
        hsnDataSet.remove(hsnOld);
        hsnDataSet.add(hsnNew);
        preferences.setDescriptions(hsnDataSet);
        Preferences.setPreference(preferences);
        setHsnData();
    }

}
