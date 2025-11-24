package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets' para consistência

import br.com.minharede.DAO.UsuarioDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/remover-amigo")
public class RemoverAmigoServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
	private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException { 
        try {
            // 1. Segurança: Inicializa o DAO dentro de um try-catch robusto
            this.usuarioDAO = new UsuarioDAO(); 
        } catch (Exception e) {
            System.err.println("Falha ao inicializar UsuarioDAO: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        // 2. Segurança: Redireciona para o login se não estiver autenticado
        if (usuarioLogado == null) { 
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required"); 
            return; 
        }

        String amigoIdParam = request.getParameter("id"); // Pode ser um amigo ou um solicitante
        
        if (amigoIdParam == null || amigoIdParam.isEmpty()) { 
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de amigo não fornecido."); 
            return; 
        }

        try {
            int amigoId = Integer.parseInt(amigoIdParam);
            
            // 3. Ação: Remove/rejeita o registro. A ação DELETE remove PENDENTE ou ACEITA.
            boolean sucesso = usuarioDAO.removerAmizade(usuarioLogado.getId(), amigoId);
            
            // 4. Redirecionar com feedback para a página de amigos
            String destino = request.getContextPath() + "/amigos";
            destino += sucesso ? "?msg=amizade_removida" : "?error=falha_remover";
            
            response.sendRedirect(destino);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de amigo inválido.");
        } catch (Exception e) {
            // 5. Robustez: Trata falhas de DB (SQLException)
            System.err.println("Erro ao processar remoção de amizade: " + e.getMessage());
            throw new ServletException("Erro na camada de persistência ao remover amizade.", e);
        }
    }
}