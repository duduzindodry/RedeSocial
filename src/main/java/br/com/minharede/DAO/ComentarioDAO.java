package br.com.minharede.DAO;

import br.com.minharede.models.Comentario;
import br.com.minharede.models.Usuario;
import br.com.minharede.models.Post;
import br.com.minharede.utils.ConexaoDB; 

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class ComentarioDAO {

   
	public ComentarioDAO() throws SQLException {
        
        try (Connection conn = getConnection()) {
            
        } 
	}
    
    
     
    private Connection getConnection() throws SQLException {
        return ConexaoDB.getConnection(); 
    }
    
    
    // CRUD
    
    
    public List<Comentario> listarComentariosPorPost(int postId) throws SQLException {
        
       
        String sql = "SELECT * FROM Comentario WHERE post_id = ? ORDER BY data_criacao ASC";
        List<Comentario> comentarios = new ArrayList<>();
        
    
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
          
            stmt.setInt(1, postId); 
            
           
            try (ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                   
                    Comentario comentario = new Comentario();
                    comentario.setId(rs.getInt("id"));
                    comentario.setConteudo(rs.getString("conteudo"));
                    
                    Timestamp timestamp = rs.getTimestamp("data_criacao");
                    if (timestamp != null) {
                        comentario.setDataCriacao(timestamp.toLocalDateTime());
                    } else {
                        comentario.setDataCriacao(null); 
                    }
                    
                    
         
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("usuario_id"));
                   comentario.setUsuario(usuario); 
                    
                   
                  Post post = new Post();
                    post.setId(rs.getInt("post_id")); 
                    comentario.setPost(post);
                    
                    comentarios.add(comentario);
                }
            } 
            
        } catch (SQLException e) {
            // documentação
            System.err.println("Erro SQL em listarComentariosPorPost: " + e.getMessage());
            throw e; 
        }
        
        return comentarios;
    }
    
     
    public boolean adicionarComentario(Comentario comentario) throws SQLException {
      
        String sql = "INSERT INTO Comentario (post_id, usuario_id, conteudo) VALUES (?, ?, ?)";
        
       
        int idPost = comentario.getPost().getId();
        int idUsuario = comentario.getUsuario().getId(); 
        String conteudo = comentario.getConteudo();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPost);
            stmt.setInt(2, idUsuario);
            stmt.setString(3, conteudo);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro SQL em adicionarComentario: " + e.getMessage());
            throw e; 
        }
    }

    
   
    public boolean excluirComentario(int comentarioId, int usuarioId) throws SQLException {
        // A condição 'AND usuario_id = ?' é a verificação de segurança (o autor)
        String sql = "DELETE FROM Comentario WHERE id = ? AND usuario_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, comentarioId);
            stmt.setInt(2, usuarioId);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro SQL em excluirComentario: " + e.getMessage());
            throw e; 
        }
    }
    
    
    public Comentario buscarComentarioPorId(int comentarioId) throws SQLException {
        // Colunas essenciais para validação
        String sql = "SELECT id, usuario_id, conteudo, post_id FROM Comentario WHERE id = ?";
        Comentario comentario = null;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, comentarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    comentario = new Comentario();
                    comentario.setId(rs.getInt("id"));
                    comentario.setConteudo(rs.getString("conteudo"));
                    
                    // Mapeamento de objetos relacionados
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("usuario_id"));
                    comentario.setUsuario(usuario);
                    
                    Post post = new Post();
                    post.setId(rs.getInt("post_id"));
                    comentario.setPost(post);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarComentarioPorId: " + e.getMessage());
            throw e;
        }
        return comentario;
    }

    
    
    public boolean atualizarComentario(Comentario comentario) throws SQLException {
        
        String sql = "UPDATE Comentario SET conteudo = ? WHERE id = ? AND usuario_id = ?";
        
        // Extrai os dados do objeto
        String novoConteudo = comentario.getConteudo();
        int idComentario = comentario.getId();
        int idUsuario = comentario.getUsuario().getId(); 
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, novoConteudo);
            stmt.setInt(2, idComentario);
            stmt.setInt(3, idUsuario);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro SQL em atualizarComentario: " + e.getMessage());
            throw e;
        }
    }
}