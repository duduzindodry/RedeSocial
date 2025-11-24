<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.minharede.models.Usuario" %>
<%@ page import="br.com.minharede.models.Comunidade" %>

<%
    // Recupera o usuário logado para garantir que a sessão esteja ativa
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
    
    // REDIRECIONAMENTO DE SEGURANÇA: Se não estiver logado, não pode criar posts
    if (usuario == null) {
        response.sendRedirect("login.jsp?error=login_required");
        return;
    }

    // A comunidade alvo pode ser passada via request se o usuário clicar no botão "Criar Post"
    // dentro de uma comunidade específica (ex: /r/programacao)
    Comunidade comunidadeAlvo = (Comunidade) request.getAttribute("comunidadeAlvo");
    
    // Verifica se há alguma mensagem de erro (enviada pelo PostServlet em caso de falha)
    String erro = request.getParameter("error"); 
%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<link rel="stylesheet" href="css/style.css">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Criar Nova Postagem</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card shadow-lg">
                    <div class="card-header bg-primary text-white">
                        <i class="fas fa-plus-circle me-2"></i> Nova Postagem
                    </div>
                    <div class="card-body">
                        
                        <% if (comunidadeAlvo != null) { %>
                            <h5 class="card-subtitle mb-3 text-muted">Postando em: **r/<%= comunidadeAlvo.getNome() %>**</h5>
                        <% } %>
                        
                        <% if (erro != null) { %>
                            <div class="alert alert-danger" role="alert">
                                <% if (erro.equals("campos_vazios")) { %>
                                    Por favor, preencha o Título e o Conteúdo/Link.
                                <% } else if (erro.equals("db_falha")) { %>
                                    Não foi possível salvar a postagem. Tente novamente mais tarde.
                                <% } else { %>
                                    Ocorreu um erro ao tentar criar o post.
                                <% } %>
                            </div>
                        <% } %>

                        <form action="postar" method="POST"> 
                            
                            <div class="mb-3">
                                <label for="titulo" class="form-label fw-bold">Título do Post</label>
                                <input type="text" class="form-control" id="titulo" name="titulo" required maxlength="255">
                            </div>

                            <div class="mb-3">
                                <label for="comunidade" class="form-label fw-bold">Publicar em:</label>
                                
                                <% if (comunidadeAlvo != null) { %>
                                    <input type="hidden" name="comunidadeId" value="<%= comunidadeAlvo.getId() %>">
                                    <input type="text" class="form-control" value="r/<%= comunidadeAlvo.getNome() %>" readonly>
                                
                                <% } else { 
                                    // Se não houver comunidade alvo, você deve carregar uma lista de todas as comunidades
                                    // (Isso exigiria que um Servlet intermediário buscasse a lista de todas as comunidades)
                                %>
                                    <select class="form-select" id="comunidade" name="comunidadeId" required>
                                        <option value="">Selecione uma Comunidade</option>
                                        <%-- AQUI ENTRARIA O LOOP PARA CARREGAR AS OPÇÕES DO BANCO DE DADOS --%>
                                        <option value="1">r/Geral</option>
                                        <option value="2">r/Programacao</option>
                                    </select>
                                <% } %>
                            </div>
                            
                            <div class="mb-3">
                                <label for="conteudo" class="form-label fw-bold">Conteúdo ou Link</label>
                                <input type="hidden" name="tipo" value="TEXTO"> 
                                <textarea class="form-control" id="conteudo" name="conteudo" rows="6" placeholder="Digite seu texto ou cole um link aqui." required></textarea>
                            </div>

                            <div class="d-grid mt-4">
                                <button type="submit" class="btn btn-success btn-lg">Postar Agora</button>
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