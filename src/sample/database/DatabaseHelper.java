package sample.database;


import org.jetbrains.annotations.NotNull;
import sample.alert.AlertMaker;
import sample.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sample.Utils.StringUtil.ADMIN;

/**
 * This class contains the functions for
 * creating and deleting the tables used in app
 */
public class DatabaseHelper {

    private static final Logger logger = Logger.getLogger(DatabaseHelper.class.getName());


    /**
     * This Function Creates the Tables.
     * This is called every time the app is started.
     */


    private DatabaseHelper() {

    }

    public static void create() {
        if (createUserTable()) {
            DatabaseHelperUser.insertNewUser(new User(ADMIN
                    , ADMIN
                    , ADMIN
                    , "123"
                    , ADMIN));
        }
        if (createCustomerTable()) {
            logger.log(Level.INFO, "Customer Table Created or Already Exists");
        }
        if (createBillTables()) {
            logger.log(Level.INFO, "Bills Table Created or Already Exists");

        }
        if (createPurchaseBillTable()) {
            logger.log(Level.INFO, "PurchaseBills Table Created or Already Exists");
        }
    }

    static boolean createTable(String createQuery) {
        Connection connection = DatabaseHandler.getInstance().getConnection();
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            preparedStatement = connection.prepareStatement(createQuery);
            okay = !preparedStatement.execute();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
        return okay;
    }

    static boolean deleteTable(@NotNull String tableName) {
        PreparedStatement preparedStatement = null;

        try {
            String q = "DROP TABLE IF EXISTS ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(q);
            preparedStatement.setString(1, tableName);
            boolean okay = false;
            try {
                okay = preparedStatement.executeUpdate() > 0;
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
                if (e.getMessage().equals("[SQLITE_LOCKED]  A table in the database is locked (database table is locked)"))
                    return true;
            }
            return okay;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                logger.log(Level.SEVERE, e1.getMessage());
            }
        }
        return false;
    }

    static void closePAndRMethod(PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            assert preparedStatement != null;
            preparedStatement.close();
            assert resultSet != null;
            resultSet.close();
        } catch (SQLException e1) {
            logger.log(Level.SEVERE, e1.getMessage());
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
                + " CompanyName TEXT NOT NULL, INVOICE TEXT NOT NULL,"
                + " AmountBeforeTax TEXT NOT NULL, TwelvePerAmt TEXT NOT NULL, "
                + " EighteenPerAmt TEXT NOT NULL, TwentyEightPerAmt TEXT NOT NULL, "
                + " AmountAfterTax TEXT NOT NULL, HasGoneToAuditor TEXT NOT NULL,"
                + "OTHERS TEXT ,DateCleared TEXT,Status NOT NULL,ExtraAmount TEXT, UNIQUE(CompanyName,INVOICE))";
        return createTable(createQuery);
    }
}
