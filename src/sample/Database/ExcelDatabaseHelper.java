package sample.Database;

import com.sun.istack.internal.NotNull;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Alert.AlertMaker;
import sample.Model.Customer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ExcelDatabaseHelper {

    public static boolean writeDBCustomer(@NotNull File dest) {
        boolean okay = false;
        try {
            String FILE_NAME = dest.getAbsolutePath();
            FileInputStream excel;
            XSSFWorkbook workbook;
            XSSFSheet sheet;
            String n = "", p = "", g = "", a1 = "", a2 = "", a3 = "", z = "", id = "";
            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }
            try {
                sheet = workbook.getSheet("Customer");
            } catch (Exception e) {
                sheet = workbook.getSheet("History");
            }
            boolean b = true;
            for (Row currentRow : sheet) {
                if (b) {
                    b = false;
                    continue;
                }
                if (currentRow.getCell(0) != null) {
                    currentRow.getCell(0).setCellType(CellType.STRING);
                    n = currentRow.getCell(0).getStringCellValue();
                }
                if (currentRow.getCell(1) != null) {
                    currentRow.getCell(1).setCellType(CellType.STRING);
                    p = currentRow.getCell(1).getStringCellValue();
                }
                if (currentRow.getCell(2) != null) {
                    currentRow.getCell(2).setCellType(CellType.STRING);
                    g = currentRow.getCell(2).getStringCellValue();
                }
                if (currentRow.getCell(3) != null) {
                    currentRow.getCell(3).setCellType(CellType.STRING);
                    a1 = currentRow.getCell(3).getStringCellValue();
                }
                if (currentRow.getCell(4) != null) {
                    a2 = currentRow.getCell(4).getStringCellValue();
                }
                if (currentRow.getCell(5) != null) {
                    a3 = "" + currentRow.getCell(5).getStringCellValue();
                }
                if (currentRow.getCell(6) != null) {
                    z = "" + currentRow.getCell(6).getStringCellValue();
                }
                if (currentRow.getCell(7) != null) {
                    id = "" + currentRow.getCell(7).getStringCellValue();
                }
                DatabaseHelper.insertNewCustomer(new Customer(n, p, g, a1, a2, a3, z, id));
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
