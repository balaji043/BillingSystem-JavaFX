package sample.custom.settings;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import sample.Utils.Preferences;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class Settings implements Initializable {
    private Preferences preferences;

    @FXML
    VBox main;
    @FXML
    JFXTextField storeName, address1, address2, gstNO, bankName, branchName, accNo, iFSCode, phone, limit;
    @FXML
    JFXComboBox<String> description, perData, the, hsnData;

    private String[] themes = {"red", "blue", "green"};

    private Set<String> d, p, h;
    private String dOLD, dNEW, pOLD, pNEW, hOLD, hNEW;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preferences = Preferences.getPreferences();
        d = preferences.getDescriptions();
        p = preferences.getPerData();
        h = preferences.getHsn();

        preferences = Preferences.getPreferences();
        storeName.setText(preferences.getName());
        address1.setText(preferences.getAddress1());
        address2.setText(preferences.getAddress2());

        gstNO.setText(preferences.getGstin());
        bankName.setText(preferences.getBank());
        branchName.setText(preferences.getBranch());
        accNo.setText(preferences.getAcc());
        iFSCode.setText(preferences.getIfsc());
        phone.setText(preferences.getPhone());
        limit.setText(preferences.getLimit());

        setDescription();
        setPerData();
        setHsnData();

        the.getItems().clear();
        the.getItems().addAll(themes);
        the.setValue(preferences.getTheme());

        description.valueProperty().addListener((observable, oldValue, newValue) -> {
            dOLD = oldValue;
            dNEW = newValue;
        });
        perData.valueProperty().addListener((observable, oldValue, newValue) -> {
            pOLD = oldValue;
            pNEW = newValue;
        });
        hsnData.valueProperty().addListener((observable, oldValue, newValue) -> {
            hOLD = oldValue;
            hNEW = newValue;
        });
    }

    private void setDescription() {
        description.getItems().clear();
        description.getEditor().clear();
        description.getItems().addAll(d);
    }

    private void setPerData() {
        perData.getItems().clear();
        perData.getEditor().clear();
        perData.getItems().addAll(p);
    }

    private void setHsnData() {
        hsnData.getItems().clear();
        hsnData.getEditor().clear();
        hsnData.getItems().addAll(h);
    }

    public void saveAll() {
        preferences.setName(storeName.getText());
        preferences.setAddress1(address1.getText());
        preferences.setAddress2(address2.getText());
        preferences.setGstin(gstNO.getText());
        preferences.setBank(bankName.getText());
        preferences.setBranch(branchName.getText());
        preferences.setAcc(accNo.getText());
        preferences.setIfsc(iFSCode.getText());
        preferences.setPhone(phone.getText());
        preferences.setLimit(limit.getText());
        preferences.setDescriptions(d);
        preferences.setPerData(p);
        preferences.setTheme(the.getValue());
        Preferences.setPreference(preferences);
    }

    @FXML
    public void handleDAdd() {
        if (description.getValue() != null && !description.getValue().isEmpty()) {
            d.add(description.getValue());
            preferences.setDescriptions(d);
            Preferences.setPreference(preferences);
            setDescription();
        }
    }

    @FXML
    public void handleDDelete() {
        d.remove(description.getValue());
        preferences.setDescriptions(d);
        Preferences.setPreference(preferences);
        setDescription();
    }

    @FXML
    public void handleDSubmit() {
        d.remove(dOLD);
        d.add(dNEW);
        preferences.setDescriptions(d);
        Preferences.setPreference(preferences);
        setDescription();
    }

    @FXML
    public void handlePAdd() {
        if (perData.getValue() != null && !perData.getValue().isEmpty()) {
            p.add(perData.getValue());
            preferences.setDescriptions(p);
            Preferences.setPreference(preferences);
            setPerData();
        }
    }

    @FXML
    public void handlePDelete() {
        p.remove(perData.getValue());
        preferences.setDescriptions(p);
        Preferences.setPreference(preferences);
        setPerData();
    }

    @FXML
    public void handlePSubmit() {
        p.remove(pOLD);
        p.add(pNEW);
        preferences.setDescriptions(p);
        Preferences.setPreference(preferences);
        setPerData();
    }

    @FXML
    public void handleHAdd() {
        if (hsnData.getValue() != null && !hsnData.getValue().isEmpty()) {
            h.add(hsnData.getValue());
            preferences.setHsn(h);
            Preferences.setPreference(preferences);
            setHsnData();
        }
    }

    @FXML
    public void handleHDelete() {
        h.remove(hsnData.getValue());
        preferences.setDescriptions(h);
        Preferences.setPreference(preferences);
        setHsnData();
    }

    @FXML
    public void handleHSubmit() {
        h.remove(hOLD);
        h.add(hNEW);
        preferences.setDescriptions(h);
        Preferences.setPreference(preferences);
        setHsnData();
    }

}
