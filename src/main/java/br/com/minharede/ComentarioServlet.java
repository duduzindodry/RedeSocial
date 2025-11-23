package br.com.minharede;

import br.com.minharede.DAO.ComentarioDAO;
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

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ComentarioDAO comentarioDAO;

    @Override
    public void init() throws ServletException {
        this.comentarioDAO = new ComentarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Verificar Autenticação
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "É necessário estar logado para comentar.");
            return;
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // 2. Coletar Parâmetros
        try {
            // ID do Post
            int postId = Integer.parseInt(request.getParameter("postId"));
            // Conteúdo do Comentário
            String conteudo = request.getParameter("conteudo");
            
            if (conteudo == null || conteudo.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "O comentário não pode estar vazio.");
                return;
            }

            // 3. Montar o Objeto Comentario
            Comentario novoComentario = new Comentario();
            novoComentario.setConteudo(conteudo.trim());
            
            // Setar o Post e o Usuario
            novoComentario.setUsuario(usuarioLogado);
            novoComentario.setPost(new Post()); // Usamos um objeto Post com apenas o ID

            // 4. Salvar no Banco de Dados
            comentarioDAO.adicionarComentario(novoComentario);

            // 5. Redirecionar para a página do post/view
            // Se você não tem uma view de post individual, volte para o index
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/index");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do post inválido.");
        } catch (Exception e) {
            System.err.println("Erro ao processar comentário: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno ao salvar o comentário.");
        }
    }
}