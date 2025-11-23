package br.com.minharede;

import br.com.minharede.DAO.ComunidadeDAO;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/seguirComunidade")
public class SeguirComunidadeServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ComunidadeDAO comunidadeDAO;

    @Override
    public void init() throws ServletException {
        this.comunidadeDAO = new ComunidadeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Verificar Autenticação
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        // 2. Coletar Parâmetros
        String comunidadeIdParam = request.getParameter("comunidadeId");

        if (comunidadeIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da comunidade não fornecido.");
            return;
        }

        try {
            int comunidadeId = Integer.parseInt(comunidadeIdParam);

            // 3. Executar a lógica de alternância (INSERT ou DELETE)
            comunidadeDAO.alternarSeguimento(usuarioLogado.getId(), comunidadeId);

            // 4. Redirecionar
            // Redireciona para o cabeçalho "Referer" (a URL de onde o usuário veio)
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/index");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da comunidade inválido.");
        }
    }
}
