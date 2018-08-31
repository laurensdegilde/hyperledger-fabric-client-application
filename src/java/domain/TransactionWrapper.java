package domain;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

public class TransactionWrapper {
    
    private SituationType situationType;
    private MethodType methodType;
    private long executionTime;
    private ProposalResponse proposalResponses;
    
    
    public TransactionWrapper(String situationType, String methodType, long executionTime, ProposalResponse response) {
        this.situationType = SituationType.fromString(situationType);
        this.methodType = MethodType.fromString(methodType);
        this.executionTime = executionTime;
        this.proposalResponses = response;
    }
    
    @Override
    public String toString() {
        try {
            return "Situation: " + this.situationType +
                    " method: " + this.methodType +
                    " peer: " + this.proposalResponses.getPeer().getName() +
                    " status: " + this.proposalResponses.getStatus() +
                    " execution time: " + this.executionTime +
                    " response: " + new String(this.proposalResponses.getChaincodeActionResponsePayload());
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
}
