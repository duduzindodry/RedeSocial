package br.com.minharede; // Sugestão: Mova para um pacote 'filtros' para organização

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter; 
import jakarta.servlet.http.*;
import java.io.IOException;


@WebFilter("/*") 
public class Filtro implements Filter {

    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        
        // CORREÇÃO DE LINKS: Incluir as rotas de Servlet corretas (ex: /login) e os JSPs
        boolean isPublicResource = path.startsWith("/login.jsp") 
                                || path.startsWith("/cadastro.jsp")
                                || path.startsWith("/index.jsp")
                                || path.startsWith("/login")      // Rota do LoginServlet POST/GET
                                || path.startsWith("/cadastro")   // Rota do CadastroServlet POST/GET
                                || path.startsWith("/css/")
                                || path.startsWith("/js/")
                                || path.startsWith("/imagens/");

        HttpSession session = httpRequest.getSession(false); 
        boolean isLoggedIn = (session != null && session.getAttribute("usuarioLogado") != null);

    
        if (isLoggedIn || isPublicResource) {
            
            chain.doFilter(request, response);
        } else {
            // Acesso negado: Redireciona para o login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?acesso=negado");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}