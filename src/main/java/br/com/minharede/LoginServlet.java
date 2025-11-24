package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets'

import br.com.minharede.DAO.UsuarioDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            this.usuarioDAO = new UsuarioDAO(); 
        } catch (Exception e) {
            System.err.println("Falha na inicialização do UsuarioDAO: " + e.getMessage());
            throw new ServletException("Falha na inicialização do UsuarioDAO.", e);
        }
    }

    // ----------------------------------------------------
    // MÉTODO GET: Exibe o Formulário de Login
    // ----------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    // ----------------------------------------------------
    // MÉTODO POST: Processa as Credenciais
    // ----------------------------------------------------
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Agora pega "usuario" do form
        String usuarioOuEmail = request.getParameter("usuario");
        String senha = request.getParameter("senha");
        
        try {
            Usuario usuario = usuarioDAO.autenticarUsuario(usuarioOuEmail, senha);

            if (usuario != null) {
                HttpSession session = request.getSession();
                session.setAttribute("usuarioLogado", usuario);
                session.setMaxInactiveInterval(30 * 60);

                // Redireciona para a página principal
                response.sendRedirect(request.getContextPath() + "/index");
            } else {
                request.setAttribute("erro", "Usuário ou senha inválidos.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.err.println("Erro crítico durante o processo de login: " + e.getMessage());
            request.setAttribute("erro", "Erro interno do sistema. Tente novamente.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    
    }
}