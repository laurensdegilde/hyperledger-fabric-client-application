package controller;

import com.google.protobuf.InvalidProtocolBufferException;
import concurrency.ConcurrencyService;
import domain.ProposalWrapper;
import domain.ProposalWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import network.NetworkExposure;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class InteractController {
    @FXML
    private ListView<String> lvArguments;
    @FXML
    private TextField tfArgument;
    @FXML
    private ComboBox cbChaincode;
    @FXML
    private ComboBox cbChaincodeMethod;
    @FXML
    private ListView<String> lvInvokeOverview;
    @FXML
    private TextField tfInvokeAmountOfTime;
    
    private ConcurrencyService concurrencyService;
    @FXML
    void initialize() throws IOException, InvalidFormatException {
        this.cbChaincode.setItems(FXCollections.observableArrayList(
                NetworkExposure.getSpecification().getChannelProperties()[1],
                NetworkExposure.getSpecification().getChannelProperties()[2]
        ));
        this.cbChaincodeMethod.setItems(FXCollections.observableArrayList(
                NetworkExposure.getSpecification().getChannelMethodProperties()[0],
                NetworkExposure.getSpecification().getChannelMethodProperties()[1]
        ));

        cbChaincode.getSelectionModel().select(0);
        cbChaincodeMethod.getSelectionModel().select(0);

        concurrencyService = new ConcurrencyService(1);
    }
    
    public void invokeChaincode() {
        String chaincode = cbChaincode.getSelectionModel().getSelectedItem().toString();
        String chaincodeMethod = cbChaincodeMethod.getSelectionModel().getSelectedItem().toString();
        String [] keyValueSet = lvArguments.getItems().toArray(new String[lvArguments.getItems().size()]);
        for (int i = 0; i < Integer.valueOf(tfInvokeAmountOfTime.getText()); i++) {
            this.concurrencyService.invoke(chaincode, chaincodeMethod, keyValueSet);
        }
        this.concurrencyService.handleConcurrency();
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
