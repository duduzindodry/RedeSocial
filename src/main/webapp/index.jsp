<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.minharede.models.Usuario" %>
<%@ page import="br.com.minharede.models.Comunidade" %>
<%@ page import="br.com.minharede.models.Post" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>

<%
    // Mensagem de debug opcional:
    System.out.println("usuarioLogado JSP: " + session.getAttribute("usuarioLogado"));

    List<Post> posts = (List<Post>) request.getAttribute("posts");
    if (posts == null) posts = Collections.emptyList();

    List<Comunidade> comunidadesSeguidas = (List<Comunidade>) request.getAttribute("comunidadesSeguidas");
    if (comunidadesSeguidas == null) comunidadesSeguidas = Collections.emptyList();

    Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
%>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MinhaRede - Home</title>
    <link rel="stylesheet" href="css/style.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container">
    <a class="navbar-brand" href="index.jsp">MinhaRede</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarNav">
      <form class="d-flex mx-auto" action="pesquisar" method="GET">
        <input 
          class="form-control me-2" 
          type="search" 
          placeholder="Buscar posts, comunidades..." 
          aria-label="Search"
          name="q" 
          style="width: 350px;"
          value="<%= request.getParameter("q") != null ? request.getParameter("q") : "" %>"
        >
        <button class="btn btn-outline-success" type="submit">
            <i class="fas fa-search"></i>
        </button>
      </form>

      <ul class="navbar-nav ms-auto">
        <% if (usuario != null) { %>
            <li class="nav-item"><a class="nav-link" href="index.jsp">Home</a></li>
            <li class="nav-item"><a class="nav-link" href="perfil">Perfil</a></li>
            <li class="nav-item"><a class="nav-link" href="amigos">Amigos</a></li>

            <li class="nav-item">
                <a class="nav-link btn btn-sm btn-outline-light ms-2" href="logout">Sair (<%= usuario.getNome() %>)</a>
            </li>
        <% } else { %>
            <li class="nav-item"><a class="nav-link" href="index.jsp">Home</a></li>
            <li class="nav-item"><a class="nav-link btn btn-sm btn-outline-light ms-2" href="login.jsp">Login</a></li>
            <li class="nav-item"><a class="nav-link btn btn-sm btn-light ms-2" href="cadastro.jsp">Cadastro</a></li>
        <% } %>
      </ul>
    </div>
  </div>
</nav>

<div class="container mt-4">
    <div class="row">

        <!-- Barra lateral de comunidades -->
        <div class="col-md-3 d-none d-md-block"> 
            <div class="card mb-3 shadow-sm">
                <div class="card-header bg-dark text-white fw-bold">
                    <i class="fas fa-list-ul me-2"></i> Comunidades
                </div>
                <ul class="list-group list-group-flush">
                    <% 
                        if (!comunidadesSeguidas.isEmpty()) { 
                            for (Comunidade c : comunidadesSeguidas) {
                    %>
                                <li class="list-group-item">
                                    <a href="r/<%= c.getSlug() %>" class="text-decoration-none">
                                        r/<%= c.getSlug() %>
                                    </a>
                                    <% if (usuario != null) { %>
                                        <a href="seguirComunidade?comunidadeId=<%= c.getId() %>" class="btn btn-outline-danger btn-sm float-end" title="Deixar de Seguir">
                                            <i class="fas fa-user-minus"></i>
                                        </a>
                                    <% } %>
                                </li>
                    <%      } 
                        } else {
                    %>
                            <li class="list-group-item text-muted small">
                                Não segue nenhuma comunidade. <a href="descobrir.jsp">Descubra!</a>
                            </li>
                    <%  } %>
                </ul>
            </div>
            
            <div class="card shadow-sm">
                <div class="card-body text-center">
                    <p class="card-text small text-secondary mb-3">
                        Compartilhe suas ideias ou crie um novo fórum!
                    </p>
                    <a href="criar-post.jsp" class="btn btn-success w-100 mb-2">
                        <i class="fas fa-plus-circle me-1"></i> Criar Post
                    </a>
                    <a href="criar-comunidade.jsp" class="btn btn-outline-secondary w-100">
                        <i class="fas fa-users me-1"></i> Criar Comunidade
                    </a>
                </div>
            </div>
        </div>

        <!-- Feed principal -->
        <div class="col-md-6">
            <div class="p-3 bg-white rounded shadow-sm mb-3 d-flex justify-content-between">
                <h5 class="mb-0">Feed (<%= usuario != null ? "Personalizado" : "Global" %>)</h5>
                <div>
                    <span class="text-muted me-2 small">Ordenar:</span>
                    <a href="?sort=hot" class="btn btn-sm btn-danger active">Quente</a>
                    <a href="?sort=new" class="btn btn-sm btn-outline-secondary">Novo</a>
                    <a href="?sort=top" class="btn btn-sm btn-outline-secondary">Top</a>
                </div>
            </div>
            
            <% if (!posts.isEmpty()) { 
                for (Post post : posts) {
            %>
                    <div class="card shadow-sm mb-3">
                        <div class="post-card d-flex">
                            <div class="post-sidebar">
                                <a href="votar?postId=<%= post.getId() %>&direcao=1" class="upvote vote-action"><i class="fas fa-arrow-up"></i></a>
                                <span class="fw-bold score"><%= post.getVotos() %></span>
                                <a href="votar?postId=<%= post.getId() %>&direcao=-1" class="downvote vote-action"><i class="fas fa-arrow-down"></i></a>
                            </div>
                            <div class="post-content w-100">
                                <div class="post-meta">
                                    <span class="badge text-bg-secondary me-2">r/<%= post.getComunidade().getSlug() %></span>
                                    Postado por <b>u/<%= post.getUsuario().getNome() %></b>
                                </div>
                                <h5 class="post-title">
                                    <a href="post?id=<%= post.getId() %>" class="text-decoration-none text-dark">
                                        <%= post.getTitulo() %>
                                    </a>
                                </h5>
                                <p class="card-text">
                                    <a href="post?id=<%= post.getId() %>" class="text-decoration-none text-body">
                                        <%= post.getConteudoCurto() != null ? post.getConteudoCurto() : post.getConteudo() %>
                                    </a>
                                </p>
                                <div class="post-actions mt-3">
                                    <a href="post?id=<%= post.getId() %>#comments" class="text-secondary text-decoration-none">
                                        <i class="fas fa-comment-alt me-1"></i> <%= post.getNumComentarios() %> Comentários
                                    </a>
                                    <a href="#"><i class="fas fa-share me-1"></i> Compartilhar</a>
                                    <a href="#"><i class="fas fa-save me-1"></i> Salvar</a>
                                </div>
                            </div>
                        </div>
                    </div>
            <% 
                } // Fim do for loop de posts
            } else { %>
                <div class="alert alert-info text-center" role="alert">
                    Nenhum post para exibir no momento. Que tal criar o primeiro?
                </div>
            <%  } %>
        </div>

        <div class="col-md-3 d-none d-lg-block"></div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/voto.js"></script>
</body>
</html>