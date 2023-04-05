package org.foi.nwtis.lsedlanic.zadaca_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Klasa glavnog klijenta od kuda dolaze sve naredbe korisnika
 * 
 * @author Leon Sedlanic
 *
 */
public class GlavniKlijent {

  /**
   * 
   * Main metoda
   * 
   * @author Leon Sedlanic
   *
   */
  public static void main(String[] args) {
    String sintaksaGlavniKlijent =
        "^-k (?<korisnik>[0-9a-zA-Z_]+) -l (?<lozinka>[0-9a-zA-Z_-]+) -a (?<adresa>[0-9a-zA-Z_.]+) -v (?<mreznaVrata>[8-9][0-9][0-9][0-9]+) -t (?<vrijeme>[0-9]+) (((--meteo (?<idUredaja>[0-9a-zA-Z_-]+))|(--makstemp (?<maxTemp>[0-9a-zA-Z_-]+))|(--maksvlaga (?<maxVlaga>[0-9a-zA-Z_-]+))|(--makstlak (?<maxTlak>[0-9a-zA-Z_-]+)))|((--alarm '(?<alarm>[0-9a-zA-Z_ -]+)')|(--udaljenost '(?<idLokacija1>[0-9a-zA-Z_ -]+)' '(?<idLokacija2>[0-9a-zA-Z_ -]+)')|(?<spremi>--udaljenost spremi)|(?<kraj>--kraj)))$";
    Pattern patternGlavniKlijent = Pattern.compile(sintaksaGlavniKlijent);


    String komanda = org.apache.commons.lang3.StringUtils.join(args, ' ');
    Matcher glavniKlijent = patternGlavniKlijent.matcher(komanda);

    var gk = new GlavniKlijent();
    if (!gk.provjeriArgumente(args, glavniKlijent)) {
      Logger.getGlobal().log(Level.SEVERE, "Nisu upisani ispravni argumenti!");
      return;
    }

    gk.spojiSeNaPosluzitelja(glavniKlijent.group("adresa"),
        Integer.parseInt(glavniKlijent.group("mreznaVrata")), glavniKlijent);
  }

  /**
   * 
   * Boolean za provjeru argumenata
   * 
   * @author Leon Sedlanic
   *
   */
  private boolean provjeriArgumente(String[] args, Matcher glavniKlijent) {
    boolean statusGlavniKlijent = glavniKlijent.matches();
    if (statusGlavniKlijent)
      return true;
    else
      return false;
  }

  /**
   * 
   * Metoda za spajanej na glavnog poslužitelja
   * 
   * @author Leon Sedlanic
   *
   */
  private void spojiSeNaPosluzitelja(String adresa, int mreznaVrata, Matcher glavniKlijent) {
    try {
      Socket mreznaUticnica = new Socket(adresa, mreznaVrata);
      var citac = new BufferedReader(
          new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));

      String zahtjev = "KORISNIK " + glavniKlijent.group("korisnik") + " LOZINKA "
          + glavniKlijent.group("lozinka") + " ";
      if (glavniKlijent.group("idUredaja") != null)
        zahtjev = zahtjev + "METEO " + glavniKlijent.group("idUredaja");
      if (glavniKlijent.group("maxTemp") != null)
        zahtjev = zahtjev + "MAKS " + "TEMP " + glavniKlijent.group("idUredaja");
      if (glavniKlijent.group("maxTlak") != null)
        zahtjev = zahtjev + "MAKS " + "TLAK " + glavniKlijent.group("idUredaja");
      if (glavniKlijent.group("maxVlaga") != null)
        zahtjev = zahtjev + "MAKS " + "VLAGA " + glavniKlijent.group("idUredaja");
      if (glavniKlijent.group("alarm") != null)
        zahtjev = zahtjev + "ALARM " + "'" + glavniKlijent.group("alarm") + "'";
      if (glavniKlijent.group("idLokacija1") != null && glavniKlijent.group("idLokacija2") != null)
        zahtjev = zahtjev + "UDALJENOST" + " '" + glavniKlijent.group("idLokacija1") + "' '"
            + glavniKlijent.group("idLokacija2") + "'";
      if (glavniKlijent.group("kraj") != null)
        zahtjev = zahtjev + "KRAJ";
      if (glavniKlijent.group("spremi") != null)
        zahtjev = zahtjev + "UDALJENOST SPREMI";
      pisac.write(zahtjev);
      pisac.flush();
      mreznaUticnica.shutdownOutput();

      var poruka = new StringBuilder();
      while (true) {
        var red = citac.readLine();
        if (red == null)
          break;
        poruka.append(red);
      }
      Logger.getGlobal().log(Level.INFO, "odgovor: " + poruka);
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
