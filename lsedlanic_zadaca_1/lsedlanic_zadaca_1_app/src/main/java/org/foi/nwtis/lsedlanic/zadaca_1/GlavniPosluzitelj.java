package org.foi.nwtis.lsedlanic.zadaca_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Korisnik;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Lokacija;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Ocitanje;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Uredaj;
import org.foi.nwtis.lsedlanic.zadaca_1.pomocnici.CitanjeKorisnika;
import org.foi.nwtis.lsedlanic.zadaca_1.pomocnici.CitanjeLokacija;
import org.foi.nwtis.lsedlanic.zadaca_1.pomocnici.CitanjeUredaja;

/**
 * 
 * Klasa GLavniPoslužitelj koja je zadužena za otvaranje veze na određenim mrežnim vratima/portu
 * 
 * @author Leon Sedlanic
 *
 */
public class GlavniPosluzitelj {

  protected Konfiguracija konf;
  protected int brojRadnika;
  protected int maksVrijemeNeaktivnosti;
  protected Map<String, Korisnik> korisnici;
  protected Map<String, Lokacija> lokacije;
  protected Map<String, Uredaj> uredaji;
  private int ispis = 0;
  private int mreznaVrata = 8000;
  private int brojCekaca = 10;
  private boolean kraj;
  private int brojac = 0;
  private int close = 0;
  protected ServerSocket posluzitelj;
  private Map<String, List<Ocitanje>> mapaOcitanja = new HashMap<String, List<Ocitanje>>();


  /**
   * 
   * Konstruktor glavnog poslužitelja
   * 
   * @author Leon Sedlanic
   *
   */
  public GlavniPosluzitelj(Konfiguracija konf) {
    this.konf = konf;
    // this.brojRadnika = Integer.parseInt(konf.dajPostavku("brojRadnika"));
    // this.maksVrijemeNeaktivnosti = Integer.parseInt(konf.dajPostavku("maksVrijemeNeaktivnosti"));
    this.ispis = Integer.parseInt(konf.dajPostavku("ispis"));
    this.mreznaVrata = Integer.parseInt(konf.dajPostavku("mreznaVrata"));
    this.brojCekaca = Integer.parseInt(konf.dajPostavku("brojCekaca"));
  }

  /**
   * 
   * Metoda za pokretanje glavnog poslužitelja
   * 
   * @author Leon Sedlanic
   *
   */
  public void pokreniPosluzitelja() {
    try {
      this.ucitajKorisnike();
      this.ucitajUredaje();
      this.ucitajLokacije();
      otvoriMreznaVrata();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }
  }


  /**
   * 
   * Metoda koja otvara mrežna vrata
   * 
   * @author Leon Sedlanic
   *
   */
  public void otvoriMreznaVrata() throws IOException {
    posluzitelj = new ServerSocket(this.mreznaVrata, this.brojCekaca);
    while (this.close == 0) {
      Socket veza = posluzitelj.accept();

      MrezniRadnik mr = new MrezniRadnik(veza, konf);
      mr.dobaviGP(this);
      mr.dobaviMapu(mapaOcitanja);
      mr.start();
      mr.naziv = "lsedlanic_" + brojac;
      brojac++;
    }
  }


  /**
   * 
   * Metoda koja zatvara mrežna vrata
   * 
   * @author Leon Sedlanic
   *
   */
  public void zatvoriMreznaVrata() throws IOException {
    close = 1;
    posluzitelj.close();

  }

  /**
   * 
   * Metoda ucitajKorisnike učitava korisnike iz datoteke za korisnike
   * 
   * @author Leon Sedlanic
   *
   */
  public void ucitajKorisnike() throws IOException {
    var nazivDatoteke = this.konf.dajPostavku("datotekaKorisnika");
    var citac = new CitanjeKorisnika();
    this.korisnici = citac.ucitajDatoteku(nazivDatoteke);
    if (this.ispis == 1) {
      for (String korime : this.korisnici.keySet()) {
        var k = this.korisnici.get(korime);
        Logger.getGlobal().log(Level.INFO, "Korisnik: " + k.prezime() + " " + k.ime());
      }
    }

  }


  /**
   * 
   * Metoda ucitajKorisnike učitava uređaje iz datoteke za uređaje
   * 
   * @author Leon Sedlanic
   *
   */
  public void ucitajUredaje() throws IOException {
    var nazivDatoteke = this.konf.dajPostavku("datotekaUredaja");
    var citac = new CitanjeUredaja();
    this.uredaji = citac.ucitajDatoteku(nazivDatoteke);
    if (this.ispis == 1) {
      for (String id : this.uredaji.keySet()) {
        var k = this.uredaji.get(id);
        Logger.getGlobal().log(Level.INFO, "Uredaj: " + k.id() + " " + k.naziv());
      }
    }

  }

  /**
   * 
   * Metoda ucitajKorisnike učitava lokacije iz datoteke za lokacije
   * 
   * @author Leon Sedlanic
   *
   */
  public void ucitajLokacije() throws IOException {
    var nazivDatoteke = this.konf.dajPostavku("datotekaLokacija");
    var citac = new CitanjeLokacija();
    this.lokacije = citac.ucitajDatoteku(nazivDatoteke);
    if (this.ispis == 1) {
      for (String naziv : this.lokacije.keySet()) {
        var k = this.lokacije.get(naziv);
        Logger.getGlobal().log(Level.INFO, "Lokacija: " + k.id() + " " + k.naziv());
      }
    }
  }


  /**
   * 
   * Metoda dobavlja HashMap očitanja mrežnom radniku.
   * 
   * @author Leon Sedlanic
   *
   */
  public Map<String, List<Ocitanje>> dobaviMapu() {
    return mapaOcitanja;
  }
}
