package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import network.Builder;
import network.NetworkExposure;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import specification.AlphaNetworkSpecification;
import specification.BetaNetworkSpecification;
import specification.PlaygroundNetwork;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class BuildController {

    @FXML
    private Label lbBuildStatus;
    @FXML
    private TextField txAdminUsername;
    @FXML
    private TextField txAdminPassword;
    @FXML
    private ComboBox cbNetworks;

    private final String COlOUR_WARNING = "#f44242";

    private final String COLOUR_SUCCESS = "#17b25a";

    public BuildController(){
        super();
    }


    @FXML
    void initialize() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidArgumentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, EnrollmentException, CryptoException, ClassNotFoundException, TransactionException, ProposalException {
        this.cbNetworks.setItems(FXCollections.observableArrayList(
                new String("Playground network"),
                new String("Alpha network"),
                new String("Beta network")));
        cbNetworks.getSelectionModel().select(0);
        changeNetwork();
    }

    @FXML
    public void changeNetwork() throws InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidArgumentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, EnrollmentException, CryptoException, ClassNotFoundException, TransactionException, ProposalException, IOException {
        String network = cbNetworks.getSelectionModel().getSelectedItem().toString();

        switch (network) {
            case "Playground network":
                NetworkExposure.builder = new Builder(new PlaygroundNetwork());
                break;
            case "Alpha network":
                NetworkExposure.builder = new Builder(new AlphaNetworkSpecification());
                break;
            case "Beta network":
                NetworkExposure.builder = new Builder(new BetaNetworkSpecification());
                break;
            default:
                NetworkExposure.builder = null;
        }
    }

    @FXML
    public boolean connectNetwork() throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, TransactionException, EnrollmentException, InvalidArgumentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, IOException {
        NetworkExposure.fabricClient = NetworkExposure.builder.constructFabricClient(txAdminUsername.getText(), txAdminPassword.getText());
        if (NetworkExposure.fabricClient == null){
            this.setStatusLabel(lbBuildStatus, this.COlOUR_WARNING, "Network connection failed.");
            return false;
        }
        NetworkExposure.channelClient = NetworkExposure.builder.constructChannelClient("mychannel", NetworkExposure.fabricClient);
        this.setStatusLabel(lbBuildStatus, this.COLOUR_SUCCESS, "Network connection successful.");
        return true;
    }

    private void setStatusLabel(Label label, String colorCode, String message){
        label.setText(message);
        label.setTextFill(Color.web(colorCode));
    }

    public void openSearch() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidArgumentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, EnrollmentException, CryptoException, ClassNotFoundException, TransactionException {
        if(connectNetwork()){
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Interact.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Alpha client application");
            stage.setScene(new Scene(root));
            stage.show();
        };
    }
    public void openGenerate() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidArgumentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, EnrollmentException, CryptoException, ClassNotFoundException, TransactionException {
        if(connectNetwork()){
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Generate.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Alpha client application");
            stage.setScene(new Scene(root));
            stage.show();
        };
    }

}
