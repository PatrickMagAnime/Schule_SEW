public class Main {
    public static void main(String[] args) {
        System.out.println("Achte darauf das die astronauten überleben!");
        System.out.println("In 2 Sekunden geht es los! (enter drücken für Gegenschub)");

        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        Ph_Thread pt = new Ph_Thread(1000, 0);
        //übergeben pt an den Steuer-Thread
        Steuer_Thread st = new Steuer_Thread(pt);

        pt.start();
        st.start();
    }
}