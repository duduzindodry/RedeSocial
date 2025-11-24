<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.minharede.models.Usuario" %>
<%@ page import="br.com.minharede.models.Comunidade" %>
<%@ page import="br.com.minharede.models.Post" %>
<%@ page import="java.util.List" %>

<nav></nav>

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            
            <%-- Variáveis passadas pelo EditServlet: 'tipo' e 'item' --%>
            <c:set var="item" value="${requestScope.item}"/>
            <c:set var="tipo" value="${requestScope.tipo}"/>
            
            <h1 class="mb-4">
                <c:choose>
                    <c:when test="${tipo == 'post'}">Editar Postagem</c:when>
                    <c:when test="${tipo == 'comentario'}">Editar Comentário</c:when>
                    <c:otherwise>Editar Item</c:otherwise>
                </c:choose>
            </h1>

            <div class="card shadow-sm mb-4 p-4">
                
                <form action="editar" method="POST">
                    
                    <%-- Campos obrigatórios para o Servlet POST --%>
                    <input type="hidden" name="id" value="${item.id}">
                    <input type="hidden" name="tipo" value="${tipo}">

                    <%-- ---------------------------------------- --%>
                    <%-- FORMULÁRIO DE EDIÇÃO DE POST --%>
                    <%-- ---------------------------------------- --%>
                    <c:if test="${tipo == 'post'}">
                        
                        <div class="mb-3">
                            <label for="titulo" class="form-label">Título</label>
                            <input type="text" 
                                   class="form-control" 
                                   id="titulo" 
                                   name="titulo" 
                                   value="${item.titulo}" 
                                   required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="conteudo" class="form-label">Conteúdo do Post</label>
                            <textarea class="form-control" 
                                      id="conteudo" 
                                      name="conteudo" 
                                      rows="10" 
                                      required>${item.conteudo}</textarea>
                        </div>
                        
                        <%-- O campo Comunidade geralmente não é editável em redes sociais como Reddit, mas você pode adicioná-lo se quiser. Vamos omitir por simplicidade. --%>
                        
                    </c:if>

                    <%-- ---------------------------------------- --%>
                    <%-- FORMULÁRIO DE EDIÇÃO DE COMENTÁRIO --%>
                    <%-- ---------------------------------------- --%>
                    <c:if test="${tipo == 'comentario'}">
                        
                        <div class="mb-3">
                            <label for="conteudo" class="form-label">Conteúdo do Comentário</label>
                            <textarea class="form-control" 
                                      id="conteudo" 
                                      name="conteudo" 
                                      rows="5" 
                                      required>${item.conteudo}</textarea>
                        </div>
                        
                        <%-- Adicionar o ID do Post original para redirecionamento após a edição --%>
                        <input type="hidden" name="postId" value="${item.postId}">
                        
                    </c:if>

                    <button type="submit" class="btn btn-success me-2">Salvar Alterações</button>
                    
                    <%-- Botão Cancelar que redireciona para a página principal ou post --%>
                    <a href="${header.referer}" class="btn btn-secondary">Cancelar</a>
                    
                </form>
            </div>
        </div>
    </div>
</div>