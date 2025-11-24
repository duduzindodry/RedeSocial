package br.com.minharede.DAO;

import br.com.minharede.utils.ConexaoDB;
import br.com.minharede.models.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Adicione a dependência jBCrypt ao projeto!
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAO {

    // ----------------------------------------------------
    // CONEXÃO E AUXILIARES
    // ----------------------------------------------------
    private Connection getConnection() throws SQLException {
        return ConexaoDB.getConnection();
    }

    private Usuario extrairUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("email")
        );
        usuario.setDataRegistro(rs.getDate("data_registro"));
        return usuario;
    }

    private int[] getAmizadeOrder(int id1, int id2) {
      return (id1 < id2) ? new int[]{id1, id2} : new int[]{id2, id1};
    }

    // ----------------------------------------------------
    // CADASTRO, AUTENTICAÇÃO E PERFIL
    // ----------------------------------------------------
    public boolean cadastrarUsuario(String nome, String usuario, String email, String senha) {
        String sql = "INSERT INTO usuarios (nome, usuario, email, senha) VALUES (?, ?, ?, ?)";
        String hashSenha = BCrypt.hashpw(senha.trim(), BCrypt.gensalt(12)); // <--- Hash seguro!

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, usuario);
            stmt.setString(3, email);
            stmt.setString(4, hashSenha);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro ao cadastrar usuário no DB: " + e.getMessage());
            return false;
        }
    }

    public Usuario autenticarUsuario(String usuarioOuEmail, String senha) {
        String sql = "SELECT id, nome, email, senha, data_registro FROM usuarios WHERE usuario = ? OR email = ?";
        String senhaInput = senha.trim();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuarioOuEmail);
            stmt.setString(2, usuarioOuEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaDoBanco = rs.getString("senha");

                    if (senhaDoBanco != null &&
                        BCrypt.checkpw(senhaInput, senhaDoBanco)) { // <--- Verificação segura!
                        return extrairUsuario(rs);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erro crítico durante a autenticação: " + e.getMessage());
        }

        return null;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT id, nome, email, data_registro FROM usuarios WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------
    // GERENCIAMENTO DE AMIZADE
    // ----------------------------------------------------
    public boolean solicitarAmizade(int solicitanteId, int receptorId) {
        int usuario1_id = Math.min(solicitanteId, receptorId);
        int usuario2_id = Math.max(solicitanteId, receptorId);

        String sql = "INSERT INTO Amizade (usuario1_id, usuario2_id, status) VALUES (?, ?, 'PENDENTE')";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario1_id);
            stmt.setInt(2, usuario2_id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062 || "23505".equals(e.getSQLState())) {
                System.out.println("Solicitação já existe ou está duplicada.");
                return false;
            }
            System.err.println("Erro SQL ao solicitar amizade: " + e.getMessage());
            return false;
        }
    }

    public boolean aceitarAmizade(int usuarioId, int solicitanteId) {
      int[] ids = getAmizadeOrder(usuarioId, solicitanteId);

      String sql = "UPDATE Amizade SET status = 'ACEITA', data_inicio = CURRENT_TIMESTAMP " +
                   "WHERE usuario1_id = ? AND usuario2_id = ? AND status = 'PENDENTE'";

      try (Connection conn = getConnection();
           PreparedStatement stmt = conn.prepareStatement(sql)) {

          stmt.setInt(1, ids[0]);
          stmt.setInt(2, ids[1]);

          return stmt.executeUpdate() > 0;

      } catch (SQLException e) {
          System.err.println("Erro ao aceitar amizade: " + e.getMessage());
          return false;
      }
    }

    public boolean removerAmizade(int usuarioId, int amigoId) {
      int[] ids = getAmizadeOrder(usuarioId, amigoId);

      String sql = "DELETE FROM Amizade WHERE usuario1_id = ? AND usuario2_id = ?";

      try (Connection conn = getConnection();
           PreparedStatement stmt = conn.prepareStatement(sql)) {

          stmt.setInt(1, ids[0]);
          stmt.setInt(2, ids[1]);

          return stmt.executeUpdate() > 0;

      } catch (SQLException e) {
          System.err.println("Erro ao remover/rejeitar amizade: " + e.getMessage());
          return false;
      }
    }

    // ----------------------------------------------------
    // BUSCA DE AMIGOS (PENDENTES E ACEITOS)
    // ----------------------------------------------------

    public List<Usuario> buscarAmigosAceitos(int usuarioId) {
        String sql = "SELECT u.id, u.nome, u.email, u.data_registro FROM usuarios u JOIN Amizade a " +
                     "ON (u.id = a.usuario1_id OR u.id = a.usuario2_id) " +
                     "WHERE a.status = 'ACEITA' AND u.id != ? AND (a.usuario1_id = ? OR a.usuario2_id = ?)";

        return buscarListaAmizade(usuarioId, sql, true);
    }

    public List<Usuario> buscarSolicitacoesRecebidas(int usuarioId) {
        String sql = "SELECT u.id, u.nome, u.email, u.data_registro FROM usuarios u JOIN Amizade a ON u.id = a.usuario1_id " +
                     "WHERE a.status = 'PENDENTE' AND a.usuario2_id = ?";

        return buscarListaAmizade(usuarioId, sql, false);
    }

    private List<Usuario> buscarListaAmizade(int usuarioId, String sql, boolean isAmigosQuery) {
        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (isAmigosQuery) {
                stmt.setInt(1, usuarioId);
                stmt.setInt(2, usuarioId);
                stmt.setInt(3, usuarioId);
            } else {
                stmt.setInt(1, usuarioId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairUsuario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar lista de amizade: " + e.getMessage());
        }
        return lista;
    }
}