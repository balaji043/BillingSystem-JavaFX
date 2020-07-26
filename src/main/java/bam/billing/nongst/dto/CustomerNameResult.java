package bam.billing.nongst.dto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomerNameResult {

    public ObservableList<String> gstCustomerNames;
    public ObservableList<String> nonGstCustomerNames;
    public ObservableList<String> allCustomerNames;

    public CustomerNameResult() {
        gstCustomerNames = FXCollections.observableArrayList();
        nonGstCustomerNames = FXCollections.observableArrayList();
        allCustomerNames = FXCollections.observableArrayList();
    }

}
