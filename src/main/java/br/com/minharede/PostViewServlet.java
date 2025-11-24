package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets'

import br.com.minharede.DAO.ComentarioDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.DAO.PostDAO;       // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Post;
import br.com.minharede.models.Comentario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Collections; // Adicionado import

@WebServlet("/post")
public class PostViewServlet extends HttpServlet {

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
            System.err.println("Falha na inicialização dos DAOs de PostView: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String postIdParam = request.getParameter("id");

        if (postIdParam == null || postIdParam.isEmpty()) {
            // Se o ID do post não for fornecido, redireciona para o índice
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        try {
            int postId = Integer.parseInt(postIdParam);

            // 1. Buscar o Post
            Post post = postDAO.buscarPostPorId(postId);
            
            if (post == null) {
                // Post não encontrado (retorna 404, que será capturado pelo web.xml)
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Postagem não encontrada.");
                return;
            }

            // 2. Buscar os Comentários
            List<Comentario> comentarios = comentarioDAO.listarComentariosPorPost(postId);
            // Garante que a lista não seja nula
            if (comentarios == null) comentarios = Collections.emptyList();

            // 3. Empacotar e Enviar para o JSP
            request.setAttribute("post", post);
            request.setAttribute("comentarios", comentarios);
            
            request.getRequestDispatcher("/post.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            // ID inválido (não é um número)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do post inválido.");
        } catch (Exception e) {
            // Robustez: Captura falhas de DB (SQLException) ou outras exceções críticas
            System.err.println("Erro ao carregar view do post: " + e.getMessage());
            // Lança uma ServletException, que será capturada pelo web.xml (Erro 500)
            throw new ServletException("Erro na persistência ao carregar a página do post.", e);
        }
    }
}