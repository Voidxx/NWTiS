package org.foi.nwtis.lsedlanic.vjezba_06;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.PostavkeBazaPodataka;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Vjezba_06_5", urlPatterns = { "/Vjezba_06_5" }, initParams = {
		@WebInitParam(name = "konfiguracija", value = "NWTiS.db.config_1.xml") })
public class Vjezba_06_5 extends HttpServlet {

	private static final long serialVersionUID = 8235007858731339605L;
	private PostavkeBazaPodataka konfBP;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		String nazivDatoteke = this.getServletContext().getRealPath("WEB-INF") + File.separator
				+ this.getInitParameter("konfiguracija");
		this.konfBP = new PostavkeBazaPodataka(nazivDatoteke);
		try {
			this.konfBP.ucitajKonfiguraciju();
			this.ispisKonfPodataka();
		} catch (NeispravnaKonfiguracija e) {
			System.out.println(e.getMessage());
			Logger.getGlobal().log(Level.SEVERE, e.getMessage());
		}
	}

	private void ispisKonfPodataka() {
		Logger.getGlobal().log(Level.SEVERE, "Postavke: " + this.konfBP.dajSvePostavke());
		Logger.getGlobal().log(Level.INFO, "Baza: " + this.konfBP.getUserDatabase());
		Logger.getGlobal().log(Level.INFO, "Korisničko ime: " + this.konfBP.getUserUsername());
		Logger.getGlobal().log(Level.INFO, "Lozinka: " + this.konfBP.getUserPassword());
		Logger.getGlobal().log(Level.INFO, "Konekcija: " + this.konfBP.getServerDatabase());
		Logger.getGlobal().log(Level.INFO, "Driver: " + this.konfBP.getDriverDatabase());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String icaoFrom = req.getParameter("icaoFrom");
		String icaoTo = req.getParameter("icaoTo");
		ArrayList<Udaljenost> odgovor = ispisUdaljenosti(icaoFrom, icaoTo, req);
		req.setAttribute("podaci", odgovor);
		RequestDispatcher rd = req.getRequestDispatcher("ispis2.jsp");
		rd.forward(req, resp);
	}

	private ArrayList<Udaljenost> ispisUdaljenosti(String icaoFrom, String icaoTo, HttpServletRequest req) {
		var udaljenosti = new ArrayList<Udaljenost>();
		String baza = konfBP.getUserDatabase();
		System.out.println(baza);
		String korime = konfBP.getUserUsername();
		String lozinka = konfBP.getUserPassword();
		String driver = konfBP.getDriverDatabase();
		String server = konfBP.getServerDatabase();

		String query = "select ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY from "
				+ "AIRPORTS_DISTANCE_MATRIX where ICAO_FROM = ? AND ICAO_TO =  ?";

		Connection con = null;
		PreparedStatement stmt = null;
		String odgovor = "";
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(server + baza, korime, lozinka);
			stmt = con.prepareStatement(query);
			stmt.setString(1, icaoFrom);
			stmt.setString(2, icaoTo);
			ResultSet rs = stmt.executeQuery();

			float ukupno = 0;
			while (rs.next()) {
				String drzava = rs.getString("COUNTRY");
				float udaljenost = rs.getFloat("DIST_CTRY");
				Udaljenost u = new Udaljenost(drzava, udaljenost);
				udaljenosti.add(u);
				ukupno += udaljenost;
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			req.setAttribute("greska", e.getMessage());
		} finally {
			try {
				if (stmt != null && !stmt.isClosed())
					stmt.close();
				if (con != null && !con.isClosed())
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				req.setAttribute("greska", e.getMessage());
			}
		}

		return udaljenosti;
	}
}
