package org.foi.nwtis.lsedlanic.vjezba_05;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.KonfiguracijaBP;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.PostavkeBazaPodataka;

public class Vjezba_05_2 {

	protected KonfiguracijaBP konfBP;

	public static void main(String[] args) {
		var vjezba = new Vjezba_05_2();
		if (!vjezba.provjeriArgumente(args)) {
			Logger.getGlobal().log(Level.SEVERE, "Krivi argumenti!");
			return;
		}
		vjezba.konfBP = new PostavkeBazaPodataka(args[0]);
		try {
			vjezba.konfBP.ucitajKonfiguraciju();
			Logger.getGlobal().log(Level.SEVERE, "Postavke: " + vjezba.konfBP.dajSvePostavke());
			Logger.getGlobal().log(Level.INFO, "Baza: " + vjezba.konfBP.getUserDatabase());
			Logger.getGlobal().log(Level.INFO, "Korisničko ime: " + vjezba.konfBP.getUserUsername());
			Logger.getGlobal().log(Level.INFO, "Lozinka: " + vjezba.konfBP.getUserPassword());
			Logger.getGlobal().log(Level.INFO, "Konekcija: " + vjezba.konfBP.getServerDatabase());
			Logger.getGlobal().log(Level.INFO, "Driver: " + vjezba.konfBP.getDriverDatabase());
		} catch (NeispravnaKonfiguracija e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage());
		}
	}

	private void ispisUdaljenosti(String icaoFrom, String icaoTo) {
		String baza = konfBP.getUserDatabase();
		String korime = konfBP.getUserUsername();
		String lozinka = konfBP.getUserPassword();
		String driver = konfBP.getDriverDatabase();
		String server = konfBP.getServerDatabase();

		String query = "select ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY from"
				+ "AIRPORTS_DISTANCE_MATRIX where ICAO_FROM = ? AND ICAO_TO = ?";

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = DriverManager.getConnection(server, korime, lozinka);
			stmt = con.prepareStatement(query);
			stmt.setString(1, icaoFrom);
			stmt.setString(2, icaoTo);
			ResultSet rs = stmt.executeQuery();

			float ukupno = 0;
			while (rs.next()) {
				String drzava = rs.getString("COUNTRY");
				float udaljenost = rs.getFloat("DIST_CTRY");
				System.out.println(drzava + " = " + udaljenost);
				ukupno += udaljenost;
			}
			System.out.println("Ukupno: " + ukupno);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed())
					stmt.close();
				if (con != null && !con.isClosed())
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private boolean provjeriArgumente(String[] args) {
		return args.length == 1 ? true : false;
	}

}
