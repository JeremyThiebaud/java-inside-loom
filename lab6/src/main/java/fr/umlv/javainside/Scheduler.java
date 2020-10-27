package fr.umlv.javainside;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Scheduler {
    public enum EXEC_POLICY{
        STACK,
        FIFO,
        RANDOM
    }

    private final ArrayList<Continuation> list = new ArrayList<>();
    private final EXEC_POLICY policy;

    public Scheduler(EXEC_POLICY policy){
        this.policy = policy;
    }
    public void enqueue(ContinuationScope scope) {
        var cont = Continuation.getCurrentContinuation(scope);
        if(null == cont) {
            throw new IllegalStateException("No continuation running");
        }
        list.add(cont);
        Continuation.yield(scope);
    }

    public void runLoop(){
        Continuation cont;
        while(!list.isEmpty()) {
            switch (policy){
                case FIFO -> cont = list.get(0);
                case STACK -> cont = list.get(list.size()-1);
                case RANDOM -> cont = list.get(
                        ThreadLocalRandom.current().nextInt(0, list.size()));
                default -> throw new IllegalArgumentException("Unknown Scheduler policy");
            }
            if(cont.isDone()){
                list.remove(cont);
            } else {
                cont.run();
            }
        }
    }
}
