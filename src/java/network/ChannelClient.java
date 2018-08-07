package network;

import domain.TransactionType;
import domain.TransactionWrapper;
import org.hyperledger.fabric.protos.peer.PeerEvents;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChannelClient {

	String name;
	Channel channel;
	FabricClient fabClient;

	public Channel getChannel() {
		return channel;
	}

	public ChannelClient(String name, Channel channel, FabricClient fabClient) {
		this.name = name;
		this.channel = channel;
		this.fabClient = fabClient;
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