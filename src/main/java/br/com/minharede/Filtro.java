package br.com.minharede;

import br.com.minharede.models.Usuario;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Filtro implements Filter {

    
     
	private static final List<String> ROTAS_PUBLICAS = Arrays.asList(
	    "/login", 
	    "/cadastro", 
	    "/loginservlet",
	    "/cadastroservlet"
	);
	
	private static final List<String> EXTENSOES_PUBLICAS = Arrays.asList(
	    ".jsp",  
	    ".css", 
	    ".js", 
	    ".jpg",
	    ".png",
	    ".gif",
	    ".ico"   
	);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	    System.out.println("Filtro de Segurança inicializado.");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
	        throws IOException, ServletException {
	    
	    HttpServletRequest req = (HttpServletRequest) request;
	    HttpServletResponse res = (HttpServletResponse) response;
	    
	    String contextPath = req.getContextPath();
	    String requestPath = req.getRequestURI().substring(contextPath.length()).toLowerCase();

	    System.out.println("Filtro verificando caminho: " + requestPath);
	    
	  
	    
	    if (isAcessoPublico(requestPath)) {
	        System.out.println("Filtro: Acesso PERMITIDO a rota pública/estática: " + requestPath);
	        chain.doFilter(request, response);
	        return; 
	    }
	    
	 
	    
	    HttpSession session = req.getSession(false);
	    Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;
	    
	    if (usuarioLogado == null) {
	        System.out.println("Filtro: Acesso NEGADO a rota restrita: " + req.getRequestURI() + ". Redirecionando para login.");
	        
	   
	        res.sendRedirect(contextPath + "/login"); 
	        return;
	    }

	  
	    System.out.println("Filtro: Acesso PERMITIDO para usuário logado.");
	    chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {
	    
	}
	
	
	 
	private boolean isAcessoPublico(String requestPath) {
	    
	  
	    if ("/".equals(requestPath)) {
	        return true;
	    }
	    
	    
	    if (ROTAS_PUBLICAS.contains(requestPath)) {
	        return true;
	    }
	    
	    
	    for (String ext : EXTENSOES_PUBLICAS) {
	        if (requestPath.endsWith(ext)) {
	            return true;
	        }
	    }
	    
	    return false;
	}
}