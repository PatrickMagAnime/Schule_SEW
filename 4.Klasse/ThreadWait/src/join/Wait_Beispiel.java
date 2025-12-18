/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package join;

class SharedResource {
    private int counter = 0;

    public synchronized void increment() {
        counter++;
        System.out.println("Counter: " + counter);
        notify();  // Ein wartender Thread wird aufgeweckt
    }

    public synchronized void waitForIncrement() throws InterruptedException {
        while (counter == 0) {
            wait();  // Der Thread wartet, wenn der Counter 0 ist
        }
    }
}

class Producer extends Thread {
    private SharedResource resource;

    public Producer(SharedResource resource) {
        this.resource = resource;
    }

    public void run() {
        try {
            Thread.sleep(2000);    // Simuliert eine Verzögerung
            resource.increment();  // Der Producer erhöht den Counter
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumer extends Thread {
    private SharedResource resource;

    public Consumer(SharedResource resource) {
        this.resource = resource;
    }

    public void run() {
        try {
            System.out.println("Warten auf recources");
            resource.waitForIncrement();  // Der Consumer wartet, bis der Counter > 0 ist
            System.out.println("Counter wurde erhöht!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Wait_Beispiel {
    public static void main(String[] args) throws InterruptedException {
        SharedResource resource = new SharedResource();
        Producer producer = new Producer(resource);
        Consumer consumer = new Consumer(resource);



        consumer.start();
        producer.start();
    }
}
