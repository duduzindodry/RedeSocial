package br.com.minharede.servlets;

import br.com.minharede.models.Usuario;
import br.com.minharede.models.Post;         
import br.com.minharede.models.Comunidade;   
import br.com.minharede.DAO.PostDAO;         
import br.com.minharede.DAO.ComunidadeDAO;   
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException; 
import java.util.Collections;
import java.util.List;


public class IndexServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
   
    private PostDAO postDAO; 
    private ComunidadeDAO comunidadeDAO; 

    
    @Override
    public void init() throws ServletException {
        try {
            
            this.postDAO = new PostDAO();
            this.comunidadeDAO = new ComunidadeDAO();
            
        } catch (Exception e) { 
            
            System.err.println("Falha CRÍTICA na inicialização dos DAOs: " + e.getMessage());
            e.printStackTrace(); 
            
           
            throw new ServletException("Erro ao iniciar IndexServlet: Falha nos DAOs.", e);
        }
    }
    

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        
        HttpSession session = request.getSession(false); 
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        
        List<Post> posts = Collections.emptyList();
        List<Comunidade> comunidadesSeguidas = Collections.emptyList();
        
        
        
        try { 
            
            if (usuarioLogado != null) {
                
                posts = postDAO.buscarFeedPersonalizado(usuarioLogado.getId());
                
                
                comunidadesSeguidas = comunidadeDAO.buscarComunidadesSeguidas(usuarioLogado.getId());
            } else {
               
                posts = postDAO.buscarFeedGlobal();
                
               
            }

        } catch (SQLException e) { 
            System.err.println("Erro SQL ao carregar dados do feed: " + e.getMessage());
            
            
        } catch (Exception e) { 
             System.err.println("Erro inesperado ao carregar dados do feed: " + e.getMessage());
             
        }

        request.setAttribute("posts", posts);
        request.setAttribute("comunidadesSeguidas", comunidadesSeguidas);
        
        
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}