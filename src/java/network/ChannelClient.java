package network;

import domain.TransactionWrapper;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChannelClient {
    
    String name;
    Channel channel;
    FabricClient fabClient;
    
    public ChannelClient(String name, Channel channel, FabricClient fabClient) {
        this.name = name;
        this.channel = channel;
        this.fabClient = fabClient;
    }
    
    public Channel getChannel() {
        return channel;
    }
    
    public List<TransactionWrapper> invokeChainCode(String situationType, String methodType, TransactionProposalRequest request) throws ProposalException, InvalidArgumentException {
        long startStepA = System.currentTimeMillis();
        System.out.println(startStepA);
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(request, channel.getPeers());
        long endTime = System.currentTimeMillis();
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
        List<TransactionWrapper> temp = new ArrayList<>();
        for (ProposalResponse pr : responses) {
            System.out.println(pr.getTransactionID());
            System.out.println(pr.getChaincodeActionResponseReadWriteSetInfo().getNsRwsetCount());
            temp.add(new TransactionWrapper(situationType, methodType, endTime - startStepA, pr));
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
