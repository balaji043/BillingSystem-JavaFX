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


    private static final String FOR_OWN_USE = "For Own Use";

    private StringUtil() {

    }

    public static String getForOwnUse() {
        return FOR_OWN_USE;
    }
}
