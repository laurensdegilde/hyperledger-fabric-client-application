package controller;

import com.google.common.graph.Network;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import network.ChannelClient;
import network.FabricClient;
import network.NetworkExposure;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import specification.LDGNetwork;
import specification.NetworkSpecification;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ConnectController {
    
    private final String COlOUR_WARNING = "#f44242";
    @FXML
    private Label lbBuildStatus;
    @FXML
    private TextField txAdminUsername;
    @FXML
    private PasswordField txAdminPassword;
    @FXML
    private ComboBox cbNetworks;
    private NetworkSpecification ns;
    
    @FXML
    void initialize() {
        this.cbNetworks.setItems(FXCollections.observableArrayList(
                new String("LDG network")));
        cbNetworks.getSelectionModel().select(0);
        changeNetwork();
    }
    
    @FXML
    public void changeNetwork() {
        String network = cbNetworks.getSelectionModel().getSelectedItem().toString();
        ns = null;
        
        switch (network) {
            case "LDG network":
                ns = new LDGNetwork();
                break;
        }
    }
    
    @FXML
    public boolean connectNetwork() throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, TransactionException, InvalidArgumentException {
        if (NetworkExposure.getBuilder(ns) == null || NetworkExposure.getFabricClient() == null || NetworkExposure.getChannelClient() == null) {
            this.setStatusLabel(lbBuildStatus, this.COlOUR_WARNING, "Failed building network artifacts.");
            return false;
        }
        return true;
    }
    
    private void setStatusLabel(Label label, String colorCode, String message) {
        label.setText(message);
        label.setTextFill(Color.web(colorCode));
    }
    
    public void openSearch() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidArgumentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, EnrollmentException, CryptoException, ClassNotFoundException, TransactionException {
        if (connectNetwork()) {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Interact.fxml"));
            this.openStage(root);
        }
    }
    
    public void openGenerate() throws IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InvalidArgumentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, EnrollmentException, CryptoException, ClassNotFoundException, TransactionException {
        if (connectNetwork()) {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Generate.fxml"));
            this.openStage(root);
        }
        ;
    }
    
    private void openStage(Parent root) {
        Stage stage = new Stage();
        stage.setTitle("Alpha client application");
        stage.setScene(new Scene(root));
        stage.show();
    }
    
}
