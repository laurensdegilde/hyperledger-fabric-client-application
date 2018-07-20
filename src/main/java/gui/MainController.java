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
    //Build tab
    @FXML
    private Label lbBuildStatus;
    @FXML
    private TextField txAdminUsername;
    @FXML
    private TextField txAdminPassword;
    @FXML
    private ComboBox cbNetworks;

    //Query tab
    @FXML
    private Tab tabQuery;
    @FXML
    private Label lbQueryResponse;

    //Invoke tab
    @FXML
    private Tab tabInvoke;
    @FXML
    private ListView<String> lvArguments;
    @FXML
    private TextField tfArgument;
    @FXML
    private TextField tfChaincodeName;
    @FXML
    private TextField tfChaincodeFunctionName;
    @FXML
    private Label lbInvokeStatus;
    @FXML
    private Button btDeleteArgument;

    private final String COlOUR_WARNING = "#f44242";

    private final String COLOUR_SUCCESS = "#17b25a";
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
            this.setStatusLabel(lbBuildStatus, this.COlOUR_WARNING, "Network connection failed.");
            return;
        }
        channelClient = builder.constructChannelClient("mychannel", client);

        this.setStatusLabel(lbBuildStatus, this.COLOUR_SUCCESS, "Network connection successful.");
    }
    private void setStatusLabel(Label label, String colorCode, String message){
        label.setText(message);
        label.setTextFill(Color.web(colorCode));
        tabInvoke.disableProperty().set(!(client != null));
        tabQuery.disableProperty().set(!(client != null));
    }

    public void invokeChaincode() throws InvalidArgumentException, ProposalException {
        TransactionProposalRequest tpr = builder.constructTPR(tfChaincodeName.getText(), tfChaincodeFunctionName.getText(),lvArguments.getItems().toArray(new String[lvArguments.getItems().size()]), client);
        Collection<ProposalResponse> invokeResponse = channelClient.invokeChainCode(tpr);
        if(invokeResponse == null){
            this.setStatusLabel(lbInvokeStatus, this.COlOUR_WARNING, "Invocation failed.");
            return;
        }
        this.setStatusLabel(lbInvokeStatus, this.COLOUR_SUCCESS, "Invocation successful.");
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
        if (!tfArgument.getText().equals("")){
            lvArguments.getItems().add(tfArgument.getText().toString());
        }
    }

    public void deleteArgument(){
        lvArguments.getItems().remove(lvArguments.getSelectionModel().getSelectedItem());
    }
}
