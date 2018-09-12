package network;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import domain.ProposalWrapper;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    
    public List<ProposalWrapper> invokeChainCode(String situationType, String methodType, TransactionProposalRequest request) throws InvalidArgumentException {
        long startStepA;
        long endStepA;
        long endStepE;
        Collection<ProposalResponse> responses;
        List<ProposalWrapper> proposalWrappers;
        ProposalWrapper proposalWrapper;

        try {
            startStepA = System.currentTimeMillis();
            responses = channel.sendTransactionProposal(request, channel.getPeers());
            endStepA = System.currentTimeMillis();
            Collection<ProposalResponse> cFirstResponse = Collections.singletonList(Iterables.get(responses, 0));
            ProposalResponse firstResponse = cFirstResponse.iterator().next();
            if (!firstResponse.isInvalid()){
                channel.sendTransaction(cFirstResponse).get();
                endStepE = System.currentTimeMillis();
    
                proposalWrappers = new ArrayList<>();
                for (ProposalResponse pr : responses) {
                    proposalWrapper = new ProposalWrapper(situationType, methodType, pr);
                    proposalWrapper.setAdditionalJSONProperties(!pr.isInvalid(), startStepA, endStepA, endStepE, situationType, methodType);
                    proposalWrappers.add(proposalWrapper);
                }
                return proposalWrappers;
            }
    
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
