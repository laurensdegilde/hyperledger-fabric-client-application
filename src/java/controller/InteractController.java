package controller;

import domain.TransactionWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import network.NetworkExposure;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.List;

public class InteractController {
    @FXML
    private ListView<String> lvArguments;
    @FXML
    private TextField tfArgument;
    @FXML
    private TextField tfChaincodeName;
    @FXML
    private TextField tfChaincodeFunctionName;
    @FXML
    private ListView<String> lvInvokeOverview;
    @FXML
    private Label lbAverageTimeSpend;

    @FXML
    private TextField tfInvokeAmountOfTime;

    private long computedTimeSpend = 0;


    public void invokeChaincode() throws ProposalException, InvalidArgumentException {
        TransactionProposalRequest tpr;
        List<TransactionWrapper> response;

        for (int i = 0; i < Integer.valueOf(tfInvokeAmountOfTime.getText()); i++){
            tpr = NetworkExposure.fabricClient.createTransactionProposalRequest(tfChaincodeName.getText(), tfChaincodeFunctionName.getText(),lvArguments.getItems().toArray(new String[lvArguments.getItems().size()]));
            response = NetworkExposure.channelClient.invokeChainCode(tpr);
            this.addInvokeInformation(response);
        }
    }

    private void addInvokeInformation(List<TransactionWrapper> transactionWrappers){
        for (TransactionWrapper transactionWrapper : transactionWrappers) {
            lvInvokeOverview.getItems().add(lvInvokeOverview.getItems().size() + 1 + " " + transactionWrapper.toString());
            computedTimeSpend += transactionWrapper.getExecutionTime();
        }
        lbAverageTimeSpend.setText(String.valueOf(computedTimeSpend / lvInvokeOverview.getItems().size()) + " ms");
    }

    public void addArgument(){
        if (!tfArgument.getText().equals("")){
            lvArguments.getItems().add(tfArgument.getText().toString());
        }
    }

    public void deleteArgument(){
        lvArguments.getItems().remove(lvArguments.getSelectionModel().getSelectedItem());
    }
}
