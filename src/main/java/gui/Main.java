package gui;


import client.ChannelClient;
import client.FabricClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.Builder;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.*;
import specification.AlphaNetworkSpecification;
import specification.NetworkSpecification;
import util.Util;

import javax.management.Query;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IllegalAccessException, InvalidArgumentException, InstantiationException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, TransactionException, ProposalException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Main.fxml"));
        primaryStage.setTitle("QL Tool");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
