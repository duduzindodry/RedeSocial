package br.com.minharede.DAO;

import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;
import br.com.minharede.utils.ConexaoDB; 

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class PostDAO {

	public PostDAO() throws SQLException { 
        try (Connection conn = getConnection()) {
        } 
    }
    
    private Connection getConnection() throws SQLException {
        return ConexaoDB.getConnection(); 
    }

   
    private Post mapearPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setTitulo(rs.getString("titulo"));
        post.setConteudo(rs.getString("conteudo"));
        post.setTipo(rs.getString("tipo"));
        post.setVotos(rs.getInt("votos"));

        
        Timestamp timestamp = rs.getTimestamp("data_criacao");
        if (timestamp != null) {
            post.setDataCriacao(timestamp.toLocalDateTime());
        }

        
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        post.setUsuario(usuario);

        Comunidade comunidade = new Comunidade();
        comunidade.setId(rs.getInt("id_comunidade"));
        post.setComunidade(comunidade);
        

        return post;
    }
    
    
    //MeTODOS DE CONSULTA
    
    public List<Post> buscarFeedPersonalizado(int idUsuario) throws SQLException {
        
        String sql = "SELECT p.* FROM posts p " +
                     "INNER JOIN seguidores_comunidade sc ON p.id_comunidade = sc.id_comunidade " +
                     "WHERE sc.id_usuario = ? ORDER BY p.data_criacao DESC LIMIT 100";
        
        List<Post> posts = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    posts.add(mapearPost(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarFeedPersonalizado: " + e.getMessage());
            throw e; 
        }
        return posts;
    }

    public List<Post> buscarFeedGlobal() throws SQLException {
        String sql = "SELECT * FROM posts ORDER BY data_criacao DESC LIMIT 50";
        List<Post> posts = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    posts.add(mapearPost(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarFeedGlobal: " + e.getMessage());
            throw e; 
        }
        return posts;
    }

    public Post buscarPostPorId(int postId) throws SQLException {
        
        String sql = "SELECT p.* FROM posts p WHERE p.id = ?";
        Post post = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);

            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    post = mapearPost(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarPostPorId: " + e.getMessage());
            throw e;
        }
        return post;
    }

    public List<Post> buscarPostsPorComunidadeId(int comunidadeId) throws SQLException {
        String sql = "SELECT * FROM posts WHERE id_comunidade = ? ORDER BY data_criacao DESC";
        List<Post> posts = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, comunidadeId);

            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    posts.add(mapearPost(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarPostsPorComunidadeId: " + e.getMessage());
            throw e;
        }
        return posts;
    }
    
    public int getComunidadeIdPorPost(int postId) throws SQLException {
        String sql = "SELECT id_comunidade FROM posts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);

            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt("id_comunidade");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL em getComunidadeIdPorPost: " + e.getMessage());
            throw e;
        }
        return -1; 
    }

    public List<Post> buscarPostsPorTermo(String termo) throws SQLException {
        String sql = "SELECT * FROM posts WHERE LOWER(titulo) LIKE ?";
        List<Post> posts = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
          
            stmt.setString(1, "%" + termo.toLowerCase() + "%"); 
            
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    posts.add(mapearPost(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarPostsPorTermo: " + e.getMessage());
            throw e;
        }
        return posts;
    }
    
 
    //  MÉTODOS DE MANIPULAÇÃO 
 

    public int salvarPost(Post post) throws SQLException {
        
        String sql = "INSERT INTO posts (id_usuario, id_comunidade, titulo, conteudo, tipo) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, post.getUsuario().getId());
            stmt.setInt(2, post.getComunidade().getId());
            stmt.setString(3, post.getTitulo());
            stmt.setString(4, post.getConteudo());
            stmt.setString(5, post.getTipo());
            
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); 
                    }
                }
            }
            return 0; 
            
        } catch (SQLException e) {
            System.err.println("Erro SQL em salvarPost: " + e.getMessage());
            throw e;
        }
    }

    public boolean excluirPost(int postId, int usuarioId) throws SQLException {
       
        String sql = "DELETE FROM posts WHERE id = ? AND id_usuario = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);
            stmt.setInt(2, usuarioId);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro SQL em excluirPost: " + e.getMessage());
            throw e; 
        }
    }
    
    public boolean deletarPostModerador(int postId) throws SQLException {
        
        String sql = "DELETE FROM posts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, postId);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro SQL em deletarPostModerador: " + e.getMessage());
            throw e;
        }
    }

    public boolean atualizarPost(Post post) throws SQLException {
       
        String sql = "UPDATE posts SET titulo=?, conteudo=? WHERE id=? AND id_usuario=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, post.getTitulo());
            stmt.setString(2, post.getConteudo());
            stmt.setInt(3, post.getId());
            stmt.setInt(4, post.getUsuario().getId()); 

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro SQL em atualizarPost: " + e.getMessage());
            throw e;
        }
    }

    public Post buscarPostPorIdSimples(int itemId) throws SQLException {
        
        String sql = "SELECT id, id_usuario FROM posts WHERE id = ?";
        Post post = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemId);

            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    post = new Post();
                    post.setId(rs.getInt("id"));
                    
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    post.setUsuario(usuario);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarPostPorIdSimples: " + e.getMessage());
            throw e;
        }
        return post;
    }
}