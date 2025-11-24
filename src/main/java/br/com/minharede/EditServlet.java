package br.com.minharede; // PACOTE CORRIGIDO: Deve ser 'servlets' para consistência

import br.com.minharede.DAO.ComentarioDAO; // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.DAO.PostDAO;       // CORRIGIDO: Pacote deve ser 'dao' minúsculo
import br.com.minharede.models.Comentario;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/editar")
public class EditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PostDAO postDAO;
    private ComentarioDAO comentarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            // 1. Segurança: Instancia os DAOs dentro de um try-catch robusto
            this.postDAO = new PostDAO();
            this.comentarioDAO = new ComentarioDAO();
        } catch (Exception e) {
            System.err.println("Falha na inicialização dos DAOs de edição: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO.", e);
        }
    }

    // ----------------------------------------------------
    // MÉTODO GET: Carrega o Item para Edição
    // ----------------------------------------------------
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

        // 2. Coletar Parâmetros e Tipo
        String tipo = request.getParameter("tipo"); // 'post' ou 'comentario'
        String idParam = request.getParameter("id"); 
        
        if (tipo == null || idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros 'tipo' e 'id' são obrigatórios.");
            return;
        }

        try {
            int itemId = Integer.parseInt(idParam);
            boolean isAuthor = false;
            
            // 3. Buscar Item e Verificar Autoria
            if (tipo.equalsIgnoreCase("post")) {
                // Assume que o método buscarPostPorIdSimples existe e retorna os IDs
                Post post = postDAO.buscarPostPorIdSimples(itemId); 
                
                if (post != null) {
                    isAuthor = post.getUsuario().getId() == usuarioLogado.getId();
                    request.setAttribute("item", post);
                }

            } else if (tipo.equalsIgnoreCase("comentario")) {
                // Assume que o método buscarComentarioPorId existe e retorna os IDs
                Comentario comentario = comentarioDAO.buscarComentarioPorId(itemId); 
                
                if (comentario != null) {
                    isAuthor = comentario.getUsuario().getId() == usuarioLogado.getId();
                    request.setAttribute("item", comentario);
                }
            }
            
            // 4. Checar Permissão
            if (!isAuthor) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Você não tem permissão para editar este item.");
                return;
            }

            // 5. Encaminhar para a View
            request.setAttribute("tipo", tipo);
            request.getRequestDispatcher("/editar-item.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de item inválido.");
        } catch (Exception e) {
            // Robustez: Captura falhas de DB ou outros erros críticos
            throw new ServletException("Erro ao carregar item para edição.", e);
        }
    }
    
    // ----------------------------------------------------
    // MÉTODO POST: Salva as Alterações
    // ----------------------------------------------------
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

        // 2. Coletar Parâmetros Comuns
        String tipo = request.getParameter("tipo");
        String idParam = request.getParameter("id");
        String conteudo = request.getParameter("conteudo"); 

        if (tipo == null || idParam == null || conteudo == null || conteudo.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dados inválidos para edição (Conteúdo vazio?).");
            return;
        }
        
        String destino = request.getContextPath() + "/index"; 
        boolean sucesso = false;
        
        try {
            int itemId = Integer.parseInt(idParam);
            
            if (tipo.equalsIgnoreCase("post")) {
                String titulo = request.getParameter("titulo");
                
                // 3. Criar Objeto Post para UPDATE
                Post postAtualizado = new Post();
                postAtualizado.setId(itemId);
                postAtualizado.setTitulo(titulo);
                postAtualizado.setConteudo(conteudo);
                postAtualizado.setUsuario(usuarioLogado); // Para checagem de segurança no DAO
                
                // 4. Chamar DAO
                sucesso = postDAO.atualizarPost(postAtualizado);
                
                // 5. Definir Destino (Volta para a página do post)
                destino = request.getContextPath() + "/post?id=" + itemId;

            } else if (tipo.equalsIgnoreCase("comentario")) {
                String postIdParam = request.getParameter("postId"); // ID do post original
                
                // 3. Criar Objeto Comentario para UPDATE
                Comentario comentarioAtualizado = new Comentario();
                comentarioAtualizado.setId(itemId);
                comentarioAtualizado.setConteudo(conteudo);
                comentarioAtualizado.setUsuario(usuarioLogado); // Para checagem de segurança no DAO
                
                // 4. Chamar DAO
                sucesso = comentarioDAO.atualizarComentario(comentarioAtualizado);
                
                // 5. Definir Destino (Volta para a página do post original)
                if (postIdParam != null) {
                    destino = request.getContextPath() + "/post?id=" + postIdParam;
                }
            }
            
            // 6. Redirecionar com feedback
            if (sucesso) {
                destino += (destino.contains("?") ? "&" : "?") + "msg=" + tipo + "_editado";
            } else {
                 destino += (destino.contains("?") ? "&" : "?") + "error=edicao_falhou";
            }
            
            response.sendRedirect(destino);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de item inválido.");
        } catch (Exception e) {
            System.err.println("Erro ao salvar edição: " + e.getMessage());
            throw new ServletException("Erro na persistência ao salvar edição.", e);
        }
    }
}