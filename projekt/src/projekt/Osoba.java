package projekt;

public class Osoba {
	private String imie;
    private double sumaWydatkow;

    public Osoba(String imie) {
        this.imie = imie;
        this.sumaWydatkow = 0;
    }

    public String getImie() {
        return imie;
    }

    public double getSumaWydatkow() {
        return sumaWydatkow;
    }

    public void dodajWydatek(double kwota) {
        sumaWydatkow += kwota;
    }

    @Override
    public String toString() {
        return imie + " - wydatki: " + sumaWydatkow + " zł";
    }
}
