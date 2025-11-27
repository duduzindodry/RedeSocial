package br.com.minharede.servlets; 

import br.com.minharede.DAO.UsuarioDAO; 
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException; 
import java.util.List;
import java.util.Collections;


public class AmigosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        try {
            
            this.usuarioDAO = new UsuarioDAO(); 
        } catch (SQLException e) { 
            System.err.println("Falha ao inicializar UsuarioDAO no AmigosServlet: " + e.getMessage());
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
        
        int usuarioId = usuarioLogado.getId();

        List<Usuario> listaAmigos = Collections.emptyList();
        List<Usuario> solicitacoesRecebidas = Collections.emptyList();

        try {
            listaAmigos = usuarioDAO.buscarAmigosAceitos(usuarioId);
            solicitacoesRecebidas = usuarioDAO.buscarSolicitacoesRecebidas(usuarioId);

            if (listaAmigos == null) listaAmigos = Collections.emptyList();
            if (solicitacoesRecebidas == null) solicitacoesRecebidas = Collections.emptyList();

        } catch (SQLException e) { 
            
            System.err.println("Erro SQL ao carregar a página de amigos: " + e.getMessage());
           
            throw new ServletException("Falha ao carregar listas de amizade na camada de persistência.", e);
        } catch (Exception e) {
            
            System.err.println("Erro inesperado ao carregar a página de amigos: " + e.getMessage());
            throw new ServletException("Erro inesperado no servidor.", e);
        }
            
       
        request.setAttribute("listaAmigos", listaAmigos);
        request.setAttribute("solicitacoesRecebidas", solicitacoesRecebidas);
        
        request.getRequestDispatcher("/amigos.jsp").forward(request, response);
    }
}