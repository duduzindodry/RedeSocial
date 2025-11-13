package hello;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

import br.com.minharede.DAO.UsuarioDAO;

public class CadastroServlet extends HttpServlet {
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

        UsuarioDAO dao = new UsuarioDAO();
        boolean sucesso = dao.cadastrarUsuario(nome, email, senha);
        
        if (sucesso) {
            // 3. Redireciona para o login com mensagem de sucesso
            response.sendRedirect("login.jsp?cadastro=sucesso");
        } else {
            // 4. Redireciona para o cadastro com mensagem de erro
            response.sendRedirect("cadastro.jsp?erro=falha");
       
    }
	}
	}
