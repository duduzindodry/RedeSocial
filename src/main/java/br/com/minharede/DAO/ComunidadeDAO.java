package br.com.minharede.DAO;

import br.com.minharede.models.Comunidade;
import br.com.minharede.utils.ConexaoDB; 

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class ComunidadeDAO {
	public ComunidadeDAO() throws SQLException { 
        
       
    }
    private Connection getConnection() throws SQLException {
        return ConexaoDB.getConnection(); 
    }

    public Comunidade buscarComunidadePorSlug(String slug) throws SQLException {
        String sql = "SELECT * FROM Comunidade WHERE slug = ?";
        Comunidade comunidade = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarComunidadePorSlug: " + e.getMessage());
            throw e; 
        }
        return comunidade;
    }
    
    public boolean salvarComunidade(Comunidade comunidade) throws SQLException {
        String sql = "INSERT INTO Comunidade (...) VALUES (...)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
           
        } catch (SQLException e) {
            System.err.println("Erro SQL em salvarComunidade: " + e.getMessage());
            throw e; 
        }
        return false;
    }

    public boolean verificarAutoridadeModerador(int comunidadeId, int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(id) FROM Comunidade WHERE id = ? AND criador_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
         
        } catch (SQLException e) {
            System.err.println("Erro SQL em verificarAutoridadeModerador: " + e.getMessage());
            throw e; 
        }
        return false;
    }

    public boolean isSeguindo(int usuarioId, int comunidadeId) throws SQLException {
        String sql = "SELECT COUNT(usuario_id) FROM SeguidorComunidade WHERE usuario_id = ? AND comunidade_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
           
        } catch (SQLException e) {
            System.err.println("Erro SQL em isSeguindo: " + e.getMessage());
            throw e; 
        }
        return false;
    }

    public boolean alternarSeguimento(int usuarioId, int comunidadeId) throws SQLException {
       
        boolean seguindo = isSeguindo(usuarioId, comunidadeId); 
        String sql = seguindo ? "DELETE FROM SeguidorComunidade..." : "INSERT INTO SeguidorComunidade...";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
        } catch (SQLException e) {
            System.err.println("Erro SQL em alternarSeguimento: " + e.getMessage());
            throw e; 
        }
        return false;
    }
    
    public Comunidade buscarComunidadePorId(int comunidadeId) throws SQLException {
        String sql = "SELECT * FROM Comunidade WHERE id = ?";
        Comunidade comunidade = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
          
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarComunidadePorId: " + e.getMessage());
            throw e;
        }
        return comunidade;
    }

    public List<Comunidade> buscarComunidadesSeguidas(int usuarioId) throws SQLException {
        String sql = "SELECT c.* FROM Comunidade c JOIN SeguidorComunidade sc ON c.id = sc.comunidade_id WHERE sc.usuario_id = ?";
        List<Comunidade> comunidades = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
           
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarComunidadesSeguidas: " + e.getMessage());
            throw e;
        }
        return comunidades;
    }

    public List<Comunidade> buscarComunidadesPorTermo(String termo) throws SQLException {
        String sql = "SELECT * FROM Comunidade WHERE LOWER(nome) LIKE ?";
        List<Comunidade> comunidades = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        } catch (SQLException e) {
            System.err.println("Erro SQL em buscarComunidadesPorTermo: " + e.getMessage());
            throw e;
        }
        return comunidades;
    }
}