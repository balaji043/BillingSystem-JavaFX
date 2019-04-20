package sample.Database;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Alert.AlertMaker;
import sample.Model.Bill;
import sample.Model.Customer;
import sample.Model.Product;
import sample.Model.User;
import sample.Utils.BillingSystemUtils;
import sample.Utils.Preferences;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class DatabaseHelper {

    public static void create() {
        if (createUserTable()) {
            insertNewUser(new User("admin"
                    , "admin"
                    , "admin"
                    , "123"
                    , "admin"));
        }
        if (createCustomerTable()) {
            System.out.println("Customer Table Created or Already Exists");
        }
        if (createBillTable()) {
            System.out.println("Bills Table Created or Already Exists");
        }
    }

    private static boolean createTable(String createQuery) {
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

    private static boolean createBillTable() {
        String create = "CREATE TABLE IF NOT EXISTS NonGstBills ( " +
                "BillID TEXT NOT NULL UNIQUE,INVOICE TEXT NOT NULL UNIQUE," +
                "DATE TEXT NOT NULL, CustomerName TEXT NOT NULL, " +
                "CustomerID TEXT NOT NULL, ADDRESS TEXT NOT NULL," +
                "PHONE TEXT NOT NULL, GstNO TEXT NOT NULL,USERNAME TEXT NOT NULL)";
        return createTable(create);
    }

    public static boolean insertNewBill(@NotNull Bill bill) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            String query = String.format("INSERT into %s (BillID,INVOICE,DATE,CustomerName,CustomerID,ADDRESS,GstNO,PHONE,USERNAME) VALUES (?,?,?,?,?,?,?,?,?) ", "NonGstBills");
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, bill.getBillId());
            preparedStatement.setString(2, bill.getInvoice());
            preparedStatement.setString(3, bill.getDate());
            preparedStatement.setString(4, bill.getCustomerName());
            preparedStatement.setString(5, bill.getCustomerId());
            preparedStatement.setString(6, bill.getAddress());
            preparedStatement.setString(7, bill.getGSTNo());
            preparedStatement.setString(8, "" + bill.getMobile());
            preparedStatement.setString(9, "" + bill.getUserName());

            if (preparedStatement.executeUpdate() > 0)
                okay = createBillProductTable(bill.getBillId(), bill.getProducts());

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

    public static ObservableList<Bill> getBillList() {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        try {
            String insert = " SELECT * FROM NonGstBills WHERE TRUE ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) bill.add(getBill(resultSet));
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
        return bill;
    }

    public static ObservableList<Bill> getBillList(@NotNull String customer
            , @NotNull LocalDate a, @NotNull LocalDate b) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        ResultSet resultSet = null;

        try {
            resultSet = getResultSet(customer);
            if (resultSet == null) {
                System.out.println("null");
                return bill;
            }
            while (resultSet.next()) {
                long l = Long.parseLong(resultSet.getString(3));
                Date date = new Date(l);
                LocalDate d = BillingSystemUtils.convertToLocalDateViaInstant(date);
                if ((d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b)))
                    bill.add(getBill(resultSet));
            }
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return bill;
    }

    public static ObservableList<Bill> getBillList(@NotNull String customer) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        ResultSet resultSet = null;
        try {
            resultSet = getResultSet(customer);
            if (resultSet == null) {
                System.out.println("null");
                return bill;
            }
            while (resultSet.next()) bill.add(getBill(resultSet));
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return bill;
    }

    private static ResultSet getResultSet(String customer) {
        PreparedStatement preparedStatement;
        try {
            String insert = String.format(" SELECT * FROM %s WHERE CustomerName = ? ", "NonGstBills");
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, customer);
            return preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
            return null;
        }
    }

    public static ObservableList<Bill> getBillLists(@NotNull String searchText) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        try {
            String insert = String.format(" SELECT * FROM %s WHERE INVOICE LIKE ? ", "NonGstBills");
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, searchText);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) bill.add(getBill(resultSet));
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
        return bill;
    }

    public static ObservableList<Bill> getBillList(@NotNull LocalDate a, LocalDate b
            , int i) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        try {
            String insert = " SELECT * FROM NonGstBills WHERE TRUE ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long l = Long.parseLong(resultSet.getString(3));
                Date date = new Date(l);
                LocalDate d = BillingSystemUtils.convertToLocalDateViaInstant(date);
                if (((i == 1 && !resultSet.getString(8).equals("For Own Use"))
                        || (i == 2 && resultSet.getString(8).equals("For Own Use"))
                        || (i == 3))
                        && ((d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b))))
                    bill.add(getBill(resultSet));
            }
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
        return bill;
    }

    public static ObservableList<Bill> getBillList(@NotNull boolean b) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;

        try {
            String insert = " SELECT * FROM  NonGstBills";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (((b && !resultSet.getString(8).equals("For Own Use"))
                        || (!b && resultSet.getString(8).equals("For Own Use"))))
                    bill.add(getBill(resultSet));
            }
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
        return bill;
    }

    private static Bill getBill(@NotNull ResultSet resultSet) throws SQLException {
        long l = Long.parseLong(resultSet.getString(3));
        String bi = resultSet.getString(1);
        Bill bill = new Bill(resultSet.getString(1)
                , resultSet.getString(2)
                , BillingSystemUtils.formatDateTimeString(l)
                , resultSet.getString(4)
                , resultSet.getString(5)
                , resultSet.getString(6)
                , resultSet.getString(7)
                , resultSet.getString(8)
                , getBillProductList(bi)
                , resultSet.getString("USERNAME"));
        bill.setTime(resultSet.getString(3));
        return bill;
    }

    public static boolean deleteBill(@NotNull String billId) {
        boolean okay = false;
        PreparedStatement preparedStatement = null;
        try {
            String delete = String.format("DELETE FROM %s WHERE BillID = ? ", "NonGstBills");
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(delete);
            preparedStatement.setString(1, billId);
            okay = preparedStatement.executeUpdate() > 0;
            okay = okay && deleteTable(billId);
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

    private static boolean createBillProductTable(@NotNull String billName
            , @NotNull ObservableList<Product> products) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            String create = "CREATE TABLE IF NOT EXISTS " + billName + " (NAME TEXT NOT NULL , " +
                    "HSN TEXT NOT NULL , QTY TEXT NOT NULL, " +
                    "RATE TEXT NOT NULL," +
                    "PER TEXT NOT NULL); ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(create);
            okay = createTable(create);
            for (Product p : products) {
                if (p.ready)
                    okay = okay && insertNewProduct(billName, p);
            }
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

    private static boolean insertNewProduct(@NotNull String tableName, @NotNull Product product) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            String insert = String.format("INSERT OR IGNORE INTO %s (NAME,HSN,QTY,RATE,PER)  values (?,?,?,?,?) ", tableName);
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getHsn());
            preparedStatement.setString(3, product.getQty());
            preparedStatement.setString(4, product.getRate());
            preparedStatement.setString(5, product.getPer());
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

    private static ObservableList<Product> getBillProductList(@NotNull String tableName) {
        ObservableList<Product> products = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String insert = " SELECT * FROM " + tableName + " WHERE TRUE ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            resultSet = preparedStatement.executeQuery();
            Product product;
            int i = 1;
            while (resultSet.next()) {
                product = new Product(
                        "" + resultSet.getString(1)
                        , "" + resultSet.getString(2)
                        , "" + resultSet.getString(3)
                        , "" + resultSet.getString(4)
                        , "" + resultSet.getString(5));
                product.setSl("" + (i++));
                products.add(product);
            }
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return products;
    }

    private static boolean createUserTable() {
        String create = "CREATE TABLE IF NOT EXISTS Employee (Name TEXT NOT NULL" +
                ",id TEXT NOT NULL UNIQUE,password TEXT NOT NULL" +
                ",access TEXT NOT NULL,UserName TEXT NOT NULL)";
        return createTable(create);
    }

    public static boolean insertNewUser(@NotNull User user) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            String query = "insert or ignore into employee ( name " +
                    ", id , password , access , username ) " +
                    " values ( ? , ? , ? , ? , ? ) ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getId());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getAccess());
            preparedStatement.setString(5, user.getUserName());
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

    public static boolean updateUser(@NotNull User user) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;

        try {
            String query = "update employee set name = ?,password = ?,access =" +
                    " ?,username = ? where id = ?";

            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getAccess());
            preparedStatement.setString(4, user.getUserName());
            preparedStatement.setString(5, user.getId());
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

    public static User getUserInfo(@NotNull String userName) {
        User u = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String query = "select * from employee where username = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, userName);
            resultSet = preparedStatement.executeQuery();
            u = new User(resultSet.getString("name")
                    , resultSet.getString("username")
                    , resultSet.getString("id")
                    , resultSet.getString("password")
                    , resultSet.getString("access"));
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            closePAndRMethod(preparedStatement, resultSet);
        }
        return u;
    }

    public static boolean deleteUser(@NotNull User user) {
        PreparedStatement preparedStatement = null;

        boolean okay = false;
        try {
            String insert = "DELETE FROM EMPLOYEE where name = ? and username = ? and id = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setString(3, user.getId());
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

    public static boolean valid(@NotNull String user, @NotNull String password) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean okay = false;
        Preferences preferences = Preferences.getPreferences();
        try {
            String query = "select *from Employee where username = ? and password = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                okay = true;
                Preferences.setPreference(preferences);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            closePAndRMethod(preparedStatement, resultSet);
        }
        return okay;
    }

    public static ObservableList<User> getUserList() {
        ObservableList<User> users = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM EMPLOYEE WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(new User(resultSet.getString("name")
                        , resultSet.getString("username")
                        , resultSet.getString("id")
                        , resultSet.getString("password")
                        , resultSet.getString("access")));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            closePAndRMethod(preparedStatement, resultSet);
        }
        return users;
    }

    private static boolean createCustomerTable() {
        String create = "CREATE TABLE IF NOT EXISTS CUSTOMER" +
                "( NAME TEXT NOT NULL UNIQUE,  PHONE TEXT NOT NULL" +
                ", GSTIN TEXT NOT NULL, STREET TEXT NOT NULL" +
                ", CITY TEXT NOT NULL,  STATE TEXT NOT NULL" +
                ", ZIP TEXT NOT NULL,   ID TEXT NOT NULL UNIQUE )";
        return createTable(create);
    }

    public static boolean insertNewCustomer(@NotNull Customer customer) {
        String query = "INSERT OR IGNORE INTO CUSTOMER " +
                "( NAME , PHONE , GSTIN , STREET , CITY , STATE , ZIP" +
                ", ID) VALUES (?,?,?,?,?,?,?,?)";
        return getResultOfUpdateOrInsert(query, customer);
    }

    public static boolean updateCustomer(@NotNull Customer customer) {
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

    private static void closePAndRMethod(PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            assert preparedStatement != null;
            preparedStatement.close();
            assert resultSet != null;
            resultSet.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public static boolean deleteCustomer(@NotNull Customer customer) {
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

    public static Customer getCustomerInfo(@NotNull String customerId) {

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
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            closePAndRMethod(preparedStatement, resultSet);
        }
        return customer;
    }

    public static ObservableList<Customer> getCustomerList() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String query = "SELECT * FROM CUSTOMER WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
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
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            closePAndRMethod(preparedStatement, resultSet);
        }
        return customers;
    }

    public static ObservableList<String> getCustomerNameList(@NotNull int gst) {
        ObservableList<String> customers = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String query = "SELECT * FROM CUSTOMER WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            if (gst == 0) {
                while (resultSet.next()) {
                    if (!resultSet.getString(3).equals("For Own Use"))
                        customers.add(resultSet.getString("NAME"));
                }
            } else if (gst == 1) {
                while (resultSet.next()) {
                    if (resultSet.getString(3).equals("For Own Use"))
                        customers.add(resultSet.getString("NAME"));
                }
            } else {
                while (resultSet.next()) {
                    customers.add(resultSet.getString("NAME"));
                }

            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            closePAndRMethod(preparedStatement, resultSet);
        }
        return customers;
    }

    private static boolean deleteTable(@NotNull String tableName) {
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

    public static boolean ifInvoiceExist(String s) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean okay = false;
        try {
            String q = "Select * from NonGstBills where invoice = ?";
            preparedStatement =
                    DatabaseHandler.getInstance().getConnection().prepareStatement(q);
            preparedStatement.setString(1, s);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            closePAndRMethod(preparedStatement, resultSet);
        }
        return okay;
    }
}