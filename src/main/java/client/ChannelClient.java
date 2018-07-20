package client;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ChannelClient {

	String name;
	Channel channel;
	FabricClient fabClient;

	public String getName() {
		return name;
	}

	public Channel getChannel() {
		return channel;
	}

	public FabricClient getFabClient() {
		return fabClient;
	}

	public ChannelClient(String name, Channel channel, FabricClient fabClient) {
		this.name = name;
		this.channel = channel;
		this.fabClient = fabClient;
	}
    public int queryNumberOfBlocks() throws InvalidArgumentException, ProposalException {
        return (int) channel.queryBlockchainInfo().getHeight();

    }
	public Collection<ProposalResponse> queryChainCode(QueryByChaincodeRequest req)
			throws InvalidArgumentException, ProposalException {
		Collection<ProposalResponse> response = channel.queryByChaincode(req);
		return response;
	}

	public Collection<ProposalResponse> invokeChainCode(TransactionProposalRequest request)
			throws ProposalException, InvalidArgumentException {
	    long start = System.currentTimeMillis();

		Collection<ProposalResponse> response = channel.sendTransactionProposal(request, channel.getPeers());
        for (ProposalResponse pres : response) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
            Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
                    "Transaction proposal on channel " + channel.getName() + " " + pres.getMessage() + " "
                            + pres.getStatus() + " with transaction id:" + pres.getTransactionID());
            Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,stringResponse);
        }
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(response);
        Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,cf.toString());

		long end = System.currentTimeMillis();
        System.out.println(end - start);
        return response;
	}

	/**
	 * 
	 * Instantiate chaincode.
	 * 
	 * @param chaincodeName
	 * @param version
	 * @param chaincodePath
	 * @param language
	 * @param functionName
	 * @param functionArgs
	 * @param policyPath
	 * @return
	 * @throws InvalidArgumentException
	 * @throws ProposalException
	 * @throws ChaincodeEndorsementPolicyParseException
	 * @throws IOException
	 */
	public Collection<ProposalResponse> instantiateChainCode(String chaincodeName, String version, String chaincodePath,
			String language, String functionName, String[] functionArgs, String policyPath)
			throws InvalidArgumentException, ProposalException, ChaincodeEndorsementPolicyParseException, IOException {
		Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
				"Instantiate proposal request " + chaincodeName + " on channel " + channel.getName()
						+ " with Fabric client " + fabClient.getInstance().getUserContext().getMspId() + " "
						+ fabClient.getInstance().getUserContext().getName());
		InstantiateProposalRequest instantiateProposalRequest = fabClient.getInstance()
				.newInstantiationProposalRequest();
		instantiateProposalRequest.setProposalWaitTime(180000);
		ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(version)
				.setPath(chaincodePath);
		ChaincodeID ccid = chaincodeIDBuilder.build();
		Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
				"Instantiating Chaincode ID " + chaincodeName + " on channel " + channel.getName());
		instantiateProposalRequest.setChaincodeID(ccid);
		if (language.equals(TransactionRequest.Type.GO_LANG.toString()))
			instantiateProposalRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
		else
			instantiateProposalRequest.setChaincodeLanguage(TransactionRequest.Type.JAVA);

		instantiateProposalRequest.setFcn(functionName);
		instantiateProposalRequest.setArgs(functionArgs);
		Map<String, byte[]> tm = new HashMap<>();
		tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
		tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
		instantiateProposalRequest.setTransientMap(tm);

		if (policyPath != null) {
			ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
			chaincodeEndorsementPolicy.fromYamlFile(new File(policyPath));
			instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
		}

		Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest);
		CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
		
		Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
				"Chaincode " + chaincodeName + " on channel " + channel.getName() + " instantiation " + cf);
		return responses;
	}

	/**
	 * Query a transaction by id.
	 * 
	 * @param txnId
	 * @return
	 * @throws ProposalException
	 * @throws InvalidArgumentException
	 */
	public TransactionInfo queryByTransactionId(String txnId) throws ProposalException, InvalidArgumentException {
		Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
				"Querying by trasaction id " + txnId + " on channel " + channel.getName());
		Collection<Peer> peers = channel.getPeers();
		for (Peer peer : peers) {
			TransactionInfo info = channel.queryTransactionByID(peer, txnId);
			return info;
		}
		return null;
	}

}
