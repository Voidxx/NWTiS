package org.foi.nwtis.lsedlanic.vjezba_06;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Vjezba_06_1
 */
public class Vjezba_06_1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.service(req, resp);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("<!doctype html>" + "<html><head>"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>" + "</head><body>");
		String korisnickoIme = this.getInitParameter("korisnickoIme");
		String lozinka = this.getInitParameter("lozinka");
		response.getWriter().append("Korisničko ime: " + korisnickoIme).append("<br>");
		response.getWriter().append("Lozinka: " + lozinka).append("<br>");
		response.getWriter().append("</body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
