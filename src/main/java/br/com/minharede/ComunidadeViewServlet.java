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
import java.util.Collections;
import java.util.List;

// Mapeia o padrão de URL /r/qualquercoisa
@WebServlet("/r/*") 
public class ComunidadeViewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ComunidadeDAO comunidadeDAO;
    private PostDAO postDAO;

    @Override
    public void init() throws ServletException {
        try {
            // 1. Segurança: Inicializa os DAOs dentro de um try-catch robusto
            this.comunidadeDAO = new ComunidadeDAO();
            this.postDAO = new PostDAO();
        } catch (Exception e) {
            System.err.println("Falha ao inicializar DAOs de ComunidadeView: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 2. Encapsula toda a lógica do DB em um try-catch para robustez
        try {
            // 1. Extrair o SLUG da URL
            String pathInfo = request.getPathInfo(); 
            if (pathInfo == null || pathInfo.length() < 2) {
                response.sendRedirect(request.getContextPath() + "/index");
                return;
            }
            String slug = pathInfo.substring(1); 

            // 2. Buscar a Comunidade
            Comunidade comunidade = comunidadeDAO.buscarComunidadePorSlug(slug);

            if (comunidade == null) {
                // Se não encontrar, lança 404 (que será capturado pelo web.xml)
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Comunidade não encontrada: r/" + slug);
                return;
            }

            // 3. Verificar status de seguimento
            boolean isSeguindo = false;
            HttpSession session = request.getSession(false);
            Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

            if (usuarioLogado != null) {
                // Assume que o método isSeguindo(usuarioId, comunidadeId) existe no ComunidadeDAO
                isSeguindo = comunidadeDAO.isSeguindo(usuarioLogado.getId(), comunidade.getId());
            }

            // 4. Buscar os Posts da Comunidade
            List<Post> posts = postDAO.buscarPostsPorComunidadeId(comunidade.getId());
            if (posts == null) posts = Collections.emptyList();

            // 5. Empacotar e Encaminhar
            request.setAttribute("comunidade", comunidade);
            request.setAttribute("posts", posts);
            request.setAttribute("isSeguindo", isSeguindo);

            request.getRequestDispatcher("/comunidade.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Captura falhas de DB ou outros erros críticos
            System.err.println("Erro crítico ao carregar view da comunidade: " + e.getMessage());
            throw new ServletException("Erro na camada de persistência ao carregar a comunidade.", e);
        }
    }
}