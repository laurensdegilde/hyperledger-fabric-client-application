package gui;

import client.ChannelClient;
import client.FabricClient;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import network.Builder;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import specification.AlphaNetworkSpecification;
import specification.BetaNetworkSpecification;
import specification.NetworkSpecification;
import util.Util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainController {
    Builder builder;
    FabricClient client;
    ChannelClient channelClient;
    @FXML
    private Tab tabInvoke;
    @FXML
    private Tab tabQuery;
    @FXML
    private Label lbBuildStatus;
    @FXML
    private TextField txAdminUsername;
    @FXML
    private TextField txAdminPassword;
    @FXML
    private Label lbQueryResponse;
    @FXML
    private ComboBox cbNetworks;
    @FXML
    private ListView<String> lvArguments;
    @FXML
    private TextField tfArgument;
    @FXML
    private TextField tfChaincodeName;
    @FXML
    private TextField tfChaincodeFunctionName;

    @FXML
    void initialize(){
        cbNetworks.setItems(FXCollections.observableArrayList(
                new String("Alpha network"),
                new String("Beta network")));
    }
    @FXML
    public void changeNetwork() {
        String network = cbNetworks.getSelectionModel().getSelectedItem().toString();
        switch (network) {
            case "Alpha network":
                builder = new Builder(new AlphaNetworkSpecification());
                break;
            case "Beta network":
                builder = new Builder(new BetaNetworkSpecification());
                break;
            default:
                builder = null;
        }
    }
    public void connectNetwork() throws IllegalAccessException, InvalidArgumentException, InstantiationException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, TransactionException, EnrollmentException, InvalidArgumentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException {
        client = builder.constructFabricClient(txAdminUsername.getText(), txAdminPassword.getText());
        if (client == null){
            this.setConnectionNetworkStatus("#f44242", "Fabric client build failed");
            return;
        }
        channelClient = builder.constructChannelClient("mychannel", client);

        this.setConnectionNetworkStatus("#17b25a", "Network connection succesfull.");
    }
    private void setConnectionNetworkStatus(String colorCode, String message){
        lbBuildStatus.setText(message);
        lbBuildStatus.setTextFill(Color.web(colorCode));
        tabInvoke.disableProperty().set(!(client != null));
        tabQuery.disableProperty().set(!(client != null));
    }

    public void invokeChaincode() throws InvalidArgumentException, ProposalException {
        TransactionProposalRequest tpr = builder.constructTPR(tfChaincodeName.getText(), tfChaincodeFunctionName.getText(), new String[]{"b", "a", "100"}, client);
        Collection<ProposalResponse> invokeResponse = channelClient.invokeChainCode(tpr);
    }

    public void queryChaincode() throws InvalidArgumentException, InstantiationException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, TransactionException, ProposalException {
        QueryByChaincodeRequest qcr = builder.constructQCR("mycc", "query", new String[]{"a"}, client);
        Collection<ProposalResponse> queryResponse = channelClient.queryChainCode(qcr);
        for (ProposalResponse pres : queryResponse) {
            String stringResponse = new String(
                    pres.getChaincodeActionResponsePayload());
            lbQueryResponse.setText("Query Response from Peer " + pres.getPeer().getName() + ":" +stringResponse);
        }
    }

    public void addArgument(){
        lvArguments.getItems().add(tfArgument.getText().toString());
    }
}
