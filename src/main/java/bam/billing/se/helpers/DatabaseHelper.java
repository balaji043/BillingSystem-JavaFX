package bam.billing.se.helpers;

import bam.billing.se.models.User;
import bam.billing.se.utils.AlertMaker;
import bam.billing.se.utils.Constants.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {

    public static void create() {
        if (DatabaseHelper_User.createUserTable()) {
            DatabaseHelper_User.insertNewUser(new User(UserRole.ADMIN
                    , UserRole.ADMIN
                    , UserRole.ADMIN
                    , "123"
                    , UserRole.ADMIN));
        }
        if (CustomerService.createCustomerTable()) {
            System.out.println("Customer Table Created or Already Exists");
        }
        if (BillService.createBillTable()) {
            System.out.println("Bills Table Created or Already Exists");
        }
        if (PurchaseBillService.createPurchaseBillTable()) {
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

    static boolean deleteTable(String tableName) {
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
