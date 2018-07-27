package network;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FabricClient {

	private HFClient instance;

	public HFClient getInstance() {
		return instance;
	}

	public FabricClient(User context) throws CryptoException, InvalidArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		instance = HFClient.createNewInstance();
		instance.setCryptoSuite(cryptoSuite);
		instance.setUserContext(context);
	}

	public ChannelClient createChannelClient(String name) throws InvalidArgumentException {
		Channel channel = instance.newChannel(name);
		ChannelClient client = new ChannelClient(name, channel, this);
		return client;
	}

	public TransactionProposalRequest constructTPR(String chaincodeName, String chaincodeMethod, String[] args){
		TransactionProposalRequest req = this.getInstance().newTransactionProposalRequest();
		ChaincodeID cid = ChaincodeID.newBuilder().setName(chaincodeName).build();
		req.setChaincodeID(cid);
		req.setFcn(chaincodeMethod);
		req.setArgs(args);
		return req;
	}

}
