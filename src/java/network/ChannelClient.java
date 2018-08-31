package network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import domain.TransactionWrapper;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import com.google.*;

import javax.json.Json;
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
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(request, channel.getPeers());
        long endStepA = System.currentTimeMillis();
        
        JsonParser parser = new JsonParser();
        
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
        
        List<TransactionWrapper> temp = new ArrayList<>();
        for (ProposalResponse pr : responses) {
            JsonObject json = (JsonObject) parser.parse(new String(pr.getChaincodeActionResponsePayload()));
            json.addProperty("StartTransaction", Long.toString(startStepA));
            json.addProperty("EndTransaction", Long.toString(endStepA));
            json.addProperty("Situation", situationType);
            json.addProperty("Method", methodType);
            json.addProperty("TransactionId", pr.getTransactionID());
            System.out.println(json);
            temp.add(new TransactionWrapper(situationType, methodType, json, pr));
        }
        
        return temp;
    }
}
