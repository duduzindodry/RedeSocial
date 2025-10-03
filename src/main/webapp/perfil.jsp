<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Perfil - MinhaRede</title>
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
        <li class="nav-item"><a class="nav-link" href="index.jsp">Home</a></li>
        <li class="nav-item"><a class="nav-link" href="perfil.jsp">Perfil</a></li>
        <li class="nav-item"><a class="nav-link" href="amigos.jsp">Amigos</a></li>
        <li class="nav-item"><a class="nav-link" href="login.jsp">Login</a></li>
        <li class="nav-item"><a class="nav-link" href="cadastro.jsp">Cadastro</a></li>
      </ul>
    </div>
  </div>
</nav>

  <div class="container mt-5">
    <h2>Meu Perfil</h2>
    <div class="card" style="width: 18rem;">
      <img src="img/perfil.png" class="card-img-top" alt="Foto do usuário">
      <div class="card-body">
        <h5 class="card-title">Nome do Usuário</h5>
        <p class="card-text">Descrição curta sobre o usuário.</p>
        <a href="#" class="btn btn-primary">Editar Perfil</a>
      </div>
    </div>
  </div>
</body>
</html>