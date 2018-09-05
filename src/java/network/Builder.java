package network;

import domain.UserWrapper;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import specification.NetworkSpecification;

import java.lang.reflect.InvocationTargetException;

class Builder {
    
    private NetworkSpecification specification;
    
    public Builder(NetworkSpecification specification) {
        this.specification = specification;
    }
    
    FabricClient createFabricClient(String adminUsername, String adminPassword) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException, CryptoException, InvalidArgumentException {
        CryptoSuite.Factory.getCryptoSuite();
        UserWrapper org1Admin = new UserWrapper();
        Enrollment enrollOrg1Admin = null;
        
        try {
            HFCAClient hfcaClient = HFCAClient.createNewInstance(specification.getOrgProperties()[2], null);
            hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            enrollOrg1Admin = hfcaClient.enroll(adminUsername, adminPassword);
        } catch (Exception ee) {
            ee.printStackTrace();
            return null;
        }
        
        org1Admin.setEnrollment(enrollOrg1Admin);
        org1Admin.setMspId(specification.getOrgProperties()[0]);
        org1Admin.setName(specification.getOrgProperties()[1]);
        FabricClient client = new FabricClient(org1Admin);
        return client;
    }
    
    ChannelClient createChannelClient(String channelName, FabricClient client) throws InvalidArgumentException, TransactionException {
        ChannelClient channelClient = client.createChannelClient(channelName);
        channelClient.getChannel().addOrderer(client.getInstance().newOrderer(specification.getOrdererProperties()[0], specification.getOrdererProperties()[1], null));
        channelClient.getChannel().addPeer(client.getInstance().newPeer(specification.getOrgProperties()[3], specification.getOrgProperties()[4], null));
        channelClient.getChannel().initialize();
        return channelClient;
    }
}
