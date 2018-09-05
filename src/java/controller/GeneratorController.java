package controller;

import com.google.protobuf.InvalidProtocolBufferException;
import domain.TransactionWrapper;
import domain.TransactionWriter;
import generator.Generator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GeneratorController {
    
    @FXML
    public TextField tfAmountOfUsers;
    @FXML
    public TextField tfAmountOfAttributes;
    @FXML
    public ListView lvGenerateOverview;
    @FXML
    public Label lbAverageTimeSpend;
    @FXML
    public ComboBox cbChaincodeName;
    private Generator generator;
    private TransactionWriter transactionWriter;
    
    public GeneratorController() throws IOException, InvalidFormatException {
        this.generator = new Generator();
        this.transactionWriter = new TransactionWriter();
    }
    @FXML
    public void initialize(){
        this.cbChaincodeName.setItems(FXCollections.observableArrayList(
                NetworkExposure.getSpecification().getChannelProperties()[1],
                NetworkExposure.getSpecification().getChannelProperties()[2]
        ));
        cbChaincodeName.getSelectionModel().select(0);
    }
    @FXML
    public void insertPlainTransactions() throws InvalidArgumentException, ProposalException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException, TransactionException, InvalidProtocolBufferException, ExecutionException, InterruptedException {
        String ccName = cbChaincodeName.getSelectionModel().getSelectedItem().toString();
        for (int i = 0; i < Integer.valueOf(tfAmountOfUsers.getText()); i++) {
            for (String[] kv : this.generator.generateRecordForUser(i, Integer.valueOf(tfAmountOfAttributes.getText()))) {
                
                TransactionProposalRequest tpr = NetworkExposure.getFabricClient().createTransactionProposalRequest(
                        ccName,
                        NetworkExposure.getSpecification().getChannelMethodProperties()[1],
                        kv
                );
                CompletableFuture.supplyAsync(()->{
                    try {
                        List<TransactionWrapper> responses = NetworkExposure.getChannelClient().invokeChainCode(ccName, NetworkExposure.getSpecification().getChannelMethodProperties()[1], tpr);
                        return responses;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }).thenAccept( responses ->{
                    Platform.runLater(()->{
                        this.print(responses);
                    });
                });
            }
        }
        
        this.printGeneratedRecordData();
    }
    private void print(List<TransactionWrapper> transactionWrappers) {
        
        for (TransactionWrapper transactionWrapper : transactionWrappers) {
            lvGenerateOverview.getItems().add(lvGenerateOverview.getItems().size() + 1 + " " + transactionWrapper.toString());
            transactionWriter.writeResponseToExcel(transactionWrapper.getJsonResponse());
        }
    }
    
    public void printGeneratedRecordData() {
        for (Map.Entry<String, String> entry : this.generator.getGeneratedDataRepresentation().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
