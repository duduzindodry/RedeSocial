package br.com.minharede;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import br.com.minharede.DAO.UsuarioDAO; 

public class CadastroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        String nome = request.getParameter("nome");
        String usuario = request.getParameter("usuario"); 
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        
        UsuarioDAO dao = new UsuarioDAO();
        
        boolean sucesso = dao.cadastrarUsuario(nome, usuario, email, senha); 
        
      
        if (sucesso) {
            response.sendRedirect("login.jsp?cadastro=sucesso");
        } else {
            
            response.sendRedirect("cadastro.jsp?erro=falha");
        }
    }
}