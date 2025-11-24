package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets' para consistência

import br.com.minharede.DAO.ComentarioDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.DAO.PostDAO;       // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/deletar")
public class DeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PostDAO postDAO;
    private ComentarioDAO comentarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            // 1. Segurança: Inicializa os DAOs dentro de um try-catch robusto
            this.postDAO = new PostDAO();
            this.comentarioDAO = new ComentarioDAO();
        } catch (Exception e) {
            System.err.println("Falha na inicialização dos DAOs de exclusão: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Verificar Autenticação
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        // 2. Determinar o tipo de item
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
                // A exclusão só ocorre se o usuarioId for o criador do post
                sucesso = postDAO.excluirPost(itemId, usuarioId);
                
            } else if (tipo.equalsIgnoreCase("comentario")) {
                // A exclusão só ocorre se o usuarioId for o criador do comentário
                sucesso = comentarioDAO.excluirComentario(itemId, usuarioId);
            }
            
            // 3. Redirecionar com feedback
            String referer = request.getHeader("Referer");
            String destino = request.getContextPath() + "/index"; // Fallback para o index

            if (sucesso) {
                // Se foi um post, sempre volta para o feed principal, pois a página do post não existirá mais.
                if (tipo.equalsIgnoreCase("post")) {
                    destino = request.getContextPath() + "/index?msg=post_deletado";
                } else {
                    // Se foi comentário, volta para a página anterior (o post)
                    destino = (referer != null) ? referer : destino;
                    destino += (destino.contains("?") ? "&" : "?") + "msg=comentario_deletado";
                }
            } else {
                // Falha: ou acesso negado (não é o autor) ou falha no DB
                destino = (referer != null) ? referer : destino;
                destino += (destino.contains("?") ? "&" : "?") + "error=acesso_negado";
            }
            
            response.sendRedirect(destino);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de item inválido.");
        } catch (Exception e) {
            // Captura erros críticos de DB/SQL
            System.err.println("Erro ao processar exclusão: " + e.getMessage());
            throw new ServletException("Erro na persistência ao processar exclusão.", e);
        }
    }
}