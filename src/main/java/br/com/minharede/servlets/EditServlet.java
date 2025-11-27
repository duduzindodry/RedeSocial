package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.ComentarioDAO; 
import br.com.minharede.DAO.PostDAO;       
import br.com.minharede.models.Comentario;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException; 


public class EditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PostDAO postDAO;
    private ComentarioDAO comentarioDAO;

    @Override
    public void init() throws ServletException {
        try {
           
            this.postDAO = new PostDAO();
            this.comentarioDAO = new ComentarioDAO();
        } catch (SQLException e) { 
            System.err.println("Falha na inicialização dos DAOs de edição: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO devido a erro de conexão.", e);
        } catch (Exception e) {
            throw new ServletException("Erro inesperado na inicialização do DAO.", e);
        }
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
       
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

       
        String tipo = request.getParameter("tipo"); 
        String idParam = request.getParameter("id"); 
        
        if (tipo == null || idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros 'tipo' e 'id' são obrigatórios.");
            return;
        }

        try {
            int itemId = Integer.parseInt(idParam);
            boolean isAuthor = false;
            
           
            if (tipo.equalsIgnoreCase("post")) {
                Post post = postDAO.buscarPostPorIdSimples(itemId); 
                
                if (post != null) {
                    isAuthor = post.getUsuario().getId() == usuarioLogado.getId();
                    request.setAttribute("item", post);
                }

            } else if (tipo.equalsIgnoreCase("comentario")) {
                Comentario comentario = comentarioDAO.buscarComentarioPorId(itemId); 
                
                if (comentario != null) {
                    isAuthor = comentario.getUsuario().getId() == usuarioLogado.getId();
                    request.setAttribute("item", comentario);
                }
            }
            
            
            if (!isAuthor) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Você não tem permissão para editar este item.");
                return;
            }

            
            request.setAttribute("tipo", tipo);
            request.getRequestDispatcher("/editar-item.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de item inválido.");
        } catch (SQLException e) { 
            System.err.println("Erro SQL ao carregar item para edição: " + e.getMessage());
            throw new ServletException("Erro na persistência ao carregar item para edição.", e);
        } catch (Exception e) {
            
            System.err.println("Erro inesperado ao carregar item para edição: " + e.getMessage());
            throw new ServletException("Erro crítico ao carregar item para edição.", e);
        }
    }
    
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

       
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
                
                
                Post postAtualizado = new Post();
                postAtualizado.setId(itemId);
                postAtualizado.setTitulo(titulo);
                postAtualizado.setConteudo(conteudo);
                postAtualizado.setUsuario(usuarioLogado); 
                
                
                sucesso = postDAO.atualizarPost(postAtualizado);
                
                
                destino = request.getContextPath() + "/post?id=" + itemId;

            } else if (tipo.equalsIgnoreCase("comentario")) {
                String postIdParam = request.getParameter("postId"); 
                
                Comentario comentarioAtualizado = new Comentario();
                comentarioAtualizado.setId(itemId);
                comentarioAtualizado.setConteudo(conteudo);
                comentarioAtualizado.setUsuario(usuarioLogado);
                
                sucesso = comentarioDAO.atualizarComentario(comentarioAtualizado);
                
              
                if (postIdParam != null) {
                    destino = request.getContextPath() + "/post?id=" + postIdParam;
                }
            }
            
           
            if (sucesso) {
                destino += (destino.contains("?") ? "&" : "?") + "msg=" + tipo + "_editado";
            } else {
                 destino += (destino.contains("?") ? "&" : "?") + "error=edicao_falhou";
            }
            
            response.sendRedirect(destino);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de item inválido.");
        } catch (SQLException e) {
            System.err.println("Erro SQL ao salvar edição: " + e.getMessage());
            throw new ServletException("Erro na persistência ao salvar edição.", e);
        } catch (Exception e) {
            System.err.println("Erro inesperado ao salvar edição: " + e.getMessage());
            throw new ServletException("Erro no servidor ao salvar edição.", e);
        }
    }
}