package org.foi.nwtis.lsedlanic.zadaca_1.pomocnici;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Uredaj;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.UredajVrsta;

public class CitanjeUredaja {

  public Map<String, Uredaj> ucitajDatoteku(String nazivDatoteke) throws IOException {
    var putanja = Path.of(nazivDatoteke);
    if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new IOException(
          "Daoteka '" + nazivDatoteke + "' ne postoji ili se ne može čitati ili je direktorij.");
    }

    var uredaji = new HashMap<String, Uredaj>();
    var citac = Files.newBufferedReader(putanja, Charset.forName("UTF-8"));

    while (true) {
      var red = citac.readLine();
      if (red == null)
        break;

      var stupci = red.split(";");
      if (!redImaCetiriStupaca(stupci)) {
        Logger.getGlobal().log(Level.WARNING, red);
      } else {
        var vrstaBroj = Integer.parseInt(stupci[3]);
        var vrstaUredaja = UredajVrsta.odBroja(vrstaBroj);
        var uredaj = new Uredaj(stupci[0], stupci[1], stupci[2], vrstaUredaja);
        uredaji.put(stupci[1], uredaj);
      }
    }
    return uredaji;
  }


  private boolean redImaCetiriStupaca(String[] stupci) {
    return stupci.length == 4;
  }
}
