package org.foi.nwtis.lsedlanic.zadaca_2.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.foi.nwtis.podaci.LetPolazak;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.LetAviona;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.json.JsonArray;
import jakarta.servlet.ServletContext;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("letovi")
@RequestScoped
public class RestLetovi {

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @Context
  ServletContext context;


  @GET
  @Path("{icao}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajLetoveAerodromeNaDan(@PathParam("icao") String icao,
      @QueryParam("odBroja") String odBroja, @QueryParam("broj") String broj,
      @QueryParam("dan") @NotNull String dan) throws ParseException {
    Response odgovor = null;
    String korisnik = (String) context.getAttribute("OpenSkyNetwork.korisnik");
    String lozinka = (String) context.getAttribute("OpenSkyNetwork.lozinka");

    odBroja = validacijaOdBrojaZaStranicenje(odBroja);
    broj = validacijaBrojaZaStranicenje(broj);
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    Date datumOd = dateFormat.parse(dan);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(datumOd);
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    Date datumDo = calendar.getTime();

    long odVremena = datumOd.getTime() / 1000;
    long doVremena = datumDo.getTime() / 1000;
    OSKlijent oSKlijent = new OSKlijent(korisnik, lozinka);

    List<LetAviona> avioniPolasci = new ArrayList<LetAviona>();
    try {
      avioniPolasci = oSKlijent.getDepartures(icao, odVremena, doVremena);
      for (int i = avioniPolasci.size() - 1; i >= 0; i--) {
        if (i < Integer.parseInt(odBroja)
            || i > Integer.parseInt(broj) + Integer.parseInt(odBroja) - 1
            || (i > Integer.parseInt(broj) - 1 && i < avioniPolasci.size() - 1)) {
          avioniPolasci.remove(i);
        }
      }
    } catch (NwtisRestIznimka e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (avioniPolasci.isEmpty())
      odgovor = Response.status(Status.NOT_FOUND).entity(JsonArray.EMPTY_JSON_ARRAY)
          .type(MediaType.APPLICATION_JSON).build();
    else
      odgovor = Response.ok().entity(avioniPolasci).build();
    return odgovor;
  }

  @GET
  @Path("{icaoOd}/{icaoDo}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajLetoveOdDoAerodromaNaDan(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo, @QueryParam("dan") @NotNull String dan)
      throws ParseException {
    Response odgovor = null;
    String korisnik = (String) context.getAttribute("OpenSkyNetwork.korisnik");
    String lozinka = (String) context.getAttribute("OpenSkyNetwork.lozinka");
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    Date datumOd = dateFormat.parse(dan);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(datumOd);
    calendar.add(Calendar.DAY_OF_YEAR, 1);

    Date datumDo = calendar.getTime();

    long odVremena = datumOd.getTime() / 1000;
    long doVremena = datumDo.getTime() / 1000;
    OSKlijent oSKlijent = new OSKlijent(korisnik, lozinka);
    List<LetAviona> avioniPolasciSaPrvogNaDrugi = new ArrayList<LetAviona>();
    List<LetAviona> avioniPolasciSaPrvog;
    try {
      avioniPolasciSaPrvog = oSKlijent.getDepartures(icaoOd, odVremena, doVremena);

      if (avioniPolasciSaPrvog != null) {
        for (LetAviona a : avioniPolasciSaPrvog) {
          if (icaoDo.equals(a.getEstArrivalAirport()))
            avioniPolasciSaPrvogNaDrugi.add(a);
        }
      }
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
    }
    if (avioniPolasciSaPrvogNaDrugi.isEmpty())
      odgovor = Response.status(Status.NOT_FOUND).entity(JsonArray.EMPTY_JSON_ARRAY)
          .type(MediaType.APPLICATION_JSON).build();
    else
      odgovor = Response.ok().entity(avioniPolasciSaPrvogNaDrugi).build();
    return odgovor;
  }


  @GET
  @Path("spremljeni")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajSpremljene() {
    Response odgovor = null;
    String queryLetovi = "select * from LETOVI_POLASCI";
    LetAviona let = null;
    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection();) {
      stmt = con.prepareStatement(queryLetovi);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String icao = rs.getString("ICAO24");
        int first_seen = Integer.parseInt(rs.getString("FIRSTSEEN"));
        String est_departure_airport = rs.getString("ESTDEPARTUREAIRPORT");
        int last_seen = Integer.parseInt(rs.getString("LASTSEEN"));
        String est_arrival_airport = rs.getString("ESTARRIVALAIRPORT");
        String callsign = rs.getString("CALLSIGN");
        int est_departure_airport_horiz_distance =
            Integer.parseInt(rs.getString("ESTDEPARTUREAIRPORTHORIZDISTANCE"));
        int est_departure_airport_vert_distance =
            Integer.parseInt(rs.getString("ESTDEPARTUREAIRPORTVERTDISTANCE"));
        int est_arrival_airport_horiz_distance =
            Integer.parseInt(rs.getString("ESTARRIVALAIRPORTHORIZDISTANCE"));
        int est_arrival_airport_vert_distance =
            Integer.parseInt(rs.getString("ESTARRIVALAIRPORTVERTDISTANCE"));
        int departure_airport_candidates_count =
            Integer.parseInt(rs.getString("DEPARTUREAIRPORTCANDIDATESCOUNT"));
        int arrival_airport_candidates_count =
            Integer.parseInt(rs.getString("ARRIVALAIRPORTCANDIDATESCOUNT"));
        let = new LetAviona(icao, first_seen, est_departure_airport, last_seen, est_arrival_airport,
            callsign, est_departure_airport_horiz_distance, est_departure_airport_vert_distance,
            est_arrival_airport_horiz_distance, est_arrival_airport_vert_distance,
            departure_airport_candidates_count, arrival_airport_candidates_count);

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
    if (let == null)
      odgovor = Response.status(Status.NOT_FOUND).entity(JsonArray.EMPTY_JSON_ARRAY)
          .type(MediaType.APPLICATION_JSON).build();
    else
      odgovor = Response.ok().entity(let).build();
    return odgovor;
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response dodajLet(LetPolazak let) {
    Response odgovor = null;
    String insertQuery =
        "INSERT INTO LETOVI_POLASCI (ID, ICAO24, FIRSTSEEN, ESTDEPARTUREAIRPORT, LASTSEEN, ESTARRIVALAIRPORT, CALLSIGN, ESTDEPARTUREAIRPORTHORIZDISTANCE, ESTDEPARTUREAIRPORTVERTDISTANCE, ESTARRIVALAIRPORTHORIZDISTANCE, ESTARRIVALAIRPORTVERTDISTANCE,DEPARTUREAIRPORTCANDIDATESCOUNT, ARRIVALAIRPORTCANDIDATESCOUNT, STORED) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection();) {
      stmt = con.prepareStatement(insertQuery);
      stmt.setString(1, let.getIcao24());
      stmt.setString(2, let.getFirstSeen());
      stmt.setString(3, let.getEstDepartureAirport());
      stmt.setString(4, let.getLastSeen());
      stmt.setString(5, let.getEstArrivalAirport());
      stmt.setString(6, let.getCallSign());
      stmt.setString(7, let.getEstDepartureAirportHorizDistance());
      stmt.setString(8, let.getEstDepartureAirportVertDistance());
      stmt.setString(9, let.getEstArrivalAirportHorizDistance());
      stmt.setString(10, let.getEstArrivalAirportVertDistance());
      stmt.setString(11, let.getDepartureAirportCandidatesCount());
      stmt.setString(12, let.getArrivalAirportCandidatesCount());
      stmt.executeUpdate();

    } catch (SQLException e) {
      odgovor = Response.status(Status.NOT_FOUND).entity(e).build();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close();
      } catch (SQLException e) {
        odgovor = Response.status(Status.NOT_FOUND).entity(e).build();
      }
    }
    return odgovor;
  }

  @DELETE
  @Path("{id}")
  public Response obrisiLet(@PathParam("id") String id) {
    Response odgovor = null;
    String deleteQuery = "DELETE FROM LETOVI_POLASCI WHERE ID = ?";
    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection();) {
      stmt = con.prepareStatement(deleteQuery);
      stmt.setString(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      odgovor = Response.status(Status.NOT_FOUND).entity(e).build();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close();
      } catch (SQLException e) {
        odgovor = Response.status(Status.NOT_FOUND).entity(e).build();
      }
    }
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

}
