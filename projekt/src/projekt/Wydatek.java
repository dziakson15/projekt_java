package projekt;

import java.util.List;

public class Wydatek {
	private String opis;
    private double kwota;
    private Osoba platnik;
    private List<Osoba> uczestnicy;

    public Wydatek(String opis, double kwota, Osoba platnik, List<Osoba> uczestnicy) {
        this.opis = opis;
        this.kwota = kwota;
        this.platnik = platnik;
        this.uczestnicy = uczestnicy;
    }

    public String getOpis() {
        return opis;
    }

    public double getKwota() {
        return kwota;
    }

    public Osoba getPlatnik() {
        return platnik;
    }

    public List<Osoba> getUczestnicy() {
        return uczestnicy;
    }

}
