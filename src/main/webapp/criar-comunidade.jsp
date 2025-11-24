<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.minharede.models.Usuario" %>

<%
    // Recupera o usuário logado (já garantido pelo ComunidadeServlet, mas é uma boa prática)
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
    
    // Pega a mensagem de erro que pode ter sido enviada pelo Servlet (se a validação falhar)
    String erro = (String) request.getAttribute("error"); 

    // REDIRECIONAMENTO DE SEGURANÇA: Embora o Servlet deva fazer isso, garantimos aqui.
    if (usuario == null) {
        response.sendRedirect("login.jsp?error=login_required");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<link rel="stylesheet" href="css/style.css">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Criar Nova Comunidade</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-7 col-lg-6">
                <div class="card shadow-lg">
                    <div class="card-header bg-success text-white">
                        <i class="fas fa-users me-2"></i> Iniciar Nova Comunidade
                    </div>
                    <div class="card-body">
                        
                        <% if (erro != null) { %>
                            <div class="alert alert-danger" role="alert">
                                <% if (erro.equals("campos_vazios")) { %>
                                    O nome e a descrição da comunidade são obrigatórios.
                                <% } else if (erro.equals("db_falha")) { %>
                                    Ocorreu uma falha ao tentar salvar no banco de dados.
                                <% } else { %>
                                    Ocorreu um erro interno ao criar a comunidade.
                                <% } %>
                            </div>
                        <% } %>
                        
                        <p class="text-muted small mb-4">
                            Você será o primeiro moderador de <strong>r/sua_nova_comunidade</strong>.
                        </p>

                        <form action="criar-comunidade" method="POST"> 
                            
                            <div class="mb-3">
                                <label for="nome" class="form-label fw-bold">Nome da Comunidade</label>
                                <input type="text" class="form-control" id="nome" name="nome" required maxlength="50" placeholder="Ex: Programadores_Java">
                                <div class="form-text">Máximo de 50 caracteres. Este nome será usado para gerar o endereço (slug).</div>
                            </div>

                            <div class="mb-3">
                                <label for="descricao" class="form-label fw-bold">Descrição Curta</label>
                                <textarea class="form-control" id="descricao" name="descricao" rows="3" placeholder="O que é esta comunidade?"></textarea>
                            </div>

                            <div class="d-grid mt-4">
                                <button type="submit" class="btn btn-success btn-lg">
                                    Criar Comunidade
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>