package bam.billing.nongst.utils;

import bam.billing.nongst.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceConstants {

    public static class Views {
        public static String BILLING = "/views/bill-registration.fxml";
        public static String CUSTOMER_PANEL = "/views/customer-panel.fxml";
        public static String LOGIN_PAGE = "/views/login-page.fxml";
        public static String PURCHASE_BILL_REGISTRATION = "/views/purchase-bill-registration.fxml";
        public static String PURCHASE_BILLS_PANEL = "/views/purchase-bills-panel.fxml";
        public static String ROOT_PAGE = "/views/root-page.fxml";
        public static String SETTINGS_PAGE = "/views/settings-page.fxml";
        public static String SINGLE_PRODUCT = "/views/single-product.fxml";
        public static String SINGLE_PURCHASE_BILL = "/views/single-purchase-bill.fxml";
        public static String USER_PANEL = "/views/user-panel.fxml";
        public static String BILLS_PANEL = "/views/bill-panel.fxml";
        public static String NON_GST_BILL = "/views/non-gst-bill.fxml";
    }

    public static class Icons {
        public static String ADD = "/icons/add.png";
        public static String BACK = "/icons/back.png";
        public static String CIRCLED = "/icons/circled.png";
        public static String DELETE = "/icons/delete.png";
        public static String DOWNLOAD = "/icons/download.png";
        public static String ENTER = "/icons/enter.png";
        public static String EXIT = "/icons/exit.png";
        public static String GEAR = "/icons/gear.png";
        public static String HEAD = "/icons/head.png";
        public static String IMPORT = "/icons/import.png";
        public static String INPUT = "/icons/input.png";
        public static String KE = "/icons/ke.png";
        public static String KE_LOGO = "/icons/ke-logo.ico";
        public static String LOCK = "/icons/lock.png";
        public static String LOGIN = "/icons/login.png";
        public static String MINUS = "/icons/minus.png";
        public static String OUTPUT = "/icons/output.png";
        public static String PASSWORD = "/icons/password.png";
        public static String REFRESH = "/icons/refresh.png";
        public static String REFRESH_WHITE = "/icons/refresh-white.png";
        public static String SAVE = "/icons/save.png";
        public static String SE = "/icons/se.png";
        public static String SE_LOGO = "/icons/se-logo.ico";
        public static String SEARCH = "/icons/search.png";
        public static String SETTING = "/icons/setting.png";
        public static String SIGN_OUT = "/icons/sign-out.png";
        public static String STD_ENT = "/icons/std-ent.png";
        public static String USER = "/icons/user.png";
    }

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
}
