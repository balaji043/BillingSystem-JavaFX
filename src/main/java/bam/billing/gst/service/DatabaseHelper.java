package bam.billing.gst.service;


import bam.billing.gst.alert.AlertMaker;
import bam.billing.gst.model.User;
import bam.billing.gst.utils.StringUtil;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        if (createTable(StringUtil.CREATE_USER_TABLE)) {
            UserService.insertNewUser(new User(StringUtil.ADMIN
                    , StringUtil.ADMIN
                    , StringUtil.ADMIN
                    , "123"
                    , StringUtil.ADMIN));
        }
        if (createTable(StringUtil.CREATE_CUSTOMER_TABLE))
            logger.log(Level.INFO, "Customer Table Created or Already Exists");

        if (createTable(StringUtil.CREATE_BILL_TABLE))
            logger.log(Level.INFO, "Bills Table Created or Already Exists");

        if (createTable(StringUtil.CREATE_IBILL_TABLE))
            logger.log(Level.INFO, "Bills Table Created or Already Exists");

        if (createTable(StringUtil.CREATE_PURCHASE_TABLE))
            logger.log(Level.INFO, "PurchaseBills Table Created or Already Exists");

        if (!isTableColumnExists(StringUtil.PURCHASE_TABLE_NAME, "ExtraAmount")) {
            addTableColumn(StringUtil.PURCHASE_TABLE_NAME, "ExtraAmount TEXT");
        }

    }


    private static boolean isTableColumnExists(String tableName, String columnName) {
        try {
            DatabaseMetaData md = DatabaseHandler.getInstance().getConnection().getMetaData();
            ResultSet rs = md.getColumns(null, null, tableName, columnName);
            if (rs.next()) {
                return true;
            }
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return false;
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

    static boolean deleteTable(String tableName) {
        PreparedStatement preparedStatement = null;

        try {
            String q = "DROP TABLE IF EXISTS " + tableName;
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(q);
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

    private static boolean addTableColumn(String tableName, String columnName) {
        String updateQuery = String.format("ALTER TABLE %s ADD %s", tableName, columnName);
        boolean okay = true;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(updateQuery);
            okay = preparedStatement.execute();

        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
        return okay;
    }
}