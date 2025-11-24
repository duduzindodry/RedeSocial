<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.minharede.models.Usuario" %>
<%@ page import="br.com.minharede.models.Comunidade" %>
<%@ page import="br.com.minharede.models.Post" %>
<%@ page import="java.util.List" %>


<%
    // --- Variáveis de Contexto (Scriptlets) ---
    // O objeto 'post' deve ser obtido do request (enviado pelo PostViewServlet)
    Post post = (Post) request.getAttribute("post");
    // O objeto 'usuarioLogado' é obtido da sessão
    Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

    // Lógica para checar se o usuário atual é o autor do post
    boolean isAuthor = (usuarioLogado != null && post != null && post.getUsuario().getId() == usuarioLogado.getId());
    
    // NOTA: Para exibir os botões de moderação de terceiros,
    // a verificação 'isModerador' deve ser feita no topo do arquivo!
    // Ex: boolean isModerador = new ComunidadeDAO().verificarAutoridadeModerador(post.getComunidade().getId(), usuarioLogado.getId());
%>

<nav></nav>

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            
            <!-- CARD DA POSTAGEM PRINCIPAL -->
            <div class="card shadow-sm mb-4">
                <div class="post-card d-flex">
                    <div class="post-sidebar">
                        <%-- Botão de Votação (AJAX) - Adicionado a classe 'vote-action' --%>
                        <a href="votar?postId=<%= post.getId() %>&direcao=1" class="upvote vote-action"><i class="fas fa-arrow-up"></i></a>
                        <span class="fw-bold score"><%= post.getVotos() %></span>
                        <a href="votar?postId=<%= post.getId() %>&direcao=-1" class="downvote vote-action"><i class="fas fa-arrow-down"></i></a>
                    </div>
                    
                    <div class="post-content w-100">
                        <div class="post-meta">
                            <span class="badge text-bg-secondary me-2">r/<%= post.getComunidade().getSlug() %></span>
                            Postado por **u/<%= post.getUsuario().getNome() %>**
                        </div>
                        
                        <h1 class="post-title"><%= post.getTitulo() %></h1>
                        <p class="card-text"><%= post.getConteudo() %></p>
                        
                        <div class="post-actions mt-3">
                            <a href="#comments"><i class="fas fa-comment-alt me-1"></i> <%= post.getNumComentarios() %> Comentários</a>
                            
                            <%-- Botões de Edição e Exclusão do POST (Autor) --%>
                            <% if (isAuthor) { %>
                                <a href="editar?tipo=post&id=<%= post.getId() %>" class="btn btn-sm btn-outline-info ms-3">
                                    <i class="fas fa-edit"></i> Editar
                                </a>
                                <a href="deletar?tipo=post&id=<%= post.getId() %>" 
                                   class="btn btn-sm btn-outline-danger" 
                                   onclick="return confirm('ATENÇÃO: Este post será excluído permanentemente. Continuar?');">
                                    <i class="fas fa-trash"></i> Excluir
                                </a>
                            <% } %>
                            <%-- Adicionar botão de Moderação aqui se a variável isModerador for verdadeira --%>
                        </div>
                    </div>
                </div>
            </div>

            <h5 class="mt-4 mb-3" id="comments">Adicionar Comentário</h5>
            
            <%-- Formulário de Comentário --%>
            <c:if test="<%= usuarioLogado != null %>">
                <div class="card mb-4">
                    <div class="card-body">
                        <form action="comentar" method="POST">
                            <input type="hidden" name="postId" value="<%= post.getId() %>">
                            <div class="mb-3">
                                <textarea name="conteudo" class="form-control" rows="3" placeholder="O que você tem a dizer?" required></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary">Comentar</button>
                        </form>
                    </div>
                </div>
            </c:if>
            <c:if test="<%= usuarioLogado == null %>">
                <div class="alert alert-warning text-center">
                    <a href="login.jsp" class="alert-link">Faça login</a> para adicionar um comentário.
                </div>
            </c:if>

            <h5 class="mt-4 mb-3">${comentarios.size()} Comentários</h5>
            
            <%-- Listagem de Comentários --%>
            <c:forEach var="comentario" items="${comentarios}">
                <div class="card mb-3 comment-card">
                    <div class="card-body p-3">
                        <small class="text-muted d-block mb-1">
                            **u/${comentario.usuario.nome}** | ${comentario.dataCriacao}
                        </small>
                        <p class="mb-0">${comentario.conteudo}</p>
                        
                        <%-- Botões de Edição e Exclusão do COMENTÁRIO (JSTL/EL) --%>
                        <c:if test="${usuarioLogado != null && comentario.usuario.id == usuarioLogado.id}">
                            <div class="mt-2 text-end">
                                
                                <a href="editar?tipo=comentario&id=${comentario.id}" 
                                   class="btn btn-sm btn-outline-secondary">
                                    Editar
                                </a>
                                
                                <a href="deletar?tipo=comentario&id=${comentario.id}" 
                                   class="btn btn-sm btn-outline-danger"
                                   onclick="return confirm('Tem certeza que deseja excluir este comentário?');">
                                    Excluir
                                </a>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty comentarios}">
                <p class="text-muted">Seja o primeiro a comentar!</p>
            </c:if>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<%-- REMOVIDO: Duplicação de script do Bootstrap --%>
<script src="js/voto.js"></script> 
</body>
</html>