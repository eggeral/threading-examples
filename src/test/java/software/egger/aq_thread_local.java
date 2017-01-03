package software.egger;

import org.junit.Test;

public class aq_thread_local {

    private ThreadLocal<String> userName = ThreadLocal.withInitial(() -> "UNKNOWN");

    @Test
    public void gettingDefaultValues() throws InterruptedException {

        Thread t = new Thread(() -> {
            System.out.println(userName.get());
        });

        t.start();
        t.join();

    }

    @Test
    public void storingValuesLocalToAThread() throws InterruptedException {

        class RequestHandler implements Runnable {
            public String request;

            @Override
            public void run() {
                String user = authenticate(request);
                if (user != null)
                    userName.set(user);
                handleRequest(request);
            }

            private void handleRequest(String request) {
                if (userName.get().equals("UNKNOWN"))
                    System.out.println("Not authorized");
                else
                    System.out.println("Authorized:" + userName.get() + " - Handling: " + request);
            }

            private String authenticate(String request) {
                if (!request.contains(":"))
                    return null;

                return request.split(":")[0];
            }
        }

        RequestHandler requestHandler1 = new RequestHandler();
        requestHandler1.request = "egal:GET /index.html";

        RequestHandler requestHandler2 = new RequestHandler();
        requestHandler2.request = "GET /index.html";

        Thread t1 = new Thread(requestHandler1);
        Thread t2 = new Thread(requestHandler2);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

    }


}
