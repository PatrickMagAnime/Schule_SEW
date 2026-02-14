import java.net.*;
        import java.io.*;
        import java.util.*;
        class MulClient
        {
          public static void main(String args[]) throws IOException {
              Scanner sc = new Scanner(System.in);
              //Ab hier passiert schon ein verbindungsaufbau


                  System.out.println("Bitte Anmelden. zB: ip:localhost port:1234 ");

                  System.out.print("Adresse:");
                  String adresse = sc.next();
                  System.out.println("");

                  System.out.print("Port:");
                  int port = sc.nextInt();
                  System.out.println("");

                  System.out.println("Jetzt bitte zwei werte eingeben die vom Server dann multipliziert werden (siehe output dann console)");
              while(true) {
                  System.out.print("Wert 1:");
                  int ersterWert = sc.nextInt();
                  System.out.println("");

                  System.out.println("Wert 2:");
                  int zweiterWert = sc.nextInt();
                  System.out.println("");

                  Socket server = new Socket(adresse, port);

                  //Jetzt braucht man ein objekt um daten empfangen/senden zu können
                  InputStream in = server.getInputStream();
                  OutputStream out = server.getOutputStream();

                  //mit out sende ich sachen zum server
                  out.write(ersterWert); //hier wird 3 gesendet mit write
                  out.write(zweiterWert);

                  int result = in.read();//ergebniss wird vom server gelesen
                  System.out.println(result);

                  System.out.println("Wolen sie Es erneut ausführen (y/n)");
                  if (sc.next().equals("n")){
                      server.close();
                      break;
                  }
              }
          }
        }