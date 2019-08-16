package sample.database;

import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import sample.alert.AlertMaker;
import sample.model.Bill;
import sample.model.Customer;
import sample.model.PurchaseBill;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ExcelHelper {

    private static final Logger LOGGER = Logger.getLogger(ExcelHelper.class.getName());
    private static boolean okay = false;

    private ExcelHelper() {
    }

    public static boolean writeExcelBills(@NotNull File dest
            , @NotNull ObservableList<Bill> bills) {
        okay = false;
        try {
            String fileName = dest.getAbsolutePath();
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
            row.createCell(7).setCellValue("Rounded Off");
            row.createCell(8).setCellValue("Taxable 12%");
            row.createCell(9).setCellValue("Taxable 18%");
            row.createCell(10).setCellValue("Taxable 28%");
            row.createCell(11).setCellValue("Total Amount");
            row.createCell(12).setCellValue("GST NO");
            row.createCell(13).setCellValue("Phone NO");
            row.createCell(14).setCellValue("Place");
            row.createCell(15).setCellValue("Prepared By");

            rowNum++;


            String place = "";
            for (Bill bill : bills) {
                row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(bill.getInvoice());
                row.createCell(1).setCellValue(bill.getCustomerName());
                row.createCell(2).setCellValue(bill.getDate());
                row.createCell(3).setCellValue(bill.getGross());
                row.createCell(4).setCellValue(bill.getGst12Total());
                row.createCell(5).setCellValue(bill.getGst18Total());
                row.createCell(6).setCellValue(bill.getGst28Total());
                row.createCell(7).setCellValue(bill.getRoundedOff());
                row.createCell(8).setCellValue(bill.getTax12BeforeTotal());
                row.createCell(9).setCellValue(bill.getTax18BeforeTotal());
                row.createCell(10).setCellValue(bill.getTax28BeforeTotal());
                row.createCell(11).setCellValue(bill.getTotalAmount());
                row.createCell(12).setCellValue(bill.getGstNo());
                row.createCell(13).setCellValue(bill.getMobile());
                try {
                    place = bill.getAddress().split("\n")[2];
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage());
                }
                row.createCell(14).setCellValue(place);
                row.createCell(15).setCellValue(bill.getUserName());
                place = "";
                rowNum++;
            }
            autoResizeColumn(sheet, 15);
            FileOutputStream outputStream = new FileOutputStream(fileName);
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
        XSSFWorkbook workbook = ExcelDatabaseHelper.getWorkBook(dest);

        try {
            String fileName = dest.getAbsolutePath();
            ExcelDatabaseHelper.getWorkBook(dest);

            XSSFSheet sheet;
            try {
                sheet = workbook.createSheet("Customer");
            } catch (Exception e) {
                sheet = workbook.getSheet("Customer");
            }
            int rowNum = 0;

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
            autoResizeColumn(sheet, 7);

            FileOutputStream outputStream = new FileOutputStream(fileName);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

    public static boolean writeExcelPurchaseBills(@NotNull File dest
            , @NotNull ObservableList<PurchaseBill> purchaseBills) {
        okay = false;
        try {
            String fileName = dest.getAbsolutePath();
            XSSFWorkbook workbook = ExcelDatabaseHelper.getWorkBook(dest);

            XSSFSheet sheet;
            try {
                sheet = workbook.createSheet("PURCHASE BILLS");
            } catch (Exception e) {
                sheet = workbook.getSheet("PURCHASE BILLS");
            }
            int rowNum = 0;

            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue("DATE");
            row.createCell(1).setCellValue("COMPANY NAME");
            row.createCell(2).setCellValue("INVOICE");
            row.createCell(3).setCellValue("AMOUNT BEFORE TAX");
            row.createCell(4).setCellValue("12% TAX AMOUNT");
            row.createCell(5).setCellValue("18% TAX AMOUNT");
            row.createCell(6).setCellValue("28% TAX AMOUNT");
            row.createCell(7).setCellValue("TOTAL NET AMOUNT");
            row.createCell(8).setCellValue("SEND TO AUDITOR");
            row.createCell(9).setCellValue("OTHERS");
            row.createCell(10).setCellValue("STATUS");
            row.createCell(11).setCellValue("DATE CLEARED");
            row.createCell(12).setCellValue("POSTAGE");
            rowNum++;

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
                row.createCell(9).setCellValue(bill.getOthers());
                row.createCell(10).setCellValue(bill.getStatus());
                row.createCell(11).setCellValue(bill.getDateCleared());
                row.createCell(12).setCellValue(bill.getExtraAmount());

                rowNum++;
            }
            autoResizeColumn(sheet, 8);
            FileOutputStream outputStream = new FileOutputStream(fileName);
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
