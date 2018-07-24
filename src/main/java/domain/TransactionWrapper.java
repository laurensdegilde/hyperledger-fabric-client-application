package domain;

import org.hyperledger.fabric.sdk.ProposalResponse;

import java.util.Collection;

public class TransactionWrapper {

    private long executionTime;
    private ProposalResponse proposalResponses;


    public TransactionWrapper(long l, ProposalResponse response) {
        this.executionTime = l;
        this.proposalResponses = response;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public String toString(){
        return "Status: " + this.proposalResponses.getStatus() + " execution time: " + this.executionTime + " valid: " + !this.proposalResponses.isInvalid();
    }
}
