package domain;

import com.google.gson.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExcelHandle {
    
    private static Workbook workbook;
    private static Sheet sheet;
    private static int sheetRowCount;
    private static FileOutputStream fileOutputStream;
    private static Row headerRow;
    private static String STEP_XLSX_PATH = "./responses/steps/";
    
    public static synchronized void open(String chaincode, String step){
        System.out.println("open");
        clean();
        try {
            workbook = WorkbookFactory.create(new File(STEP_XLSX_PATH + "response_" + chaincode + "_" + step + ".xlsx"));
            sheet = workbook.getSheetAt(0);
            fileOutputStream = new FileOutputStream(new File("./responses/temp.xlsx"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        
        
    }
    public static synchronized  void clean(){
        try {
            if(workbook != null ){
                workbook.close();
                workbook = null;
            }
            
            if(fileOutputStream != null ){
                fileOutputStream.flush();
                fileOutputStream.close();
                fileOutputStream = null;
            }
            if(sheet != null ){
                sheet = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    public static synchronized void persist(){
        System.out.println("Persisiting workbook.");
        try {
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public static synchronized void write(List<ProposalWrapper> proposalWrappers) {
        for (ProposalWrapper proposalWrapper : proposalWrappers) {
            addRow(proposalWrapper.getJsonResponse());
        }
    }
    
    public static synchronized List<String []> read(int amountOfKeys, String chaincode, String step) throws IOException, InvalidFormatException {
        open(chaincode, step);
        Random rand = new Random();
        List<String []> keys = new ArrayList<>();
        if(amountOfKeys > sheet.getPhysicalNumberOfRows()){
            return null;
        }
        
        for (int i = 0; i < amountOfKeys; i++){
            Row r = sheet.getRow(rand.nextInt(sheet.getPhysicalNumberOfRows() - 1));
            keys.add(new String [] {String.valueOf(r.getCell(4))});
        }
        return keys;
    }
    
    private static synchronized void addRow(JsonObject jsonResponse) {
        System.out.println(jsonResponse.toString());
        sheetRowCount = sheet.getPhysicalNumberOfRows();
        if(headerRow == null){
            headerRow = sheet.getRow(0);
        }
        Row row = sheet.createRow(sheetRowCount++);
        
        for(int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++){
            Cell cell = row.createCell(i);
            cell.setCellValue(String.valueOf(jsonResponse
                    .get(headerRow.getCell(i).getStringCellValue()))
                    .replaceAll("\"", "")
                    .replaceAll("null", ""));
        }
    }
}
