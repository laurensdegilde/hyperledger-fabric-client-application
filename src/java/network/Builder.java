package network;

import domain.UserWrapper;
import org.hyperledger.fabric.sdk.Channel;
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
        UserWrapper orgAdmin = new UserWrapper();
        Enrollment enrollment = null;
        
        try {
            HFCAClient hfcaClient = HFCAClient.createNewInstance(specification.getOrgProperties()[2], null);
            hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            enrollment = hfcaClient.enroll(adminUsername, adminPassword);
        } catch (Exception ee) {
            ee.printStackTrace();
            return null;
        }
        
        orgAdmin.setEnrollment(enrollment);
        orgAdmin.setMspId(specification.getOrgProperties()[0]);
        orgAdmin.setName(specification.getOrgProperties()[1]);
        FabricClient client = new FabricClient(orgAdmin);
        return client;
    }
    
    
    ChannelClient createChannelClient(String channelName, FabricClient client) throws InvalidArgumentException, TransactionException {
        Channel channel = client.getInstance().newChannel(channelName);
        ChannelClient channelClient = new ChannelClient(channelName, channel);
        channelClient.getChannel().addOrderer(client.getInstance().newOrderer(specification.getOrdererProperties()[0], specification.getOrdererProperties()[1], null));
        channelClient.getChannel().addPeer(client.getInstance().newPeer(specification.getOrg1Properties()[3], specification.getOrg1Properties()[4], null));
        channelClient.getChannel().addPeer(client.getInstance().newPeer(specification.getOrg2Properties()[3], specification.getOrg2Properties()[4], null));
        channelClient.getChannel().initialize();
        return channelClient;
    }
}
