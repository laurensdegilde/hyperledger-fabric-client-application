package generator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Generator {

    private final String XLS_FILE_PATH = getClass().getClassLoader().getResource("generation-codes.xlsx").getFile();
    private Map<String, String> generatedDataRepresentation;

    private Workbook workbook;
    private Sheet sheet;


    public Generator() throws IOException, InvalidFormatException {
        this.generatedDataRepresentation = new HashMap<>();
        this.readPredefinedCodes();
    }

    public List<String[]> generateRecordForUser(int user_id, int amount_of_attributes) {
        int amountOfRecordsInSheet = this.sheet.getPhysicalNumberOfRows();
        this.generatedDataRepresentation = new HashMap<>();
        Random random = new Random();
        String key;
        String value;
        List<String []> generatedListOfUserData = new ArrayList<>();
        int specificTempCounter = 0;
        int counter = 0;
        for (int i = 1; i <= amount_of_attributes; i++){
            boolean isSpecific = false;
            counter++;
            if (1 != specificTempCounter){
                isSpecific = random.nextBoolean();
            }
            key = "User_" + user_id + "_" + this.sheet.getRow(i).getCell(0).getStringCellValue();
            value = sheet.getRow(random.nextInt(amountOfRecordsInSheet)).getCell(1).getStringCellValue();

            if (isSpecific){
                key = "User_" + user_id + "_00/0000";
                value = "385";
                specificTempCounter++;
            }

            generatedListOfUserData.add(new String [] {key, value});
            this.generatedDataRepresentation.put(key, value);
        }
        return generatedListOfUserData;
    }

    private void readPredefinedCodes() throws IOException, InvalidFormatException {
        this.workbook = WorkbookFactory.create(new File(XLS_FILE_PATH));
        this.sheet = workbook.getSheetAt(0);
    }

    public Map<String, String> getGeneratedDataRepresentation() {
        return generatedDataRepresentation;
    }
}
