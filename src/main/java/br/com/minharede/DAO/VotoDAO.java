package br.com.minharede.DAO;

import br.com.minharede.utils.ConexaoDB; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VotoDAO {
	public VotoDAO() throws SQLException { 
        
        
    }
    private Connection getConnection() throws SQLException {
        
        return ConexaoDB.getConnection(); 
    }
    
    
    public boolean salvarVoto(int postId, int usuarioId, int novaDirecao) throws SQLException {
        
        String selectSql = "SELECT direcao FROM VotoPost WHERE post_id = ? AND usuario_id = ?";
        
        try (Connection conn = getConnection()) {
            
            conn.setAutoCommit(false); 
            int direcaoAtual = 0;

          
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, postId);
                selectStmt.setInt(2, usuarioId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        direcaoAtual = rs.getInt("direcao");
                    }
                }
            }
            
            String operacaoSql;
          
            boolean removerVoto = (direcaoAtual == novaDirecao); 

            if (direcaoAtual == 0) {
              
                operacaoSql = "INSERT INTO VotoPost (post_id, usuario_id, direcao) VALUES (?, ?, ?)";
                
            } else if (removerVoto) {
                
                operacaoSql = "DELETE FROM VotoPost WHERE post_id = ? AND usuario_id = ?";
                
            } else {
              
                operacaoSql = "UPDATE VotoPost SET direcao = ? WHERE post_id = ? AND usuario_id = ?";
            }
            
        
            try (PreparedStatement operacaoStmt = conn.prepareStatement(operacaoSql)) {
                
                if (direcaoAtual == 0) { 
                    operacaoStmt.setInt(1, postId);
                    operacaoStmt.setInt(2, usuarioId);
                    operacaoStmt.setInt(3, novaDirecao);
                } else if (removerVoto) { // DELETE
                    operacaoStmt.setInt(1, postId);
                    operacaoStmt.setInt(2, usuarioId);
                } else { // UPDATE
                    operacaoStmt.setInt(1, novaDirecao);
                    operacaoStmt.setInt(2, postId);
                    operacaoStmt.setInt(3, usuarioId);
                }
                operacaoStmt.executeUpdate();
            }

           

            conn.commit(); 

        } catch (SQLException e) {
            System.err.println("Erro SQL ao registrar voto: " + e.getMessage());
            
            
            try {
                if (getConnection() != null) getConnection().rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erro durante o rollback: " + rollbackEx.getMessage());
            }
            
            throw e; 
        }
		return false;
    }
    
   
}