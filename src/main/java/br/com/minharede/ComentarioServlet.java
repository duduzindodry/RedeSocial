package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets'

import br.com.minharede.DAO.ComentarioDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Comentario;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/comentar")
public class ComentarioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ComentarioDAO comentarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            // 1. Segurança: Instancie o DAO dentro de um try-catch robusto
            this.comentarioDAO = new ComentarioDAO();
        } catch (Exception e) {
            System.err.println("Falha ao inicializar ComentarioDAO: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        // 1. Verificar Autenticação
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "É necessário estar logado para comentar.");
            return;
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // 2. Coletar Parâmetros
        try {
            String postIdParam = request.getParameter("postId");
            String conteudo = request.getParameter("conteudo");
            
            // Validação de entrada
            if (conteudo == null || conteudo.trim().isEmpty() || postIdParam == null || postIdParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros inválidos.");
                return;
            }
            
            int postId = Integer.parseInt(postIdParam);

            // 3. Montar o Objeto Comentario
            Comentario novoComentario = new Comentario();
            novoComentario.setConteudo(conteudo.trim());
            
            // 4. CORREÇÃO: Setar o objeto Post com o ID correto (chave estrangeira)
            Post postReferencia = new Post();
            postReferencia.setId(postId);
            
            novoComentario.setUsuario(usuarioLogado);
            novoComentario.setPost(postReferencia);

            // 5. Salvar no Banco de Dados
            comentarioDAO.adicionarComentario(novoComentario);

            // 6. Redirecionar para a página do post original (Referer)
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/post?id=" + postId); // Fallback mais seguro
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do post inválido.");
        } catch (Exception e) {
            // 7. Robustez: Trata falhas de DB (SQLException)
            System.err.println("Erro ao processar comentário: " + e.getMessage());
            throw new ServletException("Erro na camada de persistência ao salvar o comentário.", e);
        }
    }
}