package domain;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProposalWrapper {
    
    private SituationType situationType;
    private MethodType methodType;
    private JsonObject jsonResponse;
    private ProposalResponse proposalResponse;
    
    
    public ProposalWrapper(String situationType, String methodType, ProposalResponse response) {
        this.situationType = SituationType.fromString(situationType);
        this.methodType = MethodType.fromString(methodType);
        this.proposalResponse = response;
    }
    
    @Override
    public String toString() {
        return "Situation: " + this.situationType +
                " method: " + this.methodType +
                " peer: " + this.proposalResponse.getPeer().getName() +
                " status: " + this.proposalResponse.getStatus();
    }
    
    public void setAdditionalJSONProperties(boolean valid, long startStepA, long endStepA, long endStepE, String situationType, String methodType) {
        JsonParser parser = new JsonParser();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.jsonResponse.addProperty("TransactionId", this.proposalResponse.getTransactionID());
        this.jsonResponse.addProperty("Peer", this.proposalResponse.getPeer().getName());
        this.jsonResponse.addProperty("StartTransaction", startStepA);
        this.jsonResponse.addProperty("EndTransaction", endStepA);
        this.jsonResponse.addProperty("ConsensusReached", endStepE);
        this.jsonResponse.addProperty("Situation", situationType);
        this.jsonResponse.addProperty("Method", methodType);
        this.jsonResponse.addProperty("Time", dtf.format(now));
        this.jsonResponse.addProperty("Valid", String.valueOf(valid));

        try{
            this.jsonResponse = (JsonObject) parser.parse(new String(this.proposalResponse.getChaincodeActionResponsePayload()));
        }catch(InvalidArgumentException e){
            System.out.println("Invalid transaction.");
        }
        
    }
    public JsonObject getJsonResponse() {
        return jsonResponse;
    }
}
