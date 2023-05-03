package org.foi.nwtis.lsedlanic.zadaca_2.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.podaci.Airport;
import org.foi.nwtis.podaci.Udaljenost;
import org.foi.nwtis.podaci.UdaljenostIzmeduAerodroma;
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
  public Response dajSveAerodrome(@QueryParam("odBroja") String odBroja,
      @QueryParam("broj") String broj) {
    List<Airport> aerodromi = new ArrayList<Airport>();
    String queryAerodromi =
        "select * from AIRPORTS ORDER BY ICAO OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    PreparedStatement stmt1 = null;
    try (Connection con = ds.getConnection();) {
      stmt1 = con.prepareStatement(queryAerodromi);
      try {
        int intValue = Integer.parseInt(odBroja);
      } catch (NumberFormatException e) {
        odBroja = "1";
      }
      try {
        int intValue = Integer.parseInt(broj);
      } catch (NumberFormatException e) {
        odBroja = "20";
      }
      if (odBroja == null)
        odBroja = "1";
      if (broj == null)
        broj = "20";
      stmt1.setString(1, odBroja);
      stmt1.setString(2, broj);

      ResultSet rs = stmt1.executeQuery();
      while (rs.next()) {
        String icao = rs.getString("ICAO");
        String type = rs.getString("TYPE");
        String name = rs.getString("NAME");
        String elevation_ft = rs.getString("ELEVATION_FT");
        String continent = rs.getString("CONTINENT");
        String iso_country = rs.getString("ISO_COUNTRY");
        String iso_region = rs.getString("ISO_REGION");
        String municipality = rs.getString("MUNICIPALITY");
        String gps_code = rs.getString("GPS_CODE");
        String iata_code = rs.getString("IATA_CODE");
        String local_code = rs.getString("LOCAL_CODE");
        String coordinates = rs.getString("COORDINATES");
        Airport aerodrom = new Airport(icao, type, name, elevation_ft, continent, iso_country,
            iso_region, municipality, gps_code, iata_code, local_code, coordinates);
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
    Response odgovor = Response.ok().entity(aerodromi).build();
    return odgovor;
  }

  // trebal bi mozda vracati ko sa vjezbi aerodrom a ne airport
  @GET
  @Path("{icao}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajAerodrom(@PathParam("icao") String icao) {
    Airport aerodrom = null;
    String queryAerodromi = "select * from AIRPORTS WHERE ICAO = ?";
    PreparedStatement stmt1 = null;
    try (Connection con = ds.getConnection();) {
      stmt1 = con.prepareStatement(queryAerodromi);
      stmt1.setString(1, icao);
      ResultSet rs = stmt1.executeQuery();
      while (rs.next()) {
        String Icao = rs.getString("ICAO");
        String type = rs.getString("TYPE");
        String name = rs.getString("NAME");
        String elevation_ft = rs.getString("ELEVATION_FT");
        String continent = rs.getString("CONTINENT");
        String iso_country = rs.getString("ISO_COUNTRY");
        String iso_region = rs.getString("ISO_REGION");
        String municipality = rs.getString("MUNICIPALITY");
        String gps_code = rs.getString("GPS_CODE");
        String iata_code = rs.getString("IATA_CODE");
        String local_code = rs.getString("LOCAL_CODE");
        String coordinates = rs.getString("COORDINATES");
        aerodrom = new Airport(Icao, type, name, elevation_ft, continent, iso_country, iso_region,
            municipality, gps_code, iata_code, local_code, coordinates);
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

  @GET
  @Path("{icao}/udaljenosti")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajUdaljenostiAerodroma(@PathParam("icao") String icao,
      @QueryParam("odBroja") String odBroja, @QueryParam("broj") String broj) {
    Response odgovor = null;
    var udaljenosti = new ArrayList<UdaljenostIzmeduAerodroma>();
    try {
      int intValue = Integer.parseInt(odBroja);
    } catch (NumberFormatException e) {
      odBroja = "1";
    }
    try {
      int intValue = Integer.parseInt(broj);
    } catch (NumberFormatException e) {
      odBroja = "20";
    }
    if (odBroja == null) {
      odBroja = "1";
    }
    if (broj == null) {
      broj = "20";
    }
    String queryAerodromi =
        "SELECT ICAO_TO, DIST_TOT FROM AIRPORTS_DISTANCE_MATRIX WHERE EXISTS(SELECT ICAO_FROM FROM AIRPORTS_DISTANCE_MATRIX WHERE ICAO_TO = ?) AND ICAO_TO != ? ORDER BY ICAO_TO OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    PreparedStatement stmt1 = null;
    try (Connection con = ds.getConnection();) {
      stmt1 = con.prepareStatement(queryAerodromi);
      stmt1.setString(1, icao);
      stmt1.setString(2, icao);
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

    odgovor = Response.ok().entity(udaljenosti).build();
    return odgovor;
  }

  @GET
  @Path("{icao}/najduljiPutDrzave")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajNajvećuUdaljenostAerodroma(@PathParam("icao") String icao) {
    Response odgovor = null;
    Udaljenost konacnaUdaljenost = new Udaljenost("", 0);
    var aerodromi = new ArrayList<String>();
    String queryAerodromi =
        "SELECT ICAO_TO, COUNTRY, DIST_CTRY FROM AIRPORTS_DISTANCE_MATRIX WHERE EXISTS(SELECT ICAO_FROM FROM AIRPORTS_DISTANCE_MATRIX WHERE ICAO_FROM = ?) AND ICAO_TO != ? AND DIST_CTRY = (SELECT MAX(DIST_CTRY) FROM AIRPORTS_DISTANCE_MATRIX WHERE ICAO_FROM = 'LDZA')";
    PreparedStatement stmt1 = null;

    try (Connection con = ds.getConnection();) {
      stmt1 = con.prepareStatement(queryAerodromi);
      stmt1.setString(1, icao);
      stmt1.setString(2, icao);
      ResultSet rs = stmt1.executeQuery();

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
    odgovor = Response.ok().entity(konacnaUdaljenost).build();
    return odgovor;
  }
}
