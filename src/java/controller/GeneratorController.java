package controller;

import domain.TransactionWrapper;
import domain.TransactionWriter;
import generator.Generator;
import generator.GeneratorHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import network.NetworkExposure;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    private TransactionWriter transactionWriter;
    
    public GeneratorController() throws IOException, InvalidFormatException {
        this.generatorHelper = new GeneratorHelper();
        this.generator = new Generator();
        this.transactionWriter = new TransactionWriter();
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
    public void insertPlainTransactions() {
        String ccName = cbChaincodeName.getSelectionModel().getSelectedItem().toString();
        String step = cbSteps.getSelectionModel().getSelectedItem().toString();
        Integer[] offsets = this.generatorHelper.getStepOffsets(step);
        
        for (int i = offsets[0]; i < offsets[1]; i++) {
            for (String[] kv : this.generator.generateRecordForUser(i, Integer.valueOf(tfAmountOfAttributes.getText()))) {
    
                CompletableFuture.supplyAsync(()->{
                    try {
                        TransactionProposalRequest tpr = NetworkExposure.getFabricClient().createTransactionProposalRequest(
                                ccName,
                                NetworkExposure.getSpecification().getChannelMethodProperties()[1],
                                kv
                        );
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
            transactionWrapper.getJsonResponse().addProperty("Step", cbSteps.getSelectionModel().getSelectedItem().toString());
            transactionWriter.writeResponseToExcel(transactionWrapper.getJsonResponse());
        }
    }
    
    public void printGeneratedRecordData() {
        for (Map.Entry<String, String> entry : this.generator.getGeneratedDataRepresentation().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
