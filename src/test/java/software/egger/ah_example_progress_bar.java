package software.egger;

public class ah_example_progress_bar {

    public static void doWork() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // execute in Terminal! \b does not work in IntelliJ console output.
    // cd build/classes/test
    // java -cp . software.egger.aj_example_progress_bar
    public static void main(String[] args) throws InterruptedException {
        System.out.println("START");
        for (int idx = 0; idx < 10; idx++) {
            System.out.print("=>");
            doWork();
            System.out.print("\b");
        }
        System.out.print(" ");
        System.out.println();
        System.out.println("DONE");

        System.out.println();
        System.out.println("--------------------------");
        System.out.println();

        // but how to play an animation while important work is done.

        Thread thread = new Thread(() -> {
            for (int idx = 0; idx < 10; idx++) {
                System.out.print("=");
                doWork();
            }
        });

        System.out.println("START");
        thread.start();
        while (thread.isAlive()) {  // this is dangerous!
            System.out.print("-\b");
            Thread.sleep(50);
            System.out.print("\\\b");
            Thread.sleep(50);
            System.out.print("|\b");
            Thread.sleep(50);
            System.out.print("/\b");
            Thread.sleep(50);
        }

        System.out.print(" ");
        System.out.println();
        System.out.println("DONE");

        System.out.println();
        System.out.println("--------------------------");
        System.out.println();

    }

}
