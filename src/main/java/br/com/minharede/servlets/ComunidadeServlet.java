package br.com.minharede.servlets; // ✅ CORRIGIDO: Pacote do Servlet

import br.com.minharede.DAO.ComunidadeDAO; 
import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException; 
import java.text.Normalizer;
import java.util.regex.Pattern;


public class ComunidadeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
	private ComunidadeDAO comunidadeDAO;

    @Override
    public void init() throws ServletException {
        
        try {
            this.comunidadeDAO = new ComunidadeDAO();
        } catch (SQLException e) { 
            System.err.println("Erro ao inicializar ComunidadeDAO: " + e.getMessage());
            
            throw new ServletException("Falha na inicialização do DAO devido a erro de conexão.", e);
        } catch (Exception e) {
             throw new ServletException("Erro inesperado na inicialização do DAO.", e);
        }
    }

    /**
     * Método auxiliar que gera um 'slug' (URL amigável) a partir de um texto.
     */
    private String slugify(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase();
        slug = slug.replaceAll("[^a-z0-9_\\s-]", "");
        slug = slug.replaceAll("[\\s-]+", "-");
        slug = slug.replaceAll("^-|-$", "");
        return slug;
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
        
       
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");

        if (nome == null || nome.trim().isEmpty() || descricao == null || descricao.trim().isEmpty()) {
            request.setAttribute("error", "campos_vazios");
            request.getRequestDispatcher("/criar-comunidade.jsp").forward(request, response);
            return;
        }
        
  
        if (nome.length() > 50) {
             nome = nome.substring(0, 50);
        }

        try {
         
            String slugBase = slugify(nome);
            String slug = slugBase;
            int counter = 1;

        
            while (comunidadeDAO.buscarComunidadePorSlug(slug) != null) {
                slug = slugBase + "-" + counter;
                counter++;
                
                if (counter > 100) { 
                    request.setAttribute("error", "erro_slug_unico");
                    request.getRequestDispatcher("/criar-comunidade.jsp").forward(request, response);
                    return;
                }
            }

         
            Comunidade novaComunidade = new Comunidade();
            novaComunidade.setNome(nome.trim());
            novaComunidade.setDescricao(descricao.trim());
            novaComunidade.setSlug(slug);
            novaComunidade.setModerador(usuarioLogado); 

          
            boolean sucesso = comunidadeDAO.salvarComunidade(novaComunidade);

            
            if (sucesso) {
               
                response.sendRedirect(request.getContextPath() + "/r/" + slug); 
            } else {
                request.setAttribute("error", "db_falha");
                request.getRequestDispatcher("/criar-comunidade.jsp").forward(request, response);
            }

        } catch (SQLException e) { 
            System.err.println("Erro SQL ao criar comunidade: " + e.getMessage());
            request.setAttribute("error", "db_falha");
            request.getRequestDispatcher("/criar-comunidade.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Erro crítico ao criar comunidade: " + e.getMessage());
            
            throw new ServletException("Erro na persistência de dados.", e);
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

     
        request.getRequestDispatcher("/criar-comunidade.jsp").forward(request, response);
    }
}