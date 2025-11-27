<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.minharede.models.Usuario" %>
<%@ page import="br.com.minharede.models.Post" %>
<%@ page import="br.com.minharede.models.Comentario" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>

<%
    
    Usuario usuarioPerfil = (Usuario) request.getAttribute("usuarioPerfil");
    List<Post> postsCriados = (List<Post>) request.getAttribute("postsCriados");
    List<Comentario> comentariosFeitos = (List<Comentario>) request.getAttribute("comentariosFeitos");
    Integer karmaTotal = (Integer) request.getAttribute("karmaTotal");
    
  
    if (postsCriados == null) postsCriados = java.util.Collections.emptyList();
    if (comentariosFeitos == null) comentariosFeitos = java.util.Collections.emptyList();
    if (karmaTotal == null) karmaTotal = 0;
    
    
    if (usuarioPerfil == null) {
        response.sendRedirect("index.jsp"); 
        return;
    }
    
 
    Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<link rel="stylesheet" href="css/style.css">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Perfil de <%= usuarioPerfil.getNome() %></title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    
    <link rel="stylesheet" href="css/style.css">
    
    <style>
        /* Estilos específicos para o perfil */
        .rounded-circle {
            object-fit: cover;
        }
        .nav-tabs .nav-link.active {
            font-weight: bold;
        }
    </style>
</head>
<body>
    
    <div class="container mt-4">
        <div class="row">
            
            <div class="col-md-4">
                <div class="card shadow-sm mb-4">
                    <div class="card-body text-center">
                        <img src="imagens/default_avatar.png" class="rounded-circle mb-3" alt="Avatar" style="width: 100px; height: 100px;">
                        <h4 class="card-title">u/<%= usuarioPerfil.getNome() %></h4>
                        
                        <% 
                        // Bloco ÚNICO para o botão Adicionar Amigo
                        if (usuarioLogado != null && usuarioLogado.getId() != usuarioPerfil.getId()) { 
                        %>
                            <a href="adicionar-amigo?id=<%= usuarioPerfil.getId() %>" class="btn btn-success btn-sm mt-2 mb-3">
                                <i class="fas fa-user-plus me-1"></i> Adicionar Amigo
                            </a>
                        <% } %>

                        <p class="text-muted small">Membro desde: <%= usuarioPerfil.getDataRegistro() %></p>
                        
                        <div class="row mt-4 pt-2 border-top border-light">
                            <div class="col-6">
                                <h5 class="fw-bold text-primary"><%= postsCriados.size() %></h5>
                                <small class="text-muted">Posts</small>
                            </div>
                            <div class="col-6">
                                <h5 class="fw-bold text-success"><%= karmaTotal %></h5>
                                <small class="text-muted">Karma</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-8">
                <h3>Atividade Recente</h3>
                
                <ul class="nav nav-tabs mb-3" id="perfilTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="posts-tab" data-bs-toggle="tab" data-bs-target="#posts" type="button">Posts</button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="comments-tab" data-bs-toggle="tab" data-bs-target="#comments" type="button">Comentários</button>
                    </li>
                </ul>

                <div class="tab-content" id="perfilTabsContent">
                    
                    <div class="tab-pane fade show active" id="posts" role="tabpanel">
                        <%
                            if (!postsCriados.isEmpty()) {
                                for (Post post : postsCriados) {
                        %>
                                    <div class="card mb-2 p-3 shadow-sm">
                                        <small class="text-muted">Em r/<%= post.getComunidade().getSlug() %> | <%= post.getDataCriacao() %></small>
                                        <h5><a href="post?id=<%= post.getId() %>" class="text-decoration-none"><%= post.getTitulo() %></a></h5>
                                    </div>
                        <%
                                }
                            } else {
                        %>
                                <p class="text-muted">Nenhum post criado ainda.</p>
                        <% } %>
                    </div>
                    
                    <div class="tab-pane fade" id="comments" role="tabpanel">
                        <%
                            if (!comentariosFeitos.isEmpty()) {
                                for (Comentario comentario : comentariosFeitos) {
                        %>
                                    <div class="card mb-2 p-3 shadow-sm">
                                        <small class="text-muted">No post: "<%= comentario.getPost().getTitulo() %>"</small> 
                                        <p class="mb-0"><%= comentario.getConteudo() %></p>
                                    </div>
                        <%
                                }
                            } else {
                        %>
                                <p class="text-muted">Nenhum comentário feito ainda.</p>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>