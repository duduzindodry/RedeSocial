package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.ComunidadeDAO; 
import br.com.minharede.DAO.PostDAO;       
import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException; 
import java.util.Collections;
import java.util.List;



public class ComunidadeViewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ComunidadeDAO comunidadeDAO;
    private PostDAO postDAO;

    @Override
    public void init() throws ServletException {
        try {
            
            this.comunidadeDAO = new ComunidadeDAO();
            this.postDAO = new PostDAO();
        } catch (SQLException e) {
            System.err.println("Falha ao inicializar DAOs de ComunidadeView: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO devido a erro de conexão.", e);
        } catch (Exception e) {
            throw new ServletException("Erro inesperado na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        
        
        try {
            
            String pathInfo = request.getPathInfo(); 
            if (pathInfo == null || pathInfo.length() < 2) {
                
                response.sendRedirect(request.getContextPath() + "/index");
                return;
            }
            String slug = pathInfo.substring(1); 

     
            Comunidade comunidade = comunidadeDAO.buscarComunidadePorSlug(slug);

            if (comunidade == null) {
               
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Comunidade não encontrada: r/" + slug);
                return;
            }

            boolean isSeguindo = false;
            HttpSession session = request.getSession(false);
            Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

            if (usuarioLogado != null) {
                isSeguindo = comunidadeDAO.isSeguindo(usuarioLogado.getId(), comunidade.getId()); 
            }

            List<Post> posts = postDAO.buscarPostsPorComunidadeId(comunidade.getId());
            if (posts == null) posts = Collections.emptyList();

            request.setAttribute("comunidade", comunidade);
            request.setAttribute("posts", posts);
            request.setAttribute("isSeguindo", isSeguindo);

            request.getRequestDispatcher("/comunidade.jsp").forward(request, response);
            
        } catch (SQLException e) { 
            System.err.println("Erro SQL ao carregar view da comunidade: " + e.getMessage());
            throw new ServletException("Erro na camada de persistência ao carregar a comunidade.", e);
        } catch (Exception e) {
            System.err.println("Erro inesperado ao carregar view da comunidade: " + e.getMessage());
            throw new ServletException("Erro crítico no servidor.", e);
        }
    }
}