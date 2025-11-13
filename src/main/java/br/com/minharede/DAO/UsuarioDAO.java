package br.com.minharede.DAO;

	import br.com.minharede.utils.ConexaoDB;
	import java.sql.*;

	public class UsuarioDAO {

	    public boolean cadastrarUsuario(String nome, String email, String senha) {
	        String sql = "INSERT INTO Usuarios (nome, email, senha) VALUES (?, ?, ?)";
	        
	        try (Connection conn = ConexaoDB.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {

	            // Preenche os placeholders (?) com os valores
	            stmt.setString(1, nome);
	            stmt.setString(2, email);
	            stmt.setString(3, senha); // ATENÇÃO: Use HASH de senha em produção!

	            int linhasAfetadas = stmt.executeUpdate();
	            return linhasAfetadas > 0; // Retorna true se o cadastro foi um sucesso

	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	}


	
