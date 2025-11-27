package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.PostDAO;       
import br.com.minharede.models.Post;
import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException; 

public class PostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PostDAO postDAO;

    @Override
    public void init() throws ServletException {
        try {
            
            this.postDAO = new PostDAO();
        } catch (SQLException e) { 
            System.err.println("Falha na inicialização do PostDAO: " + e.getMessage());
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
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

     
        String titulo = request.getParameter("titulo");
        String conteudo = request.getParameter("conteudo");
        String tipo = request.getParameter("tipo"); 
        String comunidadeIdParam = request.getParameter("comunidadeId");

        if (titulo == null || titulo.trim().isEmpty() || conteudo == null || conteudo.trim().isEmpty() || comunidadeIdParam == null) {
            request.setAttribute("error", "campos_vazios");
            request.getRequestDispatcher("/criar-post.jsp").forward(request, response);
            return;
        }
        
     
        if (titulo.length() > 255) {
             titulo = titulo.substring(0, 255);
        }

        try {
            int comunidadeId = Integer.parseInt(comunidadeIdParam);

      
            Post novoPost = new Post();
            novoPost.setTitulo(titulo.trim());
            novoPost.setConteudo(conteudo.trim());
            novoPost.setTipo(tipo != null ? tipo : "TEXTO");

  
            novoPost.setUsuario(usuarioLogado);
            
            Comunidade comunidadeReferencia = new Comunidade();
            comunidadeReferencia.setId(comunidadeId);
            novoPost.setComunidade(comunidadeReferencia);

       
            int postId = postDAO.salvarPost(novoPost); 

           
            if (postId > 0) {
               
                response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
            } else {
              
                request.setAttribute("error", "db_falha");
                request.getRequestDispatcher("/criar-post.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "id_comunidade_invalido");
            request.getRequestDispatcher("/criar-post.jsp").forward(request, response);
        } catch (SQLException e) { 
            System.err.println("Erro SQL ao salvar post: " + e.getMessage());
            request.setAttribute("error", "db_falha");
            request.getRequestDispatcher("/criar-post.jsp").forward(request, response);
        } catch (Exception e) {
            
            System.err.println("Erro inesperado ao salvar post: " + e.getMessage());
            throw new ServletException("Erro na persistência ao criar post.", e);
        }
    }
}