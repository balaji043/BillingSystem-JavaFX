package sample.Utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class BillingSystemUtils {

    private static final String[] units = {"", "One", "Two", "Three", "Four",
            "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
            "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
            "Eighteen", "Nineteen"};
    private static final String[] tens = {
            "",          // 0
            "",          // 1
            "Twenty",    // 2
            "Thirty",    // 3
            "Forty",     // 4
            "Fifty",     // 5
            "Sixty",     // 6
            "Seventy",   // 7
            "Eighty",    // 8
            "Ninety"     // 9
    };

    public static String convert(final int n) {
        if (n < 0) {
            return "Minus " + convert(-n);
        }
        if (n < 20) {
            return units[n];
        }
        if (n < 100) {
            return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
        }
        if (n < 1000) {
            return units[n / 100] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
        }
        if (n < 100000) {
            return convert(n / 1000) + " Thousand" + ((n % 10000 != 0) ? " " : "") + convert(n % 1000);
        }
        if (n < 10000000) {
            return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " : "") + convert(n % 100000);
        }
        return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " : "") + convert(n % 10000000);
    }

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static String formatDateTimeString(Long time) {
        return DATE_TIME_FORMAT.format(new Date(time));
    }

    public static String getTableName(String value) {
        switch (value) {
            case "GST": {
                return "BILLS";
            }
            case "Non-GST": {
                return "NONGST";
            }
            case "I-GST": {
                return "IBILLS";
            }
            default: {
                return "BILLS";
            }
        }
    }

    public static boolean getN(String value) {
        switch (value) {
            case "GST": {
                return false;
            }
            case "I-GST": {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
