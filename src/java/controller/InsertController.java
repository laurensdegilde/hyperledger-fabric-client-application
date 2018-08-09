package controller;

import domain.TransactionWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import network.NetworkExposure;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import generator.Generator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class InsertController {

    @FXML
    public TextField tfAmountOfUsers;
    @FXML
    public TextField tfAmountOfAttributes;
    @FXML
    public ListView lvGenerateOverview;
    @FXML
    public Label lbAverageTimeSpend;

    private int computedTimeSpend = 0;

    private Generator generator;

    public InsertController() throws IOException, InvalidFormatException {
        this.generator = new Generator();
    }

    @FXML
    public void insertPlainTransactions() throws InvalidArgumentException, ProposalException {
        List<TransactionWrapper> response = null;

        for (int i = 0; i < Integer.valueOf(tfAmountOfUsers.getText()); i++){
            for (String [] kv: this.generator.generateRecordForUser(i, Integer.valueOf(tfAmountOfAttributes.getText()))){
                TransactionProposalRequest tpr = NetworkExposure.fabricClient.createTransactionProposalRequest(
                        "testkeyvalue-1", "testkeyvalue", kv);
                response = NetworkExposure.channelClient.invokeChainCode(tpr);
                this.addGenerateInformation(response);
            }
        }

        this.printGeneratedRecordData();
    }

    private void addGenerateInformation(List<TransactionWrapper> transactionWrappers){

        for (TransactionWrapper transactionWrapper : transactionWrappers) {
            lvGenerateOverview.getItems().add(lvGenerateOverview.getItems().size() + 1 + " " + transactionWrapper.toString());
            computedTimeSpend += transactionWrapper.getExecutionTime();
        }
        lbAverageTimeSpend.setText(String.valueOf(computedTimeSpend / lvGenerateOverview.getItems().size()) + " ms");
    }

    public void printGeneratedRecordData(){
        for (Map.Entry<String, Double> entry : this.generator.getGeneratedDataRepresentation().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
