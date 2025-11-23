package br.com.minharede;

import br.com.minharede.DAO.PostDAO;
import br.com.minharede.models.Post;
import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/postar") // Mapeamento para onde o formulário será enviado
public class PostServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PostDAO postDAO;

    @Override
    public void init() throws ServletException {
        this.postDAO = new PostDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Configurar a codificação para receber acentos corretamente (se necessário)
        request.setCharacterEncoding("UTF-8");

        // 1. Verificar Autenticação
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        // 2. Coletar e Validar Parâmetros
        String titulo = request.getParameter("titulo");
        String conteudo = request.getParameter("conteudo");
        String tipo = request.getParameter("tipo"); // TEXTO, LINK, IMAGEM
        String comunidadeIdParam = request.getParameter("comunidadeId");

        // --- Validação de Entrada (Segurança) ---
        if (titulo == null || titulo.trim().isEmpty() || conteudo == null || conteudo.trim().isEmpty() || comunidadeIdParam == null) {
            // Redireciona com uma mensagem de erro se campos essenciais estiverem vazios
            response.sendRedirect(request.getContextPath() + "/criar-post.jsp?error=campos_vazios");
            return;
        }

        try {
            int comunidadeId = Integer.parseInt(comunidadeIdParam);

            // 3. Montar o Objeto Post
            Post novoPost = new Post();
            novoPost.setTitulo(titulo.trim());
            novoPost.setConteudo(conteudo.trim());
            novoPost.setTipo(tipo != null ? tipo : "TEXTO"); // Garante um valor padrão

            // Setar as FKs
            novoPost.setUsuario(usuarioLogado);
            novoPost.setComunidade(new Comunidade()); // Usamos um objeto Comunidade com apenas o ID

            // 4. Salvar no Banco de Dados
            // O seu PostDAO precisa do método salvarPost()
            int postId = postDAO.salvarPost(novoPost);

            // 5. Redirecionar
            if (postId > 0) {
                // Redireciona para a nova página do post
                response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
            } else {
                // Falha no DAO
                response.sendRedirect(request.getContextPath() + "/criar-post.jsp?error=db_falha");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/criar-post.jsp?error=id_comunidade_invalido");
        } catch (Exception e) {
            System.err.println("Erro ao salvar post: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno ao criar post.");
        }
    }
}
