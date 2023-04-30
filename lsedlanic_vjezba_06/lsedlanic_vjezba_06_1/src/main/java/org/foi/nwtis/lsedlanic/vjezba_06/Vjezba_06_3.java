package org.foi.nwtis.lsedlanic.vjezba_06;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Vjezba_06_3", urlPatterns = { "/Vjezba_06_3" })
public class Vjezba_06_3 extends HttpServlet {

	private static final long serialVersionUID = 7202653895867190684L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int brojCiklusa = Integer.parseInt(req.getParameter("brojCiklusa"));
		int trajanjeCiklusa = Integer.parseInt(req.getParameter("trajanjeCiklusa"));

		DretvaVremena dv = new DretvaVremena(brojCiklusa, trajanjeCiklusa);
		dv.start();
	}
}
