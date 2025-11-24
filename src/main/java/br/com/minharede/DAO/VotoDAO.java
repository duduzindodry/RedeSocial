package br.com.minharede.DAO; // PACOTE CORRIGIDO: Padronizado para 'dao'

import br.com.minharede.utils.ConexaoDB; // Adicionado o utilitário de conexão
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement; // Necessário para Statement.RETURN_GENERATED_KEYS (embora não usado aqui)

public class VotoDAO {
    
    private PostDAO postDAO; // 1. Adicionado PostDAO como campo para eficiência

    // Inicializa o PostDAO na construção do VotoDAO
    public VotoDAO() {
        this.postDAO = new PostDAO();
    }
    
    // Método utilitário para obter a conexão
    private Connection getConnection() throws SQLException {
        // CORRIGIDO: Usa o utilitário de conexão do projeto
        return ConexaoDB.getConnection();
    }
    
    /**
     * Registra ou atualiza o voto de um usuário em um post (UPSERT).
     * @param postId ID do post.
     * @param usuarioId ID do usuário.
     * @param direcao 1 para upvote, -1 para downvote.
     * @return true se a operação foi bem sucedida.
     */
    public boolean salvarVoto(int postId, int usuarioId, int direcao) {
        
        // SQL: UPSERT (Atualiza se a chave duplicar, senão insere)
        String sql = "INSERT INTO VotoPost (post_id, usuario_id, direcao) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE direcao = VALUES(direcao), data_voto = NOW()";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, usuarioId);
            stmt.setInt(3, direcao);
            
            // O executeUpdate retornará 1 (inserção) ou 2 (atualização) se for MySQL/MariaDB
            stmt.executeUpdate();
            
            // 2. Chama o método de recalcular do objeto PostDAO (mais eficiente)
            this.postDAO.recalcularVotos(postId); 
            
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar voto: " + e.getMessage());
            // Em caso de falha, retorne false para o Servlet
            return false;
        }
    }
}