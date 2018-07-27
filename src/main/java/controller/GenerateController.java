package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import util.Generator;

import java.io.IOException;

public class GenerateController {
    @FXML
    public TextField tfRandomRecords;

    @FXML
    public TextField tfSpecificRecords;


    private Generator generator;

    public GenerateController() {
        generator = new Generator();
    }

    @FXML
    public void generateRecords() throws IOException, InvalidFormatException {
        generator.generateRecords(Integer.valueOf(tfRandomRecords.getText()),
                Integer.valueOf(tfSpecificRecords.getText()));
    }
}
