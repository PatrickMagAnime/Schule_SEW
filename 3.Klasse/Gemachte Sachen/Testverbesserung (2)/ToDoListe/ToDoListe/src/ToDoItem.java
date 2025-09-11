import java.awt.*;

public class ToDoItem {
    private String titel;
    private String beschreibung;
    private boolean erledigt;
    private int prioritaet;
    private Category kategorie;
    private String farbe; // gespeichert als Hex

    public void setFarbe(Color color) {
        if (color == null) {
            this.farbe = null;
        } else {
            this.farbe = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }
    }

    public Color getFarbeAsColor() {
        if(farbe != null){
            return Color.decode(farbe);
        }
        return null;
    }

    public String getFarbe() {
        return farbe;
    }

    public void setFarbe(String farbe) {
        this.farbe = farbe;
    }

    public ToDoItem(String titel, String beschreibung,
                    boolean erledigt, int prioritaet,
                    Category kategorie, String farbe) {
        this.titel        = titel;
        this.beschreibung = beschreibung;
        this.erledigt     = erledigt;
        this.prioritaet   = prioritaet;
        this.kategorie    = kategorie;
        this.farbe        = farbe;
    }

    // Standard-Constructor für Gson
    public ToDoItem() {}

    public String getTitel() { return titel; }
    public void setTitel(String titel) { this.titel = titel; }
    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    public boolean isErledigt() { return erledigt; }
    public void setErledigt(boolean erledigt) { this.erledigt = erledigt; }
    public int getPrioritaet() { return prioritaet; }
    public void setPrioritaet(int prioritaet) { this.prioritaet = prioritaet; }
    public Category getKategorie() { return kategorie; }
    public void setKategorie(Category kategorie) { this.kategorie = kategorie; }

    @Override
    public String toString() {
        return titel + " [" + (erledigt ? "X" : " ") + "] (P" + prioritaet + ") [" +
                (kategorie != null ? kategorie : "") + "]" +
                (farbe != null ? " Farbe: " + farbe : "");
    }
}