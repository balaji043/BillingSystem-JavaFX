package sample.CustomUI.EditBill;

import com.jfoenix.controls.JFXListView;
import sample.CustomUI.SingleProduct.SingleProduct;
import sample.Model.Bill;
import sample.Model.Product;

public class EditBill {

    public JFXListView<SingleProduct> listView;

    void setBill(Bill bill){
        for (Product p:bill.getProducts()){
            SingleProduct product = new SingleProduct();
            product.setSlNO(listView.getItems().size() + 1);
            product.setProduct(p);
            listView.getItems().add(product);
        }
    }

}
