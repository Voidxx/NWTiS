package org.foi.nwtis.lsedlanic.zadaca_2.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Lokacija;
import org.foi.nwtis.podaci.NajvecaUdaljenostIzmeduAerodromaUDrzavi;
import org.foi.nwtis.podaci.Udaljenost;
<<<<<<< HEAD
import org.foi.nwtis.podaci.UdaljenostIzmeduAerodroma;
=======
>>>>>>> origin/HEAD
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.json.JsonArray;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("aerodromi")
@RequestScoped
public class RestAerodromi {

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
<<<<<<< HEAD
  public Response dajSveAerodrome(@QueryParam("odBroja") String odBroja,
      @QueryParam("broj") String broj) {
    Response odgovor = null;
    List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();
    String queryAerodromi =
        "select * from AIRPORTS ORDER BY ICAO OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    PreparedStatement stmt1 = null;
    try (Connection con = ds.getConnection();) {
      stmt1 = con.prepareStatement(queryAerodromi);
      odBroja = validacijaOdBrojaZaStranicenje(odBroja);
      broj = validacijaBrojaZaStranicenje(broj);
      stmt1.setInt(1, Integer.parseInt(odBroja) - 1);
      stmt1.setInt(2, Integer.parseInt(broj));

      ResultSet rs = stmt1.executeQuery();
      while (rs.next()) {
        String icao = rs.getString("ICAO");;
        String naziv = rs.getString("NAME");
        String drzava = rs.getString("ISO_COUNTRY");
        String koordinate = rs.getString("COORDINATES");
        String[] koordinatePodijeljeno = koordinate.split(",", 2);
        Lokacija lokacija = new Lokacija(koordinatePodijeljeno[0], koordinatePodijeljeno[1]);
        Aerodrom aerodrom = new Aerodrom(icao, naziv, drzava, lokacija);
        aerodromi.add(aerodrom);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt1 != null && !stmt1.isClosed())
          stmt1.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    if (aerodromi.isEmpty())
      odgovor = Response.status(Status.NOT_FOUND).entity(JsonArray.EMPTY_JSON_ARRAY)
          .type(MediaType.APPLICATION_JSON).build();
    else
      odgovor = Response.ok().entity(aerodromi).build();
    return odgovor;

  }


  // trebal bi mozda vracati ko sa vjezbi aerodrom a ne airport
  @GET
  @Path("{icao}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajAerodrom(@PathParam("icao") String icao) {
    Response odgovor = null;
    Aerodrom aerodrom = null;
    String queryAerodromi = "select * from AIRPORTS WHERE ICAO = ?";
    PreparedStatement stmt1 = null;
    try (Connection con = ds.getConnection();) {
      stmt1 = con.prepareStatement(queryAerodromi);
      stmt1.setString(1, icao);
      ResultSet rs = stmt1.executeQuery();
      while (rs.next()) {
        String Icao = rs.getString("ICAO");;
        String naziv = rs.getString("NAME");
        String drzava = rs.getString("ISO_COUNTRY");
        String koordinate = rs.getString("COORDINATES");
        String[] koordinatePodijeljeno = koordinate.split(",", 2);
        Lokacija lokacija = new Lokacija(koordinatePodijeljeno[0], koordinatePodijeljeno[1]);
        aerodrom = new Aerodrom(Icao, naziv, drzava, lokacija);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt1 != null && !stmt1.isClosed())
          stmt1.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    if (aerodrom != null)
      odgovor = Response.ok().entity(aerodrom).build();
    else
      odgovor = Response.status(Status.NOT_FOUND).entity(JsonArray.EMPTY_JSON_ARRAY)
          .type(MediaType.APPLICATION_JSON).build();
    return odgovor;
  }


  @GET
  @Path("{icaoOd}/{icaoDo}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajUdaljenostiAerodoma(@PathParam("icaoOd") String icaoFrom,
      @PathParam("icaoDo") String icaoTo) {
    Response odgovor = null;
    var udaljenosti = new ArrayList<Udaljenost>();
    String query = "select ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY from "
        + "AIRPORTS_DISTANCE_MATRIX where ICAO_FROM = ? AND ICAO_TO =  ?";

    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection();) {
      stmt = con.prepareStatement(query);
      stmt.setString(1, icaoFrom);
      stmt.setString(2, icaoTo);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        String drzava = rs.getString("COUNTRY");
        float udaljenost = rs.getFloat("DIST_CTRY");
        Udaljenost u = new Udaljenost(drzava, udaljenost);
        udaljenosti.add(u);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    if (udaljenosti.isEmpty())
      odgovor = Response.status(Status.NOT_FOUND).entity(JsonArray.EMPTY_JSON_ARRAY)
          .type(MediaType.APPLICATION_JSON).build();
    else
      odgovor = Response.ok().entity(udaljenosti).build();
    return odgovor;
  }

  @GET
  @Path("{icao}/udaljenosti")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajUdaljenostiAerodroma(@PathParam("icao") String icao,
      @QueryParam("odBroja") String odBroja, @QueryParam("broj") String broj) {
    Response odgovor = null;
    var udaljenosti = new ArrayList<UdaljenostIzmeduAerodroma>();
    odBroja = validacijaOdBrojaZaStranicenje(odBroja);
    broj = validacijaBrojaZaStranicenje(broj);
    String queryAerodromi =
        "SELECT DISTINCT ICAO_TO, DIST_TOT FROM AIRPORTS_DISTANCE_MATRIX WHERE ICAO_FROM = ? OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    PreparedStatement stmt1 = null;
    try (Connection con = ds.getConnection();) {
      stmt1 = con.prepareStatement(queryAerodromi);
      stmt1.setString(1, icao);
      stmt1.setInt(2, Integer.parseInt(odBroja) - 1);
      stmt1.setInt(3, Integer.parseInt(broj));
      ResultSet rs = stmt1.executeQuery();
      while (rs.next()) {
        String icaoDo = rs.getString("ICAO_TO");
        float udaljenost = rs.getFloat("DIST_TOT");
        UdaljenostIzmeduAerodroma u = new UdaljenostIzmeduAerodroma(icaoDo, udaljenost);
        udaljenosti.add(u);

      }

    } catch (SQLException e) {
      odgovor = Response.status(Status.NOT_FOUND).entity(JsonArray.EMPTY_JSON_ARRAY)
          .type(MediaType.APPLICATION_JSON).build();
    } finally {
      try {
        if (stmt1 != null && !stmt1.isClosed())
          stmt1.close();
      } catch (SQLException e) {
        odgovor = Response.status(Status.NOT_FOUND).entity(JsonArray.EMPTY_JSON_ARRAY)
            .type(MediaType.APPLICATION_JSON).build();
      }
    }

    if (udaljenosti.isEmpty())
      odgovor = Response.status(Status.NOT_FOUND).entity(JsonArray.EMPTY_JSON_ARRAY)
          .type(MediaType.APPLICATION_JSON).build();
    else
      odgovor = Response.ok().entity(udaljenosti).build();
    return odgovor;
  }

  @GET
  @Path("{icao}/najduljiPutDrzave")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajNajvećuUdaljenostAerodroma(@PathParam("icao") String icao) {
    Response odgovor = null;
    NajvecaUdaljenostIzmeduAerodromaUDrzavi najvecaUdaljenost = null;
    String queryAerodromi =
        "SELECT DISTINCT ICAO_TO, COUNTRY, DIST_CTRY FROM AIRPORTS_DISTANCE_MATRIX WHERE ICAO_FROM = ? AND DIST_CTRY = (SELECT MAX(DIST_CTRY) FROM AIRPORTS_DISTANCE_MATRIX WHERE ICAO_FROM = ?)";
    PreparedStatement stmt1 = null;

    try (Connection con = ds.getConnection();) {
      stmt1 = con.prepareStatement(queryAerodromi);
      stmt1.setString(1, icao);
      stmt1.setString(2, icao);
      ResultSet rs = stmt1.executeQuery();
      while (rs.next()) {
        String drzava = rs.getString("COUNTRY");
        String icaoDo = rs.getString("ICAO_TO");
        float udaljenost = rs.getFloat("DIST_CTRY");
        najvecaUdaljenost = new NajvecaUdaljenostIzmeduAerodromaUDrzavi(drzava, icaoDo, udaljenost);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt1 != null && !stmt1.isClosed())
          stmt1.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    if (najvecaUdaljenost == null)
      odgovor = Response.status(Status.NOT_FOUND).entity(JsonArray.EMPTY_JSON_ARRAY)
          .type(MediaType.APPLICATION_JSON).build();
    else
      odgovor = Response.ok().entity(najvecaUdaljenost).build();
    return odgovor;
  }


  private String validacijaOdBrojaZaStranicenje(String odBroj) {
    try {
      int intValue = Integer.parseInt(odBroj);
    } catch (NumberFormatException e) {
      odBroj = "1";
    }
    if (odBroj == null || Integer.parseInt(odBroj) < 0)
      odBroj = "1";
    return odBroj;
  }

  private String validacijaBrojaZaStranicenje(String broj) {
    try {
      int intValue = Integer.parseInt(broj);
    } catch (NumberFormatException e) {
      broj = "20";
    }
    if (broj == null || Integer.parseInt(broj) < 0)
      broj = "20";
    return broj;
  }
=======
  public Response dajSveAerodrome() {
    List<Aerodrom> aerodromi = new ArrayList<>();
    Aerodrom ad = new Aerodrom("LDZA", "Airport Zagreb", "HR", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("LDVA", "Airport Varaždin", "HR", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("EDDF", "Airport Frankfurt", "DE", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("EDDB", "Airport Berlin", "DE", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("LOWW", "Airport Vienna", "AT", new Lokacija("0", "0"));
    aerodromi.add(ad);
    Response odgovor = Response.ok().entity(aerodromi).build();
    return odgovor;
  }

  @GET
  @Path("{icao}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajAerodrom(@PathParam("icao") String icao) {
    List<Aerodrom> aerodromi = new ArrayList<>();
    Aerodrom ad = new Aerodrom("LDZA", "Airport Zagreb", "HR", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("LDVA", "Airport Varaždin", "HR", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("EDDF", "Airport Frankfurt", "DE", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("EDDB", "Airport Berlin", "DE", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("LOWW", "Airport Vienna", "AT", new Lokacija("0", "0"));
    aerodromi.add(ad);

    Aerodrom aerodrom = null;
    for (Aerodrom a : aerodromi) {
      if (a.getIcao().compareTo(icao) == 0) {
        aerodrom = a;
        break;
      }
    }

    if (aerodrom == null) {
      return Response.noContent().build();
    }

    Response odgovor = Response.ok().entity(aerodrom).build();
    return odgovor;
  }

  @GET
  @Path("{icaoOd}/{icaoDo}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajUdaljenostiAerodoma(@PathParam("icaoOd") String icaoFrom,
      @PathParam("icaoDo") String icaoTo) {
    var udaljenosti = new ArrayList<Udaljenost>();
    String query = "select ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY from "
        + "AIRPORTS_DISTANCE_MATRIX where ICAO_FROM = ? AND ICAO_TO =  ?";

    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection();) {
      stmt = con.prepareStatement(query);
      stmt.setString(1, icaoFrom);
      stmt.setString(2, icaoTo);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        String drzava = rs.getString("COUNTRY");
        float udaljenost = rs.getFloat("DIST_CTRY");
        Udaljenost u = new Udaljenost(drzava, udaljenost);
        udaljenosti.add(u);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    Response odgovor = Response.ok().entity(udaljenosti).build();
    return odgovor;
  }
>>>>>>> origin/HEAD
}
