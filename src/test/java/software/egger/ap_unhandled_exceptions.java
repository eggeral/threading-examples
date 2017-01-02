package software.egger;

import org.junit.Test;

public class ap_unhandled_exceptions {

    @Test
    public void throwingAnExceptionInAThread() throws InterruptedException {

        Thread t = new Thread(() -> {
            // Exception is handled by standard default handle which prints the stack trace to std out.
            throw new IllegalStateException("KABOOM");
        });

        t.start();
        t.join();

    }


    @Test
    public void settingAJvmDefaultExceptionHandler() throws InterruptedException {

        Thread.setDefaultUncaughtExceptionHandler((thread, example) -> {
            System.out.println("Global Thread: Thread: " + thread.getName() + ", exception: " + example.getMessage());
        });

        Thread t = new Thread(() -> {
            throw new IllegalStateException("KABOOM");
        });

        t.start();
        t.join();

    }

    @Test
    public void settingAJThreadExceptionHandler() throws InterruptedException {

        Thread.setDefaultUncaughtExceptionHandler((thread, example) -> {
            System.out.println("Global Thread: Thread: " + thread.getName() + ", exception: " + example.getMessage());
        });

        Thread t = new Thread(() -> {
            throw new IllegalStateException("KABOOM");
        });

        t.setUncaughtExceptionHandler((thread, example) -> {
            System.out.println("Local Handler: Thread: " + thread.getName() + ", exception: " + example.getMessage());
        });

        t.start();
        t.join();

    }

    @Test
    public void throwingExceptionsInExceptionHandlers() throws InterruptedException {

        Thread.setDefaultUncaughtExceptionHandler((thread, example) -> {
            System.out.println("Global Thread: Thread: " + thread.getName() + ", exception: " + example.getMessage());
        });

        Thread t = new Thread(() -> {
            throw new IllegalStateException("KABOOM");
        });

        t.setUncaughtExceptionHandler((thread, example) -> {
            System.out.println("Local Handler: Thread: " + thread.getName() + ", exception: " + example.getMessage());
            throw new IllegalStateException("KABOOM in local handler");
        });

        t.start();
        t.join();

    }

}
