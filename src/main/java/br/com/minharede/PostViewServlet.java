package br.com.minharede;

import br.com.minharede.models.Post;
import br.com.minharede.models.Comentario;
import br.com.minharede.DAO.PostDAO;
import br.com.minharede.DAO.ComentarioDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/post")
public class PostViewServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PostDAO postDAO;
    private ComentarioDAO comentarioDAO;

    @Override
    public void init() throws ServletException {
        this.postDAO = new PostDAO();
        this.comentarioDAO = new ComentarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String postIdParam = request.getParameter("id");

        if (postIdParam == null) {
            // Se o ID do post não for fornecido, redireciona para o índice
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        try {
            int postId = Integer.parseInt(postIdParam);

            // 1. Buscar o Post
            Post post = postDAO.buscarPostPorId(postId);
            
            if (post == null) {
                // Post não encontrado (retorna 404)
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Postagem não encontrada.");
                return;
            }

            // 2. Buscar os Comentários
            List<Comentario> comentarios = comentarioDAO.listarComentariosPorPost(postId);

            // 3. Empacotar e Enviar para o JSP
            request.setAttribute("post", post);
            request.setAttribute("comentarios", comentarios);
            
            request.getRequestDispatcher("/post.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do post inválido.");
        } catch (Exception e) {
            System.err.println("Erro ao carregar view do post: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno ao carregar a página.");
        }
    }
}
