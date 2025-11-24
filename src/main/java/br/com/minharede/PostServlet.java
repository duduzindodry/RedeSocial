package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets'

import br.com.minharede.DAO.PostDAO;       // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Post;
import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/postar") // Mapeamento para onde o formulário será enviado
public class PostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PostDAO postDAO;

    @Override
    public void init() throws ServletException {
        try {
            // 1. Segurança: Instancia o DAO dentro de um try-catch robusto
            this.postDAO = new PostDAO();
        } catch (Exception e) {
            System.err.println("Falha na inicialização do PostDAO: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        // 1. Verificar Autenticação
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        // 2. Coletar e Validar Parâmetros
        String titulo = request.getParameter("titulo");
        String conteudo = request.getParameter("conteudo");
        String tipo = request.getParameter("tipo"); // TEXTO, LINK, IMAGEM
        String comunidadeIdParam = request.getParameter("comunidadeId");

        // --- Validação de Entrada (Segurança e Integridade) ---
        if (titulo == null || titulo.trim().isEmpty() || conteudo == null || conteudo.trim().isEmpty() || comunidadeIdParam == null) {
            request.setAttribute("error", "campos_vazios");
            request.getRequestDispatcher("/criar-post.jsp").forward(request, response);
            return;
        }
        
        // Limitar tamanho do título para evitar erros no DB
        if (titulo.length() > 255) {
             titulo = titulo.substring(0, 255);
        }

        try {
            int comunidadeId = Integer.parseInt(comunidadeIdParam);

            // 3. Montar o Objeto Post
            Post novoPost = new Post();
            novoPost.setTitulo(titulo.trim());
            novoPost.setConteudo(conteudo.trim());
            novoPost.setTipo(tipo != null ? tipo : "TEXTO");

            // Setar as FKs
            novoPost.setUsuario(usuarioLogado);
            
            // É essencial que a classe Comunidade tenha um construtor que aceite apenas o ID
            Comunidade comunidadeReferencia = new Comunidade();
            comunidadeReferencia.setId(comunidadeId);
            novoPost.setComunidade(comunidadeReferencia);

            // 4. Salvar no Banco de Dados
            int postId = postDAO.salvarPost(novoPost);

            // 5. Redirecionar
            if (postId > 0) {
                // Sucesso: Redireciona para a nova página do post
                response.sendRedirect(request.getContextPath() + "/post?id=" + postId);
            } else {
                // Falha no DAO/DB
                request.setAttribute("error", "db_falha");
                request.getRequestDispatcher("/criar-post.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "id_comunidade_invalido");
            request.getRequestDispatcher("/criar-post.jsp").forward(request, response);
        } catch (Exception e) {
            // Robustez: Captura erros críticos de DB/SQL e relança
            System.err.println("Erro interno ao salvar post: " + e.getMessage());
            throw new ServletException("Erro na persistência ao criar post.", e);
        }
    }
}