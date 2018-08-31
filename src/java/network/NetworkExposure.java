package network;


import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import specification.NetworkSpecification;

import java.lang.reflect.InvocationTargetException;

public class NetworkExposure {

    public static Builder builder;
    public static FabricClient fabricClient;
    public static ChannelClient channelClient;
    public static NetworkSpecification specification;

    public static void setBuilder(NetworkSpecification ns){
        builder = new Builder(ns);
        specification = ns;
    }

    public static void setFabricClient(String adminUsername, String adminPassword) throws IllegalAccessException, InvalidArgumentException, InstantiationException, CryptoException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        fabricClient = builder.createFabricClient(adminUsername, adminPassword);
    }

    public static void setChannelClient(String channelName, FabricClient client) throws InvalidArgumentException, TransactionException {
        channelClient = builder.createChannelClient(channelName, client);
    }
    public static NetworkSpecification getSpecification(){
        return specification;
    }

}
