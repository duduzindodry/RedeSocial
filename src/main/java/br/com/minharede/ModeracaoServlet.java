package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets'

import br.com.minharede.DAO.ComunidadeDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.DAO.PostDAO;       // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/moderacao")
public class ModeracaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PostDAO postDAO;
    private ComunidadeDAO comunidadeDAO;

    @Override
    public void init() throws ServletException {
        // 1. Segurança: Instancia os DAOs dentro de um try-catch robusto
        try {
            super.init();
            this.postDAO = new PostDAO();
            this.comunidadeDAO = new ComunidadeDAO();
        } catch (Exception e) {
            System.err.println("Falha na inicialização dos DAOs de Moderação: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // 1. Obter a sessão e o usuário logado
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        String acao = request.getParameter("acao");
        String postIdStr = request.getParameter("id");

        if ("deletar_post_moderacao".equals(acao) && postIdStr != null) {
            deletarPostModeracao(request, response, usuarioLogado, postIdStr);
        } else {
            response.sendRedirect(request.getContextPath() + "/index"); 
        }
    }

    private void deletarPostModeracao(HttpServletRequest request, HttpServletResponse response, 
                                      Usuario usuarioLogado, String postIdStr) throws IOException, ServletException {
        
        int postId;
        try {
            postId = Integer.parseInt(postIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/index?erro=PostIdInvalido");
            return;
        }

        try {
            // 2. Obter a comunidade à qual o post pertence
            int comunidadeId = postDAO.getComunidadeIdPorPost(postId);
            
            if (comunidadeId <= 0) {
                response.sendRedirect(request.getContextPath() + "/index?erro=PostNaoEncontrado");
                return;
            }
            
            // 3. Verificar se o usuário logado é o moderador (criador) da comunidade
            boolean isModerador = comunidadeDAO.verificarAutoridadeModerador(comunidadeId, usuarioLogado.getId());

            if (!isModerador) {
                // Erro de permissão
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Você não tem autoridade para moderar este conteúdo.");
                return;
            }

            // 4. Excluir o post (Método que não checa a autoria)
            boolean sucesso = postDAO.deletarPostModerador(postId);

            // 5. Redirecionar para a URL amigável da comunidade (Correção de Routing)
            if (sucesso) {
                // Buscar o SLUG da comunidade para o redirecionamento /r/slug
                Comunidade comunidade = comunidadeDAO.buscarComunidadePorId(comunidadeId);
                String destino = request.getContextPath() + "/r/" + comunidade.getSlug() + "?msg=post_removido";
                response.sendRedirect(destino);
            } else {
                response.sendRedirect("post?id=" + postId + "&erro=FalhaDeletarPost");
            }
        } catch (Exception e) {
            // Robustez: Captura erros críticos de DB/SQL
            System.err.println("Erro crítico na moderação: " + e.getMessage());
            throw new ServletException("Erro na persistência ao processar moderação.", e);
        }
    }
}