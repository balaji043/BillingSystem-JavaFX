package bam.billing.se.utils;

import static bam.billing.se.utils.Constants.*;

public enum Message {
    LOG_OUT("Confirm Logout", "Are you sure you want to Logout?"),
    CLOSE_WINDOW("Confirm exit?"
            , "Are you sure you want to exit?"),
    FILE_CHOOSE("Select a file", "", SnackBarColor.GREEN),


    LIMIT_EXCEEDED(INFO, "Can not add more than "
            + Preferences.getPreferences().getProductMaxLimitPerBill() + " items", SnackBarColor.GREEN),
    SELECT_DATE(INFO, "Select a date", SnackBarColor.RED),
    ADD_MIN_PRODUCT(INFO, "Fill at least One Product", SnackBarColor.RED),

    CUSTOMER_NOT_FOUND(INFO, " Customer Data Doesn't Exists.\n Choose a Valid Customer", SnackBarColor.RED),
    SELECT_ONE_ROW(INFO, "Select a row to Delete", SnackBarColor.RED),
    CUSTOMER_DELETE_SUCCESS(SUCCESS
            , "Selected Customer's data is deleted"
            , SnackBarColor.GREEN),
    CUSTOMER_DELETE_FAIL(FAILED
            , "Selected User's data is not deleted"
            , SnackBarColor.RED),
    CUSTOMER_ADD_SUCCESS(SUCCESS, "New Customer Added Successfully", SnackBarColor.GREEN),
    CUSTOMER_ADD_FAIL(FAILED, "New Customer Not Added", SnackBarColor.RED),
    CUSTOMER_UPDATE_SUCCESS(SUCCESS, "Customer Data Updated Successfully", SnackBarColor.GREEN),
    CUSTOMER_UPDATE_FAIL(FAILED, "Customer Data Not Updated Successfully", SnackBarColor.RED),

    ;


    public String title;
    public String message;
    public SnackBarColor color;

    Message(String title, String message) {
        this.title = title;
        this.message = message;
    }

    Message(String title, String message, SnackBarColor color) {
        this.title = title;
        this.message = message;
        this.color = color;
    }

}
