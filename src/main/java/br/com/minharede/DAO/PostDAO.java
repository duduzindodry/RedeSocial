package br.com.minharede.DAO;

import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;
import br.com.minharede.utils.ConexaoDB; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PostDAO {
    
    // Método auxiliar para obter a conexão (Usa o utilitário)
	private Connection getConnection() throws SQLException {
        return ConexaoDB.getConnection(); 
	}

    
    // Método auxiliar para extração completa de Post (Feed e PostView)
    private Post extrairPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setTitulo(rs.getString("titulo"));
        post.setConteudo(rs.getString("conteudo"));
        post.setVotos(rs.getInt("votos"));
        post.setNumComentarios(rs.getInt("num_comentarios")); 

        Comunidade comunidade = new Comunidade();
        comunidade.setId(rs.getInt("comunidade_id"));
        comunidade.setSlug(rs.getString("comunidade_slug")); 
        post.setComunidade(comunidade);

        Usuario usuario = new Usuario(); 
        usuario.setNome(rs.getString("usuario_nome"));
        post.setUsuario(usuario);
        
        return post;
    }

    // Método auxiliar para extração SIMPLES (Edição/Moderação)
    private Post extrairPostSimples(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setTitulo(rs.getString("titulo"));
        post.setConteudo(rs.getString("conteudo"));
        
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("usuario_id")); 
        post.setUsuario(usuario);
        
        Comunidade comunidade = new Comunidade();
        comunidade.setId(rs.getInt("comunidade_id")); 
        post.setComunidade(comunidade);
        
        return post;
    }


    // ----------------------------------------------------------------------
    // 1. MÉTODOS DE CONSULTA (LEITURA)
    // ----------------------------------------------------------------------

    public List<Post> buscarPostsPorComunidades(List<Comunidade> comunidades, String orderBy) {
        String ids = comunidades.stream().map(c -> String.valueOf(c.getId())).collect(Collectors.joining(","));
        String sql = "SELECT p.*, c.slug as comunidade_slug, u.nome as usuario_nome, " +
                     "(SELECT COUNT(*) FROM Comentario co WHERE co.post_id = p.id) as num_comentarios " +
                     "FROM Post p JOIN Comunidade c ON p.comunidade_id = c.id " +
                     "JOIN Usuario u ON p.usuario_id = u.id " +
                     "WHERE p.comunidade_id IN (" + ids + ") ORDER BY " + orderBy + " LIMIT 50"; 

        List<Post> posts = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) { posts.add(extrairPost(rs)); }
        } catch (SQLException e) { System.err.println("Erro ao buscar posts por comunidades: " + e.getMessage()); }
        return posts;
    }
    
    public List<Post> buscarPostsPopulares(String orderBy) {
        String sql = "SELECT p.*, c.slug as comunidade_slug, u.nome as usuario_nome, " +
                     "(SELECT COUNT(*) FROM Comentario co WHERE co.post_id = p.id) as num_comentarios " +
                     "FROM Post p JOIN Comunidade c ON p.comunidade_id = c.id " +
                     "JOIN Usuario u ON p.usuario_id = u.id " +
                     "WHERE p.data_criacao > DATE_SUB(NOW(), INTERVAL 7 DAY) " + 
                     "ORDER BY " + orderBy + " LIMIT 50"; 
        List<Post> posts = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) { posts.add(extrairPost(rs)); }
        } catch (SQLException e) { System.err.println("Erro ao buscar posts populares: " + e.getMessage()); }
        return posts;
    }
    
    public Post buscarPostPorId(int postId) {
        Post post = null;
        String sql = "SELECT p.*, u.nome AS usuario_nome, c.nome AS nome_comunidade, c.slug AS comunidade_slug " +
                     "FROM Post p JOIN Usuario u ON p.usuario_id = u.id " +
                     "JOIN Comunidade c ON p.comunidade_id = c.id WHERE p.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    post = extrairPost(rs);
                    // NOTA: A contagem de comentários é preenchida no Servlet ou com outro DAO.
                }
            }
        } catch (SQLException e) { System.err.println("Erro ao buscar post por ID: " + e.getMessage()); }
        return post;
    }

    public Post buscarPostPorIdSimples(int postId) {
        String sql = "SELECT p.titulo, p.conteudo, p.usuario_id, p.comunidade_id, p.id FROM Post p WHERE p.id = ?";
        Post post = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { post = extrairPostSimples(rs); }
            }
        } catch (SQLException e) { System.err.println("Erro ao buscar post simples: " + e.getMessage()); }
        return post;
    }
    
    public List<Post> buscarPostsPorUsuario(int usuarioId) {
        String sql = "SELECT p.*, c.slug AS comunidade_slug, u.nome AS usuario_nome, 0 AS num_comentarios " +
                     "FROM Post p JOIN Comunidade c ON p.comunidade_id = c.id " +
                     "JOIN Usuario u ON p.usuario_id = u.id WHERE p.usuario_id = ? ORDER BY p.data_criacao DESC";
        List<Post> posts = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { posts.add(extrairPost(rs)); }
            }
        } catch (SQLException e) { System.err.println("Erro ao buscar posts por usuário: " + e.getMessage()); }
        return posts;
    }

    public List<Post> buscarPostsPorTermo(String termo) {
        String sql = "SELECT p.*, c.slug AS comunidade_slug, u.nome AS usuario_nome, " +
                     "(SELECT COUNT(*) FROM Comentario co WHERE co.post_id = p.id) AS num_comentarios " +
                     "FROM Post p JOIN Comunidade c ON p.comunidade_id = c.id " +
                     "JOIN Usuario u ON p.usuario_id = u.id " +
                     "WHERE LOWER(p.titulo) LIKE ? OR LOWER(p.conteudo) LIKE ? " + 
                     "ORDER BY p.data_criacao DESC LIMIT 50"; 

        List<Post> posts = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, termo);
            stmt.setString(2, termo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { posts.add(extrairPost(rs)); }
            }
        } catch (SQLException e) { System.err.println("Erro ao buscar posts por termo: " + e.getMessage()); }
        return posts;
    }
    
    public List<Post> buscarPostsPorComunidadeId(int comunidadeId) {
        String sql = "SELECT p.*, u.nome AS usuario_nome, c.slug AS comunidade_slug, " +
                     "(SELECT COUNT(*) FROM Comentario co WHERE co.post_id = p.id) AS num_comentarios " +
                     "FROM Post p JOIN Usuario u ON p.usuario_id = u.id " +
                     "JOIN Comunidade c ON p.comunidade_id = c.id " +
                     "WHERE p.comunidade_id = ? ORDER BY p.data_criacao DESC LIMIT 50";
        List<Post> posts = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, comunidadeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { posts.add(extrairPost(rs)); }
            }
        } catch (SQLException e) { System.err.println("Erro ao buscar posts por Comunidade ID: " + e.getMessage()); }
        return posts;
    }

    public int getComunidadeIdPorPost(int postId) {
	    String sql = "SELECT comunidade_id FROM Post WHERE id = ?";
	    try (Connection conn = getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, postId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("comunidade_id");
	            }
	        }
	    } catch (SQLException e) { 
	        System.err.println("Erro ao buscar comunidade ID do post: " + e.getMessage());
	    }
	    return -1;
	}


    // ----------------------------------------------------------------------
    // 2. MÉTODOS DE MANIPULAÇÃO (DML/MODERAÇÃO/VOTOS)
    // ----------------------------------------------------------------------
    
    public int salvarPost(Post post) {
        String sql = "INSERT INTO Post (comunidade_id, usuario_id, titulo, conteudo, tipo) VALUES (?, ?, ?, ?, ?)";
        int postId = -1;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, post.getComunidade().getId());
            stmt.setInt(2, post.getUsuario().getId());
            stmt.setString(3, post.getTitulo());
            stmt.setString(4, post.getConteudo());
            stmt.setString(5, post.getTipo());
            
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) { 
                if (rs.next()) { postId = rs.getInt(1); } 
            }
        } catch (SQLException e) { System.err.println("Erro SQL ao salvar novo post: " + e.getMessage()); }
        return postId;
    }

    public void recalcularVotos(int postId) {
        String selectSql = "SELECT SUM(direcao) AS total_votos FROM VotoPost WHERE post_id = ?";
        String updateSql = "UPDATE Post SET votos = ? WHERE id = ?";
        int totalVotos = 0;
        
        try (Connection conn = getConnection()) {
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, postId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) { totalVotos = rs.getInt("total_votos"); }
                }
            }
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, totalVotos);
                updateStmt.setInt(2, postId);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) { System.err.println("Erro ao recalcular votos: " + e.getMessage()); }
    }

    public boolean excluirPost(int postId, int usuarioId) {
        String sql = "DELETE FROM Post WHERE id = ? AND usuario_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.setInt(2, usuarioId);
            return stmt.executeUpdate() > 0; 
        } catch (SQLException e) { return false; }
    }

    public boolean deletarPostModerador(int postId) {
        String sql = "DELETE FROM Post WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    
    public boolean atualizarPost(Post post) {
        String sql = "UPDATE Post SET titulo = ?, conteudo = ? WHERE id = ? AND usuario_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, post.getTitulo());
            stmt.setString(2, post.getConteudo());
            stmt.setInt(3, post.getId());
            stmt.setInt(4, post.getUsuario().getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    
    public int calcularKarmaTotal(int targetUserId) {
        String sqlPosts = "SELECT SUM(votos) FROM Post WHERE usuario_id = ?";
        int karma = 0;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPosts)) {
            stmt.setInt(1, targetUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    karma = rs.getInt(1); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao calcular Karma: " + e.getMessage());
        }
        return karma;
    }
}