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
    private Map<String, Double> generatedDataRepresentation;

    private Workbook workbook;
    private Sheet sheet;


    public Generator() throws IOException, InvalidFormatException {
        this.generatedDataRepresentation = new HashMap<>();
        this.readPredefinedCodes();
    }

    public List<String[]> generateRecordForUser(int user_id, int amount_of_attributes) {
        int amountOfRecordsInSheet = this.sheet.getPhysicalNumberOfRows();
        Random random = new Random();
        String key;
        double value;
        List<String []> generatedListOfUserData = new ArrayList<>();
        int specificTempCounter = 0;
        boolean isSpecific = false;
        for (int i = 1; i <= amount_of_attributes; i++){
            isSpecific = false;
            if (1 != specificTempCounter){
                isSpecific = random.nextBoolean();
            }
            key = String.format ("%.0f",this.sheet.getRow(user_id).getCell(0).getNumericCellValue()) + "-" + this.sheet.getRow(i).getCell(1).getStringCellValue();
            value = sheet.getRow(random.nextInt(amountOfRecordsInSheet)).getCell(2).getNumericCellValue();

            if (isSpecific){
                key = String.format ("%.0f",this.sheet.getRow(user_id).getCell(0).getNumericCellValue()) + "-" + "eigen_risico";
                value = 385;
                specificTempCounter++;
            }

            generatedListOfUserData.add(new String [] {key, Double.toString(value)});
            this.generatedDataRepresentation.put(key, value);
        }
        if (!isSpecific){
            key = String.format ("%.0f",this.sheet.getRow(user_id).getCell(0).getNumericCellValue()) + "-" + "eigen_risico";
            value = 385;
            generatedListOfUserData.remove(generatedListOfUserData.size() - 1);
            generatedListOfUserData.add(new String [] {key, Double.toString(value)});
        }
        return generatedListOfUserData;
    }

    private void readPredefinedCodes() throws IOException, InvalidFormatException {
        this.workbook = WorkbookFactory.create(new File(XLS_FILE_PATH));
        this.sheet = workbook.getSheetAt(0);
    }

    public Map<String, Double> getGeneratedDataRepresentation() {
        return generatedDataRepresentation;
    }
}
