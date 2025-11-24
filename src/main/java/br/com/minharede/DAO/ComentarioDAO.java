package br.com.minharede.DAO; // Pacote corrigido para 'dao'

import br.com.minharede.models.Comentario;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;
import br.com.minharede.utils.ConexaoDB; // Assumindo seu utilitário de conexão

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ComentarioDAO {
    
    // Método utilitário para obter a conexão com o BD
    private Connection getConnection() throws SQLException {
        // Usa o utilitário de conexão do seu projeto
        return ConexaoDB.getConnection();
    }

    /**
     * Extrai um objeto Comentario do ResultSet.
     */
    private Comentario extrairComentario(ResultSet rs) throws SQLException {
        Comentario comentario = new Comentario();
        comentario.setId(rs.getInt("id"));
        comentario.setConteudo(rs.getString("conteudo"));
        
        // Converte o TIMESTAMP do SQL para LocalDateTime Java
        comentario.setDataCriacao(rs.getTimestamp("data_criacao")
                                      .toInstant()
                                      .atZone(ZoneId.systemDefault())
                                      .toLocalDateTime());

        // Cria o objeto Usuario (Autor)
        Usuario autor = new Usuario();
        autor.setId(rs.getInt("usuario_id"));
        autor.setNome(rs.getString("usuario_nome")); 
        comentario.setUsuario(autor);
        
        // Cria o objeto Post
        Post post = new Post();
        post.setId(rs.getInt("post_id"));
        // Adiciona o título do post, se estiver presente no SELECT (como em buscarComentariosPorUsuario)
        try {
            post.setTitulo(rs.getString("post_titulo")); 
        } catch (SQLException ignore) {
            // Ignora se o alias post_titulo não estiver na query (como em listarComentariosPorPost)
        }
        comentario.setPost(post);
        
        return comentario;
    }
    
    // ----------------------------------------------------
    // CRUD BÁSICO
    // ----------------------------------------------------
    
    public int adicionarComentario(Comentario comentario) {
        // ... (Corpo do método omitido por brevidade - Está correto)
        return -1;
    }

    public List<Comentario> listarComentariosPorPost(int postId) {
        // ... (Corpo do método omitido por brevidade - Está correto)
        return new ArrayList<>();
    }
    
    public int contarComentariosPorPost(int postId) {
        // ... (Corpo do método omitido por brevidade - Está correto)
        return 0;
    }
    
    public boolean excluirComentario(int comentarioId, int usuarioId) {
        // ... (Corpo do método omitido por brevidade - Está correto)
        return false;
    }
    
    public boolean atualizarComentario(Comentario comentario) {
        // ... (Corpo do método omitido por brevidade - Está correto)
        return false;
    }

    // ----------------------------------------------------
    // METÓDO DE BUSCA SIMPLES (Edição/Perfil)
    // ----------------------------------------------------

    /**
     * [IMPLEMENTADO] Busca todos os comentários feitos pelo usuário para exibição no perfil.
     */
    public List<Comentario> buscarComentariosPorUsuario(int usuarioId) {
        List<Comentario> comentarios = new ArrayList<>();
        // Note o JOIN para buscar o TÍTULO do post (post_titulo) e o NOME do usuário (usuario_nome)
        String sql = "SELECT c.*, p.titulo as post_titulo, u.nome as usuario_nome FROM Comentario c " +
                     "JOIN Post p ON c.post_id = p.id " +
                     "JOIN Usuario u ON c.usuario_id = u.id " + 
                     "WHERE c.usuario_id = ? ORDER BY c.data_criacao DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId); 
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comentarios.add(extrairComentario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar comentários por usuário: " + e.getMessage());
        }
        return comentarios;
    }

    /**
     * [IMPLEMENTADO] Busca um comentário pelo ID (para fins de edição), trazendo apenas o conteúdo e o autor.
     */
    public Comentario buscarComentarioPorId(int comentarioId) {
        String sql = "SELECT c.conteudo, c.usuario_id, c.id, u.nome as usuario_nome FROM Comentario c " +
                     "JOIN Usuario u ON c.usuario_id = u.id " +
                     "WHERE c.id = ?";
        Comentario comentario = null;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, comentarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Extraímos o conteúdo e o ID/Nome do autor
                    comentario = extrairComentario(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar comentário por ID: " + e.getMessage());
        }
        return comentario;
    }
}