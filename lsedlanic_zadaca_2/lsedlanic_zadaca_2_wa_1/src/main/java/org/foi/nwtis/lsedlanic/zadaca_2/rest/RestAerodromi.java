package org.foi.nwtis.lsedlanic.zadaca_2.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Lokacija;
import org.foi.nwtis.podaci.Udaljenost;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("aerodromi")
@RequestScoped
public class RestAerodromi {

	@Resource(lookup = "java:app/jdbc/nwtis_bp")
	javax.sql.DataSource ds;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
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
	public Response dajUdaljenostiAerodoma(@PathParam("icaoOd") String icaoFrom, @PathParam("icaoDo") String icaoTo) {
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
}
