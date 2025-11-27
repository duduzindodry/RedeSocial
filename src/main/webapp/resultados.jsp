<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="br.com.minharede.models.Post" %>
<%@ page import="br.com.minharede.models.Comunidade" %>



<div class="container mt-4">
    <h3>Resultados da Pesquisa por: "<c:out value="${query}"/>"</h3>
    <hr>

    
    <h4>Comunidades Encontradas (${empty comunidadesEncontradas ? 0 : comunidadesEncontradas.size()})</h4>
    
    <c:choose>
       
        <c:when test="${not empty comunidadesEncontradas}"> 
            <c:forEach var="c" items="${comunidadesEncontradas}">
                <div class="card mb-2 shadow-sm p-3">
                    <a href="r/${c.slug}" class="text-decoration-none">
                        <h5 class="mb-1">r/${c.nome}</h5>
                    </a>
                    <p class="small text-muted">${c.descricao}</p>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <p class="text-muted">Nenhuma comunidade encontrada com este termo.</p>
        </c:otherwise>
    </c:choose>
    
    <h4 class="mt-4">Posts Encontrados (${empty postsEncontrados ? 0 : postsEncontrados.size()})</h4>
    
    
    <c:choose>
        
        <c:when test="${not empty postsEncontrados}"> 
            <c:forEach var="post" items="${postsEncontrados}">
                <div class="card mb-2 p-3 shadow-sm">
                    <small class="text-muted">Em r/${post.comunidade.slug}</small>
                    <h5>
                        <a href="post?id=${post.id}" class="text-decoration-none">
                            ${post.titulo}
                        </a>
                    </h5>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <p class="text-muted">Nenhum post encontrado com este termo.</p>
        </c:otherwise>
    </c:choose>

</div>