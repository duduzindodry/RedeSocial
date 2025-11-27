package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.ComunidadeDAO; 
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;


public class SeguirComunidadeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
	private ComunidadeDAO comunidadeDAO;

    @Override
    public void init() throws ServletException {
        try {
           
            this.comunidadeDAO = new ComunidadeDAO();
        } catch (SQLException e) { 
            System.err.println("Falha ao inicializar ComunidadeDAO: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO devido a erro de conexão.", e);
        } catch (Exception e) {
            throw new ServletException("Erro inesperado na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

       
        String comunidadeIdParam = request.getParameter("comunidadeId");

        if (comunidadeIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da comunidade não fornecido.");
            return;
        }

        try {
            int comunidadeId = Integer.parseInt(comunidadeIdParam);

            
            comunidadeDAO.alternarSeguimento(usuarioLogado.getId(), comunidadeId);

            
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/index");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da comunidade inválido.");
        } catch (SQLException e) { 
            System.err.println("Erro SQL ao alternar seguimento de comunidade: " + e.getMessage());
            throw new ServletException("Erro na persistência ao processar seguimento.", e);
        } catch (Exception e) {
          
            System.err.println("Erro inesperado ao processar seguimento: " + e.getMessage());
            throw new ServletException("Erro crítico no servidor.", e);
        }
    }
}