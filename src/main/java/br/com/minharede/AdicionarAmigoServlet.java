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

@WebServlet("/adicionar-amigo")
public class AdicionarAmigoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            // 1. Segurança: Instancie o DAO dentro de um try-catch robusto
            this.usuarioDAO = new UsuarioDAO();
        } catch (Exception e) {
            System.err.println("Falha ao inicializar UsuarioDAO: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Verificar Autenticação
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        // 2. Segurança: Checa login e redireciona (melhor prática que usar sendError 401)
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        // 3. Coletar o ID do receptor (amigo a ser adicionado)
        String receptorIdParam = request.getParameter("id"); 
        
        if (receptorIdParam == null || receptorIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do usuário receptor é obrigatório.");
            return;
        }

        try {
            int receptorId = Integer.parseInt(receptorIdParam);
            int solicitanteId = usuarioLogado.getId();

            // 4. Validação: Evitar adicionar a si mesmo
            if (solicitanteId == receptorId) {
                // Redireciona para o perfil com uma mensagem de erro mais específica
                response.sendRedirect(request.getContextPath() + "/perfil?id=" + solicitanteId + "&error=nao_pode_adicionar_si");
                return;
            }

            // 5. Chamar o DAO para registrar a solicitação (PENDENTE)
            boolean sucesso = usuarioDAO.solicitarAmizade(solicitanteId, receptorId);

            // 6. Redirecionar com feedback
            String destino = request.getContextPath() + "/perfil?id=" + receptorId;

            if (sucesso) {
                destino += "&msg=solicitacao_enviada";
            } else {
                // Se falhou (geralmente porque já existe um registro PK violation)
                destino += "&error=solicitacao_existente";
            }
            
            response.sendRedirect(destino);

        } catch (NumberFormatException e) {
            // Trata erro de ID inválido
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuário inválido.");
        } catch (Exception e) {
            // 7. Robustez: Trata falhas de DB (SQLException)
            System.err.println("Erro ao processar solicitação de amizade: " + e.getMessage());
            throw new ServletException("Erro na camada de persistência ao enviar solicitação.", e);
        }
    }
}