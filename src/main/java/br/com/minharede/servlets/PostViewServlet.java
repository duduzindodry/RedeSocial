package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.ComentarioDAO; 
import br.com.minharede.DAO.PostDAO;       
import br.com.minharede.models.Post;
import br.com.minharede.models.Comentario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException; 
import java.util.List;
import java.util.Collections;


public class PostViewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PostDAO postDAO;
    private ComentarioDAO comentarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            
            this.postDAO = new PostDAO();
            this.comentarioDAO = new ComentarioDAO();
        } catch (SQLException e) { 
            System.err.println("Falha na inicialização dos DAOs de PostView: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO devido a erro de conexão.", e);
        } catch (Exception e) {
            throw new ServletException("Erro inesperado na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String postIdParam = request.getParameter("id");

        if (postIdParam == null || postIdParam.isEmpty()) {
     
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        try {
            int postId = Integer.parseInt(postIdParam);

           
            Post post = postDAO.buscarPostPorId(postId);
            
            if (post == null) {
              
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Postagem não encontrada.");
                return;
            }

           
            List<Comentario> comentarios = comentarioDAO.listarComentariosPorPost(postId);
            if (comentarios == null) comentarios = Collections.emptyList();

            request.setAttribute("post", post);
            request.setAttribute("comentarios", comentarios);
            
            request.getRequestDispatcher("/post.jsp").forward(request, response);

        } catch (NumberFormatException e) {
           
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do post inválido.");
        } catch (SQLException e) { 
            System.err.println("Erro SQL ao carregar view do post: " + e.getMessage());
            throw new ServletException("Erro na persistência ao carregar a página do post.", e);
        } catch (Exception e) {
            
            System.err.println("Erro inesperado ao carregar view do post: " + e.getMessage());
            throw new ServletException("Erro na persistência ao carregar a página do post.", e);
        }
    }
}