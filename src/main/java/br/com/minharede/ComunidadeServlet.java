package br.com.minharede; // Certifique-se de que o pacote é 'servlets'

import br.com.minharede.DAO.ComunidadeDAO; // Importe o DAO com 'dao' minúsculo
import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.Normalizer;
import java.util.regex.Pattern;

@WebServlet("/criar-comunidade")
public class ComunidadeServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ComunidadeDAO comunidadeDAO;

    @Override
    public void init() throws ServletException {
        // O try-catch aqui é crucial se o getConnection falhar
        try {
            this.comunidadeDAO = new ComunidadeDAO();
        } catch (Exception e) {
            System.err.println("Erro ao inicializar ComunidadeDAO: " + e.getMessage());
            // Lançar exceção para falhar a inicialização do Servlet
            throw new ServletException("Falha na inicialização do DAO.", e);
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

        // 1. Verificar Autenticação
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (Usuario) (session != null ? session.getAttribute("usuarioLogado") : null);

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }
        
        // 2. Coletar e Validar Parâmetros
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");

        if (nome == null || nome.trim().isEmpty() || descricao == null || descricao.trim().isEmpty()) {
            request.setAttribute("error", "campos_vazios");
            request.getRequestDispatcher("/criar-comunidade.jsp").forward(request, response);
            return;
        }
        
        // Limitar tamanho do nome
        if (nome.length() > 50) {
             nome = nome.substring(0, 50);
        }

        try {
            // 3. Gerar Slug e Garantir Unicidade
            String slugBase = slugify(nome);
            String slug = slugBase;
            int counter = 1;

            // CORREÇÃO: Usa buscarComunidadePorSlug (que retorna Comunidade ou null)
            while (comunidadeDAO.buscarComunidadePorSlug(slug) != null) {
                slug = slugBase + "-" + counter;
                counter++;
                
                if (counter > 100) { 
                    request.setAttribute("error", "erro_slug_unico");
                    request.getRequestDispatcher("/criar-comunidade.jsp").forward(request, response);
                    return;
                }
            }

            // 4. Montar e Salvar
            Comunidade novaComunidade = new Comunidade();
            novaComunidade.setNome(nome.trim());
            novaComunidade.setDescricao(descricao.trim());
            novaComunidade.setSlug(slug);
            novaComunidade.setModerador(usuarioLogado);

            boolean sucesso = comunidadeDAO.salvarComunidade(novaComunidade);

            // 5. Redirecionar
            if (sucesso) {
                response.sendRedirect(request.getContextPath() + "/r/" + slug);
            } else {
                request.setAttribute("error", "db_falha");
                request.getRequestDispatcher("/criar-comunidade.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.err.println("Erro crítico ao criar comunidade: " + e.getMessage());
            // Encaminha para o 500.jsp configurado no web.xml
            throw new ServletException("Erro na persistência de dados.", e);
        }
    }
    
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

        // 2. Encaminha para o formulário
        request.getRequestDispatcher("/criar-comunidade.jsp").forward(request, response);
    }
}