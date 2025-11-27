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


public class AdicionarAmigoServlet extends HttpServlet {

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

        
        String receptorIdParam = request.getParameter("id"); 
        
        if (receptorIdParam == null || receptorIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do usuário receptor é obrigatório.");
            return;
        }

        try {
            int receptorId = Integer.parseInt(receptorIdParam);
            int solicitanteId = usuarioLogado.getId();

            if (solicitanteId == receptorId) {
               
                response.sendRedirect(request.getContextPath() + "/perfil?id=" + solicitanteId + "&error=nao_pode_adicionar_si");
                return;
            }

            
            boolean sucesso = usuarioDAO.solicitarAmizade(solicitanteId, receptorId); 

        
            String destino = request.getContextPath() + "/perfil?id=" + receptorId;

            if (sucesso) {
                destino += "&msg=solicitacao_enviada";
            } else {
            
                destino += "&error=solicitacao_existente";
            }
            
            response.sendRedirect(destino);

        } catch (NumberFormatException e) {
         
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuário inválido.");
        } catch (SQLException e) { 
            
            System.err.println("Erro SQL ao processar solicitação de amizade: " + e.getMessage());
            throw new ServletException("Erro na camada de persistência ao enviar solicitação.", e);
        } catch (Exception e) {
            
            System.err.println("Erro inesperado ao processar solicitação de amizade: " + e.getMessage());
            throw new ServletException("Erro inesperado no servidor.", e);
        }
    }
}