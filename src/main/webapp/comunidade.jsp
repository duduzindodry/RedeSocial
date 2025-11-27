<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.minharede.models.Comunidade" %>
<%@ page import="br.com.minharede.models.Post" %>
<%@ page import="java.util.List" %>
<%@ page import="br.com.minharede.models.Usuario" %>

<%
    Comunidade comunidade = (Comunidade) request.getAttribute("comunidade");
    List<Post> posts = (List<Post>) request.getAttribute("posts");
    boolean isSeguindo = (Boolean) request.getAttribute("isSeguindo");
    Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
%>

<div class="container-fluid bg-secondary text-white p-4">
    <div class="container">
        <h1 class="display-4">r/<%= comunidade.getNome() %></h1>
    </div>
</div>

<div class="container mt-4">
    <div class="row">
        
        <div class="col-md-8">
            
            
            <a href="criar-post.jsp?comunidadeId=<%= comunidade.getId() %>" class="btn btn-success mb-3">
                <i class="fas fa-plus-circle"></i> Criar Post em r/<%= comunidade.getNome() %>
            </a>

            <% 
                if (posts != null && !posts.isEmpty()) { 
                    for (Post post : posts) {
                        // COLOQUE O CÓDIGO DO CARD DO POST AQUI
            %>
                        <div class="card mb-3 shadow-sm post-card">
                            <div class="post-card-content">
                                <h5><%= post.getTitulo() %></h5>
                                <p class="small text-muted">por u/<%= post.getUsuario().getNome() %></p>
                            </div>
                        </div>
            <% 
                    }
                } else {
            %>
                <div class="alert alert-info">Nenhum post nesta comunidade ainda.</div>
            <% } %>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm mb-3">
                <div class="card-header bg-light">
                    Sobre a Comunidade
                </div>
                <div class="card-body">
                    <p class="card-text"><%= comunidade.getDescricao() %></p>
                    <p class="small text-muted">Membro desde: <%= comunidade.getDataCriacao() %></p>
                    <hr>
                    
                   
                    <% if (usuarioLogado != null) { %>
                        <a href="seguirComunidade?comunidadeId=<%= comunidade.getId() %>" 
                           class="btn <%= isSeguindo ? "btn-outline-danger" : "btn-primary" %> w-100">
                            <%= isSeguindo ? "Deixar de Seguir" : "Seguir" %>
                        </a>
                    <% } else { %>
                        <a href="login.jsp" class="btn btn-primary w-100">Faça login para seguir</a>
                    <% } %>
                    
                </div>
            </div>
        </div>
        
    </div>
</div>
</html>