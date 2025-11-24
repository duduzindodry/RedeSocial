package br.com.minharede; // Pacote ajustado para 'servlets'

import br.com.minharede.DAO.VotoDAO;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/votar")
public class VotoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L; // Mantido
    private VotoDAO votoDAO;

    @Override
    public void init() throws ServletException {
        try {
            // Inicializa o DAO
            this.votoDAO = new VotoDAO();
        } catch (Exception e) {
            throw new ServletException("Falha ao inicializar VotoDAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Verificar Autenticação (Ajustado para AJAX)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            // Se não estiver logado, retorna 401 (Unauthorized). O JS captura este status.
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login necessário para votar.");
            return;
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // 2. Coletar e Processar Parâmetros
        try {
            int postId = Integer.parseInt(request.getParameter("postId"));
            int direcao = Integer.parseInt(request.getParameter("direcao")); 
            
            // 3. Validação Básica
            if (direcao != 1 && direcao != -1) {
                System.err.println("Tentativa de voto com direção inválida: " + direcao);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Direção do voto inválida.");
                return;
            }

            // 4. Chamar a Camada DAO para salvar/atualizar o voto
            boolean sucesso = votoDAO.salvarVoto(postId, usuarioLogado.getId(), direcao);
            
            // 5. Enviar Resposta de Sucesso/Falha
            if (sucesso) {
                // Sucesso: Retorna 200 OK e encerra a requisição (essencial para AJAX).
                response.setStatus(HttpServletResponse.SC_OK);
                // Opcional: response.getWriter().write(String.valueOf(novoScore)); se o DAO retornar o score
            } else {
                // Falha do DAO (ex: erro no banco)
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Não foi possível registrar o voto.");
            }

        } catch (NumberFormatException e) {
            // Parâmetros ausentes ou inválidos
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de Post ou Direção inválida.");
        } catch (Exception e) {
            System.err.println("Erro ao processar voto: " + e.getMessage());
            // Erro genérico
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
        }
        
        // Removemos o bloco de redirecionamento (response.sendRedirect)
    }
    
    // Mantido o doPost chamando o doGet
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}