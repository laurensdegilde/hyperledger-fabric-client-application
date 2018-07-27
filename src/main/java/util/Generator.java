package util;

import domain.RecordWrapper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Generator {

    private final int AMOUNT_OF_DECLARATIONS = 320000;
    private final int AMOUNT_OF_INVALID_OPTICAL_DECLARATIONS = 2000;
    private final int AMOUNT_OF_VALID_OPTICAL_DECLARATIONS = 1200;
    private final String XLS_FILE_PATH = getClass().getClassLoader().getResource("generation-codes.xlsx").getFile();

    private Map healthInsuredCodes;
    private Map agbCodes;
    private Map serviceCodes;

    private List<RecordWrapper> generatedRecords;

    private Workbook workbook;
    private Sheet sheet;
    private DataFormatter dataFormatter;


    public Generator(){
        healthInsuredCodes= new HashMap();
        agbCodes = new HashMap();
        serviceCodes = new HashMap();
        dataFormatter = new DataFormatter();
    }

    public void generateRecords(int randomRecords, int specificRecords) throws IOException, InvalidFormatException {
        this.readPredefinedCodes();
        this.generatedRecords = new ArrayList<>();
        int amountOfRecordsInSheet = this.sheet.getPhysicalNumberOfRows();
        Random random = new Random();

        for (int i = 0; i < randomRecords; i++){
            int randomRow = random.nextInt(amountOfRecordsInSheet);
            boolean randomIsAcknowledged = random.nextBoolean();

            Row row = sheet.getRow(randomRow);
            this.generatedRecords.add(new RecordWrapper(
                    dataFormatter.formatCellValue(row.getCell(0)),
                    dataFormatter.formatCellValue(row.getCell(1)),
                    randomIsAcknowledged)
            );
        }
        for (RecordWrapper rw : this.generatedRecords){
            System.out.println(rw.toString());
        }

    }

    private void readPredefinedCodes() throws IOException, InvalidFormatException {
        this.workbook = WorkbookFactory.create(new File(XLS_FILE_PATH));
        this.sheet = workbook.getSheetAt(0);
    }
    public String [] getNewRandomDataRecord(){
        return new String [] { "test1", "test2", "test3", "test4", "test5"};
    }

}
