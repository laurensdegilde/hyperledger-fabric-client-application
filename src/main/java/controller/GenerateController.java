package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import network.NetworkExposure;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import util.Generator;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class GenerateController {
    @FXML
    public TextField tfRandomRecords;

    @FXML
    public TextField tfSpecificRecords;

    private Generator generator;

    public GenerateController() throws IOException, InvalidFormatException {
        generator = new Generator();
    }

    @FXML
    public void generateTransactions() throws InvalidArgumentException, ProposalException {
        int specificTempCounter = 0;
        Random random = new Random();
        for (int i = 0; i < Integer.valueOf(tfRandomRecords.getText()); i++){
            boolean isSpecific = false;
            if (Integer.valueOf(tfSpecificRecords.getText()) != specificTempCounter){
                isSpecific = random.nextBoolean();
            }

            if (isSpecific){
                specificTempCounter++;
            }
            TransactionProposalRequest tpr = NetworkExposure.builder.constructTPR(
                    "emptycc", "empty", generator.generateRecord(isSpecific), NetworkExposure.client);
            NetworkExposure.channelClient.invokeChainCode(tpr);
        }
        System.out.println("Specifc amount of records has been generated: " + (Integer.valueOf(tfSpecificRecords.getText()) == specificTempCounter));
        this.printGeneratedRecordsStatistics();
    }

    public void printGeneratedRecordsStatistics(){
        for (Map.Entry<String, Integer> entry : this.generator.getHealthInsuredCodes().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        for (Map.Entry<String, Integer> entry : this.generator.getServiceCodes().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
