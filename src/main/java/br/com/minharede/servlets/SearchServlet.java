package br.com.minharede.servlets;

import br.com.minharede.models.Post;
import br.com.minharede.models.Comunidade;
import br.com.minharede.DAO.PostDAO;
import br.com.minharede.DAO.ComunidadeDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException; 
import java.util.Collections; 
import java.util.List;

public class SearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
	private PostDAO postDAO;
    private ComunidadeDAO comunidadeDAO;

    @Override
    public void init() throws ServletException {
        try {
           
            this.postDAO = new PostDAO();
            this.comunidadeDAO = new ComunidadeDAO();
            
        
        } catch (Exception e) { 
            System.err.println("Falha inesperada ao iniciar o Servlet: " + e.getMessage());
            throw new ServletException("Erro inesperado durante a inicialização.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    
        String query = request.getParameter("q");

        if (query == null || query.trim().isEmpty()) {
            request.getRequestDispatcher("/resultados.jsp").forward(request, response);
            return;
        }
        
     
        String termoPesquisa = "%" + query.trim().toLowerCase() + "%";

      
        List<Post> postsEncontrados = Collections.emptyList();
        List<Comunidade> comunidadesEncontradas = Collections.emptyList();

        try {
       
            postsEncontrados = postDAO.buscarPostsPorTermo(termoPesquisa);
            comunidadesEncontradas = comunidadeDAO.buscarComunidadesPorTermo(termoPesquisa);
            
        } catch (SQLException e) {
            System.err.println("Erro SQL durante a pesquisa: " + e.getMessage());
          
            
        } catch (Exception e) {
             System.err.println("Erro inesperado durante a pesquisa: " + e.getMessage());
        }
        
      
        request.setAttribute("query", query);
        request.setAttribute("postsEncontrados", postsEncontrados);
        request.setAttribute("comunidadesEncontradas", comunidadesEncontradas);
        
      
        request.getRequestDispatcher("/resultados.jsp").forward(request, response);
    }
}