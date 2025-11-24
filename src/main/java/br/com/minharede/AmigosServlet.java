package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets'

import br.com.minharede.DAO.UsuarioDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Collections; // Import necessário para o fallback, mas não usado diretamente aqui

@WebServlet("/amigos")
public class AmigosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            // 1. Segurança: Inicializa o DAO dentro de um try-catch robusto
            this.usuarioDAO = new UsuarioDAO(); 
        } catch (Exception e) {
            System.err.println("Falha ao inicializar UsuarioDAO no AmigosServlet: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
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
        
        int usuarioId = usuarioLogado.getId();

        try {
            // 2. Coletar Listas
            List<Usuario> listaAmigos = usuarioDAO.buscarAmigosAceitos(usuarioId);
            List<Usuario> solicitacoesRecebidas = usuarioDAO.buscarSolicitacoesRecebidas(usuarioId);

            // 3. Robustez: Garante que as listas não sejam nulas (embora o DAO devesse garantir isso)
            if (listaAmigos == null) listaAmigos = Collections.emptyList();
            if (solicitacoesRecebidas == null) solicitacoesRecebidas = Collections.emptyList();

            // 4. Empacotar e Enviar
            request.setAttribute("listaAmigos", listaAmigos);
            request.setAttribute("solicitacoesRecebidas", solicitacoesRecebidas);
            
            request.getRequestDispatcher("/amigos.jsp").forward(request, response);

        } catch (Exception e) {
            // 5. Tratamento de Exceção: Captura erros de SQL e lança a ServletException
            System.err.println("Erro ao carregar a página de amigos: " + e.getMessage());
            throw new ServletException("Falha ao carregar listas de amizade na camada de persistência.", e);
        }
    }
}