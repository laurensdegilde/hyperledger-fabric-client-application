package concurrency;

import domain.ProposalWrapper;
import domain.ProposalWriter;
import javafx.application.Platform;
import network.NetworkExposure;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

import java.io.IOException;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrencyService {
    
    private ExecutorService executor;
    private static ProposalWriter proposalWriter;
    private CountDownLatch countDownLatch;
    private int threadCount;
    public ConcurrencyService(int threadCount) {
        this.threadCount = threadCount;
        executor = getExecutorService(threadCount);
    }
    
    public void invoke(String chaincode, String chaincodeMethod, String []keyValueSet) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            try {
                TransactionProposalRequest tpr = NetworkExposure.getFabricClient().createTransactionProposalRequest(
                        chaincode,
                        chaincodeMethod,
                        keyValueSet
                );
                List<ProposalWrapper> responses = NetworkExposure.getChannelClient().invokeChainCode(
                        chaincode,
                        chaincodeMethod,
                        tpr
                );
                return responses;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        },this.executor).thenAccept(responses -> {
            getProposalWriterInstance().write(responses);
            this.countDownLatch.countDown();
        });
    }
    
    private ExecutorService getExecutorService(int threadCount){
        ExecutorService executor = Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
            int count = 1;

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "custom-executor-" + count++);
            }
        });
        return executor;
    }
    
    public void handleConcurrency(){
        this.countDownLatch = new CountDownLatch(threadCount);
        try {
            getProposalWriterInstance().open();
            this.getCountDownLatch().await(15, TimeUnit.SECONDS);
            getProposalWriterInstance().persist();
            getProposalWriterInstance().clean();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
    
    }
    
    public static ProposalWriter getProposalWriterInstance() {
        if(proposalWriter == null){
            proposalWriter = new ProposalWriter();
        }
        return proposalWriter;
    }
    
    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }
}
