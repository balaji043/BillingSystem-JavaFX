package bam.billing.gst.controller;

import bam.billing.gst.model.Bill;
import javafx.scene.layout.VBox;

public interface GenericBillController {

    void setBill(Bill bill);

    void setCopyText(String s);

    VBox getRoot();

}
