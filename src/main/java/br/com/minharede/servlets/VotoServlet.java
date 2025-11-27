package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.VotoDAO;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;


public class VotoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L; 
    private VotoDAO votoDAO;

    @Override
    public void init() throws ServletException {
        try {
           
            this.votoDAO = new VotoDAO();
        } catch (SQLException e) {
            System.err.println("Falha ao inicializar VotoDAO: " + e.getMessage());
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
          
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login necessário para votar.");
            return;
        }

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        try {
            int postId = Integer.parseInt(request.getParameter("postId"));
            int direcao = Integer.parseInt(request.getParameter("direcao")); 
            
            
            if (direcao != 1 && direcao != -1) {
                System.err.println("Tentativa de voto com direção inválida: " + direcao);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Direção do voto inválida.");
                return;
            }

           
            boolean sucesso = votoDAO.salvarVoto(postId, usuarioLogado.getId(), direcao);
            
            
            if (sucesso) {
             
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
               
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Não foi possível registrar o voto.");
            }

        } catch (NumberFormatException e) {
           
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de Post ou Direção inválida.");
        } catch (SQLException e) { 
            System.err.println("Erro SQL ao processar voto: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro de persistência ao votar.");
        } catch (Exception e) {
            System.err.println("Erro inesperado ao processar voto: " + e.getMessage());
          
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
        }
    }
    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}