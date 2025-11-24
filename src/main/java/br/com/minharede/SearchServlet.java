package br.com.minharede;

import br.com.minharede.models.Post;
import br.com.minharede.models.Comunidade;
import br.com.minharede.DAO.PostDAO;
import br.com.minharede.DAO.ComunidadeDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/pesquisar")
public class SearchServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PostDAO postDAO;
    private ComunidadeDAO comunidadeDAO;

    @Override
    public void init() throws ServletException {
        this.postDAO = new PostDAO();
        this.comunidadeDAO = new ComunidadeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Receber o Termo de Busca
        String query = request.getParameter("q");

        if (query == null || query.trim().isEmpty()) {
            // Se a busca estiver vazia, apenas redireciona para a página de resultados vazia
            request.getRequestDispatcher("/resultados.jsp").forward(request, response);
            return;
        }
        
        // Limpar o termo de busca para SQL
        String termoPesquisa = "%" + query.trim().toLowerCase() + "%";

        // 2. Coletar Resultados dos DAOs (Requer novos métodos)
        List<Post> postsEncontrados = postDAO.buscarPostsPorTermo(termoPesquisa);
        List<Comunidade> comunidadesEncontradas = comunidadeDAO.buscarComunidadesPorTermo(termoPesquisa);
        
        // 3. Empacotar e Enviar
        request.setAttribute("query", query);
        request.setAttribute("postsEncontrados", postsEncontrados);
        request.setAttribute("comunidadesEncontradas", comunidadesEncontradas);
        
        request.getRequestDispatcher("/resultados.jsp").forward(request, response);
    }
}