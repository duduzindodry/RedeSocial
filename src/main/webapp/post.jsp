<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="br.com.minharede.models.Usuario" %>

<nav></nav>

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            
            <div class="card shadow-sm mb-4">
                <div class="post-card d-flex">
                    <div class="post-sidebar">
                        <a href="votar?postId=${post.id}&direcao=1" class="upvote"><i class="fas fa-arrow-up"></i></a>
                        <span class="fw-bold">${post.votos}</span>
                        <a href="votar?postId=${post.id}&direcao=-1" class="downvote"><i class="fas fa-arrow-down"></i></a>
                    </div>
                    
                    <div class="post-content w-100">
                        <div class="post-meta">
                            <span class="badge text-bg-secondary me-2">r/${post.comunidade.slug}</span>
                            Postado por **u/${post.usuario.nome}**
                        </div>
                        
                        <h1 class="post-title">${post.titulo}</h1>
                        <p class="card-text">${post.conteudo}</p>
                        
                        <div class="post-actions mt-3">
                            <a href="#comments"><i class="fas fa-comment-alt me-1"></i> ${post.numComentarios} Comentários</a>
                            </div>
                    </div>
                </div>
            </div>

            <h5 class="mt-4 mb-3" id="comments">Adicionar Comentário</h5>
            <% Usuario usuario = (Usuario) session.getAttribute("usuarioLogado"); %>
            
            <c:if test="<%= usuario != null %>">
                <div class="card mb-4">
                    <div class="card-body">
                        <form action="comentar" method="POST">
                            <input type="hidden" name="postId" value="${post.id}">
                            <div class="mb-3">
                                <textarea name="conteudo" class="form-control" rows="3" placeholder="O que você tem a dizer?" required></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary">Comentar</button>
                        </form>
                    </div>
                </div>
            </c:if>
            <c:if test="<%= usuario == null %>">
                <div class="alert alert-warning text-center">
                    <a href="login.jsp" class="alert-link">Faça login</a> para adicionar um comentário.
                </div>
            </c:if>

            <h5 class="mt-4 mb-3">${comentarios.size()} Comentários</h5>
            
            <c:forEach var="comentario" items="${comentarios}">
                <div class="card mb-3 comment-card">
                    <div class="card-body p-3">
                        <small class="text-muted d-block mb-1">
                            **u/${comentario.usuario.nome}** | ${comentario.dataCriacao}
                        </small>
                        <p class="mb-0">${comentario.conteudo}</p>
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