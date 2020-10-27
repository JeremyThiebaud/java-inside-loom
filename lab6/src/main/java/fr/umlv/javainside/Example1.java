package fr.umlv.javainside;

import java.util.List;

public class Example1 {
    public static void main(String[] args) {
        var scope = new ContinuationScope("scope");
        var scheduler = new Scheduler(Scheduler.EXEC_POLICY.FIFO);
        var continuation1 = new Continuation(scope, () -> {
            System.out.println("start 1");
            scheduler.enqueue(scope);
            System.out.println("middle 1");
            scheduler.enqueue(scope);
            System.out.println("end 1");
        });
        var continuation2 = new Continuation(scope, () -> {
            System.out.println("start 2");
            scheduler.enqueue(scope);
            System.out.println("middle 2");
            scheduler.enqueue(scope);
            System.out.println("end 2");
        });
        var list = List.of(continuation1, continuation2);

//        while(!list.stream().allMatch(Continuation::isDone)) {
//            list.forEach((continuation -> {
//                if (!continuation.isDone())
//                    continuation.run();
//            }));
//        }
        list.forEach(Continuation::run);
        scheduler.runLoop();
    }
}
