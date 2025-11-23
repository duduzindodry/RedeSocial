package br.com.minharede.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VotoDAO {
    
    // Assumindo que você tem um método utilitário de conexão
    private Connection getConnection() throws SQLException {
        // [USAR O MESMO CÓDIGO DE CONEXÃO DO PostDAO AQUI]
        throw new UnsupportedOperationException("Método de conexão não implementado.");
    }
    
    /**
     * Registra ou atualiza o voto de um usuário em um post.
     * @param postId ID do post.
     * @param usuarioId ID do usuário.
     * @param direcao 1 para upvote, -1 para downvote.
     * @return true se a operação foi bem sucedida.
     */
    public boolean salvarVoto(int postId, int usuarioId, int direcao) {
        // Usa INSERT OR REPLACE INTO (ou REPLACE INTO no MySQL) 
        // ou UPSERT (se for PostgreSQL) para atomicidade.
        
        String sql = "INSERT INTO VotoPost (post_id, usuario_id, direcao) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE direcao = VALUES(direcao), data_voto = NOW()";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, usuarioId);
            stmt.setInt(3, direcao);
            
            stmt.executeUpdate();
            
            // Depois de salvar o voto, você DEVE recalcular o total de votos do Post
            new PostDAO().recalcularVotos(postId); 
            
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar voto: " + e.getMessage());
            return false;
        }
    }
}