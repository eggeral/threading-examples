package software.egger;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class bo_atomic {
    static volatile int shared = 0;
    static AtomicInteger atomicShared = new AtomicInteger(0);

    @Test
    public void atomicDataTypesSupportAtomicOperations() throws InterruptedException {
        Runnable adder = () -> {
            for (int i = 0; i < 10_000; i++) {
                shared++;
            }
        };
        runInTwoThreads(adder);
        // This is not 20000!
        System.out.println(shared);

        Runnable adderWithAtomic = () -> {
            for (int i = 0; i < 10_000; i++) {
                atomicShared.incrementAndGet();
            }
        };
        runInTwoThreads(adderWithAtomic);
        // This is 20000!
        System.out.println(atomicShared);

        atomicShared.set(0);
        // A more complex situation!
        Runnable notSoAtomic = () -> {
            for (int i = 0; i < 10_000; i++) {
                int oldValue = atomicShared.get();
                atomicShared.set(++oldValue);
            }
        };
        runInTwoThreads(notSoAtomic);
        // This is 20000!
        System.out.println(atomicShared);

        atomicShared.set(0);
        Runnable atomicAgain = () -> {
            for (int i = 0; i < 10_000; i++) {
                int oldValue;
                int newValue;
                do {
                    oldValue = atomicShared.get();
                    newValue = oldValue + 1;
                } while (!atomicShared.compareAndSet(oldValue, newValue));
            }
        };
        runInTwoThreads(atomicAgain);
        // This is 20000!
        System.out.println(atomicShared);

        // Two new methods help avoid the loop above
        atomicShared.set(0);
        Runnable updateAndGet = () -> {
            for (int i = 0; i < 10_000; i++) {
                atomicShared.updateAndGet(x -> x + 1);
            }
        };
        runInTwoThreads(updateAndGet);
        // This is 20000!
        System.out.println(atomicShared);

        atomicShared.set(0);
        Runnable accumulateAndGet = () -> {
            for (int i = 0; i < 10_000; i++) {
                atomicShared.accumulateAndGet(1, (prev, next) -> prev + next);
            }
        };
        runInTwoThreads(accumulateAndGet);
        // This is 20000!
        System.out.println(atomicShared);

    }

    private static void runInTwoThreads(Runnable runnable) throws InterruptedException {
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
