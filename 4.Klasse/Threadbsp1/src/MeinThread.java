
public class MeinThread extends Thread{
    //run erstellen um zu überschreiben
    @Override
    public void run(){
        for (int i = 1; i<6;i++){
            try {
                Thread.sleep(1000); //das ist neu.
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread "+i);
        }
    }

}
