package br.com.minharede.servlets;

import br.com.minharede.models.Usuario;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;



public class PerfilServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

      
        if (usuarioLogado == null) {
            
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

       
        request.getRequestDispatcher("/perfil.jsp").forward(request, response);
    }
}