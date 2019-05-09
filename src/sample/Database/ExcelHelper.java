package sample.Database;

import com.sun.istack.internal.NotNull;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Alert.AlertMaker;
import sample.Model.Bill;
import sample.Model.Customer;
import sample.Model.PurchaseBill;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@SuppressWarnings("Duplicates")
public class ExcelHelper {

    private static boolean okay = false;

    public static boolean writeExcelBills(@NotNull File dest
            , @NotNull ObservableList<Bill> bills) {
        okay = false;
        try {
            String FILE_NAME = dest.getAbsolutePath();
            FileInputStream excel;
            XSSFWorkbook workbook;
            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }
            XSSFSheet sheet;
            try {
                sheet = workbook.createSheet("History");
            } catch (Exception e) {
                sheet = workbook.getSheet("History");
            }
            int rowNum = 0;
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue("Invoice");
            row.createCell(1).setCellValue("Customer Name");
            row.createCell(2).setCellValue("Date");
            row.createCell(3).setCellValue("Total Amount");
            row.createCell(4).setCellValue("Rounded Off");
            row.createCell(5).setCellValue("Phone NO");
            row.createCell(6).setCellValue("Place");
            row.createCell(7).setCellValue("Prepared By");

            rowNum++;
            autoResizeColumn(sheet, 7);
            for (int columnIndex = 0; columnIndex <= 10; columnIndex++) {
                sheet.autoSizeColumn(columnIndex);
            }
            String place = "";
            for (Bill bill : bills) {
                row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(bill.getInvoice());
                row.createCell(1).setCellValue(bill.getCustomerName());
                row.createCell(2).setCellValue(bill.getDate());
                row.createCell(3).setCellValue("" + Math.ceil((Float.parseFloat(bill.getTotalAmount()))));
                row.createCell(4).setCellValue(bill.getRoundOff());
                row.createCell(5).setCellValue(bill.getMobile());
                try {
                    place = bill.getAddress().split("\n")[2];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                row.createCell(6).setCellValue(place);
                row.createCell(7).setCellValue(bill.getUserName());
                place = "";
                rowNum++;
            }
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

    public static boolean writeExcelCustomer(@NotNull File dest
            , @NotNull ObservableList<Customer> customers) {
        okay = false;
        try {
            String FILE_NAME = dest.getAbsolutePath();
            FileInputStream excel;
            XSSFWorkbook workbook;
            XSSFSheet sheet;

            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }
            try {
                sheet = workbook.createSheet("Customer");
            } catch (Exception e) {
                sheet = workbook.getSheet("Customer");
            }
            int rowNum = 0;
            for (int columnIndex = 0; columnIndex <= 7; columnIndex++)
                sheet.autoSizeColumn(columnIndex);

            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue("Name");
            row.createCell(1).setCellValue("PHONE");
            row.createCell(2).setCellValue("GSTIN");
            row.createCell(3).setCellValue("ADDRESS LINE 1");
            row.createCell(4).setCellValue("ADDRESS LINE 2");
            row.createCell(5).setCellValue("ADDRESS LINE 3");
            row.createCell(6).setCellValue("ZIP");
            row.createCell(7).setCellValue("CUSTOMER ID (DO NOT DELETE) ");
            rowNum++;
            autoResizeColumn(sheet, 7);
            for (Customer customer : customers) {
                row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(customer.getName());
                row.createCell(1).setCellValue(customer.getPhone());
                row.createCell(2).setCellValue(customer.getGstIn());
                row.createCell(3).setCellValue(customer.getStreetAddress());
                row.createCell(4).setCellValue(customer.getCity());
                row.createCell(5).setCellValue(customer.getState());
                row.createCell(6).setCellValue(customer.getZipCode());
                row.createCell(7).setCellValue(customer.getId());
                rowNum++;
            }
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

    public static boolean writeExcelPurchaseBills(@NotNull File dest) {
        okay = false;
        try {
            String FILE_NAME = dest.getAbsolutePath();
            FileInputStream excel;
            XSSFWorkbook workbook;
            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }
            String[] typeArray = {"StdEnt", "StdEqm"};
            for (String s : typeArray) {
                ObservableList<PurchaseBill> purchaseBills = DatabaseHelper_PurchaseBill.getAllPurchaseBillList(s);
                XSSFSheet sheet;
                try {
                    sheet = workbook.createSheet("PURCHASE BILLS FOR " + s);
                } catch (Exception e) {
                    sheet = workbook.getSheet("PURCHASE BILLS");
                }
                int rowNum = 0;
                for (int columnIndex = 0; columnIndex <= 7; columnIndex++)
                    sheet.autoSizeColumn(columnIndex);

                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue("DATE");
                row.createCell(1).setCellValue("COMPANY NAME");
                row.createCell(2).setCellValue("INVOICE");
                row.createCell(3).setCellValue("AMOUNT BEFORE TAX");
                row.createCell(4).setCellValue("12% TAX AMOUNT");
                row.createCell(5).setCellValue("18% TAX AMOUNT");
                row.createCell(6).setCellValue("28% TAX AMOUNT");
                row.createCell(7).setCellValue("TOTAL NET AMOUNT");
                row.createCell(8).setCellValue("Send To Auditor");
                rowNum++;
                autoResizeColumn(sheet, 8);
                for (PurchaseBill bill : purchaseBills) {
                    row = sheet.createRow(rowNum);
                    row.createCell(0).setCellValue(bill.getDateAsString());
                    row.createCell(1).setCellValue(bill.getCompanyName());
                    row.createCell(2).setCellValue(bill.getInvoiceNo());
                    row.createCell(3).setCellValue(bill.getAmountBeforeTax());
                    row.createCell(4).setCellValue(bill.getTwelve());
                    row.createCell(5).setCellValue(bill.getEighteen());
                    row.createCell(6).setCellValue(bill.getTwentyEight());
                    row.createCell(7).setCellValue(bill.getTotalAmount());
                    row.createCell(8).setCellValue(bill.getHasGoneToAuditorString());
                    rowNum++;
                }
            }
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

    private static void autoResizeColumn(Sheet sheet, int n) {
        for (int i = 0; i <= n; i++)
            sheet.autoSizeColumn(i);

    }

}
