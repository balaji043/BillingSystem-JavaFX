package sample.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Alert.AlertMaker;
import sample.Model.PurchaseBill;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper_PurchaseBill {

    private static String tableName = "PURCHASEBILLS";

    public static boolean insertNewPurchaseBill(PurchaseBill purchaseBill) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            String query = String.format("INSERT INTO %s (DATE , CompanyName ," +
                    " INVOICE , AmountBeforeTax , TwelvePerAmt , EighteenPerAmt ," +
                    " TwentyEightPerAmt , AmountAfterTax) VALUES (?,?,?,?,?,?,?,?)", tableName);
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, purchaseBill.getDateInLong());
            preparedStatement.setString(2, purchaseBill.getCompanyName());
            preparedStatement.setString(3, purchaseBill.getAmountBeforeTax());
            preparedStatement.setString(4, purchaseBill.getDateInLong());
            preparedStatement.setString(5, purchaseBill.getTaxAmounts()[0]);
            preparedStatement.setString(6, purchaseBill.getTaxAmounts()[1]);
            preparedStatement.setString(7, purchaseBill.getTaxAmounts()[2]);
            preparedStatement.setString(8, purchaseBill.getTotalAmount());
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

    public static boolean deletePurchaseBill(PurchaseBill purchaseBill) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;

        return okay;
    }

    public static ObservableList<PurchaseBill> getAllPurchaseBillList() {
        ObservableList<PurchaseBill> bills = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        try {
            String getQuery = " SELECT * FROM " + tableName + " WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(getQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) bills.add(getPurchaseBill(resultSet));
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
        return bills;

    }

    private static PurchaseBill getPurchaseBill(ResultSet resultSet) throws SQLException {
        String[] taxAmounts = {resultSet.getString(5)
                , resultSet.getString(6)
                , resultSet.getString(7)};
        return new PurchaseBill(resultSet.getString(1)
                , resultSet.getString(2)
                , resultSet.getString(3)
                , resultSet.getString(4)
                , taxAmounts
                , resultSet.getString(8));
    }
}
