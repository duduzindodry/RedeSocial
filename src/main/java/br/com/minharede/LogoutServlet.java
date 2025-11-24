package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets' para consistência

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // Adicionar import para a anotação
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout") // 1. Mapeamento necessário para a rota /logout
public class LogoutServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtém a sessão existente (não cria uma nova)
        HttpSession sessao = request.getSession(false);

        if (sessao != null) {
            // 2. Invalida a sessão, removendo todos os atributos (incluindo 'usuarioLogado')
            sessao.invalidate();
        }

        // 3. Redireciona para a página de login com feedback
        // Adiciona request.getContextPath() para robustez em qualquer ambiente
        response.sendRedirect(request.getContextPath() + "/login.jsp?logout=sucesso");
    }
    
    // Opcional: Chama o doGet para lidar com requisições POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}