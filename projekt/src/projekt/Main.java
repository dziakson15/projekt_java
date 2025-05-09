package projekt;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseManager manager = new ExpenseManager();
        
        // Wczytaj dane z pliku rozliczenia poprzedniego
        System.out.print("Podaj nazwę pliku rozliczenia z poprzedniego wyjazdu (np. rozliczenie.txt): ");
        String nazwaPliku = scanner.nextLine();
        manager.wczytajZPliku(nazwaPliku);

        // Menu główne
        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Dodaj osobę");
            System.out.println("2. Dodaj wydatek");
            System.out.println("3. Pokaż wydatki");
            System.out.println("4. Rozlicz");
            System.out.println("0. Zakończ i zapisz rozliczenie");
            System.out.print("Wybierz opcję: ");
            int opcja = scanner.nextInt();
            scanner.nextLine();  // aby usunąć pozostały znak nowej linii

            switch (opcja) {
                case 1:
                    System.out.print("Podaj imię osoby: ");
                    String imie = scanner.nextLine();
                    manager.dodajOsobe(imie);
                    break;
                case 2:
                    System.out.print("Podaj opis wydatku: ");
                    String opis = scanner.nextLine();
                    System.out.print("Podaj kwotę wydatku: ");
                    double kwota = scanner.nextDouble();
                    scanner.nextLine();  // aby usunąć pozostały znak nowej linii
                    System.out.print("Podaj imię osoby płacącej: ");
                    String platnik = scanner.nextLine();
                    System.out.print("Podaj imiona uczestników (oddzielone przecinkiem): ");
                    String uczestnicyStr = scanner.nextLine();
                    String[] uczestnicyImiona = uczestnicyStr.split(",");
                    manager.dodajWydatek(opis, kwota, platnik, List.of(uczestnicyImiona));
                    manager.zapiszWydatkiDoPliku("wydatki.txt");
                    break;
                case 3:
                    manager.pokazWydatki();
                    break;
                case 4:
                    manager.rozlicz();
                    break;
                case 0:
                    // Zapisz dane do pliku
                    String plikRozliczenia = "rozliczenie_auto.txt";
                    manager.zapiszRozliczenie(plikRozliczenia);
                    System.out.println("Do zobaczenia!");
                    return;
                default:
                    System.out.println("Nieznana opcja.");
            }
        }
    }
}
