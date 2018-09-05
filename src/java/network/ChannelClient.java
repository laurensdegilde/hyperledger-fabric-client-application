package network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.InvalidProtocolBufferException;
import domain.TransactionWrapper;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

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
    
    public List<TransactionWrapper> invokeChainCode(String situationType, String methodType, TransactionProposalRequest request) throws ProposalException, InvalidArgumentException, ExecutionException, InterruptedException {
        
        long startStepA = System.currentTimeMillis();
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(request, channel.getPeers());
        long endStepA = System.currentTimeMillis();
        channel.sendTransaction(responses).get();
        long endStepE = System.currentTimeMillis();

        List<TransactionWrapper> temp = new ArrayList<>();
        JsonObject json = null;
        for (ProposalResponse pr : responses) {
            try {
                json = this.toJson(pr, startStepA, endStepA, endStepE, situationType, methodType);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
            temp.add(new TransactionWrapper(situationType, methodType, json, pr));
        }
        System.out.println("Thread: " + Thread.currentThread().getId() + " is done in: " + (endStepE - endStepA));
        return temp;
    }
    
    
    private JsonObject toJson(ProposalResponse pr, long startStepA, long endStepA, long endStepE, String situationType, String methodType) throws InvalidArgumentException {
        JsonParser parser = new JsonParser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        JsonObject json = (JsonObject) parser.parse(new String(pr.getChaincodeActionResponsePayload()));
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
