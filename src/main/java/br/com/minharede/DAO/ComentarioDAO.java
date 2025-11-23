package br.com.minharede.DAO;

import br.com.minharede.models.Comentario;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ComentarioDAO {
    
    // [USAR O MESMO CÓDIGO DO getConnection() AQUI]
    private Connection getConnection() throws SQLException {
        // ... (Implementação omitida por brevidade, use o seu código existente)
        throw new UnsupportedOperationException("Método de conexão não implementado.");
    }

    /**
     * Extrai um objeto Comentario do ResultSet.
     * @param rs O ResultSet
     * @return Um objeto Comentario.
     * @throws SQLException
     */
    private Comentario extrairComentario(ResultSet rs) throws SQLException {
        Comentario comentario = new Comentario();
        comentario.setId(rs.getInt("id"));
        comentario.setConteudo(rs.getString("conteudo"));
        
        // Converte o TIMESTAMP do SQL para LocalDateTime Java
        comentario.setDataCriacao(rs.getTimestamp("data_criacao")
                                      .toInstant()
                                      .atZone(ZoneId.systemDefault())
                                      .toLocalDateTime());

        // Cria o objeto Usuario (Autor)
        Usuario autor = new Usuario();
        autor.setId(rs.getInt("usuario_id"));
        autor.setNome(rs.getString("usuario_nome")); // Assume que o SELECT usa um alias "usuario_nome"
        comentario.setUsuario(autor);
        
        // Cria o objeto Post
        Post post = new Post();
        post.setId(rs.getInt("post_id"));
        comentario.setPost(post);
        
        return comentario;
    }
    
    /**
     * Adiciona um novo comentário ao banco de dados.
     * @param comentario O objeto Comentario a ser salvo.
     * @return O ID do comentário criado.
     */
    public int adicionarComentario(Comentario comentario) {
        String sql = "INSERT INTO Comentario (post_id, usuario_id, conteudo) VALUES (?, ?, ?)";
        int idGerado = -1;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, comentario.getPost().getId());
            stmt.setInt(2, comentario.getUsuario().getId());
            stmt.setString(3, comentario.getConteudo());
            
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar comentário: " + e.getMessage());
        }
        return idGerado;
    }

    /**
     * Lista todos os comentários de um post.
     * @param postId ID do post.
     * @return Lista de Comentarios.
     */
    public List<Comentario> listarComentariosPorPost(int postId) {
        List<Comentario> comentarios = new ArrayList<>();
        // Note o JOIN para buscar o NOME do usuário
        String sql = "SELECT c.*, u.nome as usuario_nome FROM Comentario c " +
                     "JOIN Usuario u ON c.usuario_id = u.id " +
                     "WHERE c.post_id = ? ORDER BY c.data_criacao ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comentarios.add(extrairComentario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar comentários: " + e.getMessage());
        }
        return comentarios;
    }
 // ... dentro da classe ComentarioDAO.java

    /**
     * Conta o número de comentários para um post específico.
     * @param postId ID do post.
     * @return Número total de comentários.
     */
    public int contarComentariosPorPost(int postId) {
        String sql = "SELECT COUNT(*) AS total FROM Comentario WHERE post_id = ?";
        int total = 0;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao contar comentários: " + e.getMessage());
        }
        return total;
    }

    
}