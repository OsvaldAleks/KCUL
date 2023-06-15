package si.uni_lj.fe.tnuv.kariernicenterul;

public class Dogodek {

    private String ime, datum, lokacija, predavatelj, opis;
    private boolean prijavljen;

    private int id;

    public Dogodek(){}

    public Dogodek(String ime, String datum, String lokacija, String predavatelj, String opis, boolean prijavljen, int id) {
        this.ime = ime;
        this.datum = datum;
        this.lokacija = lokacija;
        this.predavatelj = predavatelj;
        this.opis = opis;
        this.prijavljen = prijavljen;
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    public String getPredavatelj() {
        return predavatelj;
    }

    public void setPredavatelj(String predavatelj) {
        this.predavatelj = predavatelj;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public boolean isPrijavljen() {
        return prijavljen;
    }

    public void setPrijavljen(boolean prijavljen) {
        this.prijavljen = prijavljen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
