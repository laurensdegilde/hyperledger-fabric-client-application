package network;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import domain.TransactionWrapper;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChannelClient {
    
    String name;
    Channel channel;
    
    public ChannelClient(String name, Channel channel) {
        this.name = name;
        this.channel = channel;
    }
    
    public Channel getChannel() {
        return channel;
    }
    
    public List<TransactionWrapper> invokeChainCode(String situationType, String methodType, TransactionProposalRequest request) throws ProposalException, InvalidArgumentException, ExecutionException, InterruptedException {
        
        long startStepA = System.currentTimeMillis();
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(request, channel.getPeers());
        long endStepA = System.currentTimeMillis();
        Collection<ProposalResponse> firstResponse = Collections.singletonList(Iterables.get(responses, 0));
        channel.sendTransaction(firstResponse).get();
        long endStepE = System.currentTimeMillis();
    
        List<TransactionWrapper> temp = new ArrayList<>();
        JsonObject json;

        for (ProposalResponse pr : responses) {
            json = this.toJson(pr, startStepA, endStepA, endStepE, situationType, methodType);
            temp.add(new TransactionWrapper(situationType, methodType, json, pr));
        }
        return temp;
    }
    
    
    private JsonObject toJson(ProposalResponse pr, long startStepA, long endStepA, long endStepE, String situationType, String methodType) throws InvalidArgumentException {
        JsonParser parser = new JsonParser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        JsonObject json = (JsonObject) parser.parse(new String(pr.getChaincodeActionResponsePayload()));
        json.addProperty("TransactionId", pr.getTransactionID());
        json.addProperty("StartTransaction", startStepA);
        json.addProperty("Peer", pr.getPeer().getName());
        json.addProperty("EndTransaction", endStepA);
        json.addProperty("ConsensusReached", endStepE);
        json.addProperty("Situation", situationType);
        json.addProperty("Method", methodType);
        json.addProperty("Time", dtf.format(now));
        return json;
    }
}
