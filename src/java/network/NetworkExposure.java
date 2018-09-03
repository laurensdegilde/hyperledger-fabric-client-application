package network;


import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import specification.NetworkSpecification;

import java.lang.reflect.InvocationTargetException;

public class NetworkExposure {
    
    private static Builder builder;
    private static FabricClient fabricClient;
    private static ChannelClient channelClient;
    private static NetworkSpecification specification;
    
    public static Builder getBuilder(NetworkSpecification ns) {
        if (builder == null){
            builder = new Builder(ns);
            specification = ns;
        }
        return builder;
    }
    
    public static FabricClient getFabricClient() throws IllegalAccessException, InvalidArgumentException, InstantiationException, CryptoException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        if (fabricClient == null){
            fabricClient = builder.createFabricClient(specification.getAdminOrg1Properties()[0], specification.getAdminOrg1Properties()[1]);
        }
        return fabricClient;
    }
    
    public static ChannelClient getChannelClient() throws InvalidArgumentException, TransactionException {
        if (channelClient == null){
            channelClient = builder.createChannelClient(specification.getChannelProperties()[0], fabricClient);
        }
        return channelClient;
    }
    
    public static NetworkSpecification getSpecification() {
        return specification;
    }
    
}
