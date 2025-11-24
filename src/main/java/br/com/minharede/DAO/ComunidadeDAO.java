package br.com.minharede.DAO; // Corrigido para 'dao' minúsculo

import br.com.minharede.models.Comunidade;
import br.com.minharede.utils.ConexaoDB; // Utilitário de conexão
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComunidadeDAO {
    
    // NOTA: O método 'getConnection()' que lança a exceção FOI REMOVIDO.
    // Todas as operações agora usam ConexaoDB.getConnection().

    /**
     * Método auxiliar para extrair dados do ResultSet.
     */
    private Comunidade extrairComunidade(ResultSet rs) throws SQLException {
        Comunidade c = new Comunidade();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setSlug(rs.getString("slug"));
        c.setDescricao(rs.getString("descricao"));
        // Adicionar c.setCriadorId(rs.getInt("criador_id")) se for necessário
        return c;
    }

    // ----------------------------------------------------------------------
    // 1. MÉTODOS DE CONSULTA
    // ----------------------------------------------------------------------

    /**
     * Busca todas as comunidades que um usuário específico está seguindo.
     */
    public List<Comunidade> buscarComunidadesSeguidas(int usuarioId) {
        String sql = "SELECT c.* FROM Comunidade c " +
                     "JOIN UsuarioComunidade uc ON c.id = uc.comunidade_id " +
                     "WHERE uc.usuario_id = ?";

        List<Comunidade> comunidades = new ArrayList<>();

        try (Connection conn = ConexaoDB.getConnection(); // Corrigido
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
    
    /**
     * Busca uma comunidade completa pelo seu slug (UNIFICADO).
     */
    public Comunidade buscarComunidadePorSlug(String slug) {
        String sql = "SELECT * FROM Comunidade WHERE slug = ?";
        Comunidade comunidade = null;

        try (Connection conn = ConexaoDB.getConnection(); // Corrigido
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, slug);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    comunidade = extrairComunidade(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar comunidade por slug: " + e.getMessage());
        }
        return comunidade;
    }

    /**
     * Busca comunidades por termo de pesquisa no nome ou descrição.
     */
	public List<Comunidade> buscarComunidadesPorTermo(String termo) {
	    String sql = "SELECT * FROM Comunidade c " +
	                 "WHERE LOWER(c.nome) LIKE ? OR LOWER(c.descricao) LIKE ? " +
	                 "ORDER BY c.data_criacao DESC LIMIT 10"; 

	    List<Comunidade> comunidades = new ArrayList<>();

	    try (Connection conn = ConexaoDB.getConnection(); // Corrigido
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, termo);
	        stmt.setString(2, termo);

	        try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comunidades.add(extrairComunidade(rs));
                }
            }
	        
	    } catch (SQLException e) {
	        System.err.println("Erro ao buscar comunidades por termo: " + e.getMessage());
	    }
	    return comunidades;
	}

    // ----------------------------------------------------------------------
    // 2. MÉTODOS DE MANIPULAÇÃO / MODERAÇÃO
    // ----------------------------------------------------------------------

    /**
     * Salva uma nova comunidade no banco de dados.
     */
	public boolean salvarComunidade(Comunidade comunidade) {
	    String sql = "INSERT INTO Comunidade (nome, slug, descricao, criador_id) VALUES (?, ?, ?, ?)";
	    int linhasAfetadas = 0;
	    
	    try (Connection conn = ConexaoDB.getConnection(); // Corrigido
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setString(1, comunidade.getNome());
	        stmt.setString(2, comunidade.getSlug());
	        stmt.setString(3, comunidade.getDescricao());
	        stmt.setInt(4, comunidade.getModerador().getId());

	        linhasAfetadas = stmt.executeUpdate();
	        
	        // Obtém o ID e realiza a ação extra de seguimento (se implementada)
	        if (linhasAfetadas > 0) {
	            // ... lógica de obtenção de ID e seguimento do criador ...
	            return true;
	        }
	        return linhasAfetadas > 0;

	    } catch (SQLException e) {
	        System.err.println("Erro SQL ao salvar nova comunidade: " + e.getMessage());
	        return false;
	    }
	}
	
    /**
     * Verifica se o usuário é o criador (moderador principal) da comunidade.
     */
    public boolean verificarAutoridadeModerador(int comunidadeId, int usuarioId) {
        String sql = "SELECT COUNT(id) FROM Comunidade WHERE id = ? AND criador_id = ?";
        
        try (Connection conn = ConexaoDB.getConnection(); // Corrigido
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, comunidadeId);
            stmt.setInt(2, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar autoridade de moderador: " + e.getMessage());
        }
        return false;
    }
    
   
 // ... dentro da classe ComunidadeDAO.java

    /**
     * [IMPLEMENTADO] Alterna o status de seguimento de uma comunidade por um usuário.
     */
    public boolean alternarSeguimento(int usuarioId, int comunidadeId) {
        if (isSeguindo(usuarioId, comunidadeId)) {
            // Usuário já segue -> Deixar de seguir (DELETE)
            String sql = "DELETE FROM UsuarioComunidade WHERE usuario_id = ? AND comunidade_id = ?";
            try (Connection conn = ConexaoDB.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, usuarioId);
                stmt.setInt(2, comunidadeId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Erro ao deixar de seguir comunidade: " + e.getMessage());
                return false;
            }
        } else {
            // Usuário não segue -> Começar a seguir (INSERT)
            String sql = "INSERT INTO UsuarioComunidade (usuario_id, comunidade_id) VALUES (?, ?)";
            try (Connection conn = ConexaoDB.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, usuarioId);
                stmt.setInt(2, comunidadeId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                // Pode falhar por PK (se outro usuário estiver tentando seguir ao mesmo tempo ou falha de lógica)
                System.err.println("Erro ao começar a seguir comunidade: " + e.getMessage());
                return false;
            }
        }
    }
 public boolean isSeguindo(int usuarioId, int comunidadeId) {
	// TODO Auto-generated method stub
	return false;
}

 // ... dentro da classe ComunidadeDAO.java

    /**
     * [IMPLEMENTADO] Busca uma comunidade pelo ID.
     */
    public Comunidade buscarComunidadePorId(int comunidadeId) {
        String sql = "SELECT * FROM Comunidade WHERE id = ?";
        Comunidade comunidade = null;

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, comunidadeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    comunidade = extrairComunidade(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar comunidade por ID: " + e.getMessage());
        }
        return comunidade;
    }

	
	
}