package sample.UI.PurchaseBills;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import sample.Main;

public class PurchaseBills {
    public JFXComboBox companyNameCBOX;
    public JFXTextField searchBox;
    public StackPane main;
    public TableView tableView;
    public JFXDatePicker fromDate, toDate;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void handleSubmit() {
    }
}
