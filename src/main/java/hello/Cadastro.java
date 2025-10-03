package hello;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class Cadastro extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // Aqui salvaria no banco de dados
        System.out.println("Novo cadastro: " + nome + " (" + email + ")");

        response.sendRedirect("login.jsp?msg=ok");
    }
}
