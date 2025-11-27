<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <link rel="stylesheet" href="css/style.css">
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Cadastro - MinhaRede</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
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
        <li class="nav-item"><a class="nav-link" href="login.jsp">Login</a></li>
        <li class="nav-item"><a class="nav-link" href="cadastro.jsp">Cadastro</a></li>
      </ul>
    </div>
  </div>
</nav>

<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-md-6">
      <div class="card shadow-lg">
        <div class="card-body">
          <h3 class="card-title text-center mb-4">Cadastre-se</h3>
          
          <!-- BLOCO QUE EXIBE ERROS DO SERVLET -->
          <% if (request.getAttribute("erro") != null) { %>
            <div class="alert alert-danger text-center mb-3">
              <%= request.getAttribute("erro") %>
            </div>
          <% } %>
          
          <form action="cadastro" method="post">
            <div class="mb-3">
              <label for="nome" class="form-label">Nome Completo</label>
              <input type="text" class="form-control" id="nome" name="nome" required>
            </div>
            <div class="mb-3">
              <label for="usuario" class="form-label">Nome de Usuário</label>
              <input type="text" class="form-control" id="usuario" name="usuario" required>
            </div>
            <div class="mb-3">
              <label for="email" class="form-label">Email</label>
              <input type="email" class="form-control" id="email" name="email" required>
            </div>
            <div class="mb-3">
              <label for="senha" class="form-label">Senha</label>
              <input type="password" class="form-control" id="senha" name="senha" required>
            </div>
            <div class="d-grid">
              <button type="submit" class="btn btn-success">Cadastrar</button>
            </div>
          </form>
          
          <p class="text-center mt-3">
            Já tem conta? <a href="login.jsp">Faça Login</a>
          </p>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>