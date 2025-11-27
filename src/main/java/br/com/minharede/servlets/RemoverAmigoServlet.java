package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.UsuarioDAO; 
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;


public class RemoverAmigoServlet extends HttpServlet {
    
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
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required"); 
            return; 
        }

        String amigoIdParam = request.getParameter("id"); 
        
        if (amigoIdParam == null || amigoIdParam.isEmpty()) { 
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de amigo não fornecido."); 
            return; 
        }

        try {
            int amigoId = Integer.parseInt(amigoIdParam);
            
          
            boolean sucesso = usuarioDAO.removerAmizade(usuarioLogado.getId(), amigoId);
            
            String destino = request.getContextPath() + "/amigos";
            destino += sucesso ? "?msg=amizade_removida" : "?error=falha_remover";
            
            response.sendRedirect(destino);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de amigo inválido.");
        } catch (SQLException e) { 
            
            System.err.println("Erro SQL ao processar remoção de amizade: " + e.getMessage());
            throw new ServletException("Erro na camada de persistência ao remover amizade.", e);
        } catch (Exception e) {
         
            System.err.println("Erro inesperado ao processar remoção de amizade: " + e.getMessage());
            throw new ServletException("Erro inesperado no servidor.", e);
        }
    }
}