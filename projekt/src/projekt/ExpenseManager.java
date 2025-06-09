package projekt;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {
    private List<Osoba> osoby;
    private List<Wydatek> wydatki;

    public ExpenseManager() {
        osoby = new ArrayList<>();
        wydatki = new ArrayList<>();
    }

    public void dodajOsobe(String imie) {
        osoby.add(new Osoba(imie));
    }

    public List<Osoba> getOsoby() {
        return osoby;
    }

    public void dodajWydatek(String opis, double kwota, String platnikImie, List<String> uczestnicyImiona) {
        Osoba platnik = znajdzOsobe(platnikImie);
        List<Osoba> uczestnicy = new ArrayList<>();
        for (String imie : uczestnicyImiona) {
            Osoba o = znajdzOsobe(imie);
            if (o != null) uczestnicy.add(o);
        }
        if (platnik != null && !uczestnicy.isEmpty()) {
            platnik.dodajWydatek(kwota);
            Wydatek w = new Wydatek(opis, kwota, platnik, uczestnicy);
            wydatki.add(w);
        }
    }

    private Osoba znajdzOsobe(String imie) {
        for (Osoba o : osoby) {
            if (o.getImie().equalsIgnoreCase(imie)) return o;
        }
        return null;
    }

    public void wczytajZPliku(String nazwaPliku) {
        try (BufferedReader reader = new BufferedReader(new FileReader(nazwaPliku))) {
            String linia;
            while ((linia = reader.readLine()) != null) {
                String[] czesci = linia.split(";", 4);
                if (czesci.length != 4) continue;

                String platnik = czesci[0];
                double kwota = Double.parseDouble(czesci[1].replace(",", ".")); 
                String opis = czesci[2];
                String uczestnicyStr = czesci[3].replace("[", "").replace("]", "").trim();
                List<String> uczestnicy = Arrays.stream(uczestnicyStr.split(";"))
                                                .map(String::trim)
                                                .collect(Collectors.toList());


                if (znajdzOsobe(platnik) == null) dodajOsobe(platnik);
                for (String u : uczestnicy) {
                    if (znajdzOsobe(u) == null) dodajOsobe(u);
                }

                dodajWydatek(opis, kwota, platnik, uczestnicy);
            }
            System.out.println("Dane wczytane z pliku: " + nazwaPliku);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Błąd podczas wczytywania pliku: " + e.getMessage());
        }
    }

    public void zapiszRozliczenie(String nazwaPliku) {
        Map<String, Map<String, Double>> rozliczenia = ktoKomuIle();
        try (PrintWriter writer = new PrintWriter(nazwaPliku)) {
            writer.println("Kto -> Komu : Kwota [zł]");
            for (String dluznik : rozliczenia.keySet()) {
                for (Map.Entry<String, Double> entry : rozliczenia.get(dluznik).entrySet()) {
                    String wierzyciel = entry.getKey();
                    double kwota = entry.getValue();
                    writer.printf("%s -> %s : %.2f zł%n", dluznik, wierzyciel, kwota);
                }
            }
            System.out.println("Rozliczenie zapisane do pliku: " + nazwaPliku);
        } catch (IOException e) {
            System.out.println("Błąd podczas zapisu rozliczenia: " + e.getMessage());
        }
    }
    public void rozlicz() {
        double suma = 0;
        for (Wydatek w : wydatki) {
            suma += w.getKwota();
        }

        double srednia = suma / osoby.size();

        System.out.println("\n--- Rozliczenie ---");
        for (Osoba o : osoby) {
            double saldo = o.getSumaWydatkow() - srednia;
            System.out.printf("%s: %+.2f zł\n", o.getImie(), saldo);
        }

        Map<String, Map<String, Double>> rozliczenia = ktoKomuIle();
        System.out.println("\n--- Kto komu ile ---");
        for (String dluznik : rozliczenia.keySet()) {
            for (Map.Entry<String, Double> entry : rozliczenia.get(dluznik).entrySet()) {
                String wierzyciel = entry.getKey();
                double kwota = entry.getValue();
                System.out.printf("%s -> %s : %.2f zł\n", dluznik, wierzyciel, kwota);
            }
        }
    }
    public Map<String, Map<String, Double>> ktoKomuIle() {
        Map<String, Double> saldo = obliczSaldo();
        Map<String, Map<String, Double>> rozliczenia = new HashMap<>();

        List<String> osoby = new ArrayList<>(saldo.keySet());

        List<String> wierzyciele = osoby.stream()
                .filter(o -> saldo.get(o) > 0.0)
                .sorted(Comparator.comparing(saldo::get).reversed())
                .collect(Collectors.toList());

        List<String> dluznicy = osoby.stream()
                .filter(o -> saldo.get(o) < 0.0)
                .sorted(Comparator.comparing(saldo::get))
                .collect(Collectors.toList());

        for (String dluznik : dluznicy) {
            double dlug = -saldo.get(dluznik);

            for (String wierzyciel : wierzyciele) {
                double naleznosc = saldo.get(wierzyciel);
                if (naleznosc == 0) continue;

                double doZaplaty = Math.min(dlug, naleznosc);
                if (doZaplaty == 0) continue;

                rozliczenia
                    .computeIfAbsent(dluznik, k -> new HashMap<>())
                    .put(wierzyciel, doZaplaty);

                saldo.put(dluznik, saldo.get(dluznik) + doZaplaty);
                saldo.put(wierzyciel, saldo.get(wierzyciel) - doZaplaty);

                dlug -= doZaplaty;
                if (dlug <= 0) break;
            }
        }
        return rozliczenia;
    }
    private Map<String, Double> obliczSaldo() {
        Map<String, Double> saldo = new HashMap<>();
        double suma = 0.0;

        for (Osoba o : osoby) {
            suma += o.getSumaWydatkow();
            saldo.put(o.getImie(), o.getSumaWydatkow());
        }

        double srednia = suma / osoby.size();
        for (String imie : saldo.keySet()) {
            saldo.put(imie, saldo.get(imie) - srednia);
        }

        return saldo;
    }

    public void pokazWydatki() {
        for (Wydatek w : wydatki) {
            System.out.println(w.getOpis() + " - " + w.getKwota() + " zł, zapłacił: " +
                w.getPlatnik().getImie() + ", uczestnicy: " + wypiszUczestnikow(w.getUczestnicy()));
        }
    }

    private String wypiszUczestnikow(List<Osoba> lista) {
        StringBuilder sb = new StringBuilder();
        for (Osoba o : lista) {
            sb.append(o.getImie()).append(", ");
        }
        return sb.toString();
    }
    public void zapiszWydatkiDoPliku(String nazwaPliku) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nazwaPliku))) {
            for (Wydatek w : wydatki) {
                String platnik = w.getPlatnik().getImie();
                double kwota = w.getKwota();
                String opis = w.getOpis();
                String uczestnicy = w.getUczestnicy().stream()
                                      .map(Osoba::getImie)
                                      .collect(Collectors.joining(";"));
                writer.printf("%s;%.2f;%s;[%s]%n", platnik, kwota, opis, uczestnicy);
            }
            System.out.println("Wydatki zapisane do pliku: " + nazwaPliku);
        } catch (IOException e) {
            System.out.println("Błąd przy zapisie wydatków: " + e.getMessage());
        }
    }

}
