package domain;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

public class TransactionWrapper {

    private TransactionType type;
    private long executionTime;
    private ProposalResponse proposalResponses;


    public TransactionWrapper(TransactionType type, long l, ProposalResponse response) {
        this.type = type;
        this.executionTime = l;
        this.proposalResponses = response;
    }

    @Override
    public String toString(){
        try {
            return "Type: " + this.type +
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
