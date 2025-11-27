package br.com.minharede.servlets;

import br.com.minharede.DAO.UsuarioDAO; 
import java.sql.SQLException;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CadastroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO; 
    
    
    private static final String CADASTRO_JSP = "/cadastro.jsp";
    private static final String LOGIN_SERVLET = "/login";
    @Override
    public void init() throws ServletException {
      
        try {
            this.usuarioDAO = new UsuarioDAO(); 
        } catch (Exception e) {
            System.err.println("Falha na inicialização do UsuarioDAO: " + e.getMessage());
            throw new ServletException("Falha ao iniciar o Servlet. Driver/Conexão DB.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        
        request.getRequestDispatcher(CADASTRO_JSP).forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

      
        String nome = request.getParameter("nome");
        String usuario = request.getParameter("nome_usuario");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        
        if (nome == null || nome.trim().isEmpty() || usuario == null || usuario.trim().isEmpty() 
            || email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
            
            request.setAttribute("erro", "Todos os campos são obrigatórios.");
            request.getRequestDispatcher(CADASTRO_JSP).forward(request, response);
            return;
        }

        try {
            
            boolean sucesso = usuarioDAO.cadastrarUsuario(nome, usuario, email, senha); 
            
            if (sucesso) {
                
                response.sendRedirect(request.getContextPath() + LOGIN_SERVLET + "?cadastro=sucesso"); 
            } else {
             
                request.setAttribute("erro", "Nome de usuário ou email já cadastrado.");
                request.getRequestDispatcher(CADASTRO_JSP).forward(request, response);
            }
            
        } catch (SQLException e) { 
           
            System.err.println("Erro de Banco de Dados durante o cadastro: " + e.getMessage());
            request.setAttribute("erro", "Ocorreu um erro ao processar o seu cadastro. Tente novamente.");
            request.getRequestDispatcher(CADASTRO_JSP).forward(request, response);
            
        } catch (Exception e) {
           
            System.err.println("Erro inesperado durante o cadastro: " + e.getMessage());
            throw new ServletException("Falha na persistência do cadastro.", e);
        }
    }
}