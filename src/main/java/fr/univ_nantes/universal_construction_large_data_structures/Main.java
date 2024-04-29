package fr.univ_nantes.universal_construction_large_data_structures;

public class Main {

    /**
     * Test function on MyQueue class
     * @param myqueue the queue
     * @param val the value to enqueue
     */
    static void test (MyQueue<Integer> myqueue, int val) {
        myqueue.enqueue(val);
        try {
            System.out.println(Thread.currentThread().getName() + " " + myqueue.dequeue());
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyQueue<Integer> myqueue = new MyQueue<>();

        var t1 = new Thread(() -> test(myqueue, 0));
        var t2 = new Thread(() -> test(myqueue, 8));
        var t3 = new Thread(() -> test(myqueue, 17));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }
}
