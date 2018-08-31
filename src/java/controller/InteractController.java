package controller;

import domain.TransactionWrapper;
import domain.TransactionWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import network.NetworkExposure;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.IOException;
import java.util.List;

public class InteractController {
    @FXML
    private ListView<String> lvArguments;
    @FXML
    private TextField tfArgument;
    @FXML
    private ComboBox cbChaincodeName;
    @FXML
    private ComboBox cbChaincodeMethodName;
    @FXML
    private ListView<String> lvInvokeOverview;
    @FXML
    private TextField tfInvokeAmountOfTime;
    
    private TransactionWriter transactionWriter;
    
    @FXML
    void initialize() throws IOException, InvalidFormatException {
        this.cbChaincodeName.setItems(FXCollections.observableArrayList(
                NetworkExposure.specification.getChannelProperties()[1],
                NetworkExposure.specification.getChannelProperties()[2]
        ));
        cbChaincodeName.getSelectionModel().select(0);
        this.cbChaincodeMethodName.setItems(FXCollections.observableArrayList(
                NetworkExposure.specification.getChannelMethodProperties()[0],
                NetworkExposure.specification.getChannelMethodProperties()[1]
        ));
        cbChaincodeMethodName.getSelectionModel().select(0);
        transactionWriter = new TransactionWriter();
    }
    
    public void invokeChaincode() throws ProposalException, InvalidArgumentException {
        String ccName = cbChaincodeName.getSelectionModel().getSelectedItem().toString();
        String ccMethodName = cbChaincodeMethodName.getSelectionModel().getSelectedItem().toString();
        TransactionProposalRequest tpr;
        List<TransactionWrapper> response;
        for (int i = 0; i < Integer.valueOf(tfInvokeAmountOfTime.getText()); i++) {
            tpr = NetworkExposure.fabricClient.createTransactionProposalRequest(ccName, ccMethodName, lvArguments.getItems().toArray(new String[lvArguments.getItems().size()]));
            response = NetworkExposure.channelClient.invokeChainCode(ccName, ccMethodName, tpr);
            
            this.addInvokeInformation(response);
        }
    }
    
    private void addInvokeInformation(List<TransactionWrapper> transactionWrappers) {
        for (TransactionWrapper transactionWrapper : transactionWrappers) {
            lvInvokeOverview.getItems().add(lvInvokeOverview.getItems().size() + 1 + " " + transactionWrapper.toString());
            transactionWriter.writeResponseToExcel(transactionWrapper.getJsonResponse());
        }
    }
    
    public void addArgument() {
        if (!tfArgument.getText().equals("")) {
            lvArguments.getItems().add(tfArgument.getText().toString());
        }
    }
    
    public void deleteArgument() {
        lvArguments.getItems().remove(lvArguments.getSelectionModel().getSelectedItem());
    }
}
