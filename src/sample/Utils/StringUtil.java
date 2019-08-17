package sample.Utils;

public class StringUtil {
    public static final String SELECT = " SELECT * FROM ";
    public static final String WHERE = " WHERE TRUE ";
    public static final String CANCEL = "Cancel";
    public static final String FX_FONT_SIZE_8 = "-fx-font-size:8px;";
    public static final String GREEN = "green";
    public static final String SUCCESS = " Success. ";
    public static final String FAILED = " Failed. ";
    public static final String INFO = " Information. ";
    public static final String ADMIN = "admin";
    public static final String RED = "red";
    public static final String CANCELLED = "Cancelled";
    public static final String ERROR = "Error Happened";
    public static final String CREATE_PURCHASE_TABLE = "CREATE TABLE IF NOT EXISTS PURCHASEBILLS ( DATE TEXT NOT NULL, "
            + " CompanyName TEXT NOT NULL, INVOICE TEXT NOT NULL,"
            + " AmountBeforeTax TEXT NOT NULL, TwelvePerAmt TEXT NOT NULL, "
            + " EighteenPerAmt TEXT NOT NULL, TwentyEightPerAmt TEXT NOT NULL, "
            + " AmountAfterTax TEXT NOT NULL, HasGoneToAuditor TEXT NOT NULL,"
            + "OTHERS TEXT ,DateCleared TEXT,Status NOT NULL,ExtraAmount TEXT, UNIQUE(CompanyName,INVOICE))";
    public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS Employee (Name TEXT NOT NULL"
            + ",id TEXT NOT NULL UNIQUE,password TEXT NOT NULL"
            + ",access TEXT NOT NULL,UserName TEXT NOT NULL)";
    public static final String CREATE_CUSTOMER_TABLE = "CREATE TABLE IF NOT EXISTS CUSTOMER"
            + "( NAME TEXT NOT NULL UNIQUE,  PHONE TEXT NOT NULL"
            + ", GSTIN TEXT NOT NULL, STREET TEXT NOT NULL"
            + ", CITY TEXT NOT NULL,  STATE TEXT NOT NULL"
            + ", ZIP TEXT NOT NULL,   ID TEXT NOT NULL UNIQUE )";
    public static final String CREATE_IBILL_TABLE = "CREATE TABLE IF NOT EXISTS IBILLS ( " +
            "BillID TEXT NOT NULL UNIQUE,INVOICE TEXT NOT NULL UNIQUE," +
            "DATE TEXT NOT NULL, CustomerName TEXT NOT NULL, " +
            "CustomerID TEXT NOT NULL, ADDRESS TEXT NOT NULL," +
            "PHONE TEXT NOT NULL, GstNO TEXT NOT NULL,USERNAME TEXT NOT NULL)";
    public static final String CREATE_BILL_TABLE = "CREATE TABLE IF NOT EXISTS BILLS ( " +
            "BillID TEXT NOT NULL UNIQUE,INVOICE TEXT NOT NULL UNIQUE," +
            "DATE TEXT NOT NULL, CustomerName TEXT NOT NULL, " +
            "CustomerID TEXT NOT NULL, ADDRESS TEXT NOT NULL," +
            "PHONE TEXT NOT NULL, GstNO TEXT NOT NULL,USERNAME TEXT NOT NULL)";
    public static final String USER_TABLE_NAME = "Employee";
    public static final String PURCHASE_TABLE_NAME = "PURCHASEBILLS";

    private static final String FOR_OWN_USE = "For Own Use";

    private StringUtil() {

    }

    public static String getForOwnUse() {
        return FOR_OWN_USE;
    }
}
