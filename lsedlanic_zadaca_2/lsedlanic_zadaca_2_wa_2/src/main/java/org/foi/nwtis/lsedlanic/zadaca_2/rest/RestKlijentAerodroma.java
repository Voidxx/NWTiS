package org.foi.nwtis.lsedlanic.zadaca_2.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Odgovor;
import com.google.gson.Gson;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class RestKlijentAerodroma {

  public RestKlijentAerodroma() {}

  public List<Aerodrom> getAerodromi(int odBroja, int broj) {
    RestKKlijent rc = new RestKKlijent();
    Aerodrom[] json_Aerodromi = rc.getAerodromi(odBroja, broj);
    List<Aerodrom> korisnici;
    if (json_Aerodromi == null) {
      korisnici = new ArrayList<>();
    } else {
      korisnici = Arrays.asList(json_Aerodromi);
    }
    rc.close();
    return korisnici;
  }

  public List<Odgovor> getAerodromUdaljenosti(String icao, int odBroja, int broj) {
    RestKKlijent rc = new RestKKlijent();
    Odgovor[] json_udaljenosti = rc.getAerodromUdaljenosti(icao, odBroja, broj);
    List<Odgovor> korisnici;
    if (json_udaljenosti == null) {
      korisnici = new ArrayList<>();
    } else {
      korisnici = Arrays.asList(json_udaljenosti);
    }
    rc.close();
    return korisnici;
  }

  public List<Aerodrom> getAerodromi() {
    return this.getAerodromi(1, 20);
  }

  public List<Odgovor> getAerodromUdaljenosti(String icao) {
    return this.getAerodromUdaljenosti(icao, 1, 20);
  }

  public Aerodrom getAerodrom(String icao) {
    RestKKlijent rc = new RestKKlijent();
    Aerodrom k = rc.getAerodrom(icao);
    rc.close();
    return k;
  }

  public Odgovor getAerodromiUdaljenost(String icaoOd, String icaoDo) {
    RestKKlijent rc = new RestKKlijent();
    Odgovor k = rc.getAerodromiUdaljenost(icaoOd, icaoDo);
    rc.close();
    return k;
  }

  public Odgovor getAerodromNajvecaUdaljenost(String icao) {
    RestKKlijent rc = new RestKKlijent();
    Odgovor k = rc.getAerodromNajvecaUdaljenost(icao);
    rc.close();
    return k;
  }

  static class RestKKlijent {

    private final WebTarget webTarget;
    private final Client client;
    private static final String BASE_URI = "http://200.20.0.4:8080/lsedlanic_zadaca_2_wa_1/api";

    public RestKKlijent() {
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("aerodromi");
    }

    public Aerodrom[] getAerodromi(int odBroja, int broj) throws ClientErrorException {
      WebTarget resource = webTarget;

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Aerodrom[] aerodromi = gson.fromJson(request.get(String.class), Aerodrom[].class);

      return aerodromi;
    }

    public Aerodrom getAerodrom(String icao) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icao}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Aerodrom aerodrom = gson.fromJson(request.get(String.class), Aerodrom.class);
      return aerodrom;
    }

    public Odgovor getAerodromiUdaljenost(String icaoOd, String icaoDo)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(
          java.text.MessageFormat.format("{0}", new Object[] {icaoOd}, new Object[] {icaoDo}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Odgovor odgovor = gson.fromJson(request.get(String.class), Odgovor.class);
      return odgovor;
    }

    public Odgovor[] getAerodromUdaljenosti(String icao, int odBroja, int broj)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource =
          resource.path(java.text.MessageFormat.format("{0}", new Object[] {icao}, "udaljenosti"));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Odgovor[] odgovor = gson.fromJson(request.get(String.class), Odgovor[].class);
      return odgovor;
    }

    public Odgovor getAerodromNajvecaUdaljenost(String icao) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource
          .path(java.text.MessageFormat.format("{0}", new Object[] {icao}, "najboljiPutDrzave"));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      if (request.get(String.class).isEmpty()) {
        return null;
      }
      Gson gson = new Gson();
      Odgovor odgovor = gson.fromJson(request.get(String.class), Odgovor.class);
      return odgovor;
    }

    public void close() {
      client.close();
    }
  }

}
