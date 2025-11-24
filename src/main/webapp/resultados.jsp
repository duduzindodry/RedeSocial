<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.minharede.models.Post" %>
<%@ page import="br.com.minharede.models.Comunidade" %>
<%@ page import="java.util.List" %>
<% 
    String query = (String) request.getAttribute("query");
    List<Post> postsEncontrados = (List<Post>) request.getAttribute("postsEncontrados");
    List<Comunidade> comunidadesEncontradas = (List<Comunidade>) request.getAttribute("comunidadesEncontradas");
%>

<div class="container mt-4">
    <h3>Resultados da Pesquisa por: "<%= query != null ? query : "" %>"</h3>
    <hr>

    <h4>Comunidades Encontradas (<%= comunidadesEncontradas != null ? comunidadesEncontradas.size() : 0 %>)</h4>
    
    <% if (comunidadesEncontradas != null && !comunidadesEncontradas.isEmpty()) { 
        for (Comunidade c : comunidadesEncontradas) {
    %>
            <div class="card mb-2 shadow-sm p-3">
                <a href="r/<%= c.getSlug() %>" class="text-decoration-none">
                    <h5 class="mb-1">r/<%= c.getNome() %></h5>
                </a>
                <p class="small text-muted"><%= c.getDescricao() %></p>
            </div>
    <%  } 
    } else { %>
        <p class="text-muted">Nenhuma comunidade encontrada com este termo.</p>
    <% } %>
    
    <h4 class="mt-4">Posts Encontrados (<%= postsEncontrados != null ? postsEncontrados.size() : 0 %>)</h4>
    
    <% if (postsEncontrados != null && !postsEncontrados.isEmpty()) { 
        for (Post post : postsEncontrados) {
    %>
            <div class="card mb-2 p-3 shadow-sm">
                <small class="text-muted">Em r/<%= post.getComunidade().getSlug() %></small>
                <h5><a href="post?id=<%= post.getId() %>" class="text-decoration-none"><%= post.getTitulo() %></a></h5>
            </div>
    <%  } 
    } else { %>
        <p class="text-muted">Nenhum post encontrado com este termo.</p>
    <% } %>

</div>