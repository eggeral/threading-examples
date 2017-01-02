package software.egger;

import org.junit.Test;

public class ab_starting_a_thread {

    @Test
    public void startingAThread() {
        Thread thread = new Thread(() -> System.out.println("Do something"));
        thread.start(); // not thread.run() !!
    }


    @Test(expected = IllegalThreadStateException.class)
    public void aThreadCanOnlyBeStartedOnce() {
        Thread thread = new Thread(() -> System.out.println("Do something"));
        thread.start();
        thread.start();
    }

}
