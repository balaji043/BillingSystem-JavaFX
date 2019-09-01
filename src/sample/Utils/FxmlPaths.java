package sample.Utils;

import sample.Main;

import java.net.URL;

public class FxmlPaths {
    public static final URL SINGLE_PURCHASE_BILL = getResourceUrl("custom/single/purchasebill/SinglePurchaseBill.fxml");
    public static final URL I_GST_BILL = getResourceUrl("custom/ibill/IBill.fxml");
    public static final URL BILL = getResourceUrl("custom/bill/Bill.fxml");
    public static final URL SETTINGS = getResourceUrl("custom/settings/Settings.fxml");
    public static final URL SINGLE_PRODUCT = getResourceUrl("custom/single/product/singleProductLayout.fxml");
    public static final URL NEW_PURCHASE_BILL = getResourceUrl("UI/NewPurchaseBill/NewPurchaseBill.fxml");
    public static final URL ROOT = getResourceUrl("UI/Root/Root.fxml");
    public static final URL LOGIN = getResourceUrl("UI/Login/Login.fxml");
    public static final URL BILLING_LAYOUT = getResourceUrl("UI/Billing/Billing.fxml");
    public static final URL VIEW_BILL = getResourceUrl("UI/ViewBills/ViewBills.fxml");
    public static final URL CUSTOMER_PANEL = getResourceUrl("UI/CustomerPanel/CustomerPanel.fxml");
    public static final URL USER_PANEL = getResourceUrl("UI/UserPanel/UserPanel.fxml");
    public static final URL PURCHASE_BILL = getResourceUrl("UI/PurchaseBills/PurchaseBills.fxml");

    private FxmlPaths() {

    }

    private static URL getResourceUrl(String path) {
        return Main.class.getResource(path);
    }

}
