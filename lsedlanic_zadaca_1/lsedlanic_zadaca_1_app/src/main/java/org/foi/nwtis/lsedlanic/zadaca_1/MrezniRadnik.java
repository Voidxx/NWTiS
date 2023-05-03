package org.foi.nwtis.lsedlanic.zadaca_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Alarm;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Korisnik;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Lokacija;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.MeteoSimulacija;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Ocitanje;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Uredaj;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.UredajVrsta;

/**
 * 
 * Klasa MrežniRadnik koja je zadužena za prepoznavanje i obrađivanje korisničkih naredbi
 * 
 * @author Leon Sedlanic
 *
 */
public class MrezniRadnik extends Thread {
  protected Socket mreznaUticnica;
  protected Konfiguracija konfig;
  private int ispis = 0;
  public String naziv;
  public int brojDretve;
  private String sintaksaKraj =
      "KORISNIK (?<korisnik>[0-9a-zA-Z_]+) LOZINKA (?<lozinka>[0-9a-zA-Z_-]+) KRAJ";
  private String sintaksaUdaljenostIzracunaj =
      "KORISNIK (?<korisnik>[0-9a-zA-Z_]+) LOZINKA (?<lozinka>[0-9a-zA-Z_-]+) UDALJENOST \'(?<idLokacija1>[0-9a-zA-Z _]+)\' \'(?<idLokacija2>[0-9a-zA-Z _]+)\'";
  private String sintaksaUdaljenostSpremi =
      "KORISNIK (?<korisnik>[0-9a-zA-Z_]+) LOZINKA (?<lozinka>[0-9a-zA-Z_-]+) (UDALJENOST SPREMI)";
  private String sintaksaAlarm =
      "KORISNIK (?<korisnik>[0-9a-zA-Z_]+) LOZINKA (?<lozinka>[0-9a-zA-Z_-]+) "
          + "(ALARM \'(?<alarm>[0-9a-zA-Z_].+)\')";
  private String sintaksaSenzor =
      "KORISNIK (?<korisnik>[0-9a-zA-Z_]+) LOZINKA (?<lozinka>[0-9a-zA-Z_-]+) SENZOR (?<idUredaj>[0-9a-zA-Z_-].+) (?<vrijeme>([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]) (?<temp>[0-9]?[0-9]?[0-9]?[0-9]?.[0-9])? ?(?<vlaga>[0-9]?[0-9]?[0-9]?[0-9]?.[0-9])? ?(?<tlak>[0-9]?[0-9]?[0-9]?[0-9]?.[0-9])?";
  private String sintaksaMeteo =
      "KORISNIK (?<korisnik>[0-9a-zA-Z_]+) LOZINKA (?<lozinka>[0-9a-zA-Z_-]+) METEO (?<idUredaj>[0-9a-zA-Z_-].+)";
  private String sintaksaMaks =
      "KORISNIK (?<korisnik>[0-9a-zA-Z_]+) LOZINKA (?<lozinka>[0-9a-zA-Z_-]+) "
          + "MAKS (?<vrsta>TEMP|VLAGA|TLAK) (?<idUredaj>[0-9a-zA-Z_-].+)";
  private Pattern patternUdaljenostIzracunaj = Pattern.compile(sintaksaUdaljenostIzracunaj);
  private Pattern patternUdaljenostSpremi = Pattern.compile(sintaksaUdaljenostSpremi);
  private Pattern patternSenzor = Pattern.compile(sintaksaSenzor);
  private Pattern patternMeteo = Pattern.compile(sintaksaMeteo);
  private Pattern patternKraj = Pattern.compile(sintaksaKraj);
  private Pattern patternAlarm = Pattern.compile(sintaksaAlarm);
  private Pattern patternMaks = Pattern.compile(sintaksaMaks);
  private GlavniPosluzitelj gp;
  private PosluziteljUdaljenosti pu;
  private Matcher trenutniMatcher;
  private Map<String, List<Ocitanje>> mapaOcitanja;
  private List<Alarm> listaAlarma = new LinkedList<Alarm>();
  String vrstaAlarma = "";
  boolean temp = false;
  boolean tlak = false;
  boolean vlaga = false;


  /**
   * 
   * Konstruktor MrezniRadnik
   * 
   * @author Leon Sedlanic
   *
   */
  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konfig) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konfig = konfig;
    this.ispis = Integer.parseInt(this.konfig.dajPostavku("ispis"));

  }



  @Override
  public synchronized void start() {

    super.start();
  }

  /**
   * 
   * Metoda run - pokreće se pri svakom pokretanju jedne instance mrežnog radnika
   * 
   * @author Leon Sedlanic
   *
   */
  @Override
  public void run() {

    try {

      var citac = new BufferedReader(
          new InputStreamReader(this.mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(this.mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));
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
      this.mreznaUticnica.shutdownInput();
      this.mapaOcitanja = gp.dobaviMapu();
      String vrstaKomande = this.odrediVrstuKomande(poruka.toString());
      String odgovor = this.obradiZahtjev(poruka.toString(), vrstaKomande, this.konfig);
      pisac.write(odgovor);
      pisac.flush();
      this.mreznaUticnica.shutdownOutput();
      this.mreznaUticnica.close();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * 
   * Metoda odrediVrstuKomande određuje koji tip komande je korisnik poslao
   * 
   * @author Leon Sedlanic
   *
   */
  public String odrediVrstuKomande(String komanda) {
    String povratKomande = "";
    Matcher udaljenostIzracunaj = patternUdaljenostIzracunaj.matcher(komanda);
    Matcher udaljenostSpremi = patternUdaljenostSpremi.matcher(komanda);
    Matcher alarm = patternAlarm.matcher(komanda);
    Matcher gkSenzor = patternSenzor.matcher(komanda);
    Matcher gkMeteo = patternMeteo.matcher(komanda);
    Matcher maks = patternMaks.matcher(komanda);
    Matcher kraj = patternKraj.matcher(komanda);

    boolean statusUdaljenostIzracunaj = udaljenostIzracunaj.matches();
    boolean statusUdaljenostSpremi = udaljenostSpremi.matches();
    boolean statusGkSenzor = gkSenzor.matches();
    boolean statusGkMeteo = gkMeteo.matches();
    boolean statusAlarm = alarm.matches();
    boolean statusMaks = maks.matches();
    boolean statusKraj = kraj.matches();
    if (statusUdaljenostIzracunaj) {
      if (this.autentifikacijaKorisnika(gp.korisnici.entrySet(), udaljenostIzracunaj)) {
        if (postojiLiLokacija(udaljenostIzracunaj, udaljenostIzracunaj.group("idLokacija1"))
            && (postojiLiLokacija(udaljenostIzracunaj, udaljenostIzracunaj.group("idLokacija2")))) {
          povratKomande = "udaljenostIzracunaj";
          trenutniMatcher = udaljenostIzracunaj;
        }
      } else
        povratKomande = "nemaKorisnikaError";
    } else if (statusUdaljenostSpremi) {
      if (this.autentifikacijaKorisnika(gp.korisnici.entrySet(), udaljenostSpremi)) {
        povratKomande = "udaljenostSpremi";
        trenutniMatcher = udaljenostSpremi;
      } else
        povratKomande = "nemaKorisnikaError";
    } else if (statusGkMeteo) {
      if (this.autentifikacijaKorisnika(gp.korisnici.entrySet(), gkMeteo)) {
        if (this.daLiJePoznatIdUredaja(gp.uredaji.entrySet(), gkMeteo)) {
          trenutniMatcher = gkMeteo;
          povratKomande = "gkMeteo";
        } else
          povratKomande = "nemaUredajaError";
      } else
        povratKomande = "nemaKorisnikaError";
    } else if (statusGkSenzor) {
      if (this.daLiJeAdmin(gp.korisnici.entrySet(), gkSenzor)) {
        if (this.daLiJePoznatIdUredaja(gp.uredaji.entrySet(), gkSenzor)) {
          if (this.daLiOdgovaraTipUredaja(gp.uredaji.entrySet(), gkSenzor)) {
            trenutniMatcher = gkSenzor;
            povratKomande = "senzor";
          } else
            povratKomande = "nemaTipaUredajaError";
        } else
          povratKomande = "nemaUredajaError";
      } else
        povratKomande = "nijeAdminError";

    } else if (statusAlarm) {
      if (this.autentifikacijaKorisnika(gp.korisnici.entrySet(), alarm)) {
        if (postojiLiLokacija(alarm, alarm.group("alarm"))) {
          povratKomande = "alarm";
          trenutniMatcher = alarm;
        }
      } else
        povratKomande = "nemaKorisnikaError";
    } else if (statusMaks) {
      if (this.autentifikacijaKorisnika(gp.korisnici.entrySet(), maks)) {
        if (this.daLiJePoznatIdUredaja(gp.uredaji.entrySet(), maks)) {
          povratKomande = "maks";
          trenutniMatcher = maks;
        } else
          povratKomande = "nemaUredajaError";
      } else
        povratKomande = "nemaKorisnikaError";
    } else if (statusKraj) {
      if (this.daLiJeAdmin(gp.korisnici.entrySet(), kraj)) {
        povratKomande = "kraj";
      } else
        povratKomande = "nijeAdminError";
    } else
      povratKomande = "formatError";
    return povratKomande;
  }


  /**
   * 
   * Metoda vratiZadnjiAlarmSaLokacije vraća zadnje spremljeni alarm koji se dogodio na određenoj
   * lokaciji
   * 
   * @author Leon Sedlanic
   *
   */
  public String vratiZadnjiAlarmSaLokacije() {
    for (Alarm alarm : listaAlarma) {
      for (Map.Entry<String, Uredaj> uredaj : gp.uredaji.entrySet()) {
        if (alarm.idUredaj().equals(uredaj.getValue().id())
            && trenutniMatcher.group("alarm").equals(uredaj.getValue().idLokacija())) {
          String povrat = uredaj.getValue().id() + " " + alarm.vrijemeDogađanja() + " ";
          if (alarm.odstupanjeTemperature() == true)
            povrat = povrat + "TEMP" + " ";
          if (alarm.odstupanjeTlaka() == true)
            povrat = povrat + "TLAK" + " ";
          if (alarm.odstupanjeVlage() == true)
            povrat = povrat + "VLAGA" + " ";
        }
      }
    }
    return null;
  }


  /**
   * 
   * postojiLiLokacija pregledava podatke dobivene iz datotekeLokacija te pronalazi da li se nalazi
   * lokacija s komande unutar tih podataka
   * 
   * @author Leon Sedlanic
   *
   */
  public boolean postojiLiLokacija(Matcher udaljenostIzracunaj, String lokacijaKomanda) {
    for (Map.Entry<String, Lokacija> lokacija : gp.lokacije.entrySet()) {
      if (lokacijaKomanda.equals(lokacija.getValue().id())) {
        return true;
      }
    }
    return false;
  }


  /**
   * 
   * Metoda obradiZahtjev obrađuje zahtjev, tj. komandu koju je korisnik poslao s obzirom na vrstu
   * koju je odredila metoda odrediVrstuKomande
   * 
   * @author Leon Sedlanic
   *
   */
  public String obradiZahtjev(String komanda, String vrstaKomande, Konfiguracija konf)
      throws IOException {
    String rezultat = "";
    switch (vrstaKomande) {
      case "udaljenostIzracunaj":
        Socket mreznaUticnicaIzracunaj = this.spojiSeNaPosluziteljaUdaljenosti(konf);
        rezultat = this.posaljiUdaljenostKomanduZaIzracun(mreznaUticnicaIzracunaj, konf);
        break;
      case "udaljenostSpremi":
        Socket mreznaUticnicaSpremi = this.spojiSeNaPosluziteljaUdaljenosti(konf);
        rezultat = this.posaljiUdaljenostKomanduZaSpremanje(mreznaUticnicaSpremi, konf);
        break;
      case "gkMeteo":
        for (Ocitanje ocitanje : this.mapaOcitanja.get(trenutniMatcher.group("idUredaj"))) {
          if (ocitanje.idUredaja().equals(trenutniMatcher.group("idUredaj"))) {
            rezultat = "OK " + ocitanje.podaci().vrijeme() + " " + ocitanje.podaci().temperatura()
                + " " + ocitanje.podaci().vlaga() + " " + ocitanje.podaci().tlak();
          }
        }
        break;
      case "senzor":
        rezultat = senzor();
        break;
      case "alarm":
        String podaciAlarma = vratiZadnjiAlarmSaLokacije();
        rezultat = "OK " + podaciAlarma;
        break;
      case "maks":
        rezultat = maksT();
        break;
      case "formatError":
        rezultat = "Error 20 - format komande nije ispravan";
        break;
      case "nemaKorisnikaError":
        rezultat = "Error 21 - korisnik ne postoji ili lozinka nije ispravna";
        break;
      case "nijeAdminError":
        rezultat = "Error 22 - korisnik nije administrator";
        break;
      case "nemaUredajaError":
        rezultat = "Error 23 - Specificirani uredaj nije poznat";
        break;
      case "nemaTipaUredajaError":
        rezultat = "Error 23 - Nema ovakvog tipa uredaja";
      case "kraj":
        gp.zatvoriMreznaVrata();
        rezultat = "OK";
        break;
    }
    return rezultat;

  }


  /**
   * 
   * Metoda za određivanje povratne poruke korisniku za naredbu vezanu uz meteo simulaciju
   * 
   * @author Leon Sedlanic
   *
   */
  public String senzor() {
    String rezultat;
    Ocitanje novoOcitanjeZaUredaj = dohvatiPodatkeDobiveneOdUredaja();
    if (this.mapaOcitanja.get(trenutniMatcher.group("idUredaj")) != null) {
      if (postojiLiOdstupanje(this.mapaOcitanja.get(trenutniMatcher.group("idUredaj")))) {
        Alarm noviAlarm = new Alarm(trenutniMatcher.group("idUredaj"),
            novoOcitanjeZaUredaj.podaci(), java.time.LocalTime.now(), temp, tlak, vlaga);
        listaAlarma.add(noviAlarm);
        this.mapaOcitanja.get(trenutniMatcher.group("idUredaj")).clear();
        this.mapaOcitanja.get(trenutniMatcher.group("idUredaj")).add(novoOcitanjeZaUredaj);
        rezultat = "OK ALARM" + vrstaAlarma;
      } else {

        this.mapaOcitanja.get(trenutniMatcher.group("idUredaj")).add(novoOcitanjeZaUredaj);
        rezultat = "OK";
      }
    } else {
      var listOcitanja = new ArrayList<Ocitanje>();
      listOcitanja.add(novoOcitanjeZaUredaj);
      this.mapaOcitanja.put(trenutniMatcher.group("idUredaj"), listOcitanja);
      rezultat = "OK";
    }
    return rezultat;
  }


  /**
   * 
   * Metoda za određivanje poruke koja će se slati nazad korisniku vezano uz maksimalnu
   * temp/vlagu/tlak
   * 
   * @author Leon Sedlanic
   *
   */
  public String maksT() {
    String rezultat;
    float max = 0;
    String vrijeme = "";
    List<Ocitanje> Lista = this.mapaOcitanja.get(trenutniMatcher.group("idUredaj"));
    for (Ocitanje ocitanje : Lista) {
      if (this.trenutniMatcher.group("vrsta").equals("TEMP")
          && ocitanje.podaci().temperatura() > max) {
        max = ocitanje.podaci().temperatura();
        vrijeme = ocitanje.podaci().vrijeme();
      }
      if (this.trenutniMatcher.group("vrsta").equals("TLAK") && ocitanje.podaci().tlak() > max) {
        max = ocitanje.podaci().tlak();
        vrijeme = ocitanje.podaci().vrijeme();
      }
      if (this.trenutniMatcher.group("vrsta").equals("VLAGA") && ocitanje.podaci().vlaga() > max) {
        max = ocitanje.podaci().vlaga();
        vrijeme = ocitanje.podaci().vrijeme();
      }
    }
    rezultat = "OK " + vrijeme + " " + max;
    return rezultat;
  }


  /**
   * 
   * Metoda dohvatiPodatkeDobiveneOdUredaja dohvaća podatke koje šalje SimulatorMeteo pri pokretanju
   * uz konfiguracijsku datoteku
   * 
   * @author Leon Sedlanic
   *
   */
  public Ocitanje dohvatiPodatkeDobiveneOdUredaja() {
    MeteoSimulacija noviPodaciOdUredaja;
    float temp;
    float tlak;
    float vlaga;
    if (trenutniMatcher.group("temp") == null)
      temp = 0;
    else
      temp = Float.parseFloat(trenutniMatcher.group("temp"));
    if (trenutniMatcher.group("vlaga") == null)
      vlaga = 0;
    else
      vlaga = Float.parseFloat(trenutniMatcher.group("vlaga"));
    if (trenutniMatcher.group("tlak") == null)
      tlak = 0;
    else
      tlak = Float.parseFloat(trenutniMatcher.group("tlak"));
    noviPodaciOdUredaja = new MeteoSimulacija(trenutniMatcher.group("idUredaj"),
        trenutniMatcher.group("vrijeme"), temp, vlaga, tlak);
    Ocitanje novoOcitanjeZaUredaj =
        new Ocitanje(trenutniMatcher.group("idUredaj"), noviPodaciOdUredaja);
    return novoOcitanjeZaUredaj;
  }



  @Override
  public void interrupt() {
    // TU RADIŠ SVOJE
    super.interrupt();
  }


  /**
   * 
   * Metoda dobavlja glavnog poslužitelja radi pristupa podacima iz datoteka.
   * 
   * @author Leon Sedlanic
   *
   */
  public void dobaviGP(GlavniPosluzitelj glavni) {
    this.gp = glavni;

  }

  /**
   * 
   * metoda dobavlja HashMap očitanja koja se spremaju pri glavnom poslužitelju.
   * 
   * @author Leon Sedlanic
   *
   */
  public void dobaviMapu(Map<String, List<Ocitanje>> mapa) {
    this.mapaOcitanja = mapa;

  }

  /**
   * 
   * Metoda postojiLiOdstupanje provjerava da li se dogodilo odstupanje u trenutačnom podatku
   * dobivenom od klase SimulatorMeteo
   * 
   * @author Leon Sedlanic
   *
   */
  public boolean postojiLiOdstupanje(List<Ocitanje> lista) {
    float odstupanjeTemp = Float.parseFloat(konfig.dajPostavku("odstupanjeTemp"));
    float odstupanjeTlak = Float.parseFloat(konfig.dajPostavku("odstupanjeTlak"));
    float odstupanjeVlaga = Float.parseFloat(konfig.dajPostavku("odstupanjeVlaga"));
    if ((trenutniMatcher.group("temp") != null)) {
      if (lista.get(0).podaci()
          .temperatura() > (Float.parseFloat(trenutniMatcher.group("temp")) + odstupanjeTemp)
          || (lista.get(0).podaci().temperatura() < (Float.parseFloat(trenutniMatcher.group("temp"))
              - odstupanjeTemp))) {
        temp = true;
        vrstaAlarma = " TEMP";
      }
    }
    if ((trenutniMatcher.group("tlak") != null)) {
      if (lista.get(0).podaci().tlak() > Float.parseFloat(trenutniMatcher.group("tlak"))
          + odstupanjeTlak
          || lista.get(0).podaci().tlak() < Float.parseFloat(trenutniMatcher.group("tlak"))
              - odstupanjeTlak) {
        tlak = true;
        vrstaAlarma = vrstaAlarma + " TLAK ";
      }
    }
    if ((trenutniMatcher.group("vlaga") != null)) {
      if (lista.get(0).podaci().vlaga() > Float.parseFloat(trenutniMatcher.group("vlaga"))
          + odstupanjeVlaga
          || lista.get(0).podaci().vlaga() < Float.parseFloat(trenutniMatcher.group("vlaga"))
              - odstupanjeVlaga)
        vlaga = true;
      vrstaAlarma = vrstaAlarma + "VLAGA";

    }
    if (tlak == true || temp == true || vlaga == true)
      return true;
    else
      return false;
  }

  /**
   * 
   * Metoda autentifikacijaKorisnika pregledava da li postoji određeni korisnik unutar
   * datotekeKorisnika
   * 
   * @author Leon Sedlanic
   *
   */
  public boolean autentifikacijaKorisnika(Set<Map.Entry<String, Korisnik>> set, Matcher komanda) {
    for (Map.Entry<String, Korisnik> korisnik : set) {
      if (komanda.group("korisnik").equals(korisnik.getValue().korisnickoIme())
          && komanda.group("lozinka").equals(korisnik.getValue().lozinka())) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * Metoda daLiJeAdmin pregledava da li je određena osoba administrator ili ne.
   * 
   * @author Leon Sedlanic
   *
   */
  public boolean daLiJeAdmin(Set<Map.Entry<String, Korisnik>> set, Matcher komanda) {
    for (Map.Entry<String, Korisnik> korisnik : set) {
      if ((komanda.group("korisnik").equals(korisnik.getValue().korisnickoIme()))
          && (komanda.group("lozinka").equals(korisnik.getValue().lozinka()))
          && (korisnik.getValue().administrator() == true)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * Metoda daLiJePoznatIdUredaja pregledava datoteku uredaja kako bi otkrila postoji li određeni ID
   * unutar nje
   * 
   * @author Leon Sedlanic
   *
   */
  public boolean daLiJePoznatIdUredaja(Set<Map.Entry<String, Uredaj>> set, Matcher komanda) {
    for (Map.Entry<String, Uredaj> uredaji : set) {
      if ((komanda.group("idUredaj").equals(uredaji.getValue().id()))) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * Metoda daLiODgovaraTipUredaja provjerava da li pašu podaci uz određeni senzor
   * 
   * @author Leon Sedlanic
   *
   */
  public boolean daLiOdgovaraTipUredaja(Set<Map.Entry<String, Uredaj>> set, Matcher komanda) {
    for (Map.Entry<String, Uredaj> uredaji : set) {
      if ((komanda.group("idUredaj").equals(uredaji.getValue().id()))
          && (uredaji.getValue().vrsta().equals(UredajVrsta.odBroja(50)))
          && (komanda.group("temp") != null) && (komanda.group("vlaga") != null)
          && (komanda.group("tlak") == null)) {
        return true;
      }

      if ((komanda.group("idUredaj").equals(uredaji.getValue().id()))
          && (uredaji.getValue().vrsta().equals(UredajVrsta.odBroja(1)))
          && (komanda.group("temp") != null) && (komanda.group("vlaga") == null)
          && (komanda.group("tlak") == null)) {
        return true;
      }

      if ((komanda.group("idUredaj").equals(uredaji.getValue().id()))
          && (uredaji.getValue().vrsta().equals(UredajVrsta.odBroja(51)))
          && (komanda.group("temp") != null) && (komanda.group("vlaga") == null)
          && (komanda.group("tlak") != null)) {
        return true;
      }

      if ((komanda.group("idUredaj").equals(uredaji.getValue().id()))
          && (uredaji.getValue().vrsta().equals(UredajVrsta.odBroja(52)))
          && (komanda.group("temp") != null) && (komanda.group("vlaga") != null)
          && (komanda.group("tlak") != null)) {
        return true;
      }
    }
    return false;
  }


  /**
   * 
   * Metoda spojiSeNaPosluziteljaUdaljenosti spaja Socket sa PosluziteljUdaljenosti uz dvije
   * postavke iz konfiguracijske datoteke
   * 
   * @author Leon Sedlanic
   *
   */
  public Socket spojiSeNaPosluziteljaUdaljenosti(Konfiguracija konf) {
    String adresaPosluzitelja = konf.dajPostavku("posluziteljUdaljenostiAdresa");
    int port = Integer.parseInt(konf.dajPostavku("posluziteljUdaljenostiVrata"));
    Socket mreznaUticnica = null;
    try {
      mreznaUticnica = new Socket(adresaPosluzitelja, port);
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return mreznaUticnica;


  }

  /**
   * 
   * Nakon što smo se povezali na PosluziteljUdaljenosti, metoda posaljiUdaljenostKomanduZaiZracun
   * šalje komandu za izračun kilometraže između dvije lokacije
   * 
   * @author Leon Sedlanic
   *
   */
  public String posaljiUdaljenostKomanduZaIzracun(Socket mreznaUticnica, Konfiguracija konf)
      throws IOException {
    StringBuilder poruka = new StringBuilder();
    double gpsSirina1 = 0;
    double gpsDuzina1 = 0;
    double gpsSirina2 = 0;
    double gpsDuzina2 = 0;
    for (Map.Entry<String, Lokacija> lokacija : gp.lokacije.entrySet()) {
      if (lokacija.getValue().id().equals(trenutniMatcher.group("idLokacija1"))) {
        gpsSirina1 = Double.parseDouble(lokacija.getValue().gpsSirina());
        gpsDuzina1 = Double.parseDouble(lokacija.getValue().gpsDuzina());
      }
      if (lokacija.getValue().id().equals(trenutniMatcher.group("idLokacija2"))) {
        gpsSirina2 = Double.parseDouble(lokacija.getValue().gpsSirina());
        gpsDuzina2 = Double.parseDouble(lokacija.getValue().gpsDuzina());
      }
    }
    String komanda =
        "UDALJENOST " + gpsSirina1 + " " + gpsDuzina1 + " " + gpsSirina2 + " " + gpsDuzina2;
    var citac = new BufferedReader(
        new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
    var pisac = new BufferedWriter(
        new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));

    pisac.write(komanda);
    pisac.flush();
    mreznaUticnica.shutdownOutput();
    while (true) {
      var red = citac.readLine();
      if (red == null)
        break;
      Logger.getGlobal().log(Level.INFO, red);
      poruka.append(red);
    }

    mreznaUticnica.shutdownInput();
    mreznaUticnica.close();
    return poruka.toString();
  }


  /**
   * 
   * Metoda posaljiUdaljenostKomanduZaSpremanje šalje poslužitelju udaljenosti komandu za spremanje
   * u datoteku serijalizacije.
   * 
   * @author Leon Sedlanic
   *
   */
  public String posaljiUdaljenostKomanduZaSpremanje(Socket mreznaUticnica, Konfiguracija konf)
      throws IOException {
    StringBuilder poruka = new StringBuilder();
    String komanda = "UDALJENOST SPREMI";
    var citac = new BufferedReader(
        new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
    var pisac = new BufferedWriter(
        new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));

    pisac.write(komanda);
    pisac.flush();
    mreznaUticnica.shutdownOutput();
    while (true) {
      var red = citac.readLine();
      if (red == null)
        break;
      Logger.getGlobal().log(Level.INFO, red);
      poruka.append(red);
    }
    mreznaUticnica.shutdownInput();
    mreznaUticnica.close();
    return poruka.toString();

  }

}
