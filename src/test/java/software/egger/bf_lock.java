package software.egger;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class bf_lock {

    // simple lock usage instead of synchronized.
    static class PrivateLockSynchronizedCounter {
        private int currentValue;

        private Lock lock = new ReentrantLock();

        void inc() {

            lock.lock(); // same as synchronized(object) but locks are more flexible
            try {
                int tmp = currentValue;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentValue = tmp + 1;
            } finally {
                lock.unlock(); // always !! unlock in a finally block!
            }
        }

        private int getCount() {
            return currentValue;
        }
    }

    @Test
    public void usingPrivateSynchronizedCounter() throws InterruptedException {
        PrivateLockSynchronizedCounter counter = new PrivateLockSynchronizedCounter();

        CountDownLatch done = new CountDownLatch(2);

        Runnable worker = () -> {
            for (int idx = 0; idx < 5; idx++) {
                counter.inc();
                System.out.println("Counter: " + counter.getCount());
            }
            done.countDown();
        };

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(worker);
        executorService.submit(worker);

        done.await();
        System.out.println("== Main thread  done");

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.
    }


    @Test
    // can we recover dead locks when using locks?
    public void simpleDeadLock() throws InterruptedException {

        CountDownLatch done = new CountDownLatch(2);

        Lock lock1 = new ReentrantLock();
        Lock lock2 = new ReentrantLock();

        Runnable r1 = () -> {
            try {
                lock1.lockInterruptibly();
                try {
                    System.out.println("r1 got lock1");
                    Thread.sleep(10);
                    System.out.println("r1 tries to get lock2");
                    lock2.lockInterruptibly();
                    try {
                        System.out.println("r1 did something");
                    } finally {
                        lock2.unlock();
                    }
                } finally {
                    lock1.unlock();
                    done.countDown();
                }
            } catch (InterruptedException e) {
                System.out.println("r1 interrupted");
            }
        };

        Runnable r2 = () -> {
            try {
                //lock2.lock(); // locks forever
                lock2.lockInterruptibly();
                try {
                    System.out.println("r2 got lock2");
                    Thread.sleep(10);
                    System.out.println("r2 tries to get lock1");
                    lock1.lockInterruptibly();
                    try {
                        System.out.println("r2 did something");
                    } finally {
                        lock1.unlock();
                    }
                } finally {
                    lock2.unlock();
                    done.countDown();
                }
            } catch (InterruptedException e) {
                System.out.println("r2 interrupted");
            }

        };

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(r1);
        executorService.submit(r2);

        boolean await = done.await(1, TimeUnit.SECONDS);
        System.out.println("== Main thread  done. Await returned with: " + await);

        executorService.shutdownNow(); // Sends an interrupt to the threads. Therefore releasing one of the threads.
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.
    }


    @Test
    public void avoidDeadlockWithTryLock() throws InterruptedException {

        CountDownLatch done = new CountDownLatch(2);

        Lock lock1 = new ReentrantLock();
        Lock lock2 = new ReentrantLock();

        Runnable r1 = () -> {
            try {
                lock1.lockInterruptibly();
                try {
                    System.out.println("r1 got lock1");
                    Thread.sleep(10);
                    System.out.println("r1 tries to get lock2");
                    boolean locked = lock2.tryLock(100, TimeUnit.MILLISECONDS);
                    if (!locked) {
                        System.out.println("r1 could not get lock");
                        return;
                    }
                    try {
                        System.out.println("r1 did something");
                    } finally {
                        lock2.unlock();
                    }
                } finally {
                    lock1.unlock();
                    done.countDown();
                }
            } catch (InterruptedException e) {
                System.out.println("r1 interrupted");
            }
        };

        Runnable r2 = () -> {
            try {
                //lock2.lock(); // locks forever
                lock2.lockInterruptibly();
                try {
                    System.out.println("r2 got lock2");
                    Thread.sleep(10);
                    System.out.println("r2 tries to get lock1");
                    boolean locked = lock1.tryLock(150, TimeUnit.MILLISECONDS);
                    if (!locked) {
                        System.out.println("r2 could not get lock");
                        return;
                    }
                    try {
                        System.out.println("r2 did something");
                    } finally {
                        lock1.unlock();
                    }
                } finally {
                    lock2.unlock();
                    done.countDown();
                }
            } catch (InterruptedException e) {
                System.out.println("r2 interrupted");
            }

        };

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(r1);
        executorService.submit(r2);

        boolean await = done.await(1, TimeUnit.SECONDS);
        System.out.println("== Main thread  done. Await returned with: " + await);

        executorService.shutdownNow(); // Sends an interrupt to the threads. Therefore releasing one of the threads.
        executorService.awaitTermination(10, TimeUnit.SECONDS); // await termination of all scheduled tasks.

    }
}


