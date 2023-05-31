package si.uni_lj.fe.tnuv.kariernicenterul;
/*
IDK če bo ta class še kaj uporaben, but just in case če bo:
https://www.youtube.com/watch?v=741QCymuky4&t=816s
^^^tutorial video zarad katerega je bil class ustvarjen
*/
public class Delo {
    private String opis;
    private String naziv;
    private String delovnik;
    private String trajanje;
    private String zacetekDela;
    private float placa;
    private int prostaMesta;

    public Delo(){}
    public Delo(String opis, String naziv, String delovnik, String trajanje, String zacetekDela, float placa, int prostaMesta){
        this.opis = opis;
        this.naziv = naziv;
        this.delovnik = delovnik;
        this.trajanje = trajanje;
        this.zacetekDela = zacetekDela;
        this.placa = placa;
        this.prostaMesta = prostaMesta;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public float getPlaca() {
        return placa;
    }

    public void setPlaca(float placa) {
        this.placa = placa;
    }

    public String getDelovnik() { return delovnik; }

    public void setDelovnik(String delovnik) { this.delovnik = delovnik; }

    public String getTrajanje() { return trajanje; }

    public void setTrajanje(String trajanje) { this.trajanje = trajanje; }

    public String getZacetekDela() { return zacetekDela; }

    public void setZacetekDela(String zacetekDela) { this.zacetekDela = zacetekDela; }

    public int getProstaMesta() { return prostaMesta; }

    public void setProstaMesta(int prostaMesta) { this.prostaMesta = prostaMesta; }
}
