package si.uni_lj.fe.tnuv.kariernicenterul;
/*
IDK če bo ta class še kaj uporaben, but just in case če bo:
https://www.youtube.com/watch?v=741QCymuky4&t=816s
^^^tutorial video zarad katerega je bil class ustvarjen
*/
public class Delo {
    private String opis;
    private String naziv;
    private float placa;

    public Delo(){}
    public Delo(String opis, String naziv, float placa){
        this.opis = opis;
        this.naziv = naziv;
        this.placa = placa;

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
}
