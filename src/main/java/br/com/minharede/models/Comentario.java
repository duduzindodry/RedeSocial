package br.com.minharede.models;

import java.time.LocalDateTime;

public class Comentario {
    
    private int id;
    private Post post; // O post ao qual pertence
    private Usuario usuario; // O autor do comentário
    private String conteudo;
    private LocalDateTime dataCriacao;
    
    // Construtor vazio e Construtor completo (opcional)
    public Comentario() {}

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}