package org.foi.nwtis.lsedlanic.zadaca_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.MeteoSimulacija;

/**
 * 
 * Klasa za slanje simulacijskih podataka glavnom poslužitelju
 * 
 * @author Leon Sedlanic
 *
 */
public class SimulatorMeteo {
  public Socket mreznaUticnica;
  private int spavanje = 0;

  public static void main(String[] args) throws NumberFormatException, InterruptedException {
    var sm = new SimulatorMeteo();
    if (!sm.provjeriArgumente(args)) {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Nije upisan naziv datoteke!");
      return;
    }

    try {
      var konf = sm.ucitajPostavke(args[0]);
      sm.pokreniSimulator(konf);
    } catch (NeispravnaKonfiguracija e) {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Pogreška kod učitavanja postavki iz datoteke!" + e.getMessage());
    } catch (IOException e) {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Pogreška kod učitavanja meteo podataka!" + e.getMessage());
    }
  }

  /**
   * 
   * Metoda ProvjeriArgumente provjerava da li su ispravni početni argumenti
   * 
   * @author Leon Sedlanic
   *
   */
  private boolean provjeriArgumente(String[] args) {
    return args.length == 1 ? true : false;
  }

  /**
   * 
   * Metoda ucitajPostavke učitava postavke iz datoteke konfiguracije
   * 
   * @author Leon Sedlanic
   *
   */
  Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
    return KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
  }

  /**
   * 
   * Metoda pokreniSimulator pokreće glavni dio simulatora
   * 
   * @author Leon Sedlanic
   *
   */
  private void pokreniSimulator(Konfiguracija konf)
      throws IOException, NumberFormatException, InterruptedException {
    var nazivDatoteke = konf.dajPostavku("datotekaMeteo");
    var putanja = Path.of(nazivDatoteke);
    if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new IOException(
          "Daoteka '" + nazivDatoteke + "' ne postoji ili se ne može čitati ili je direktorij.");
    }
    var citac = Files.newBufferedReader(putanja, Charset.forName("UTF-8"));

    int rbroj = 0;
    MeteoSimulacija prethodniMeteo = null;
    while (true) {
      var red = citac.readLine();
      if (red == null)
        break;

      rbroj++;
      if (isZaglavlje(rbroj))
        continue;

      var stupci = red.split(";");
      if (!redImaPetStupaca(stupci)) {
        Logger.getGlobal().log(Level.WARNING, red);
      } else {
        var vazeciMeteo = new MeteoSimulacija(stupci[0], stupci[1], Float.parseFloat(stupci[2]),
            Float.parseFloat(stupci[3]), Float.parseFloat(stupci[4]));

        int trajanjeSekunde = Integer.parseInt(konf.dajPostavku("trajanjeSekunde"));
        if (!isPrviPodatak(rbroj)) {
          spavanje = this.izracunajOdradiSpavanje(prethodniMeteo, vazeciMeteo, trajanjeSekunde);
          if (spavanje >= 0) {
            try {
              Thread.sleep(spavanje);
              mreznaUticnica = spojiSeNaGlavnogPosluzitelja(konf);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          this.posaljiMeteoPodatak(vazeciMeteo, mreznaUticnica, konf);
        } else if (isPrviPodatak(rbroj) || spavanje < 0) {
          mreznaUticnica = spojiSeNaGlavnogPosluzitelja(konf);
          this.posaljiMeteoPodatak(vazeciMeteo, mreznaUticnica, konf);
        }
        prethodniMeteo = vazeciMeteo;
      }
    }
  }

  /**
   * 
   * Boolean redImaPedStupaca pregledava da li red pročitan iz datoteke ima 5 stupaca
   * 
   * @author Leon Sedlanic
   *
   */
  private boolean redImaPetStupaca(String[] stupci) {
    return stupci.length == 5;
  }

  /**
   * 
   * Boolean isZaglavlje pregledava da li smo došli do zaglavlja
   * 
   * @author Leon Sedlanic
   *
   */
  private boolean isZaglavlje(int rbroj) {
    return rbroj == 1;
  }

  /**
   * 
   * Boolean isPrviPodatak pregledava da li je pročitani podatak prvi
   * 
   * @author Leon Sedlanic
   *
   */
  private boolean isPrviPodatak(int rbroj) {
    return rbroj == 2;
  }

  /**
   * 
   * Metoda posaljiMeteoPodatak šalje podatke glavnom poslužitelju
   * 
   * @author Leon Sedlanic
   *
   */
  private void posaljiMeteoPodatak(MeteoSimulacija meteoPodatak, Socket mreznaUticnica,
      Konfiguracija konf) throws IOException, NumberFormatException, InterruptedException {
    StringBuilder poruka = new StringBuilder();
    String komanda = "";
    var citac = new BufferedReader(
        new InputStreamReader(this.mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
    var pisac = new BufferedWriter(
        new OutputStreamWriter(this.mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));
    komanda = komanda + "KORISNIK" + " " + konf.dajPostavku("korisnickoIme") + " " + "LOZINKA" + " "
        + konf.dajPostavku("korisnickaLozinka") + " SENZOR ";
    komanda = komanda + meteoPodatak.id() + " ";
    komanda = komanda + meteoPodatak.vrijeme() + " ";
    if (meteoPodatak.temperatura() != -999)
      komanda = komanda + meteoPodatak.temperatura() + " ";
    if (meteoPodatak.vlaga() != -999)
      komanda = komanda + meteoPodatak.vlaga() + " ";
    if (meteoPodatak.tlak() != -999)
      komanda = komanda + meteoPodatak.tlak();
    pisac.write(komanda);
    pisac.flush();
    this.mreznaUticnica.shutdownOutput();

    while (true) {
      var red = citac.readLine();
      if (red == null)
        break;
      if (Integer.parseInt(konf.dajPostavku("ispis")) == 1) {
        Logger.getGlobal().log(Level.INFO, red);
      }
      poruka.append(red);
    }
    Logger.getGlobal().log(Level.INFO, "odgovor: " + poruka);
    this.mreznaUticnica.shutdownInput();
    this.mreznaUticnica.close();
  }

  /**
   * 
   * Metoda izracunajOdradiSpavanje izračunava vrijeme koje mora dretva odspavati prije slanja
   * idučeg podatka
   * 
   * @author Leon Sedlanic
   *
   */
  private int izracunajOdradiSpavanje(MeteoSimulacija prethodniMeteo, MeteoSimulacija vazeciMeteo,
      int trajanjeSekunde) {
    String prvi = prethodniMeteo.vrijeme();
    String drugi = vazeciMeteo.vrijeme();
    String[] prviPolje = prvi.split(":");
    String[] drugiPolje = drugi.split(":");
    int prvoVrijeme = (Integer.parseInt(prviPolje[0]) * 3600000
        + Integer.parseInt(prviPolje[1]) * 60000 + Integer.parseInt(prviPolje[2]) * 1000);
    int drugoVrijeme = (Integer.parseInt(drugiPolje[0]) * 3600000
        + Integer.parseInt(drugiPolje[1]) * 60000 + Integer.parseInt(drugiPolje[2]) * 1000);
    int razlika = drugoVrijeme - prvoVrijeme;
    if (razlika > 0) {
      if (trajanjeSekunde == 1000)
        spavanje = razlika;
      else
        spavanje = razlika * trajanjeSekunde / 1000;
    } else
      spavanje = 0;
    return spavanje;


  }

  /**
   * 
   * Metoda služi za spajanej na glavnog poslužitelja
   * 
   * @author Leon Sedlanic
   *
   */
  private Socket spojiSeNaGlavnogPosluzitelja(Konfiguracija konf) {
    String adresaPosluzitelja = konf.dajPostavku("posluziteljGlavniAdresa");
    int port = Integer.parseInt(konf.dajPostavku("posluziteljGlavniVrata"));
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

}
