package sample.Database;

import com.sun.istack.internal.NotNull;
import sample.Alert.AlertMaker;
import sample.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class contains the functions for
 * creating and deleting the tables used in app
 */
public class DatabaseHelper {

    /**
     * This Function Creates the Tables.
     * This is called every time the app is started.
     */

    public static void create() {
        if (createUserTable()) {
            DatabaseHelper_User.insertNewUser(new User("admin"
                    , "admin"
                    , "admin"
                    , "123"
                    , "admin"));
        }
        if (createCustomerTable()) {
            System.out.println("Customer Table Created or Already Exists");
        }
        if (createBillTables()) {
            System.out.println("Bills Table Created or Already Exists");
        }
        if (createPurchaseBillTable()) {
            System.out.println("PurchaseBills Table Created or Already Exists");
        }
    }

    public static boolean createTable(String createQuery) {
        Connection connection = DatabaseHandler.getInstance().getConnection();
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            preparedStatement = connection.prepareStatement(createQuery);
            okay = !preparedStatement.execute();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
        }
        return okay;
    }

    public static boolean deleteTable(@NotNull String tableName) {
        PreparedStatement preparedStatement = null;

        try {
            String q = "DROP TABLE IF EXISTS " + tableName;
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(q);
            boolean okay = false;
            try {
                okay = preparedStatement.executeUpdate() > 0;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                if (e.getMessage().equals("[SQLITE_LOCKED]  A table in the database is locked (database table is locked)"))
                    return true;
            }
            return okay;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    public static void closePAndRMethod(PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            assert preparedStatement != null;
            preparedStatement.close();
            assert resultSet != null;
            resultSet.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private static boolean createBillTable(String tableName) {
        String create = "CREATE TABLE IF NOT EXISTS " + tableName + " ( " +
                "BillID TEXT NOT NULL UNIQUE,INVOICE TEXT NOT NULL UNIQUE," +
                "DATE TEXT NOT NULL, CustomerName TEXT NOT NULL, " +
                "CustomerID TEXT NOT NULL, ADDRESS TEXT NOT NULL," +
                "PHONE TEXT NOT NULL, GstNO TEXT NOT NULL,USERNAME TEXT NOT NULL)";
        return createTable(create);
    }

    private static boolean createBillTables() {
        boolean b = createBillTable("BILLS");
        b = b && createBillTable("IBILLS");
        return b;
    }

    private static boolean createUserTable() {
        String create = "CREATE TABLE IF NOT EXISTS Employee (Name TEXT NOT NULL" +
                ",id TEXT NOT NULL UNIQUE,password TEXT NOT NULL" +
                ",access TEXT NOT NULL,UserName TEXT NOT NULL)";
        return createTable(create);
    }

    private static boolean createCustomerTable() {
        String create = "CREATE TABLE IF NOT EXISTS CUSTOMER" +
                "( NAME TEXT NOT NULL UNIQUE,  PHONE TEXT NOT NULL" +
                ", GSTIN TEXT NOT NULL, STREET TEXT NOT NULL" +
                ", CITY TEXT NOT NULL,  STATE TEXT NOT NULL" +
                ", ZIP TEXT NOT NULL,   ID TEXT NOT NULL UNIQUE )";
        return createTable(create);
    }

    private static boolean createPurchaseBillTable() {
        String createQuery = "CREATE TABLE IF NOT EXISTS PURCHASEBILLS ( DATE TEXT NOT NULL, "
                + " CompanyName TEXT NOT NULL, INVOICE TEXT NOT NULL UNIQUE,"
                + " AmountBeforeTax TEXT NOT NULL, TwelvePerAmt TEXT NOT NULL, "
                + " EighteenPerAmt TEXT NOT NULL, TwentyEightPerAmt TEXT NOT NULL, "
                + " AmountAfterTax TEXT NOT NULL)";
        return createTable(createQuery);
    }
}