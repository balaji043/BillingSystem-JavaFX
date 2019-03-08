package sample.Database;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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

    private static PreparedStatement preparedStatement = null;

    private static ResultSet resultSet = null;

    private static boolean okay = false;

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
        if (createBillsTable()) {
            System.out.println("Bills Table Created or Already Exists");
        }
        if (createIBillsTable()) {
            System.out.println("IBills Table Created or Already Exists");
        }
    }

    private static boolean createTable(String createQuery) {
        Connection connection = DatabaseHandler.getInstance().getConnection();
        okay = false;
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

    private static boolean createBillsTable() {
        okay = false;
        String create = "CREATE TABLE IF NOT EXISTS BILLS ( " +
                "BillID TEXT NOT NULL UNIQUE,INVOICE TEXT NOT NULL UNIQUE," +
                "DATE TEXT NOT NULL, CustomerName TEXT NOT NULL, " +
                "CustomerID TEXT NOT NULL, ADDRESS TEXT NOT NULL," +
                "PHONE TEXT NOT NULL, GstNO TEXT NOT NULL,USERNAME TEXT NOT NULL)";
        okay = createTable(create);
        return okay;
    }

    private static boolean createIBillsTable() {
        okay = false;
        String create = "CREATE TABLE IF NOT EXISTS IBILLS ( " +
                "BillID TEXT NOT NULL UNIQUE,INVOICE TEXT NOT NULL UNIQUE," +
                "DATE TEXT NOT NULL, CustomerName TEXT NOT NULL, " +
                "CustomerID TEXT NOT NULL, ADDRESS TEXT NOT NULL," +
                "PHONE TEXT NOT NULL, GstNO TEXT NOT NULL,USERNAME TEXT NOT NULL)";
        okay = createTable(create);
        return okay;
    }

    public static boolean insertNewBill(@NotNull Bill bill, @NotNull String tableName) {
        okay = false;
        try {
            String query = "INSERT into " + tableName + " (BillID,INVOICE,DATE" +
                    ",CustomerName,CustomerID,ADDRESS,GstNO," +
                    "PHONE,USERNAME) VALUES (?,?,?,?,?,?,?,?,?) ";

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

    public static ObservableList<Bill> getBillList(String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        try {
            String insert = " SELECT * FROM " + tableName + " WHERE TRUE ";
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
            try {
                assert resultSet != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return bill;
    }

    public static ObservableList<Bill> getBillList(@NotNull String customer
            , @NotNull LocalDate a, @NotNull LocalDate b, @NotNull String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        try {
            String insert = " SELECT * FROM " + tableName + " WHERE CUSTOMERNAME = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, customer);
            ResultSet resultSet = preparedStatement.executeQuery();
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
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                assert resultSet != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return bill;
    }

    public static ObservableList<Bill> getBillList(@NotNull String customer
            , @NotNull String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        try {
            String insert = " SELECT * FROM " + tableName + " WHERE CUSTOMERNAME = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, customer);
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
            try {
                assert resultSet != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return bill;
    }

    public static ObservableList<Bill> getBillLists(@NotNull String searchText
            , @NotNull String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        try {
            String insert = " SELECT * FROM " + tableName + " WHERE INVOICE LIKE ? ";
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
            try {
                assert resultSet != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return bill;
    }

    public static ObservableList<Bill> getBillList(@NotNull LocalDate a, LocalDate b
            , int i, @NotNull String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        try {

            String insert = " SELECT * FROM " + tableName + " WHERE TRUE ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long l = Long.parseLong(resultSet.getString(3));
                Date date = new Date(l);
                LocalDate d = BillingSystemUtils.convertToLocalDateViaInstant(date);
                if (((i == 0 && !resultSet.getString(7).equals("For Own Use"))
                        || (i == 1 && resultSet.getString(7).equals("For Own Use"))
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
            try {
                assert resultSet != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return bill;
    }

    public static ObservableList<Bill> getBillList(@NotNull boolean b, @NotNull String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        try {
            String insert = " SELECT * FROM  " + tableName;
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (((b && !resultSet.getString(7).equals("For Own Use"))
                        || (!b && resultSet.getString(7).equals("For Own Use"))))
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
            try {
                assert resultSet != null;
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
        return new Bill(resultSet.getString(1)
                , resultSet.getString(2)
                , BillingSystemUtils.formatDateTimeString(l)
                , resultSet.getString(4)
                , resultSet.getString(5)
                , resultSet.getString(6)
                , resultSet.getString(7)
                , resultSet.getString(8)
                , getBillProductList(bi)
                , resultSet.getString("USERNAME"));
    }

    public static boolean deleteBill(@NotNull String billId, @NotNull String tableName) {
        okay = false;
        try {
            String delete = "DELETE FROM " + tableName + " WHERE BillID = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(delete);
            preparedStatement.setString(1, billId);
            okay = preparedStatement.executeUpdate() > 0 && deleteTable(billId);
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
        okay = false;
        try {
            String create = "CREATE TABLE IF NOT EXISTS " + billName + " (NAME TEXT NOT NULL , " +
                    "HSN TEXT NOT NULL , QTY TEXT NOT NULL, " +
                    "TAX TEXT NOT NULL,RATE TEXT NOT NULL," +
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
        okay = false;
        try {
            String insert = "INSERT OR IGNORE INTO " + tableName + " (NAME,HSN,QTY,TAX,RATE,PER) " +
                    " values (?,?,?,?,?,?) ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getHsn());
            preparedStatement.setString(3, product.getQty());
            preparedStatement.setString(4, product.getTax());
            preparedStatement.setString(5, product.getRate());
            preparedStatement.setString(6, product.getPer());
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
        try {
            String insert = " SELECT * FROM " + tableName + " WHERE TRUE ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            resultSet = preparedStatement.executeQuery();
            Product product;
            int i = 1;
            while (resultSet.next()) {
                product = new Product(
                        resultSet.getString(1), resultSet.getString(2)
                        , resultSet.getString(3), resultSet.getString(4)
                        , resultSet.getString(5), resultSet.getString(6));
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
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return products;
    }

    private static boolean createUserTable() {
        okay = false;
        String create = "CREATE TABLE IF NOT EXISTS Employee (Name TEXT NOT NULL" +
                ",id TEXT NOT NULL UNIQUE,password TEXT NOT NULL" +
                ",access TEXT NOT NULL,UserName TEXT NOT NULL)";
        okay = createTable(create);
        return okay;
    }

    public static boolean insertNewUser(@NotNull User user) {
        okay = false;
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
        okay = false;
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
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    public static User getUserInfo(@NotNull String userName) {
        User u = null;
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
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return u;
    }

    public static boolean deleteUser(@NotNull User user) {
        okay = false;
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

    public static boolean valid(@NotNull String user, @NotNull String password)
            throws SQLException {
        okay = false;
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
            preparedStatement.close();
            assert resultSet != null;
            resultSet.close();
        }
        return okay;
    }

    public static ObservableList<User> getUserList() {
        ObservableList<User> users = FXCollections.observableArrayList();
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
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return users;
    }

    private static boolean createCustomerTable() {
        okay = false;
        String create = "CREATE TABLE IF NOT EXISTS CUSTOMER" +
                "( NAME TEXT NOT NULL UNIQUE,  PHONE TEXT NOT NULL" +
                ", GSTIN TEXT NOT NULL, STREET TEXT NOT NULL" +
                ", CITY TEXT NOT NULL,  STATE TEXT NOT NULL" +
                ", ZIP TEXT NOT NULL,   ID TEXT NOT NULL UNIQUE )";
        okay = createTable(create);
        return okay;
    }

    public static boolean insertNewCustomer(@NotNull Customer customer) {
        okay = false;
        try {
            String query = "INSERT OR IGNORE INTO CUSTOMER " +
                    "( NAME , PHONE , GSTIN , STREET , CITY , STATE , ZIP" +
                    ", ID) VALUES (?,?,?,?,?,?,?,?)";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getPhone());
            preparedStatement.setString(3, customer.getGstIn());
            preparedStatement.setString(4, customer.getStreetAddress());
            preparedStatement.setString(5, customer.getCity());
            preparedStatement.setString(6, customer.getState());
            preparedStatement.setString(7, customer.getZipCode());
            preparedStatement.setString(8, customer.getId());
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

    public static boolean updateCustomer(@NotNull Customer customer) {
        okay = false;
        try {
            String query = "UPDATE CUSTOMER SET  NAME = ? , PHONE = ? " +
                    ", GSTIN = ? , STREET = ? , CITY = ? , STATE = ? , ZIP = ? " +
                    " WHERE ID = ?";

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
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    public static boolean deleteCustomer(@NotNull Customer customer) {
        okay = false;
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
        try {
            String query = "SELECT * FROM CUSTOMER WHERE ID = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, customerId);
            resultSet = preparedStatement.executeQuery();
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
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return customer;
    }

    public static ObservableList<Customer> getCustomerList() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
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
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return customers;
    }

    public static ObservableList<String> getCustomerNameList(@NotNull int gst) {
        ObservableList<String> customers = FXCollections.observableArrayList();
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
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return customers;
    }

    public static ObservableMap<String, String> getCustomerNameList() {
        ObservableMap<String, String> customers = FXCollections.observableHashMap();
        try {
            String query = "SELECT * FROM CUSTOMER WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                customers.put(resultSet.getString("NAME")
                        , resultSet.getString("ID"));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return customers;
    }

    private static boolean deleteTable(@NotNull String tableName) {
        okay = false;
        try {
            String q = "DROP TABLE IF EXISTS " + tableName;
            preparedStatement =
                    DatabaseHandler.getInstance().getConnection().prepareStatement(q);
            okay = !preparedStatement.execute();
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
        return okay;
    }


}