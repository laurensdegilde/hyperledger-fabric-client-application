package controller;

import concurrency.ConcurrencyService;
import domain.ExcelHandle;
import generator.Generator;
import generator.GeneratorHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import network.NetworkExposure;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GeneratorController {
    
    @FXML
    public TextField tfAmountOfAttributes;
    @FXML
    public ListView lvGenerateOverview;
    @FXML
    public ComboBox cbChaincodeName;
    @FXML
    public ComboBox cbSteps;
    
    private Generator generator;
    private GeneratorHelper generatorHelper;
    private ConcurrencyService concurrencyService;
    public GeneratorController() throws IOException, InvalidFormatException {
        this.generatorHelper = new GeneratorHelper();
        this.generator = new Generator();
    }
    @FXML
    public void initialize(){
        this.cbChaincodeName.setItems(FXCollections.observableArrayList(
                NetworkExposure.getSpecification().getChannelProperties()[1],
                NetworkExposure.getSpecification().getChannelProperties()[2]
        ));
        this.cbSteps.setItems((FXCollections.observableArrayList(
                generatorHelper.getSteps().keySet()
        )));
        cbChaincodeName.getSelectionModel().select(0);
        cbSteps.getSelectionModel().select(0);
    }
    @FXML
    public void insertKeys() {
        String chaincode = cbChaincodeName.getSelectionModel().getSelectedItem().toString();
        String step = cbSteps.getSelectionModel().getSelectedItem().toString();
        
        this.concurrencyService = new ConcurrencyService(100, chaincode, step);
        
        Integer[] offsets = this.generatorHelper.getStepOffsets(step);

        for (int i = offsets[0]; i <= offsets[1]; i++) {
            for (String[] keyValueSet : this.generator.generateRecordForUser(i, Integer.valueOf(tfAmountOfAttributes.getText()))) {
                this.concurrencyService.invoke(
                        chaincode,
                        NetworkExposure.getSpecification().getChannelMethodProperties()[1],
                        keyValueSet
                );
            }
            this.concurrencyService.handleConcurrency();
        }
        ExcelHandle.clean();
        this.printGeneratedRecordData();
    }
    
    @FXML
    public void getKeys() throws IOException, InvalidFormatException {
        String chaincode = cbChaincodeName.getSelectionModel().getSelectedItem().toString();
        String step = cbSteps.getSelectionModel().getSelectedItem().toString();
        
        this.concurrencyService = new ConcurrencyService(100, chaincode, step);
        
        List<String []> keyValueSet = ExcelHandle.read(Integer.valueOf(tfAmountOfAttributes.getText()), chaincode, step);
        
        for (String [] key : keyValueSet) {
            System.out.println(key[0]);
            this.concurrencyService.invoke(
                    chaincode,
                    NetworkExposure.getSpecification().getChannelMethodProperties()[0],
                    key
            );
        }
        this.concurrencyService.handleConcurrency();
    }


    public void printGeneratedRecordData() {
        for (Map.Entry<String, String> entry : this.generator.getGeneratedDataRepresentation().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
