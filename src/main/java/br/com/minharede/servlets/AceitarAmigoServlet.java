package br.com.minharede.servlets;

import br.com.minharede.DAO.UsuarioDAO;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException; 


public class AceitarAmigoServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
	private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException { 
        try {
            
            this.usuarioDAO = new UsuarioDAO(); 
        } catch (SQLException e) { 
            System.err.println("Falha ao inicializar UsuarioDAO: " + e.getMessage());
           
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
            response.sendRedirect("login.jsp?error=login_required"); 
            return; 
        }

        String solicitanteIdParam = request.getParameter("id");
        if (solicitanteIdParam == null || solicitanteIdParam.isEmpty()) { 
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do solicitante não fornecido."); 
            return; 
        }

        try {
            int solicitanteId = Integer.parseInt(solicitanteIdParam);
            
           
            boolean sucesso = usuarioDAO.aceitarAmizade(usuarioLogado.getId(), solicitanteId); 
            
            String destino = request.getContextPath() + "/amigos";
            destino += sucesso ? "?msg=amizade_aceita" : "?error=falha_aceitar";
            
            response.sendRedirect(destino);

        } catch (NumberFormatException e) {
            
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de solicitante inválido.");
        } catch (SQLException e) {
            
            System.err.println("Erro SQL ao aceitar amizade: " + e.getMessage());
            throw new ServletException("Erro na camada de persistência ao aceitar amizade.", e);
        } catch (Exception e) {
           
            System.err.println("Erro inesperado ao processar aceitação de amizade: " + e.getMessage());
            throw new ServletException("Erro inesperado no servidor.", e);
        }
    }
}
