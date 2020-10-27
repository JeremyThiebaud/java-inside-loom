package fr.umlv.javainside;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {
    @Test
    public void testSTACK(){
        var scope = new ContinuationScope("scope");
        var scheduler = new Scheduler(Scheduler.EXEC_POLICY.STACK);
        var str = new StringBuilder();
        var continuation1 = new Continuation(scope, () -> {
            str.append("start 1\n");
            scheduler.enqueue(scope);
            str.append("middle 1\n");
            scheduler.enqueue(scope);
            str.append("end 1\n");
        });
        var continuation2 = new Continuation(scope, () -> {
            str.append("start 2\n");
            scheduler.enqueue(scope);
            str.append("middle 2\n");
            scheduler.enqueue(scope);
            str.append("end 2\n");
        });
        var list = List.of(continuation1, continuation2);
        list.forEach(Continuation::run);
        scheduler.runLoop();
        assertEquals("start 1\nstart 2\nmiddle 1\nmiddle 2\nend 1\nend 2\n", str.toString());
    }

    @Test
    public void testFIFO(){
        var scope = new ContinuationScope("scope");
        var scheduler = new Scheduler(Scheduler.EXEC_POLICY.FIFO);
        var str = new StringBuilder();
        var continuation1 = new Continuation(scope, () -> {
            str.append("start 1\n");
            scheduler.enqueue(scope);
            str.append("middle 1\n");
            scheduler.enqueue(scope);
            str.append("end 1\n");
        });
        var continuation2 = new Continuation(scope, () -> {
            str.append("start 2\n");
            scheduler.enqueue(scope);
            str.append("middle 2\n");
            scheduler.enqueue(scope);
            str.append("end 2\n");
        });
        var list = List.of(continuation1, continuation2);
        list.forEach(Continuation::run);
        scheduler.runLoop();
        assertEquals("start 1\nstart 2\nmiddle 2\nend 2\nmiddle 1\nend 1\n", str.toString());
    }

    @Test
    public void testRANDOM(){
        var scope = new ContinuationScope("scope");
        var scheduler = new Scheduler(Scheduler.EXEC_POLICY.RANDOM);
        var str = new StringBuilder();
        var continuation1 = new Continuation(scope, () -> {
            str.append("start 1\n");
            scheduler.enqueue(scope);
            str.append("middle 1\n");
            scheduler.enqueue(scope);
            str.append("end 1\n");
        });
        var continuation2 = new Continuation(scope, () -> {
            str.append("start 2\n");
            scheduler.enqueue(scope);
            str.append("middle 2\n");
            scheduler.enqueue(scope);
            str.append("end 2\n");
        });
        var list = List.of(continuation1, continuation2);
        list.forEach(Continuation::run);
        scheduler.runLoop();
        assertTrue(true);
    }
}