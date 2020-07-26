package bam.billing.gst.service;

import bam.billing.gst.model.Customer;
import bam.billing.gst.utils.StringUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerService {

    private static final Logger l = Logger.getLogger(CustomerService.class.getName());

    private CustomerService() {

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
            l.log(Level.SEVERE, e.getMessage());
        } finally {
            assert preparedStatement != null;
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                l.log(Level.SEVERE, e.getMessage());
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
            l.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                l.log(Level.SEVERE, e1.getMessage());
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
            if (!resultSet.next()) return null;
            customer = new Customer(
                    "" + resultSet.getString("NAME")
                    , "" + resultSet.getString("PHONE")
                    , "" + resultSet.getString("GSTIN")
                    , "" + resultSet.getString("STREET")
                    , "" + resultSet.getString("CITY")
                    , "" + resultSet.getString("STATE")
                    , "" + resultSet.getString("ZIP")
                    , "" + customerId);
        } catch (SQLException e) {
            l.log(Level.SEVERE, e.getMessage());
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            DatabaseHelper.closePAndRMethod(preparedStatement, resultSet);
        }
        return customer;
    }

    public static ObservableList<Customer> getCustomerList() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;

        try {
            String query = "SELECT * FROM CUSTOMER WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            customers = getCustomerList(preparedStatement);
        } catch (Exception e) {
            l.log(Level.SEVERE, e.getMessage());
        } finally {
            assert preparedStatement != null;
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                l.log(Level.SEVERE, e.getMessage());
            }
        }
        return customers;
    }

    public static ObservableList<Customer> getCustomerList(String searchText) {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;

        try {
            String query = "SELECT * FROM CUSTOMER WHERE NAME LIKE ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, searchText);
            customers = getCustomerList(preparedStatement);
        } catch (Exception e) {
            l.log(Level.SEVERE, e.getMessage());
        } finally {
            assert preparedStatement != null;
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                l.log(Level.SEVERE, e.getMessage());

            }
        }
        return customers;
    }

    private static ObservableList<Customer> getCustomerList(PreparedStatement preparedStatement) {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        ResultSet resultSet = null;

        try {
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                customers.add(new Customer("" + resultSet.getString(1)
                        , "" + resultSet.getString(2)
                        , "" + resultSet.getString(3)
                        , "" + resultSet.getString(4)
                        , "" + resultSet.getString(5)
                        , "" + resultSet.getString(6)
                        , "" + resultSet.getString(7)
                        , "" + resultSet.getString(8)));
            }
        } catch (Exception e) {
            l.log(Level.SEVERE, e.getMessage());
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            DatabaseHelper.closePAndRMethod(preparedStatement, resultSet);
        }
        return customers;
    }

    /**
     * This functions returns the customer list
     *
     * @param no is either 0,1 or any to indicate whether to return gst customers or non-gst Customers or both;
     *           if  no == 0 then non-gst customer list will be returned;
     *           if  no == 0 then gst customer list will be returned;
     *           if  no == 0 then both gst and non-gst customer list will be returned;
     **/

    public static ObservableList<String> getCustomerNameList(int no) {
        ObservableList<String> customers = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String query = "SELECT * FROM CUSTOMER WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            customers = getCList(resultSet, no);
        } catch (Exception e) {
            l.log(Level.SEVERE, e.getMessage());
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            DatabaseHelper.closePAndRMethod(preparedStatement, resultSet);
        }
        return customers;
    }

    private static ObservableList<String> getCList(ResultSet resultSet, int no) throws SQLException {
        ObservableList<String> customers = FXCollections.observableArrayList();

        switch (no) {
            case 0: {
                while (resultSet.next()) {
                    if (!resultSet.getString(3).equals(StringUtil.getForOwnUse()))
                        customers.add(resultSet.getString("NAME"));
                }
                break;
            }
            case 1: {
                while (resultSet.next()) {
                    if (resultSet.getString(3).equals(StringUtil.getForOwnUse()))
                        customers.add(resultSet.getString("NAME"));
                }
                break;
            }
            default: {
                while (resultSet.next()) {
                    customers.add(resultSet.getString("NAME"));
                }
                break;
            }
        }

        return customers;
    }
}
