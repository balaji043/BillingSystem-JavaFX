package bam.billing.nongst.helpers;

import bam.billing.nongst.dto.CustomerNameResult;
import bam.billing.nongst.models.Customer;
import bam.billing.nongst.utils.AlertMaker;
import bam.billing.nongst.utils.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CustomerService extends DatabaseHelper {


    static boolean createCustomerTable() {
        String create = "CREATE TABLE IF NOT EXISTS CUSTOMER" +
                "( NAME TEXT NOT NULL UNIQUE,  PHONE TEXT NOT NULL" +
                ", GSTIN TEXT NOT NULL, STREET TEXT NOT NULL" +
                ", CITY TEXT NOT NULL,  STATE TEXT NOT NULL" +
                ", ZIP TEXT NOT NULL,   ID TEXT NOT NULL UNIQUE )";
        return createTable(create);
    }

    public static boolean insertNewCustomer(Customer customer) {
        String query = "INSERT OR IGNORE INTO CUSTOMER " +
                "( NAME , PHONE , GSTIN , STREET , CITY , STATE , ZIP" +
                ", ID) VALUES (?,?,?,?,?,?,?,?)";
        return getResultOfUpdateOrInsert(query, customer);
    }

    public static boolean updateCustomer(Customer customer) {
        String query = "UPDATE CUSTOMER SET  NAME = ? , PHONE = ? " +
                ", GSTIN = ? , STREET = ? , CITY = ? , STATE = ? , ZIP = ? " +
                " WHERE ID = ?";
        return getResultOfUpdateOrInsert(query, customer);
    }

    private static boolean getResultOfUpdateOrInsert(String query, Customer customer) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getPhone());
            preparedStatement.setString(3, customer.getGstIn());
            preparedStatement.setString(4, customer.getStreetAddress());
            preparedStatement.setString(5, customer.getCity());
            preparedStatement.setString(6, customer.getState());
            preparedStatement.setString(7, customer.getZipCode());
            preparedStatement.setString(8, customer.getId());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return okay;
    }

    public static boolean deleteCustomer(Customer customer) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            String insert = "DELETE FROM CUSTOMER WHERE ID = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, customer.getId());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
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

    public static Customer getCustomerInfo(String customerId) {

        Customer customer = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String query = "SELECT * FROM CUSTOMER WHERE NAME = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, customerId);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                return null;

            customer = getCustomerFromResultSet(resultSet);
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            DatabaseHelper.closePAndRMethod(preparedStatement, resultSet);
        }
        return customer;
    }

    public static ObservableList<Customer> getCustomerList(int offset) {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;

        try {
            String query = "SELECT * FROM CUSTOMER LIMIT 10 OFFSET " + offset + ";";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            customers = getCustomerListFromPreparedStatement(preparedStatement);
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return customers;
    }

    public static ObservableList<Customer> getCustomerListBySearchText(String searchText, int offset) {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;

        try {
            String query = "SELECT * FROM CUSTOMER WHERE NAME LIKE ? LIMIT 10 OFFSET " + offset + ";";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, searchText);
            customers = getCustomerListFromPreparedStatement(preparedStatement);
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return customers;
    }

    private static ObservableList<Customer> getCustomerListFromPreparedStatement(PreparedStatement preparedStatement) {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                customers.add(getCustomerFromResultSet(resultSet));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            DatabaseHelper.closePAndRMethod(preparedStatement, resultSet);
        }
        return customers;
    }


    private static Customer getCustomerFromResultSet(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();
        customer.setName(resultSet.getString(Column.NAME));
        customer.setPhone(resultSet.getString(Column.PHONE));
        customer.setGstIn(resultSet.getString(Column.GSTIN));
        customer.setStreetAddress(resultSet.getString(Column.STREET));
        customer.setCity(resultSet.getString(Column.CITY));
        customer.setState(resultSet.getString(Column.STATE));
        customer.setZipCode(resultSet.getString(Column.ZIP));
        customer.setId(resultSet.getString(Column.ID));
        return customer;
    }

    /**
     * This functions returns the customer list result by segregating them by GSTIN column
     **/
    public static CustomerNameResult getCustomerNameResult() {
        CustomerNameResult customerNameResult = new CustomerNameResult();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT GSTIN, NAME FROM CUSTOMER WHERE TRUE";
        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(Column.NAME);
                customerNameResult.allCustomerNames.add(name);

                if (resultSet.getString(Column.GSTIN).equals(Constants.FOR_OWN_USE))
                    customerNameResult.nonGstCustomerNames.add(name);
                else
                    customerNameResult.gstCustomerNames.add(name);
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            DatabaseHelper.closePAndRMethod(preparedStatement, resultSet);
        }
        return customerNameResult;
    }

    static class Column {
        public static String NAME = "NAME";
        public static String PHONE = "PHONE";
        public static String GSTIN = "GSTIN";
        public static String STREET = "STREET";
        public static String CITY = "CITY";
        public static String STATE = "STATE";
        public static String ZIP = "ZIP";
        public static String ID = "ID";
    }

}
