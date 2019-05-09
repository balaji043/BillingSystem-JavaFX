package sample.Database;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Alert.AlertMaker;
import sample.Model.Bill;
import sample.Model.Product;
import sample.Utils.BillingSystemUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

/**
 * This Class contains all the functions
 * for Bill features like creating table and insert,update and delete bills
 */
public class DatabaseHelper_Bills {
    /**
     * This function is to insert a new bill in database
     *
     * @param bill      which is the bill to be inserted;
     * @param tableName which is String contains the tableName i.e either BILLS or IBILLS
     */
    public static boolean insertNewBill(@NotNull Bill bill, @NotNull String tableName) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            String query = String.format("INSERT into %s (BillID,INVOICE,DATE,CustomerName,CustomerID,ADDRESS,GstNO,PHONE,USERNAME) VALUES (?,?,?,?,?,?,?,?,?) ", tableName);
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
        PreparedStatement preparedStatement = null;
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
        }
        return bill;
    }

    public static ObservableList<Bill> getBillList(@NotNull String customer
            , @NotNull LocalDate a, @NotNull LocalDate b, @NotNull String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        ResultSet resultSet = null;

        try {
            resultSet = getResultSet(tableName, customer);
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

    public static ObservableList<Bill> getBillList(@NotNull String customer
            , @NotNull String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        ResultSet resultSet = null;
        try {
            resultSet = getResultSet(tableName, customer);
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

    private static ResultSet getResultSet(String tableName, String customer) {
        PreparedStatement preparedStatement;
        try {
            String insert = String.format(" SELECT * FROM %s WHERE CustomerName = ? ", tableName);
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, customer);
            return preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
            return null;
        }
    }

    public static ObservableList<Bill> getBillLists(@NotNull String searchText
            , @NotNull String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        try {
            String insert = String.format(" SELECT * FROM %s WHERE INVOICE LIKE ? ", tableName);
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
            , int i, @NotNull String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        try {

            String insert = " SELECT * FROM " + tableName + " WHERE TRUE ";
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

    public static ObservableList<Bill> getBillList(@NotNull boolean b, @NotNull String tableName) {
        ObservableList<Bill> bill = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;

        try {
            String insert = " SELECT * FROM  " + tableName;
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

    public static boolean deleteBill(@NotNull String billId, @NotNull String tableName) {
        boolean okay = false;
        PreparedStatement preparedStatement = null;
        try {
            String delete = String.format("DELETE FROM %s WHERE BillID = ? ", tableName);
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(delete);
            preparedStatement.setString(1, billId);
            okay = preparedStatement.executeUpdate() > 0;
            preparedStatement.close();
            okay = okay && DatabaseHelper.deleteTable(billId);
        } catch (SQLException e) {
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

    private static boolean createBillProductTable(@NotNull String billName
            , @NotNull ObservableList<Product> products) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            String create = "CREATE TABLE IF NOT EXISTS " + billName + " (NAME TEXT NOT NULL , " +
                    "HSN TEXT NOT NULL , QTY TEXT NOT NULL, " +
                    "TAX TEXT NOT NULL,RATE TEXT NOT NULL," +
                    "PER TEXT NOT NULL); ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(create);
            okay = DatabaseHelper.createTable(create);
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
            String insert = String.format("INSERT OR IGNORE INTO %s (NAME,HSN,QTY,TAX,RATE,PER)  values (?,?,?,?,?,?) ", tableName);
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
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return products;
    }

    public static boolean ifInvoiceExist(String s) {
        String[] tableNames = {"Bills", "IBills"};
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean okay = false;
        try {
            for (String tableName : tableNames) {
                String q = String.format("Select invoice from %s where invoice = ?", tableName);
                preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(q);
                preparedStatement.setString(1, s);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) okay = true;
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            DatabaseHelper.closePAndRMethod(preparedStatement, resultSet);
        }
        return okay;
    }

    public static Bill getBillInfo(String invoice, String tableName) {

        Bill bill = null;
        PreparedStatement preparedStatement = null;

        try {
            String insert = " SELECT * FROM  " + tableName + " WHERE INVOICE = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, invoice);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
                bill = getBill(resultSet);

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
}
