package fr.umlv.javainside;

import java.util.ArrayDeque;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;


public class Scheduler {
    public enum EXEC_POLICY{
        FIFO {
            PolicyImpl createPolicyImpl() {
                return new PolicyImpl() {
                    private final ArrayDeque<Continuation> queue = new ArrayDeque<>();

                    @Override
                    public void add(Continuation continuation){
                        queue.offerLast(continuation);
                    }

                    @Override
                    public Continuation remove(){
                        return queue.pollLast();
                    }

                    @Override
                    public boolean isEmpty(){
                        return queue.isEmpty();
                    }
                };
            }
        },
        STACK {
            PolicyImpl createPolicyImpl() {
                return new PolicyImpl() {
                    private final ArrayDeque<Continuation> queue = new ArrayDeque<>();

                    @Override
                    public void add(Continuation continuation){
                        queue.offerLast(continuation);
                    }

                    @Override
                    public Continuation remove(){
                        return queue.pollFirst();
                    }

                    @Override
                    public boolean isEmpty(){
                        return queue.isEmpty();
                    }
                };
            }
        },
        RANDOM {
            PolicyImpl createPolicyImpl() {
                return new PolicyImpl() {
                    private final TreeMap<Integer, ArrayDeque<Continuation>> tree = new TreeMap<>();

                    @Override
                    public void add(Continuation continuation){
                        int random = ThreadLocalRandom.current().nextInt();
                        tree.computeIfAbsent(random, __ -> new ArrayDeque<>()).offer(continuation);
                    }

                    @Override
                    public Continuation remove(){
                        var random = ThreadLocalRandom.current().nextInt();
                        var key = tree.floorKey(random);
                        if(key == null){
                            key = tree.firstKey();
                        }
                        var continuations = tree.get(key);
                        var continuation = continuations.poll();
                        if(continuations.isEmpty()){
                            tree.remove(key);
                        }
                        return continuation;
                    }

                    @Override
                    public boolean isEmpty(){
                        return tree.isEmpty();
                    }
                };
            }
        };

        abstract PolicyImpl createPolicyImpl();
    }

    private interface PolicyImpl {
        void add(Continuation continuation);
        Continuation remove();
        boolean isEmpty();
    }

    private final PolicyImpl policyImpl;

    public Scheduler(EXEC_POLICY policy){
        this.policyImpl = policy.createPolicyImpl();
    }
    public void enqueue(ContinuationScope scope) {
        var cont = Continuation.getCurrentContinuation(scope);
        if(null == cont) {
            throw new IllegalStateException("No continuation running");
        }
        policyImpl.add(cont);

        Continuation.yield(scope);
    }

    public void runLoop(){
        Continuation cont;
        while(!policyImpl.isEmpty()) {
            cont = policyImpl.remove();
            cont.run();
        }
    }
}
