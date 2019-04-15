package sample.Database;

import com.sun.istack.internal.NotNull;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Alert.AlertMaker;
import sample.Model.Bill;
import sample.Model.Customer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ExcelHelper {

    private static boolean okay = false;

    public static boolean writeExcelBills(@NotNull File dest
            , @NotNull ObservableList<Bill> bills) {
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
                sheet = workbook.createSheet("History");
            } catch (Exception e) {
                sheet = workbook.getSheet("History");
            }
            int rowNum = 0;
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue("Invoice");
            row.createCell(1).setCellValue("Customer Name");
            row.createCell(2).setCellValue("Date");
            row.createCell(3).setCellValue("Taxable Amount");
            row.createCell(4).setCellValue("12%");
            row.createCell(5).setCellValue("18%");
            row.createCell(6).setCellValue("28%");
            row.createCell(7).setCellValue("Total Amount");
            row.createCell(8).setCellValue("GST NO");
            row.createCell(9).setCellValue("Phone NO");
            row.createCell(10).setCellValue("Place");
            row.createCell(11).setCellValue("Prepared By");

            rowNum++;

            for (int columnIndex = 0; columnIndex <= 10; columnIndex++)
                sheet.autoSizeColumn(columnIndex);

            for (Bill bill : bills) {
                row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(bill.getInvoice());
                row.createCell(1).setCellValue(bill.getCustomerName());
                row.createCell(2).setCellValue(bill.getDate());
                row.createCell(3).setCellValue(bill.getTotalTaxAmount());
                row.createCell(4).setCellValue(bill.getGst12Total());
                row.createCell(5).setCellValue(bill.getGst18Total());
                row.createCell(6).setCellValue(bill.getGst28Total());
                row.createCell(7).setCellValue("" + Math.ceil((Float.parseFloat(bill.getTotalAmount()))));
                row.createCell(8).setCellValue(bill.getGSTNo());
                row.createCell(9).setCellValue(bill.getMobile());
                row.createCell(10).setCellValue(bill.getAddress().split("\n")[2]);
                row.createCell(11).setCellValue(bill.getUserName());
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
            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }

            XSSFSheet sheet;
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
}
