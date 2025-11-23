package br.com.minharede.DAO;

import br.com.minharede.models.Comunidade;
import br.com.minharede.models.Post;
import br.com.minharede.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



public class PostDAO {

	// ... dentro da classe PostDAO.java

	private Connection getConnection() throws SQLException {
	    // 1. Defina suas credenciais
	    String url = "jdbc:mysql://127.0.0.1:3306/bd?useTimezone=true&serverTimezone=UTC";
	    String user = "root"; // Ex: root
	    String password = "12345"; // A senha do seu usuário do banco

	    try {
	        // 2. Garanta que o Driver MySQL seja carregado (Opcional, mas boa prática)
	        // Não é estritamente necessário para JDBC 4.0+ (Java 6+), mas evita problemas
	        Class.forName("com.mysql.cj.jdbc.Driver");
	    } catch (ClassNotFoundException e) {
	        // Se este erro ocorrer, o JAR do MySQL Connector não está na pasta WEB-INF/lib
	        throw new SQLException("Driver JDBC não encontrado.", e);
	    }

	    // 3. Estabelece e retorna a conexão
	    return java.sql.DriverManager.getConnection(url, user, password);
	}

    
    private Post extrairPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setTitulo(rs.getString("titulo"));
     
        post.setConteudo(rs.getString("conteudo"));
        post.setVotos(rs.getInt("votos"));
        post.setNumComentarios(rs.getInt("num_comentarios")); 

      
        Comunidade comunidade = new Comunidade();
        comunidade.setId(rs.getInt("comunidade_id"));
        comunidade.setSlug(rs.getString("comunidade_slug"));
        post.setComunidade(comunidade);

        
        Usuario usuario = new Usuario(0, null, null);
        usuario.setNome(rs.getString("usuario_nome"));
        post.setUsuario(usuario);
        
        return post;
    }


    /**
     * Busca posts de comunidades específicas (Feed Personalizado).
     */
    public List<Post> buscarPostsPorComunidades(List<Comunidade> comunidades, String orderBy) {
       
        if (comunidades == null || comunidades.isEmpty()) {
            return new ArrayList<>();
        }

        
        String ids = comunidades.stream()
                                .map(c -> String.valueOf(c.getId()))
                                .collect(Collectors.joining(","));

        
        String sql = "SELECT p.*, c.slug as comunidade_slug, u.nome as usuario_nome, " +
                     "(SELECT COUNT(*) FROM Comentario co WHERE co.post_id = p.id) as num_comentarios " +
                     "FROM Post p " +
                     "JOIN Comunidade c ON p.comunidade_id = c.id " +
                     "JOIN Usuario u ON p.usuario_id = u.id " +
                     "WHERE p.comunidade_id IN (" + ids + ") " +
                     "ORDER BY " + orderBy + " LIMIT 50"; 

        List<Post> posts = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                posts.add(extrairPost(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar posts por comunidades: " + e.getMessage());
           
        }
        return posts;
    }
    
    /**
     * Busca posts globais populares (Feed Global/Deslogado).
     */
    public List<Post> buscarPostsPopulares(String orderBy) {
        
        String sql = "SELECT p.*, c.slug as comunidade_slug, u.nome as usuario_nome, " +
                     "(SELECT COUNT(*) FROM Comentario co WHERE co.post_id = p.id) as num_comentarios " +
                     "FROM Post p " +
                     "JOIN Comunidade c ON p.comunidade_id = c.id " +
                     "JOIN Usuario u ON p.usuario_id = u.id " +
                     "WHERE p.data_criacao > DATE_SUB(NOW(), INTERVAL 7 DAY) " + // Exemplo: apenas posts da última semana
                     "ORDER BY " + orderBy + " LIMIT 50"; 

        List<Post> posts = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                posts.add(extrairPost(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar posts populares: " + e.getMessage());
            
        }
        return posts;
    }

 // ... dentro da classe PostDAO.java

    /**
     * Recalcula a soma total de votos para um post e atualiza a coluna 'votos'
     * na tabela Post. É chamado após qualquer registro ou alteração de voto.
     * * @param postId O ID do post a ser recalculado.
     */
    public void recalcularVotos(int postId) {
        // 1. SQL para somar todos os votos (direcao) da tabela VotoPost para o postId
        String selectSql = "SELECT SUM(direcao) AS total_votos FROM VotoPost WHERE post_id = ?";
        
        // 2. SQL para atualizar a tabela Post com o total calculado
        String updateSql = "UPDATE Post SET votos = ? WHERE id = ?";
        
        int totalVotos = 0;

        try (Connection conn = getConnection()) {
            
            // --- Passo 1: Calcular o total ---
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, postId);
                
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        // O método getInt é seguro mesmo se a soma for NULL (retorna 0)
                        totalVotos = rs.getInt("total_votos");
                    }
                }
            }
            
            // --- Passo 2: Atualizar a coluna na tabela Post ---
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, totalVotos); // Novo total
                updateStmt.setInt(2, postId);     // ID do post
                
                updateStmt.executeUpdate();
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao recalcular votos para o post " + postId + ": " + e.getMessage());
        }
        }
     // ... dentro da classe PostDAO.java

        /**
         * Busca um post específico pelo seu ID, juntamente com informações do autor e comunidade.
         *
         * @param postId O ID do post.
         * @return O objeto Post completo ou null se não for encontrado.
         */
        public Post buscarPostPorId(int postId) {
            Post post = null;
            // SQL com JOINs para buscar Post, Usuario (autor) e Comunidade
            String sql = "SELECT p.*, u.nome AS nome_usuario, c.nome AS nome_comunidade, c.slug AS slug_comunidade " +
                         "FROM Post p " +
                         "JOIN Usuario u ON p.usuario_id = u.id " +
                         "JOIN Comunidade c ON p.comunidade_id = c.id " +
                         "WHERE p.id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, postId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Assumimos que você tem um método auxiliar 'extrairPost' para mapear o ResultSet
                        // para o objeto Post, incluindo Usuario e Comunidade.
                        post = extrairPost(rs);

                        // Preenche o campo 'numComentarios' do post
                        int numComentarios = new ComentarioDAO().contarComentariosPorPost(postId);
                        post.setNumComentarios(numComentarios);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar post por ID: " + e.getMessage());
                // Trate a exceção de forma mais robusta em um ambiente de produção
            }
            return post;
        
        }
         // ... dentro da classe PostDAO.java

            /**
             * Salva um novo post no banco de dados.
             *
             * @param post O objeto Post contendo título, conteúdo, tipo, usuario e comunidade.
             * @return O ID gerado para o novo post ou -1 em caso de erro.
             */
            public int salvarPost(Post post) {
                String sql = "INSERT INTO Post (comunidade_id, usuario_id, titulo, conteudo, tipo) VALUES (?, ?, ?, ?, ?)";
                int postId = -1;

                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                    stmt.setInt(1, post.getComunidade().getId());
                    stmt.setInt(2, post.getUsuario().getId());
                    stmt.setString(3, post.getTitulo());
                    stmt.setString(4, post.getConteudo());
                    stmt.setString(5, post.getTipo());

                    stmt.executeUpdate();

                    // Obtém o ID gerado pelo banco
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            postId = rs.getInt(1);
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Erro SQL ao salvar novo post: " + e.getMessage());
                }
                return postId;
            }
    }

    // ...
		
	

    
