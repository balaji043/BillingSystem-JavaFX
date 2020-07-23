package bam.billing.se.utils;


import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final String PURCHASE_BILLS_FOR = "Purchase bills for";

    public static final String PURCHASE_BILLS_FOR_STD_ENT = "Purchase bills for StdEnt";
    public static final String PURCHASE_BILLS_FOR_STD_EQM = "Purchase bills for StdEnt";
    public static final String TITLE = Preferences.getPreferences().getStoreName();
    public static final String CHECK_FOR_ERROR = "Check the Following";
    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";
    public static final String INFO = "INFO";

    public static final String BILL_NOT_SAVED = " Bill item is not saved !";
    public static final String BILL_SAVE = " Bill is saved successfully!";


    private Constants() {

    }

    public enum SnackBarColor {
        RED("#ffcccc"),
        GREEN("#ccffcc");
        public String style;

        SnackBarColor(String style) {
            this.style = "-fx-background-color:" + style + ";";
        }
    }

    public static class UserRole {
        public static final List<String> userRoleList = new ArrayList<>();
        public static final String ADMIN = "admin";
        public static final String EMPLOYEE = "employee";

        static {
            userRoleList.add(ADMIN);
            userRoleList.add(EMPLOYEE);
        }
    }

    public static class MenuNames {
        public static String MODIFY_BILL = "Edit Bill";
    }
}
