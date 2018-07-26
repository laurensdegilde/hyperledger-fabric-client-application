package network;

import domain.TransactionType;
import domain.TransactionWrapper;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	public List<TransactionWrapper> queryChainCode(QueryByChaincodeRequest req) throws InvalidArgumentException, ProposalException {
		long startTime = System.currentTimeMillis();
		Collection<ProposalResponse> responses = channel.queryByChaincode(req);
		List<TransactionWrapper> temp = new ArrayList<>();
		long endTime = System.currentTimeMillis();

		for (ProposalResponse pr : responses){
			temp.add(new TransactionWrapper(TransactionType.QUERY, endTime - startTime, pr));
		}

		return temp;
	}

	public List<TransactionWrapper> invokeChainCode(TransactionProposalRequest request)	throws ProposalException, InvalidArgumentException {
	    long startTime = System.currentTimeMillis();

	    Collection<ProposalResponse> responses = channel.sendTransactionProposal(request, channel.getPeers());
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
        long endTime = System.currentTimeMillis();

        List<TransactionWrapper> temp = new ArrayList<>();
        for (ProposalResponse pr : responses){
			System.out.println(pr.getTransactionID());
            temp.add(new TransactionWrapper(TransactionType.INVOKE,endTime - startTime, pr));
        }

        return temp;
	}

	public TransactionInfo queryByTransactionId(String txnId) throws ProposalException, InvalidArgumentException {
		Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
				"Querying by trasaction id " + txnId + " on channel " + channel.getName());
		Collection<Peer> peers = channel.getPeers();
		TransactionInfo info = null;
		for (Peer peer : peers) {
			info = channel.queryTransactionByID(peer, txnId);

		}
		return info;
	}

}
