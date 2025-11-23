package br.com.minharede;

import br.com.minharede.models.Usuario;
import br.com.minharede.DAO.VotoDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/votar") // Mapeamento da URL para o servlet
public class VotoServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VotoDAO votoDAO;

    @Override
    public void init() throws ServletException {
        // Inicializa o DAO
        this.votoDAO = new VotoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Verificar Autenticação
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            // Se o usuário não estiver logado, redireciona para a página de login
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // 2. Coletar Parâmetros
        try {
            int postId = Integer.parseInt(request.getParameter("postId"));
            int direcao = Integer.parseInt(request.getParameter("direcao")); // Deve ser 1 ou -1
            
            // 3. Validação Básica
            if (direcao != 1 && direcao != -1) {
                // Se a direção não for válida, logar e ignorar
                System.err.println("Tentativa de voto com direção inválida: " + direcao);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Direção do voto inválida.");
                return;
            }

            // 4. Chamar a Camada DAO para salvar/atualizar o voto
            votoDAO.salvarVoto(postId, usuarioLogado.getId(), direcao);

            // 5. Redirecionar de volta
            // Redireciona para o cabeçalho "Referer" (a URL de onde o usuário veio: index, feed, ou página do post)
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                // Se não houver Referer (acesso direto), volta para o index
                response.sendRedirect(request.getContextPath() + "/index");
            }

        } catch (NumberFormatException e) {
            // Lidar com parâmetros ausentes ou inválidos
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros do post ausentes ou inválidos.");
        } catch (Exception e) {
            // Lidar com erros de banco de dados
            System.err.println("Erro ao processar voto: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno ao processar o voto.");
        }
    }
    
    // O POST pode simplesmente chamar o doGet para simplificar, já que a ação não modifica o estado da aplicação além do DB.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}