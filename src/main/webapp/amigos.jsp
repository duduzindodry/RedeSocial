<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.minharede.models.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<link rel="stylesheet" href="css/style.css">
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Amigos - MinhaRede</title>
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
      <ul class="navbar-nav ms-auto">
      
        <%
          Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
          if (usuario != null) {
        %>
        <li class="nav-item"><a class="nav-link" href="index.jsp">Home</a></li>
        <li class="nav-item"><a class="nav-link" href="perfil">Perfil</a></li> <%-- Link corrigido para o Servlet /perfil --%>
        <li class="nav-item"><a class="nav-link" href="amigos">Amigos</a></li> <%-- Link corrigido para o Servlet /amigos --%>
        
        <li class="nav-item">
            <a class="nav-link btn btn-sm btn-outline-light ms-2" href="logout">Sair (<%= usuario.getNome() %>)</a>
        </li>
        
        <%
          } else {
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

<%
    // Variáveis enviadas pelo AmigosServlet. O import de List e Collections agora está no topo.
    List<Usuario> listaAmigos = (List<Usuario>) request.getAttribute("listaAmigos");
    List<Usuario> solicitacoesRecebidas = (List<Usuario>) request.getAttribute("solicitacoesRecebidas");

    if (listaAmigos == null) listaAmigos = Collections.emptyList();
    if (solicitacoesRecebidas == null) solicitacoesRecebidas = Collections.emptyList();
%>

<div class="container mt-5">
    <h1><i class="fas fa-user-friends me-2"></i> Gerenciar Amigos</h1>
    <hr>

    <ul class="nav nav-tabs mb-4" id="amigosTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="amigos-tab" data-bs-toggle="tab" data-bs-target="#amigos" type="button">
                Amigos (<%= listaAmigos.size() %>)
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="solicitacoes-tab" data-bs-toggle="tab" data-bs-target="#solicitacoes" type="button">
                Solicitações (<%= solicitacoesRecebidas.size() %>)
            </button>
        </li>
    </ul>

    <div class="tab-content" id="amigosTabsContent">
        
        <div class="tab-pane fade show active" id="amigos" role="tabpanel">
            <div class="row">
            <% if (listaAmigos.isEmpty()) { %>
                <p class="text-muted">Você não tem amigos adicionados ainda. Encontre um perfil para adicionar!</p>
            <% } else { 
                for (Usuario amigo : listaAmigos) {
            %>
                    <div class="col-md-4 mb-3">
                        <div class="card shadow-sm">
                            <div class="card-body">
                                <h5 class="card-title">u/<%= amigo.getNome() %></h5>
                                <a href="perfil?id=<%= amigo.getId() %>" class="btn btn-sm btn-outline-primary">Ver Perfil</a>
                                <a href="remover-amigo?id=<%= amigo.getId() %>" class="btn btn-sm btn-outline-danger float-end" 
                                    onclick="return confirm('Tem certeza que deseja remover <%= amigo.getNome() %>?');">Remover</a>
                            </div>
                        </div>
                    </div>
            <%  } 
            } %>
            </div>
        </div>
        
        <div class="tab-pane fade" id="solicitacoes" role="tabpanel">
             <% if (solicitacoesRecebidas.isEmpty()) { %>
                <p class="text-muted">Você não tem solicitações de amizade pendentes.</p>
            <% } else { 
                for (Usuario solicitante : solicitacoesRecebidas) {
            %>
                    <div class="card mb-3 p-3 shadow-sm d-flex flex-row justify-content-between align-items-center">
                        <div>
                            <span class="fw-bold">u/<%= solicitante.getNome() %></span> quer ser seu amigo.
                        </div>
                        <div>
                            <a href="aceitar-amigo?id=<%= solicitante.getId() %>" class="btn btn-success me-2">Aceitar</a>
                            <a href="rejeitar-amigo?id=<%= solicitante.getId() %>" class="btn btn-outline-secondary">Rejeitar</a>
                        </div>
                    </div>
            <%  } 
            } %>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>