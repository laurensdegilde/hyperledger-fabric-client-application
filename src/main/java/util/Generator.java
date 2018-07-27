package util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Generator {

    private final String XLS_FILE_PATH = getClass().getClassLoader().getResource("generation-codes.xlsx").getFile();

    private Map<String, Integer> healthInsuredCodes;
    private Map<String, Integer> agbCodes;
    private Map<String, Integer> serviceCodes;
    private Map<String, Integer> serviceDates;
    private Map<Boolean, Integer> acknowledged;


    private Workbook workbook;
    private Sheet sheet;
    private DataFormatter dataFormatter;


    public Generator() throws IOException, InvalidFormatException {
        this.healthInsuredCodes= new HashMap();
        this.agbCodes = new HashMap();
        this.serviceCodes = new HashMap();
        this.serviceDates = new HashMap();
        this.acknowledged = new HashMap();
        this.dataFormatter = new DataFormatter();
        this.readPredefinedCodes();

    }

    public String[] generateRecord(boolean isSpecific) {

        int amountOfRecordsInSheet = this.sheet.getPhysicalNumberOfRows();
        Random random = new Random();


        String randomAGBCode = sheet.getRow(random.nextInt(amountOfRecordsInSheet)).getCell(0).getRichStringCellValue().toString();
        String randomHealthInsuredCode = String.format ("%.0f", sheet.getRow(random.nextInt(amountOfRecordsInSheet)).getCell(2).getNumericCellValue());
        String randomServiceDate = sheet.getRow(random.nextInt(amountOfRecordsInSheet)).getCell(3).getDateCellValue().toString();
        boolean randomIsAcknowledged = random.nextBoolean();
        String randomServiceCode = sheet.getRow(random.nextInt(amountOfRecordsInSheet)).getCell(1).getRichStringCellValue().toString();

        if (isSpecific){
            randomServiceCode = "00/0000";
        }

        this.healthInsuredCodes.merge(randomHealthInsuredCode, 1, Integer::sum);
        this.agbCodes.merge(randomAGBCode, 1, Integer::sum);
        this.serviceCodes.merge(randomServiceCode, 1, Integer::sum);
        this.serviceDates.merge(randomServiceDate, 1, Integer::sum);
        this.acknowledged.merge(randomIsAcknowledged, 1, Integer::sum);

        return new String [] {randomHealthInsuredCode, randomAGBCode, randomServiceCode,
                randomServiceDate, String.valueOf(randomIsAcknowledged)};
    }

    private void readPredefinedCodes() throws IOException, InvalidFormatException {
        this.workbook = WorkbookFactory.create(new File(XLS_FILE_PATH));
        this.sheet = workbook.getSheetAt(0);
    }

    public Map<String, Integer> getHealthInsuredCodes() {
        return healthInsuredCodes;
    }

    public Map<String, Integer> getAgbCodes() {
        return agbCodes;
    }

    public Map<String, Integer> getServiceCodes() {
        return serviceCodes;
    }

    public Map<String, Integer> getServiceDates() {
        return serviceDates;
    }

    public Map<Boolean, Integer> getAcknowledged() {
        return acknowledged;
    }
}
