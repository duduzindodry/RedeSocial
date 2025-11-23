package br.com.minharede;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import br.com.minharede.DAO.UsuarioDAO; 
import br.com.minharede.models.Usuario;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    
	    request.getRequestDispatcher("login.jsp").forward(request, response);
	}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String senha = request.getParameter("senha");

        
        UsuarioDAO dao = new UsuarioDAO();
        

        Usuario usuarioAutenticado = dao.autenticarUsuario(usuario, senha);

      
        if (usuarioAutenticado != null) {
      
            HttpSession sessao = request.getSession();
            sessao.setAttribute("usuarioLogado", usuarioAutenticado); 
            
            response.sendRedirect("index.jsp");
        } else {
            
            response.sendRedirect("login.jsp?erro=credenciais_invalidas"); 
        }
    }
}
