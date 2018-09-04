package network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.InvalidProtocolBufferException;
import domain.TransactionWrapper;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.InvalidTransactionException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    
    public synchronized List<TransactionWrapper> invokeChainCode(String situationType, String methodType, TransactionProposalRequest request) throws ProposalException, InvalidArgumentException, InvalidProtocolBufferException, ExecutionException, InterruptedException {
        
        long startStepA = System.currentTimeMillis();
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(request, channel.getPeers());
        long endStepA = System.currentTimeMillis();
    
        CompletableFuture<BlockEvent.TransactionEvent> cf = channel.sendTransaction(responses);
        BlockEvent.TransactionEvent transactionEvent = cf.get();
        System.out.println(transactionEvent.isValid());
        long endStepE = System.currentTimeMillis();
        
        List<TransactionWrapper> temp = new ArrayList<>();
        JsonObject json;
        
        for (ProposalResponse pr : responses) {
            if (pr.isInvalid()){
                throw new ProposalException("Invalid transaction proposal. returning null as transaction wrapper. ");
            }
            json = this.toJson(pr, startStepA, endStepA, endStepE, situationType, methodType);
            temp.add(new TransactionWrapper(situationType, methodType, json, pr));
        }
        
        return temp;
    }
    
    public JsonObject toJson(ProposalResponse pr, long startStepA, long endStepA, long endStepE, String situationType, String methodType) throws InvalidArgumentException {
        JsonParser parser = new JsonParser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        JsonObject json = (JsonObject) parser.parse(new String(pr.getChaincodeActionResponsePayload()));
        json.addProperty("StartTransaction", startStepA);
        json.addProperty("EndTransaction", endStepA);
        json.addProperty("ConsensusReached", endStepE);
        json.addProperty("Situation", situationType);
        json.addProperty("Method", methodType);
        json.addProperty("Time", dtf.format(now));
        return json;
    }
}
