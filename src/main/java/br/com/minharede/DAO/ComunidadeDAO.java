package br.com.minharede.DAO;

import br.com.minharede.models.Comunidade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComunidadeDAO {

    // Método utilitário para obter a conexão com o BD
    // Copie e adapte o método de conexão que você está usando no PostDAO
    private Connection getConnection() throws SQLException {
        // Exemplo fictício de conexão:
        // return DriverManager.getConnection("jdbc:mysql://localhost:3306/minharede_db", "user", "senha");
        throw new UnsupportedOperationException("Implementar o método de conexão com o seu banco de dados.");
    }
    
    /**
     * Monta um objeto Comunidade a partir de um ResultSet.
     * Necessário para extrair dados em ambos os DAOs.
     */
    private Comunidade extrairComunidade(ResultSet rs) throws SQLException {
        Comunidade c = new Comunidade();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setSlug(rs.getString("slug"));
        c.setDescricao(rs.getString("descricao"));
        
        return c;
    }

    /**
     * Busca todas as comunidades que um usuário específico está seguindo.
     * * @param usuarioId O ID do usuário logado.
     * @return Lista de objetos Comunidade.
     */
    public List<Comunidade> buscarComunidadesSeguidas(int usuarioId) {
        
        String sql = "SELECT c.* FROM Comunidade c " +
                     "JOIN UsuarioComunidade uc ON c.id = uc.comunidade_id " +
                     "WHERE uc.usuario_id = ?";

        List<Comunidade> comunidades = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            
            stmt.setInt(1, usuarioId); 

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                   
                    comunidades.add(extrairComunidade(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar comunidades seguidas: " + e.getMessage());
           
        }
        return comunidades;
    }
    
    

    public Comunidade buscarPorSlug(String slug) {
     
        return null;
    }

    public void salvarComunidade(Comunidade comunidade) {
        
    }

	public void alternarSeguimento(int id, int comunidadeId) {
		// TODO Auto-generated method stub
		
	}
    
}