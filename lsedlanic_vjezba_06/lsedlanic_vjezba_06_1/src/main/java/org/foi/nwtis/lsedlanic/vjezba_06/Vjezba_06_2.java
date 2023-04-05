package org.foi.nwtis.lsedlanic.vjezba_06;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Vjezba_06_2", urlPatterns = { "/Vjezba_06_2" }, initParams = {
		@WebInitParam(name = "konfiguracija", value = "NWTiS.db.config_1.xml") })
public class Vjezba_06_2 extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String icaoFrom = req.getParameter("icaoFrom");
		String icaoTo = req.getParameter("icaoTo");
		String odgovor = ispisUdaljenosti(icaoFrom, icaoTo);
		resp.getWriter().append(odgovor);
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
}
