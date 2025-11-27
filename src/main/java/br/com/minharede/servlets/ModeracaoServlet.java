package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.ComunidadeDAO; 
import br.com.minharede.DAO.PostDAO;       
import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException; 

public class ModeracaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PostDAO postDAO;
    private ComunidadeDAO comunidadeDAO;

    @Override
    public void init() throws ServletException {
        
        try {
            this.postDAO = new PostDAO();
            this.comunidadeDAO = new ComunidadeDAO();
        } catch (SQLException e) { 
            System.err.println("Falha na inicialização dos DAOs de Moderação: " + e.getMessage());
            throw new ServletException("Falha na inicialização do DAO devido a erro de conexão.", e);
        } catch (Exception e) {
            throw new ServletException("Erro inesperado na inicialização do DAO.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

       
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        String acao = request.getParameter("acao");
        String postIdStr = request.getParameter("id");

        if ("deletar_post_moderacao".equals(acao) && postIdStr != null) {
          
            deletarPostModeracao(request, response, usuarioLogado, postIdStr);
        } else {
            
            response.sendRedirect(request.getContextPath() + "/index"); 
        }
    }

    private void deletarPostModeracao(HttpServletRequest request, HttpServletResponse response, 
                                      Usuario usuarioLogado, String postIdStr) throws IOException, ServletException {
        
        int postId;
        try {
            postId = Integer.parseInt(postIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/index?erro=PostIdInvalido");
            return;
        }

        try {
          
            int comunidadeId = postDAO.getComunidadeIdPorPost(postId);
            
            if (comunidadeId <= 0) {
                response.sendRedirect(request.getContextPath() + "/index?erro=PostNaoEncontrado");
                return;
            }
            
        
            boolean isModerador = comunidadeDAO.verificarAutoridadeModerador(comunidadeId, usuarioLogado.getId());

            if (!isModerador) {
              
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Você não tem autoridade para moderar este conteúdo.");
                return;
            }

       
            
            boolean sucesso = postDAO.deletarPostModerador(postId);

            
            if (sucesso) {
               
                Comunidade comunidade = comunidadeDAO.buscarComunidadePorId(comunidadeId);
                String destino = request.getContextPath() + "/r/" + comunidade.getSlug() + "?msg=post_removido";
                response.sendRedirect(destino);
            } else {
                response.sendRedirect("post?id=" + postId + "&erro=FalhaDeletarPost");
            }
            
        } catch (SQLException e) {
            System.err.println("Erro SQL na moderação: " + e.getMessage());
            throw new ServletException("Erro na persistência ao processar moderação.", e);
        } catch (Exception e) {
            
            System.err.println("Erro inesperado na moderação: " + e.getMessage());
            throw new ServletException("Erro crítico no servidor durante a moderação.", e);
        }
    }
}