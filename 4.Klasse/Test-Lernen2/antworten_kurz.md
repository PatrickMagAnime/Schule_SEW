# Fragen und kurze Antworten

1. **Port-Adressierung: Was ist die Aufgabe einer Portnummer in der TCP-Kommunikation und in welchem Bereich liegen die sogenannten „Well Known Ports“?**

   Eine Portnummer bestimmt, welche Anwendung auf einem Rechner angesprochen wird. **Well Known Ports** liegen bei **0 bis 1023**.

2. **Nenne zwei blockierende Methoden in der Java TCP Client Server Programmierung. (Java Code plus Erklärung)**

   `ServerSocket.accept()` wartet auf einen Client. `BufferedReader.readLine()` wartet auf eingehende Daten.

3. **Wie viele Sockets braucht ein Server mindestens und warum?**

   Ein Server braucht mindestens einen **ServerSocket** zum Warten und einen **Socket** für die Verbindung mit dem Client.

4. **Wie erfolgt der Verbindungsaufbau eines Java TCP Clients? (Java Code)**

   ```java
   Socket socket = new Socket("localhost", 5000);
   ```