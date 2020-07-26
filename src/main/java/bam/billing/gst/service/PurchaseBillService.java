package bam.billing.gst.service;

import bam.billing.gst.alert.AlertMaker;
import bam.billing.gst.model.PurchaseBill;
import bam.billing.gst.utils.StringUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PurchaseBillService {

    private static final Logger logger = Logger.getLogger(PurchaseBillService.class.getName());

    private static final String TABLE_NAME = "PURCHASEBILLS";

    private PurchaseBillService() {

    }

    public static boolean insertNewPurchaseBill(PurchaseBill purchaseBill) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            String query = String.format("INSERT INTO %s (DATE , CompanyName ," +
                    " INVOICE , AmountBeforeTax , TwelvePerAmt , EighteenPerAmt ," +
                    " TwentyEightPerAmt , AmountAfterTax, HasGoneToAuditor, others, dateCleared, status,ExtraAmount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)", TABLE_NAME);
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, purchaseBill.getDateInLong());
            preparedStatement.setString(2, purchaseBill.getCompanyName());
            preparedStatement.setString(3, purchaseBill.getInvoiceNo());
            preparedStatement.setString(4, purchaseBill.getAmountBeforeTax());
            preparedStatement.setString(5, purchaseBill.getTwelve());
            preparedStatement.setString(6, purchaseBill.getEighteen());
            preparedStatement.setString(7, purchaseBill.getTwentyEight());
            preparedStatement.setString(8, String.format("%.2f", Double.parseDouble(purchaseBill.getTotalAmount())));
            preparedStatement.setString(9, purchaseBill.getHasSentToAuditor());
            preparedStatement.setString(10, purchaseBill.getOthers());
            preparedStatement.setString(11, purchaseBill.getDateClearedAsLong());
            preparedStatement.setString(12, purchaseBill.getStatus());
            preparedStatement.setString(13, purchaseBill.getExtraAmount());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
        return okay;
    }

    public static boolean deletePurchaseBill(PurchaseBill purchaseBill) {
        boolean okay = false;
        PreparedStatement preparedStatement = null;
        try {
            String delete = String.format("DELETE FROM %s WHERE INVOICE = ? AND CompanyName =? ", TABLE_NAME);
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(delete);
            preparedStatement.setString(1, purchaseBill.getInvoiceNo());
            preparedStatement.setString(2, purchaseBill.getCompanyName());
            okay = preparedStatement.executeUpdate() > 0;
            preparedStatement.close();
        } catch (SQLException e) {
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
        return okay;
    }

    public static boolean updatePurchaseBill(PurchaseBill purchaseBill) {
        boolean okay = true;
        PreparedStatement preparedStatement;

        String updateQuery = " UPDATE " + TABLE_NAME + " SET DATE = ?, " +
                " AmountBeforeTax = ?, TwelvePerAmt = ? , EighteenPerAmt = ?," +
                " TwentyEightPerAmt = ?, AmountAfterTax = ?, hasGoneToAuditor = ?,dateCleared = ?,status = ?,others = ?,ExtraAmount = ? WHERE INVOICE = ? AND CompanyName = ?";
        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(updateQuery);
            preparedStatement.setString(1, purchaseBill.getDateInLong());
            preparedStatement.setString(2, purchaseBill.getAmountBeforeTax());
            preparedStatement.setString(3, purchaseBill.getTwelve());
            preparedStatement.setString(4, purchaseBill.getEighteen());
            preparedStatement.setString(5, purchaseBill.getTwentyEight());
            preparedStatement.setString(6, purchaseBill.getTotalAmount());
            preparedStatement.setString(7, purchaseBill.getHasSentToAuditor());
            preparedStatement.setString(8, purchaseBill.getDateClearedAsLong());
            preparedStatement.setString(9, purchaseBill.getStatus());
            preparedStatement.setString(10, purchaseBill.getExtraAmount());
            preparedStatement.setString(11, purchaseBill.getOthers());
            preparedStatement.setString(12, purchaseBill.getInvoiceNo());
            preparedStatement.setString(13, purchaseBill.getCompanyName());


            okay = preparedStatement.executeUpdate() > 0;

        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

    public static ObservableList<PurchaseBill> getAllPurchaseBillList() {
        ObservableList<PurchaseBill> bills = FXCollections.observableArrayList();
        try {
            ResultSet resultSet = getResultSet();
            while (resultSet.next()) bills.add(getPurchaseBill(resultSet));
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        }
        return bills;
    }

    public static ObservableList<PurchaseBill> getPurchaseBillList(String text) {
        String getQuery = StringUtil.SELECT + TABLE_NAME + " WHERE INVOICE LIKE ?";
        return getResultSetSearchByString(getQuery, text);
    }

    public static ObservableList<PurchaseBill> getPurchaseBillListByTotalNetAmount(String text) {
        String getQuery = StringUtil.SELECT + TABLE_NAME + " WHERE AmountAfterTax LIKE ? ";
        return getResultSetSearchByString(getQuery, text);
    }

    public static ObservableList<PurchaseBill> getPurchaseBillList(String companyName, LocalDate a, LocalDate b) {
        String getQuery = StringUtil.SELECT + TABLE_NAME + " WHERE CompanyName LIKE ?";
        ObservableList<PurchaseBill> bills = FXCollections.observableArrayList();
        for (PurchaseBill bill : getResultSetSearchByString(getQuery, companyName)) {
            try {
                long l = Long.parseLong(bill.getDateInLong());
                Date date = new Date(l);
                LocalDate d = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if ((d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b)))
                    bills.add(bill);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
        return bills;
    }

    public static ObservableList<PurchaseBill> getPurchaseBillListByCompanyName(String text) {
        String getQuery = StringUtil.SELECT + TABLE_NAME + " WHERE CompanyName LIKE ?";
        return getResultSetSearchByString(getQuery, text);
    }

    public static ObservableList<PurchaseBill> getPurchaseBillList(LocalDate a, LocalDate b) {
        ObservableList<PurchaseBill> bills = FXCollections.observableArrayList();
        try {
            ResultSet resultSet = getResultSet();
            assert resultSet != null;
            while (resultSet.next()) {
                try {
                    long l = Long.parseLong(resultSet.getString(1));
                    Date date = new Date(l);
                    LocalDate d = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    if ((d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b)))
                        bills.add(getPurchaseBill(resultSet));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage());
                }
            }
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        }
        return bills;
    }

    private static PurchaseBill getPurchaseBill(ResultSet resultSet) throws SQLException {
        String invoice = resultSet.getString(3);

        return new PurchaseBill(resultSet.getString(1)
                , resultSet.getString(2)
                , invoice
                , getValue(resultSet.getString(4))
                , getValue(resultSet.getString(5))
                , getValue(resultSet.getString(6))
                , getValue(resultSet.getString(7))
                , resultSet.getString(8)
                , resultSet.getString(9)
                , resultSet.getString(10)
                , resultSet.getString(11)
                , resultSet.getString(12)
                , resultSet.getString(13));
    }

    private static String getValue(String value) {
        try {
            value = "" + String.format("%.2f", Double.parseDouble(value));
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return value;
    }

    private static ObservableList<PurchaseBill> getResultSetSearchByString(String getQuery, String text) {
        ObservableList<PurchaseBill> bills = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(getQuery);
            preparedStatement.setString(1, text);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) bills.add(getPurchaseBill(resultSet));
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                logger.log(Level.SEVERE, e1.getMessage());
            }
        }
        return bills;
    }

    private static ResultSet getResultSet() throws SQLException {
        PreparedStatement preparedStatement;
        String getQuery = StringUtil.SELECT + TABLE_NAME + " WHERE TRUE";
        preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(getQuery);
        return preparedStatement.executeQuery();
    }

    public static boolean markBillAsGoneToAuditor(PurchaseBill purchaseBill) {
        boolean okay = true;
        PreparedStatement preparedStatement;
        String updateQuery = " UPDATE " + TABLE_NAME + " SET HasGoneToAuditor = ? WHERE INVOICE = ? AND CompanyName = ? ";
        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(updateQuery);
            preparedStatement.setString(1, "true");
            preparedStatement.setString(2, purchaseBill.getInvoiceNo());
            preparedStatement.setString(3, purchaseBill.getCompanyName());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }
}
