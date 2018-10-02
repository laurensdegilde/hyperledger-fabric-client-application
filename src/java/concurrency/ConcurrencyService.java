package concurrency;

import domain.ExcelHandle;
import domain.ProposalWrapper;
import network.NetworkExposure;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

import java.util.List;
import java.util.concurrent.*;

public class ConcurrencyService {
    
    private ExecutorService executor;
    private CountDownLatch countDownLatch;
    private String chaincode;
    private String step;
    private int threadCount;

    public ConcurrencyService(int threadCount, String chaincode, String step) {
        this.threadCount = threadCount;
        this.executor = getExecutorService(threadCount);
        this.chaincode = chaincode;
        this.step = step;
        ExcelHandle.open(chaincode, step);
    }
    
    public void invoke(String chaincode, String chaincodeMethod, String [] arguments) {
        CompletableFuture.supplyAsync(() -> {
            try {
                TransactionProposalRequest tpr = NetworkExposure.getFabricClient().createTransactionProposalRequest(
                        chaincode,
                        chaincodeMethod,
                        60000,
                        arguments
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
            if(responses != null){
                ExcelHandle.write(responses);
            }
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
        this.countDownLatch = new CountDownLatch(this.threadCount);
        try {
            this.getCountDownLatch().await(20, TimeUnit.SECONDS);
            ExcelHandle.persist();
            ExcelHandle.open(chaincode, step);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
    
    }
    
    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }
}
