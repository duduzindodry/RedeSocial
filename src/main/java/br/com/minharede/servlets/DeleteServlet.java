package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.ComentarioDAO; 
import br.com.minharede.DAO.PostDAO;       
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException; 

public class DeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PostDAO postDAO;
    private ComentarioDAO comentarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            this.postDAO = new PostDAO();
            this.comentarioDAO = new ComentarioDAO();
        } catch (SQLException e) { 
            System.err.println("Falha na inicialização dos DAOs de exclusão: " + e.getMessage());
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

       
        String tipo = request.getParameter("tipo"); 
        String idParam = request.getParameter("id"); 
        
        if (tipo == null || idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros 'tipo' e 'id' são obrigatórios.");
            return;
        }

        try {
            int itemId = Integer.parseInt(idParam);
            int usuarioId = usuarioLogado.getId();
            boolean sucesso = false;

            if (tipo.equalsIgnoreCase("post")) {
                
                sucesso = postDAO.excluirPost(itemId, usuarioId);
                
            } else if (tipo.equalsIgnoreCase("comentario")) {
               
                sucesso = comentarioDAO.excluirComentario(itemId, usuarioId);
            }
            
            
            String referer = request.getHeader("Referer");
            String destino = request.getContextPath() + "/index"; 

            if (sucesso) {
                if (tipo.equalsIgnoreCase("post")) {
                    destino = request.getContextPath() + "/index?msg=post_deletado";
                } else {
                    destino = (referer != null && referer.contains("/post?id=")) ? referer : destino;
                    destino += (destino.contains("?") ? "&" : "?") + "msg=comentario_deletado";
                }
            } else {
                
                destino = (referer != null) ? referer : destino;
                destino += (destino.contains("?") ? "&" : "?") + "error=acesso_negado";
            }
            
            response.sendRedirect(destino);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de item inválido.");
        } catch (SQLException e) { 
            
            System.err.println("Erro SQL ao processar exclusão: " + e.getMessage());
            throw new ServletException("Erro na persistência ao processar exclusão.", e);
        } catch (Exception e) {
            
            System.err.println("Erro inesperado ao processar exclusão: " + e.getMessage());
            throw new ServletException("Erro inesperado no servidor.", e);
        }
    }
}