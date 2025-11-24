package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets'

import br.com.minharede.DAO.UsuarioDAO; // CORRIGIDO: Pacote deve ser 'dao'
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/aceitar-amigo")
public class AceitarAmigoServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
	private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException { 
        try {
            // 1. Segurança: Trata a falha na conexão do DAO durante a inicialização
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
            response.sendRedirect("login.jsp?error=login_required"); 
            return; 
        }

        String solicitanteIdParam = request.getParameter("id");
        if (solicitanteIdParam == null || solicitanteIdParam.isEmpty()) { 
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do solicitante não fornecido."); 
            return; 
        }

        try {
            int solicitanteId = Integer.parseInt(solicitanteIdParam);
            
            // Lógica de aceitação
            boolean sucesso = usuarioDAO.aceitarAmizade(usuarioLogado.getId(), solicitanteId);
            
            String destino = request.getContextPath() + "/amigos";
            destino += sucesso ? "?msg=amizade_aceita" : "?error=falha_aceitar";
            
            response.sendRedirect(destino);

        } catch (NumberFormatException e) {
            // Trata se o ID não for um número
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de solicitante inválido.");
        } catch (Exception e) {
            // 3. Robustez: Trata falhas de DB (SQLException)
            System.err.println("Erro ao processar aceitação de amizade: " + e.getMessage());
            throw new ServletException("Erro na camada de persistência ao aceitar amizade.", e);
        }
    }
}
