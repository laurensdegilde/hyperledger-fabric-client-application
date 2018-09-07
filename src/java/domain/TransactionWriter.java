package domain;

import com.google.gson.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TransactionWriter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private int sheetRowCount;
    private final String XLS_FILE_PATH = "./responses/response.xlsx";
    
    public TransactionWriter() throws IOException, InvalidFormatException {
        workbook = new XSSFWorkbook(new FileInputStream(XLS_FILE_PATH));
    }
    public void writeResponseToExcel(JsonObject jsonResponse){
        this.addRowToSheet(jsonResponse);
        try {
            FileOutputStream outputStream = new FileOutputStream(XLS_FILE_PATH);
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception could not write. Is the file open already?");
        }
    }
    
    private void addRowToSheet(JsonObject jsonResponse) {
        System.out.println(jsonResponse.toString());
        sheet = workbook.getSheetAt(0);
        this.sheetRowCount = sheet.getPhysicalNumberOfRows();
        Row headerRow = sheet.getRow(0);
        Row row = sheet.createRow(this.sheetRowCount++);
        
        for(int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++){
            Cell cell = row.createCell(i);
            cell.setCellValue(String.valueOf(jsonResponse
                    .get(headerRow.getCell(i).getStringCellValue()))
                    .replaceAll("\"", "")
                    .replaceAll("null", ""));
        }
    }
}
