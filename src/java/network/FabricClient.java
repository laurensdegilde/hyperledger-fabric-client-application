package network;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.lang.reflect.InvocationTargetException;

public class FabricClient {
    
    private HFClient instance;
    
    public FabricClient(User context) throws CryptoException, InvalidArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        instance = HFClient.createNewInstance();
        instance.setCryptoSuite(cryptoSuite);
        instance.setUserContext(context);
        
    }
    
    public HFClient getInstance() {
        return instance;
    }
    
    public TransactionProposalRequest createTransactionProposalRequest(String chaincodeName, String chaincodeMethod, long timeOut, String[] args) {
        TransactionProposalRequest req = this.getInstance().newTransactionProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName(chaincodeName).build();
        req.setChaincodeID(cid);
        req.setFcn(chaincodeMethod);
        req.setProposalWaitTime(timeOut);
        req.setArgs(args);
        return req;
    }
    
}
