package domain;

import com.google.gson.JsonObject;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

public class TransactionWrapper {
    
    private SituationType situationType;
    private MethodType methodType;
    private JsonObject jsonResponse;
    private ProposalResponse proposalResponses;
    
    
    public TransactionWrapper(String situationType, String methodType, JsonObject jsonResponse, ProposalResponse response) {
        this.situationType = SituationType.fromString(situationType);
        this.methodType = MethodType.fromString(methodType);
        this.jsonResponse = jsonResponse;
        this.proposalResponses = response;
    }
    
    @Override
    public String toString() {
        return "Situation: " + this.situationType +
                " method: " + this.methodType +
                " peer: " + this.proposalResponses.getPeer().getName() +
                " status: " + this.proposalResponses.getStatus();
    }
    
    public JsonObject getJsonResponse() {
        return jsonResponse;
    }
}
