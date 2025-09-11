
package test;


public class Schueler {
 private String Name;
 private String Alter;
 private String Klasse;
 private String Schülernummer;  

 public Schueler(String name,String alter, String klasse, String schuelern){
     this.Name=name;
     this.Alter=alter;
     this.Klasse=klasse;
     this.Schülernummer=schuelern;
 }
 
 
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getAlter() {
        return Alter;
    }

    public void setAlter(String Alter) {
        this.Alter = Alter;
    }

    public String getKlasse() {
        return Klasse;
    }

    public void setKlasse(String Klasse) {
        this.Klasse = Klasse;
    }

    public String getSchülernummer() {
        return Schülernummer;
    }

    public void setSchülernummer(String Schülernummer) {
        this.Schülernummer = Schülernummer;
    }

    @Override
    public String toString() {
        return "Schueler{" + "Name=" + Name + ", Alter=" + Alter + ", Klasse=" + Klasse + ", Sch\u00fclernummer=" + Schülernummer + '}';
    }
    
}
