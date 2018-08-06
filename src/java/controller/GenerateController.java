package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import network.NetworkExposure;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import generator.Generator;

import java.io.IOException;
import java.util.Map;

public class GenerateController {

    @FXML
    public TextField tfAmountOfUsers;
    @FXML
    public TextField tfAmountOfAttributes;
    private Generator generator;

    public GenerateController() throws IOException, InvalidFormatException {
        this.generator = new Generator();
    }

    @FXML
    public void generateTransactions() throws InvalidArgumentException, ProposalException {
        for (int i = 0; i < Integer.valueOf(tfAmountOfUsers.getText()); i++){
            for (String [] sa: this.generator.generateRecordForUser(i, Integer.valueOf(tfAmountOfAttributes.getText()))){
                TransactionProposalRequest tpr = NetworkExposure.fabricClient.constructTPR(
                        "testkeyvalue-1", "testkeyvalue", sa);
                NetworkExposure.channelClient.invokeChainCode(tpr);
            }
        }
    }

    public void printGeneratedRecordData(){
        for (Map.Entry<String, String> entry : this.generator.getGeneratedDataRepresentation().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
