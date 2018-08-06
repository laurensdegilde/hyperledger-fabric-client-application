package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import network.NetworkExposure;
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;


public class LookupController {

    @FXML
    public TextField txID;

    public void lookUp() throws InvalidArgumentException, ProposalException {
        long startTime = System.currentTimeMillis();
        NetworkExposure.channelClient.queryByTransactionId(txID.getText());
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}
