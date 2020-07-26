package bam.billing.gst.service;

import bam.billing.gst.alert.AlertMaker;
import bam.billing.gst.model.Customer;
import bam.billing.gst.model.PurchaseBill;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelDatabaseHelper {

    private static final Logger LOGGER = Logger.getLogger(ExcelDatabaseHelper.class.getName());

    private ExcelDatabaseHelper() {
    }

    public static boolean writeDBCustomer(File dest) {
        boolean okay = false;
        try {
            String fileName = dest.getAbsolutePath();

            XSSFWorkbook workbook = getWorkBook(dest);
            XSSFSheet sheet;

            try {
                sheet = workbook.getSheet("Customer");
            } catch (Exception e) {
                sheet = workbook.getSheet("History");
            }

            boolean b = true;
            Customer customer;
            for (Row currentRow : sheet) {
                if (b) {
                    b = false;
                    continue;
                }
                customer = new Customer(
                        "" + getCellValue(currentRow.getCell(0))
                        , "" + getCellValue(currentRow.getCell(1))
                        , "" + getCellValue(currentRow.getCell(2))
                        , "" + getCellValue(currentRow.getCell(3))
                        , "" + getCellValue(currentRow.getCell(4))
                        , "" + getCellValue(currentRow.getCell(5))
                        , "" + getCellValue(currentRow.getCell(6))
                        , "" + getCellValue(currentRow.getCell(7)));
                CustomerService.insertNewCustomer(customer);
            }

            FileOutputStream outputStream = new FileOutputStream(fileName);
            workbook.write(outputStream);

            outputStream.close();

            okay = true;

        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

    public static boolean writeDBPurchaseBill(File dest) {
        boolean okay = false;
        try {
            String fileName = dest.getAbsolutePath();
            getWorkBook(dest);
            XSSFWorkbook workbook = getWorkBook(dest);
            Sheet sheet = null;
            try {
                sheet = workbook.getSheet("PURCHASE BILLS");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            PurchaseBill purchaseBill;
            boolean b = true;
            assert sheet != null;
            for (Row currentRow : sheet) {
                if (b) {
                    b = false;
                    continue;
                }
                purchaseBill = new PurchaseBill("" + getDateStringValue(currentRow.getCell(0))
                        , "" + getStringValue(currentRow.getCell(1))
                        , "" + getStringValue(currentRow.getCell(2))
                        , "" + getStringValue(currentRow.getCell(3))
                        , "" + getStringValue(currentRow.getCell(4))
                        , "" + getStringValue(currentRow.getCell(5))
                        , "" + getStringValue(currentRow.getCell(6))
                        , "" + getStringValue(currentRow.getCell(8))
                        , "" + getStringValue(currentRow.getCell(9))
                        , "" + getStringValue(currentRow.getCell(10))
                        , "" + getDateStringValue(currentRow.getCell(11))
                        , "" + getStringValue(currentRow.getCell(12))
                        , "" + getStringValue(currentRow.getCell(7)));
                try {
                    PurchaseBillService.insertNewPurchaseBill(purchaseBill);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage());
                }
            }

            FileOutputStream outputStream = new FileOutputStream(fileName);
            workbook.write(outputStream);
            outputStream.close();
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

    static XSSFWorkbook getWorkBook(File dest) {
        FileInputStream excel;
        XSSFWorkbook workbook;
        try {
            excel = new FileInputStream(dest);
            workbook = new XSSFWorkbook(excel);
        } catch (Exception e) {
            workbook = new XSSFWorkbook();
        }
        return workbook;
    }

    private static String getStringValue(Cell cell) {
        if (cell != null) {
            try {
                if (cell.getCellType() == CellType.STRING) {
                    return cell.getStringCellValue();
                } else {
                    return "" + cell.getNumericCellValue();

                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }

        }
        return "";
    }

    private static String getDateStringValue(Cell cell) {
        String value;
        Date date = new Date();

        if (cell.getCellType() == CellType.STRING) {
            value = cell.getStringCellValue();
            try {
                date = new SimpleDateFormat("dd/MM/yyyy").parse(value);
            } catch (Exception e) {
                try {
                    date = new SimpleDateFormat("dd-MM-yyyy").parse(value);
                } catch (Exception e1) {
                    LOGGER.log(Level.SEVERE, e.getMessage());
                }
            }
        } else {
            date = cell.getDateCellValue();
        }

        return "" + date.getTime();
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }
}
