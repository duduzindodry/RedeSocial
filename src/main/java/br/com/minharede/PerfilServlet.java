package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets'

import br.com.minharede.DAO.ComentarioDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.DAO.PostDAO;       // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.DAO.UsuarioDAO;     // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Comentario;
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

@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;
    private PostDAO postDAO;
    private ComentarioDAO comentarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            // 1. Segurança: Instancia os DAOs dentro de um try-catch robusto
            this.usuarioDAO = new UsuarioDAO();
            this.postDAO = new PostDAO();
            this.comentarioDAO = new ComentarioDAO();
        } catch (Exception e) {
            System.err.println("Falha na inicialização dos DAOs do PerfilServlet: " + e.getMessage());
            throw new ServletException("Falha na inicialização dos DAOs.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuarioPerfil;
        int targetUserId; 

        // 1. Determinar o Usuário Alvo (Prioriza o ID do parâmetro URL)
        String userIdParam = request.getParameter("id"); 

        if (userIdParam != null && !userIdParam.isEmpty()) {
            // Buscando o perfil de OUTRO usuário (pelo ID na URL)
            try {
                targetUserId = Integer.parseInt(userIdParam);
                usuarioPerfil = usuarioDAO.buscarPorId(targetUserId); 
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuário inválido.");
                return;
            }
        } else {
            // Buscando o PRÓPRIO perfil
            Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);
            if (usuarioLogado == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=perfil_required");
                return;
            }
            targetUserId = usuarioLogado.getId();
            usuarioPerfil = usuarioLogado;
        }

        // Se o ID foi passado, mas o usuário não existe
        if (usuarioPerfil == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado.");
            return;
        }

        // 2. Coletar Atividades (Envolva a lógica de DB em try-catch)
        try {
            List<Post> postsCriados = postDAO.buscarPostsPorUsuario(targetUserId);
            List<Comentario> comentariosFeitos = comentarioDAO.buscarComentariosPorUsuario(targetUserId);
            
            // 2.5. Coletar o Karma 
            int karma = postDAO.calcularKarmaTotal(targetUserId); 
            
            // 3. Empacotar e Enviar
            request.setAttribute("usuarioPerfil", usuarioPerfil);
            request.setAttribute("postsCriados", postsCriados);
            request.setAttribute("comentariosFeitos", comentariosFeitos);
            request.setAttribute("karmaTotal", karma); 

            request.getRequestDispatcher("/perfil.jsp").forward(request, response);

        } catch (Exception e) {
            // Captura erros críticos de DB/SQL
            System.err.println("Erro de acesso ao DB no PerfilServlet: " + e.getMessage());
            throw new ServletException("Falha ao carregar dados do perfil.", e); 
        }
    }
}