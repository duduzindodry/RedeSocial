package br.com.minharede.DAO;

import br.com.minharede.utils.ConexaoDB;
import br.com.minharede.models.Usuario;
import java.sql.*;

public class UsuarioDAO {

    public boolean cadastrarUsuario(String nome, String usuario, String email, String senha) {
        
        String sql = "INSERT INTO usuarios (nome, usuario, email, senha) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, usuario); 
            stmt.setString(3, email);   
            stmt.setString(4, senha);   

            int linhasAfetadas = stmt.executeUpdate();
            
            return linhasAfetadas > 0; 

        } catch (SQLException e) {
            System.err.println("❌ Erro ao cadastrar usuário no DB: " + e.getMessage());
            e.printStackTrace();
            return false; 
        }
    }
    
    public Usuario autenticarUsuario(String usuarioOuEmail, String senha) {
        String sql = "SELECT id, nome, email, senha FROM usuarios WHERE usuario = ? OR email = ?"; 
        
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuarioOuEmail);
            stmt.setString(2, usuarioOuEmail);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaHashedDoBanco = rs.getString("senha");
                    
                    if (senha.equals(senhaHashedDoBanco)) { 
                        int id = rs.getInt("id");
                        String nome = rs.getString("nome");
                        String email = rs.getString("email");
                        
                        return new Usuario(id, nome, email); 
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro de SQL durante a autenticação: " + e.getMessage());
        }
        
        return null; 
    }
}