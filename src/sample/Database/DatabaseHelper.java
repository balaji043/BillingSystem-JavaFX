package sample.Database;

import com.sun.istack.internal.NotNull;
import sample.Alert.AlertMaker;
import sample.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {

    public static void create() {
        if (DatabaseHelper_User.createUserTable()) {
            DatabaseHelper_User.insertNewUser(new User("admin"
                    , "admin"
                    , "admin"
                    , "123"
                    , "admin"));
        }
        if (DatabaseHelper_Customer.createCustomerTable()) {
            System.out.println("Customer Table Created or Already Exists");
        }
        if (DatabaseHelper_Bill.createBillTable()) {
            System.out.println("Bills Table Created or Already Exists");
        }
        if (DatabaseHelper_PurchaseBill.createPurchaseBillTable()) {
            System.out.println("PurchaseBills Table Created or Already Exists");
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
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    static void closePAndRMethod(PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            assert preparedStatement != null;
            preparedStatement.close();
            assert resultSet != null;
            resultSet.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    static boolean deleteTable(@NotNull String tableName) {
        PreparedStatement preparedStatement = null;

        try {
            String q = "DROP TABLE IF EXISTS " + tableName;
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(q);
            boolean okay;
            try {
                okay = preparedStatement.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
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
}
