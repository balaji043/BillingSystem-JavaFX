package sample.custom;

import javafx.scene.layout.VBox;
import sample.model.Bill;

public interface GenericBillController {

    void setBill(Bill bill);

    void setCopyText(String s);

    VBox getRoot();

}
