package org.foi.nwtis.lsedlanic.zadaca_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * 
 * Klasa PosluziteljUdaljenosti koja služi za izvršavanje dvaju naredbi od korisnika - izračunavanje
 * udaljenosti i spremanje zahtjeva za izračunavanje
 * 
 * @author Leon Sedlanic
 *
 */
public class PosluziteljUdaljenosti {
  protected Konfiguracija konf;
  private int mreznaVrata = 8001;
  private int brojCekaca = 10;
  private int brojZadnjihSpremljenih = 0;
  private int ispis = 0;
  private boolean kraj = true;
  protected static Queue<Zahtjev> zadnjiZahtjevi;
  private String sintaksaSamoIzracun =
      "(UDALJENOST (?<gpsSirina1>[0-9]{1,13}(\\.[0-9]+)?) (?<gpsDuzina1>[0-9]{1,13}(\\.[0-9]+)?) (?<gpsSirina2>[0-9]{1,13}(\\.[0-9]+)?) (?<gpsDuzina2>[0-9]{1,13}(\\.[0-9]+)?))";
  private String sintaksaSamoSpremi = "(UDALJENOST SPREMI)";
  private Pattern patternSamoIzracun = Pattern.compile(sintaksaSamoIzracun);
  private Pattern patternSamoSpremi = Pattern.compile(sintaksaSamoSpremi);
  Matcher trenutniMatcher;
  private List<Zahtjev> zahtjeviIzDatoteke = new ArrayList<Zahtjev>();

  /**
   * 
   * Klasa zahtjev koja implementira Serializable se koristi za upisivanje i čitanje u/iz datoteke
   * serijalizacije.
   * 
   * @author Leon Sedlanic
   *
   */
  class Zahtjev implements Serializable {
    public Zahtjev(double gpsSirina1, double gpsDuzina1, double gpsSirina2, double gpsDuzina2,
        double rezultat) {
      this.gpsSirina1 = gpsSirina1;
      this.gpsDuzina1 = gpsDuzina1;
      this.gpsSirina2 = gpsSirina2;
      this.gpsDuzina2 = gpsDuzina2;
      this.rezultat = rezultat;
    }

    public double gpsSirina1;
    public double gpsDuzina1;
    public double gpsSirina2;
    public double gpsDuzina2;
    public double rezultat;
  };

  /**
   * 
   * Konstruktor
   * 
   * @author Leon Sedlanic
   *
   */
  public PosluziteljUdaljenosti(Konfiguracija konf) {
    this.konf = konf;
    this.mreznaVrata = Integer.parseInt(konf.dajPostavku("mreznaVrata"));
    this.brojCekaca = Integer.parseInt(konf.dajPostavku("brojCekaca"));
    this.brojZadnjihSpremljenih = Integer.parseInt(konf.dajPostavku("brojZadnjihSpremljenih"));
    this.ispis = Integer.parseInt(konf.dajPostavku("ispis"));
  }

  /**
   * 
   * Main - pokretanje poslužitelja, učitavanje postavki
   * 
   * @author Leon Sedlanic
   *
   */
  public static void main(String[] args)
      throws ClassNotFoundException, NeispravnaKonfiguracija, IOException {
    var konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
    var pu = new PosluziteljUdaljenosti(konf);
    pu.pripremiPosluzitelja(konf.dajPostavku("datotekaSerijalizacije"));
    if (!pu.provjeriArgumente(args)) {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Nije upisan naziv datoteke!");
      return;
    }

    try {
      zadnjiZahtjevi =
          new CircularFifoQueue<>(Integer.parseInt(konf.dajPostavku("brojZadnjihSpremljenih")));
      pu.pokreniPosluziteljUdaljenosti(konf);
    } catch (IOException e) {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Pogreška kod otvaranja mrežnih vrata!" + e.getMessage());
    }
  }


  /**
   * 
   * Metoda za određivanje vrste komande od korisnika
   * 
   * @author Leon Sedlanic
   *
   */
  private String odrediVrstuKomande(String komanda) {
    String povratKomande = "";
    Matcher samoIzracun = patternSamoIzracun.matcher(komanda);
    Matcher samoSpremi = patternSamoSpremi.matcher(komanda);
    boolean statusSamoSpremi = samoSpremi.matches();
    boolean statusSamoIzracun = samoIzracun.matches();
    if (statusSamoIzracun) {

      povratKomande = "samoIzracunaj";
      trenutniMatcher = samoIzracun;
    } else if (statusSamoSpremi) {
      povratKomande = "samoSpremi";
      trenutniMatcher = samoSpremi;
    }
    return povratKomande;
  }

  /**
   * 
   * Metoda za pripremanje poslužitelja za spajanje
   * 
   * @author Leon Sedlanic
   *
   */
  public void pripremiPosluzitelja(String nazivDatoteke) throws IOException {
    ServerSocket posluzitelj = new ServerSocket(this.mreznaVrata, this.brojCekaca);
    while (this.kraj) {
      Socket veza = posluzitelj.accept();
      try {
        var citac = new BufferedReader(
            new InputStreamReader(veza.getInputStream(), Charset.forName("UTF-8")));
        var pisac = new BufferedWriter(
            new OutputStreamWriter(veza.getOutputStream(), Charset.forName("UTF-8")));
        var poruka = new StringBuilder();

        while (true) {
          var red = citac.readLine();
          if (red == null)
            break;

          if (this.ispis == 1) {
            Logger.getGlobal().log(Level.INFO, red);
          }
          poruka.append(red);
        }
        veza.shutdownInput();
        String vrstaKomande = this.odrediVrstuKomande(poruka.toString());
        String odgovor =
            this.obradiZahtjev(poruka.toString(), vrstaKomande, this.konf, nazivDatoteke);
        pisac.write(odgovor);
        pisac.flush();
        veza.shutdownOutput();
        veza.close();

      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }


  /**
   * 
   * Metoda za obrađivanje zahtjeva korisnika
   * 
   * @author Leon Sedlanic
   *
   */
  private String obradiZahtjev(String komanda, String vrstaKomande, Konfiguracija konf,
      String nazivDatoteke) throws IOException {
    String rezultat = "";
    switch (vrstaKomande) {
      case "samoIzracunaj":
        rezultat = "OK " + Double.toString(
            this.IzracunajUdaljenost(Double.parseDouble(trenutniMatcher.group("gpsSirina1")),
                Double.parseDouble(trenutniMatcher.group("gpsDuzina1")),
                Double.parseDouble(trenutniMatcher.group("gpsSirina2")),
                Double.parseDouble(trenutniMatcher.group("gpsDuzina2"))));
        break;
      case "samoSpremi":
        this.Spremi(nazivDatoteke);
        rezultat = "OK";
        break;

      case "formatError":
        rezultat = "ERROR 10 - neispravan format komande";
        break;
    }
    return rezultat;
  }


  /**
   * 
   * Metoda za pokretanje poslužitelja
   * 
   * @author Leon Sedlanic
   *
   */
  public void pokreniPosluziteljUdaljenosti(Konfiguracija konf)
      throws IOException, ClassNotFoundException {
    var nazivDatoteke = konf.dajPostavku("datotekaSerijalizacije");
    var putanja = Path.of(nazivDatoteke);
    if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new IOException(
          "Daoteka '" + nazivDatoteke + "' ne postoji ili se ne može čitati ili je direktorij.");
    }

    FileInputStream fis = new FileInputStream(putanja.toString());
    ObjectInputStream ois = new ObjectInputStream(fis);

    try {
      while (true) {
        Zahtjev trenutniZahtjev = null;
        trenutniZahtjev = (Zahtjev) ois.readObject();
        zahtjeviIzDatoteke.add(trenutniZahtjev);
      }
    } catch (OptionalDataException e) {
      if (!e.eof)
        throw e;
    } finally {
      ois.close();
    }



  }


  /**
   * 
   * Boolean za provjeru argumenata
   * 
   * @author Leon Sedlanic
   *
   */
  public boolean provjeriArgumente(String[] args) {
    return args.length == 1 ? true : false;
  }

  /**
   * 
   * Metoda za učitavanje postavki
   * 
   * @author Leon Sedlanic
   *
   */
  Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
    return KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
  }

  /**
   * 
   * Metoda za izračunavanje udaljenosti između dvije geografske lokacije
   * 
   * @author Leon Sedlanic
   *
   */
  public double IzracunajUdaljenost(double gpsSirina1, double gpsDuzina1, double gpsSirina2,
      double gpsDuzina2) {
    double rezultat = 0;
    gpsSirina1 = Math.toRadians(gpsSirina1);
    gpsSirina2 = Math.toRadians(gpsSirina2);
    gpsDuzina1 = Math.toRadians(gpsDuzina1);
    gpsDuzina2 = Math.toRadians(gpsDuzina2);

    Zahtjev trenutniZahtjev = new Zahtjev(gpsSirina1, gpsSirina2, gpsDuzina1, gpsDuzina2, 0);
    if (zadnjiZahtjevi != null) {
      for (Zahtjev zahtjev : zadnjiZahtjevi) {
        if (zahtjev.gpsSirina1 == gpsSirina1 && zahtjev.gpsSirina2 == gpsSirina2
            && zahtjev.gpsDuzina1 == gpsDuzina1 && zahtjev.gpsDuzina2 == gpsDuzina2) {
          return zahtjev.rezultat;
        }
      }
    }
    double dlon = gpsSirina2 - gpsSirina1;
    double dlat = gpsDuzina2 - gpsDuzina1;
    double a = Math.pow(Math.sin(dlat / 2), 2)
        + Math.cos(gpsDuzina1) * Math.cos(gpsDuzina2) * Math.pow(Math.sin(dlon / 2), 2);

    double c = 2 * Math.asin(Math.sqrt(a));
    double r = 6371;
    rezultat = c * r;
    trenutniZahtjev.rezultat = rezultat;
    zahtjeviIzDatoteke.add(trenutniZahtjev);
    return ((double) Math.round(rezultat * 100) / 100);
  }


  /**
   * 
   * Metoda za spremanje zadnjih zahtjeva za izračun u datoteku serijalizacije
   * 
   * @author Leon Sedlanic
   *
   */
  public void Spremi(String nazivDatoteke) throws IOException {
    FileOutputStream fileOut = new FileOutputStream(nazivDatoteke);
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(zadnjiZahtjevi);
    out.close();
    fileOut.close();
  }


}

