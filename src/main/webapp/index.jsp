<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.minharede.models.Usuario" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>MinhaRede - Home</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="css/style.css">
  
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
      <ul class="navbar-nav ms-auto">
      
        <%
          Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
          if (usuario != null) {
            // USUÁRIO LOGADO: MOSTRA PERFIL, AMIGOS E LOGOUT
        %>
        <li class="nav-item"><a class="nav-link" href="index.jsp">Home</a></li>
        <li class="nav-item"><a class="nav-link" href="perfil.jsp">Perfil</a></li>
        <li class="nav-item"><a class="nav-link" href="amigos.jsp">Amigos</a></li>
        
        <li class="nav-item">
            <a class="nav-link btn btn-sm btn-outline-light ms-2" href="logout">Sair (<%= usuario.getNome() %>)</a>
        </li>
        
        <%
          } else {
            // USUÁRIO DESLOGADO: MOSTRA LOGIN E CADASTRO
        %>
        <li class="nav-item"><a class="nav-link" href="index.jsp">Home</a></li>
        <li class="nav-item"><a class="nav-link btn btn-sm btn-outline-light ms-2" href="login.jsp">Login</a></li>
        <li class="nav-item"><a class="nav-link btn btn-sm btn-light ms-2" href="cadastro.jsp">Cadastro</a></li>
        <%
          }
        %>
      </ul>
    </div>
  </div>
</nav>

<div class="container mt-4">
    <div class="row">

        <div class="col-md-3 d-none d-md-block"> 
            <div class="card mb-3 shadow-sm">
                <div class="card-header bg-dark text-white fw-bold">
                    <i class="fas fa-list-ul me-2"></i> Comunidades
                </div>
                <ul class="list-group list-group-flush">
                    <c:choose>
                        <c:when test="${not empty comunidadesSeguidas}">
                            <c:forEach var="comunidadeSeguida" items="${comunidadesSeguidas}">
                                <li class="list-group-item">
                                
                                    <a href="r/${comunidadeSeguida.slug}" class="text-decoration-none">
                                        r/${comunidadeSeguida.slug}
                                    </a>
                                    <a href="seguirComunidade?comunidadeId=${comunidade.id}" class="btn btn-primary">
    Seguir Comunidade
</a>

<a href="seguirComunidade?comunidadeId=${comunidade.id}" class="btn btn-outline-secondary">
    Deixar de Seguir
</a>
                                </li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <li class="list-group-item text-muted small">
                                Não segue nenhuma comunidade. <a href="descobrir.jsp">Descubra!</a>
                            </li>
                        </c:otherwise>
                    </c:choose>
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
            
            <c:choose>
                <c:when test="${not empty posts}">
                   <%-- Trecho dentro do loop <c:forEach var="post" items="${posts}"> no index.jsp --%>

<div class="card shadow-sm mb-3">
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
            
            <%-- ✨ IMPORTANTE: Link para a página do post individual --%>
            <h5 class="post-title">
                <a href="post?id=${post.id}" class="text-decoration-none text-dark">
                    ${post.titulo}
                </a>
            </h5>
            
            <%-- Opcional: Se houver conteúdo curto no card, transforme-o em link também --%>
            <p class="card-text">
                <a href="post?id=${post.id}" class="text-decoration-none text-body">
                    ${post.conteudoCurto} <%-- Ou use substring do post.conteudo --%>
                </a>
            </p>
            
            <div class="post-actions mt-3">
                <%-- Links de Comentário e Ações --%>
                <a href="post?id=${post.id}#comments" class="text-secondary text-decoration-none">
                    <i class="fas fa-comment-alt me-1"></i> ${post.numComentarios} Comentários
                </a>
                </div>
        </div>
    </div>
</div>
                            <div class="post-content">
                                
                                <div class="post-meta">
                                    <span class="badge text-bg-secondary me-2">r/${post.comunidade.slug}</span>
                                    Postado por **u/${post.usuario.nome}** há ${post.tempoAtras}
                                </div>
                                
                                <h5 class="post-title">
                                    <a href="post?id=${post.id}" class="text-decoration-none">${post.titulo}</a>
                                </h5>
                                
                                <p class="text-muted small mb-2">${post.conteudoCurto}...</p>
                                
                                <div class="post-actions">
                                    <a href="post?id=${post.id}#comments">
                                        <i class="fas fa-comment-alt me-1"></i> ${post.numComentarios} Comentários
                                    </a>
                                    <a href="#"><i class="fas fa-share me-1"></i> Compartilhar</a>
                                    <a href="#"><i class="fas fa-save me-1"></i> Salvar</a>
                                </div>
                            </div>
                        </div>
                    
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info text-center" role="alert">
                        Nenhum post para exibir no momento. Que tal criar o primeiro?
                    </div>
                </c:otherwise>
            </c:choose>
            
        </div>

        <div class="col-md-3 d-none d-lg-block">
            <div class="card sticky-top shadow-sm" style="top: 70px;">
                <div class="card-header bg-warning text-dark fw-bold">
                    <i class="fas fa-fire me-2"></i> Tendências Agora
                </div>
                <div class="card-body">
                    <p class="card-text small text-secondary">
                        Os posts e comunidades que estão bombando neste momento!
                    </p>
                    <ul class="list-unstyled small">
                        <li><a href="post?id=5" class="text-decoration-none">Discussão sobre Java e Jakarta EE...</a></li>
                        <li><a href="r/ideias" class="text-decoration-none">Comunidade r/ideias em alta.</a></li>
                        <li><a href="post?id=12" class="text-decoration-none">Melhores livros de programação de 2025.</a></li>
                    </ul>
                </div>
            </div>
            
            <c:if test="${usuario == null}">
                <div class="card mt-3 shadow-sm">
                    <div class="card-body text-center">
                        <p class="card-text small mb-3">Junte-se a nós para personalizar seu feed!</p>
                        <a href="cadastro.jsp" class="btn btn-primary w-100">Crie sua Conta</a>
                    </div>
                </div>
            </c:if>
        </div>
        
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>