package network;

import client.ChannelClient;
import client.FabricClient;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import specification.NetworkSpecification;
import user.UserContext;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Builder {

    private NetworkSpecification specification;

    public Builder(NetworkSpecification specification){
        this.specification = specification;
    }

    public FabricClient constructFabricClient(String adminUsername, String adminPassword) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException, CryptoException, InvalidArgumentException {
        CryptoSuite.Factory.getCryptoSuite();
        UserContext org1Admin = new UserContext();
        Enrollment enrollOrg1Admin;

        try{
            HFCAClient hfcaClient = HFCAClient.createNewInstance(specification.getCAOrg1URL(),null);
            hfcaClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            enrollOrg1Admin = hfcaClient.enroll(adminUsername, adminPassword);
        }catch(Exception ee){
            return null;
        }

        org1Admin.setEnrollment(enrollOrg1Admin);
        org1Admin.setMspId(specification.getORG1_MSP());
        org1Admin.setName(specification.getOrg1Name());
        FabricClient client = new FabricClient(org1Admin);
        return client;
    }
    public ChannelClient constructChannelClient(String channelName, FabricClient client) throws InvalidArgumentException, TransactionException {
        ChannelClient channelClient = client.createChannelClient(channelName);
        channelClient.getChannel().addOrderer(client.getInstance().newOrderer(specification.getOrdererName(), specification.getOrdererUrl(), this.getOrdererProperties()));
        channelClient.getChannel().addPeer(client.getInstance().newPeer("peer0.org1.ldegilde.com", "grpc://192.168.99.100:7051", this.getPeerProperties("org1.ldegilde.com", "peer0.org1.ldegilde.com")));
        channelClient.getChannel().initialize();
        return channelClient;
    }
    public TransactionProposalRequest constructTPR(String chaincodeName, String chaincodeMethod, String[] args, FabricClient client){
        TransactionProposalRequest req = client.getInstance().newTransactionProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName(chaincodeName).build();
        req.setChaincodeID(cid);
        req.setFcn(chaincodeMethod);
        req.setArgs(args);
        return req;
    }

    public QueryByChaincodeRequest constructQCR(String chaincodeName, String chaincodeMethod, String[] args, FabricClient client){
        QueryByChaincodeRequest req = client.getInstance().newQueryProposalRequest();
        ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincodeName).build();
        req.setChaincodeID(ccid);
        req.setFcn(chaincodeMethod);
        if (args != null)
            req.setArgs(args);
        return req;
    }

    private Properties getOrdererProperties(){
        Properties properties = new Properties();
        properties.setProperty("pemFile", specification.getOrdererBasePath() + File.separator + "server.crt");
        properties.setProperty("trustServerCertificate", "true"); // testing
        properties.setProperty("hostnameOverride", specification.getOrdererName());
        properties.setProperty("sslProvider", "openSSL");
        properties.setProperty("negotiationType", "TLS");
        properties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] { 5L, TimeUnit.MINUTES });
        properties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] { 8L, TimeUnit.SECONDS });
        return properties;
    }


    private Properties getPeerProperties(String orgName, String peerName) {
        Properties properties = new Properties();
        properties.setProperty("pemFile", specification.getBasePath() + File.separator + "peerOrganizations"+ File.separator + orgName + File.separator + "peers" + File.separator + peerName + File.separator + "tls\\server.crt");
        properties.setProperty("trustServerCertificate", "true"); // testing                                                                //                                                              // PRODUCTION!
        properties.setProperty("hostnameOverride", peerName);
        properties.setProperty("sslProvider", "openSSL");
        properties.setProperty("negotiationType", "TLS");
        properties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);
        return properties;
    }
}
