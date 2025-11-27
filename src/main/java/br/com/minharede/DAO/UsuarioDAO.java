package br.com.minharede.DAO;

import br.com.minharede.models.Usuario;
import br.com.minharede.utils.ConexaoDB; 

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
	public UsuarioDAO() throws SQLException { 
       
    }
    private Connection getConnection() throws SQLException {
        return ConexaoDB.getConnection(); 
    }

    // METODOS DE AUTENTICAÇÃO E PERFIL

    public Usuario autenticarUsuario(String credencial, String senhaInput) throws SQLException {
        String sql = "SELECT id, nome, email, senha_hash FROM usuarios WHERE email = ? OR nome = ?";
        Usuario usuario = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
         
        } catch (SQLException e) {
            System.err.println("Erro SQL em autenticarUsuario: " + e.getMessage());
            throw e; 
        }
        return usuario;
    }

    public boolean solicitarAmizade(int solicitanteId, int receptorId) throws SQLException {
        String sql = "INSERT INTO Amizade (usuario1_id, usuario2_id, status) VALUES (?, ?, 'PENDENTE')";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
          
        } catch (SQLException e) {
            System.err.println("Erro SQL em solicitarAmizade: " + e.getMessage());
            throw e; 
        }
        return false;
    }

    public boolean aceitarAmizade(int usuarioId, int solicitanteId) throws SQLException {
        String sql = "UPDATE Amizade SET status = 'ACEITA' WHERE usuario1_id = ? AND usuario2_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
           
        } catch (SQLException e) {
            System.err.println("Erro SQL em aceitarAmizade: " + e.getMessage());
            throw e; 
        }
        return false;
    }

    public boolean removerAmizade(int usuarioId, int amigoId) throws SQLException {
        String sql = "DELETE FROM Amizade WHERE usuario1_id = ? AND usuario2_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
        } catch (SQLException e) {
            System.err.println("Erro SQL em removerAmizade: " + e.getMessage());
            throw e; 
        }
        return false;
    }

    public List<Usuario> buscarAmigosAceitos(int usuarioId) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id IN (SELECT ...) ";
        List<Usuario> amigos = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
           
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarAmigosAceitos: " + e.getMessage());
            throw e; 
        }
        return amigos;
    }

    public List<Usuario> buscarSolicitacoesRecebidas(int usuarioId) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id IN (SELECT ...) ";
        List<Usuario> solicitantes = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
           
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarSolicitacoesRecebidas: " + e.getMessage());
            throw e; 
        }
        return solicitantes;
    }
public boolean cadastrarUsuario(String nome, String usuario, String email, String senha) throws SQLException {
        
        
        if (existeUsuario(usuario, email)) {
            return false; 
        }
        
       
        String sql = "INSERT INTO usuarios (nome, nome_usuario, email, senha) VALUES (?, ?, ?, ?)";
        
       
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            
            stmt.setString(1, nome);  
            stmt.setString(2, usuario); 
            stmt.setString(3, email); 
            stmt.setString(4, senha); 
            
         
            int linhasAfetadas = stmt.executeUpdate();
            
            return linhasAfetadas > 0;
        } 
    }
    

     
    private boolean existeUsuario(String usuario, String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE nome = ? OR email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, email);
            
            
            try (var rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; 
                }
            }
        }
        return false; 
    }
    public Usuario buscarUsuarioPorCredenciais(String login, String senha) throws SQLException {
        
      
        String sql = "SELECT id, nome, nome_usuario, email FROM usuarios WHERE (nome_usuario = ? OR email = ?) AND senha = ?";
        
        Usuario usuario = null; 
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
       
            stmt.setString(1, login);  
            stmt.setString(2, login); 
            stmt.setString(3, senha); 
            
            try (ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
             
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id")); 
                    usuario.setNome(rs.getString("nome"));
                    usuario.setNomeUsuario(rs.getString("nome_usuario"));
                    usuario.setEmail(rs.getString("email"));
                }
            }
        }
        return usuario; 
    }
}
	
