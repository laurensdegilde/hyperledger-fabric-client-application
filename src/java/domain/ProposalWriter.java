package domain;

import com.google.gson.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

public class ProposalWriter {
    private Workbook workbook;
    private Sheet sheet;
    private int sheetRowCount;
    private final String XLS_FILE_PATH = "./responses/response";
    private FileOutputStream f;
    private File file;
    private Row headerRow;
    
    public ProposalWriter() {
        this.open();
    }
    
    public synchronized  void open(){
        try {
            this.file = new File(XLS_FILE_PATH + System.currentTimeMillis() + ".xlsx");
            this.workbook = WorkbookFactory.create(new File(XLS_FILE_PATH + ".xlsx"));
            this.sheet = this.workbook.getSheetAt(0);
            this.f = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        
    
    }
    public synchronized  void clean(){
        try {
            this.workbook.close();
            this.f.flush();
            this.f.close();
            this.file.delete();
            this.workbook = null;
            this.sheet = null;
            this.f = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
    public synchronized void persist(){
        System.out.println("Persisiting workbook.");
        try {
            this.workbook.write(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public synchronized void write(List<ProposalWrapper> proposalWrappers) {
        for (ProposalWrapper proposalWrapper : proposalWrappers) {
            this.addRowToSheet(proposalWrapper.getJsonResponse());
        }
    }

    private synchronized void addRowToSheet(JsonObject jsonResponse) {
        System.out.println(jsonResponse.toString());
        this.sheetRowCount = this.sheet.getPhysicalNumberOfRows();
        if(this.headerRow == null){
            this.headerRow = this.sheet.getRow(0);
        }
        Row row = this.sheet.createRow(this.sheetRowCount++);
        
        for(int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++){
            Cell cell = row.createCell(i);
            cell.setCellValue(String.valueOf(jsonResponse
                    .get(headerRow.getCell(i).getStringCellValue()))
                    .replaceAll("\"", "")
                    .replaceAll("null", ""));
        }
    }
}
