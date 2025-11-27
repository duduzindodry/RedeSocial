package br.com.minharede.servlets;

import br.com.minharede.DAO.UsuarioDAO;
import br.com.minharede.models.Usuario;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;
    
   
    private static final String LOGIN_JSP = "/WEB-INF/view/login.jsp"; 

    @Override
    public void init() throws ServletException {
       
        try {
            this.usuarioDAO = new UsuarioDAO(); 
        } catch (Exception e) {
            System.err.println("Falha na inicialização do UsuarioDAO no LoginServlet: " + e.getMessage());
          
            throw new ServletException("Falha ao iniciar o LoginServlet.", e); 
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
  
        request.getRequestDispatcher(LOGIN_JSP).forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String login = request.getParameter("login"); 
        String senha = request.getParameter("senha");

        
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            request.setAttribute("erro", "Preencha o campo de usuário/email e a senha.");
            request.getRequestDispatcher(LOGIN_JSP).forward(request, response);
            return;
        }

        try {
            Usuario usuarioLogado = usuarioDAO.buscarUsuarioPorCredenciais(login, senha); 
            
            if (usuarioLogado != null) {
             
                HttpSession session = request.getSession(); 
                session.setAttribute("usuarioLogado", usuarioLogado); 
                
                System.out.println("Usuário logado com sucesso: " + usuarioLogado.getNome());
                
               
                request.removeAttribute("erro"); 

                
                response.sendRedirect(request.getContextPath() + "/index"); 
                
            } else {
           
                request.setAttribute("erro", "Usuário ou senha inválidos. Tente novamente.");
                request.getRequestDispatcher(LOGIN_JSP).forward(request, response);
            }
            
        } catch (SQLException e) {
            System.err.println("Erro de Banco de Dados durante o login: " + e.getMessage());
            request.setAttribute("erro", "Ocorreu um erro ao tentar aceder ao serviço de autenticação.");
            request.getRequestDispatcher(LOGIN_JSP).forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Erro inesperado no login: " + e.getMessage());
          
            throw new ServletException("Falha crítica no processamento do login.", e); 
        }
    }

}