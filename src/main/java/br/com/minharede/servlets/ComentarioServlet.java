package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.ComentarioDAO; 
import br.com.minharede.models.Comentario;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException; 

public class ComentarioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ComentarioDAO comentarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            
            this.comentarioDAO = new ComentarioDAO();
        } catch (SQLException e) { 
            System.err.println("Falha ao inicializar ComentarioDAO: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO devido a erro de conexão.", e);
        } catch (Exception e) {
            throw new ServletException("Erro inesperado na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "É necessário estar logado para comentar.");
            return;
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

       
        try {
            String postIdParam = request.getParameter("postId");
            String conteudo = request.getParameter("conteudo");
            
            
            if (conteudo == null || conteudo.trim().isEmpty() || postIdParam == null || postIdParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros inválidos.");
                return;
            }
            
            int postId = Integer.parseInt(postIdParam);

           
            Comentario novoComentario = new Comentario();
            novoComentario.setConteudo(conteudo.trim());
           
            Post postReferencia = new Post();
            postReferencia.setId(postId);
            
            novoComentario.setUsuario(usuarioLogado);
            novoComentario.setPost(postReferencia);

            
            comentarioDAO.adicionarComentario(novoComentario);

         
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/post?id=" + postId); 
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do post inválido.");
        } catch (SQLException e) { 
            
            System.err.println("Erro SQL ao processar comentário: " + e.getMessage());
            throw new ServletException("Erro na camada de persistência ao salvar o comentário.", e);
        } catch (Exception e) {
            
            System.err.println("Erro inesperado ao processar comentário: " + e.getMessage());
            throw new ServletException("Erro inesperado no servidor.", e);
        }
    }
}