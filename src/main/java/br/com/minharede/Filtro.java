package br.com.minharede;

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
        boolean isPublicResource = path.startsWith("/login.jsp") 
                                || path.startsWith("/cadastro.jsp")
                                || path.startsWith("/index.jsp")
                                || path.startsWith("/login")
                                || path.startsWith("/cadastro")
                                || path.startsWith("/css/")
                                || path.startsWith("/js/")
                                || path.startsWith("/imagens/");

        HttpSession session = httpRequest.getSession(false); 
        boolean isLoggedIn = (session != null && session.getAttribute("usuarioLogado") != null);

    
        if (isLoggedIn || isPublicResource) {
            
            chain.doFilter(request, response);
        } else {
  
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?acesso=negado");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}