package bam.billing.gst.utils;

import bam.billing.gst.Main;
import javafx.fxml.FXMLLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceConstants {
    public static class CSS {
        public static List<String> CSS_NAME_LIST = new ArrayList<>();

        private static Map<String, String> CSS_MAP = new HashMap<>();
        private static String BLUE = "Blue";
        private static String RED = "Red";
        private static String GREEN = "Green";

        private static String BLUE_THEME = "/css/blue-theme.css";
        private static String RED_THEME = "/css/red-theme.css";
        private static String GREEN_THEME = "/css/green-theme.css";

        static {
            CSS_MAP.put(CSS.RED, CSS.RED_THEME);
            CSS_MAP.put(CSS.GREEN, CSS.GREEN_THEME);
            CSS_MAP.put(CSS.BLUE, CSS.BLUE_THEME);
            CSS_NAME_LIST.add(CSS.BLUE);
            CSS_NAME_LIST.add(CSS.RED);
            CSS_NAME_LIST.add(CSS.GREEN);
        }

        public static String getCSS() {
            String themeName = Preferences.getPreferences().getCssThemeName();
            String themePath = CSS_MAP.get(themeName);
            if (themePath == null) {
                themePath = BLUE_THEME;
            }
            return Main.class.getResource(themePath).toExternalForm();
        }

    }

    public static class Views {
        public static final String SINGLE_PURCHASE_BILL = "/views/single-purchase-bill-view.fxml";
        public static final String I_GST_BILL = "/views/igst-bill-vie.fxml";
        public static final String BILL = "/views/gst-bill-view.fxml";
        public static final String SETTINGS = "/views/settings-view.fxml";
        public static final String SINGLE_PRODUCT = "/views/single-product-view.fxml";
        public static final String NEW_PURCHASE_BILL = "/views/purchase-bill-registration.fxml";
        public static final String ROOT = "/views/root-view.fxml";
        public static final String LOGIN = "/views/login-panel.fxml";
        public static final String BILLING_LAYOUT = "/views/bill-registration.fxml";
        public static final String VIEW_BILL = "/views/bill-panel.fxml";
        public static final String CUSTOMER_PANEL = "/views/customer-panel.fxml";
        public static final String USER_PANEL = "/views/user-panel.fxml";
        public static final String PURCHASE_BILL = "/views/purchase-bill-panel.fxml";

        public static FXMLLoader getFXMLLoaderWithUrl(String path) {
            return new FXMLLoader(Main.class.getResource(path));
        }

    }
}