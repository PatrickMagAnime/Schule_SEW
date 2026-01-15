class Ph_Thread extends Thread {
    public static double g = 1.625; //anziehungskraft
    public double t = 0; //Nicht statisch, damit jede rakete eigene zeut hat
    public double v = 0;
    public double h;

    public Ph_Thread(double h, double v) {
        this.h = h;
        this.v = v;
    }
    @Override
    public void run() {
        while (h > 0) {
            //physik pro Sekunde:
            v = v + g;      // Geschwindigkeit nimmt durch g zu
            h = h - v;      // Höhe nimmt durch v ab

            if (h < 0) h = 0;

            System.out.println("Höhe: " + h + " m und Geschwindigkeit: " + v + " m/s");

            try {
                Thread.sleep(1000); //1sek pause
            } catch (InterruptedException e) {
                break;
            }
            t++;
        }
        if (v > 5) {
            System.out.println("Too, du hast alle getötet mit " + v + " m/s");
        } else {
            System.out.println("sicher gelandet");
        }
    }
}