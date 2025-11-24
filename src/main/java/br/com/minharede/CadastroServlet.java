package br.com.minharede; // PACOTE CORRIGIDO: Assumindo que o local correto é 'servlets'

import br.com.minharede.DAO.UsuarioDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO; 

    // ----------------------------------------------------
    // INICIALIZAÇÃO: Ocorre apenas uma vez
    // ----------------------------------------------------
    @Override
    public void init() throws ServletException {
        try {
            this.usuarioDAO = new UsuarioDAO(); 
        } catch (Exception e) {
            System.err.println("Falha na inicialização do UsuarioDAO: " + e.getMessage());
            // Lança a exceção para que o servidor falhe ao iniciar o Servlet
            throw new ServletException("Falha na inicialização do Servle. Driver/Conexão DB.", e);
        }
    }

    // ----------------------------------------------------
    // MÉTODO GET: Exibe o Formulário
    // ----------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Encaminha para o JSP do formulário, resolvendo a rota /cadastro
        request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
    }
    
    // ----------------------------------------------------
    // MÉTODO POST: Processa o Registro
    // ----------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nome");
        String usuario = request.getParameter("usuario"); 
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // 1. Validação Crucial de Entrada (Segurança)
        if (nome == null || nome.trim().isEmpty() || senha == null || senha.isEmpty()) {
            request.setAttribute("erro", "Todos os campos são obrigatórios.");
            request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
            return;
        }

        try {
            // 2. Chamada ao DAO
            // NOTA: Se você usa HASHING (BCrypt), ele deve ser aplicado aqui, ANTES de chamar o DAO!
            boolean sucesso = usuarioDAO.cadastrarUsuario(nome, usuario, email, senha); 
            
            if (sucesso) {
                // 3. Sucesso: Redireciona para o login
                response.sendRedirect(request.getContextPath() + "/login.jsp?cadastro=sucesso");
            } else {
                // 4. Falha (Geralmente por PK/Unique Key violation no DB)
                request.setAttribute("erro", "Nome de usuário ou email já cadastrado.");
                request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            // 5. Captura erros críticos de SQL ou DB
            System.err.println("Erro crítico durante o cadastro: " + e.getMessage());
            throw new ServletException("Falha na persistência dos dados de cadastro.", e);
        }
    }
}