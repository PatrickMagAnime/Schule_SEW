
package oop_beispiel;
//220227
public class Auto {
    String marke;
    String modell;
    int baujahr;
    String kennzeichen;
    int geschwindigkeit;
    
    public String getMarke() {
        return marke;
    }

    public void setMarke(String marke) {
        this.marke = marke;
    }

    public String getModell() {
        return modell;
    }

    public void setModell(String modell) {
        this.modell = modell;
    }

    public int getBaujahr() {
        return baujahr;
    }

    public void setBaujahr(int baujahr) {
        this.baujahr = baujahr;
    }

    public String getKennzeichen() {
        return kennzeichen;
    }

    public void setKennzeichen(String kennzeichen) {
        this.kennzeichen = kennzeichen;
    }

    public int getGeschwindigkeit(int a) {
      
        geschwindigkeit = geschwindigkeit + (a);
        return geschwindigkeit;
    }

    public void setGeschwindigkeit(int geschwindigkeit) {
        this.geschwindigkeit = geschwindigkeit;
    }
   
   public String autoDaten (){
         
      String str;
      str="Marke: "+marke+"\n";
      str=str+"Modell: "+modell+"\n";
      str+="Farbe: "+baujahr+"\n";
        
      return str;
    
    }
}
