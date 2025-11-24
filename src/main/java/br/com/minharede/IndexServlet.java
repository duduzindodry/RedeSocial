package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets'

import br.com.minharede.DAO.ComunidadeDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.DAO.PostDAO;       // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Collections;

@WebServlet(name = "IndexServlet", urlPatterns = {"/", "/index"})
public class IndexServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private PostDAO postDAO; 
    private ComunidadeDAO comunidadeDAO;

    @Override
    public void init() throws ServletException {
        try {
            // 1. Segurança: Instancia os DAOs dentro de um try-catch robusto
            this.postDAO = new PostDAO(); 
            this.comunidadeDAO = new ComunidadeDAO();
        } catch (Exception e) {
            System.err.println("Falha ao inicializar DAOs do IndexServlet: " + e.getMessage());
            // Se falhar, impede o carregamento do Servlet (Erro 500 no startup)
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = null;

        if (session != null) {
            usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        }

        List<Post> listaDePosts;
        List<Comunidade> comunidadesSeguidas = Collections.emptyList();
        
        // 1. Lógica de Ordenação
        String sortParam = request.getParameter("sort");
        String orderBy = "data_criacao DESC"; 

        if ("hot".equalsIgnoreCase(sortParam)) {
            orderBy = "votos_recente DESC"; 
        } else if ("top".equalsIgnoreCase(sortParam)) {
            orderBy = "votos DESC"; 
        }

       
        if (usuarioLogado != null) {
            
            try {
               
                comunidadesSeguidas = comunidadeDAO.buscarComunidadesSeguidas(usuarioLogado.getId());
                
               
                if (!comunidadesSeguidas.isEmpty()) {
                    listaDePosts = postDAO.buscarPostsPorComunidades(comunidadesSeguidas, orderBy);
                } else {
                   
                    listaDePosts = postDAO.buscarPostsPopulares(orderBy);
                }
            } catch (Exception e) {
                // 2. Robustez: Em caso de falha de DB (SQLException), loga o erro e mostra o feed global (fallback)
                System.err.println("Erro ao buscar feed personalizado: " + e.getMessage());
                listaDePosts = postDAO.buscarPostsPopulares(orderBy);
            }
        } else {
        
            listaDePosts = postDAO.buscarPostsPopulares(orderBy);
        }

       
        request.setAttribute("posts", listaDePosts);
        request.setAttribute("comunidadesSeguidas", comunidadesSeguidas);
        
        
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}