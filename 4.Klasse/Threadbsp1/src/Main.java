
public class Main {
    public static void main(String[] args) {
//den main thread hat man immer
        MeinThread t1 = new MeinThread(); //Klasse zu T1 zugewiesen und ist ein Thread weil es Klasse Thread erbt
        MeinThread t2 = new MeinThread();
        //thread starten mit methode start() starten!
        // so ist es falsch         t1.run();

        t1.start(); //ganze klasse wird ausgeführt als thread
        t2.start();

        for (int i = 1; i < 6; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Main "+i);
        }
        }
    }
