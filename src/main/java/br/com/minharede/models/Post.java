package br.com.minharede.models;

import java.io.Serializable;
import java.time.LocalDateTime; 

/**
 * Documentação: Modelo que representa um Post na rede social.
 * Implementa Serializable para poder ser armazenado em Session ou caches.
 */
public class Post implements Serializable {
    
    private static final long serialVersionUID = 1L;


    private int id;
    private Comunidade comunidade; 
    private Usuario usuario;      
    private String titulo;
    private String conteudo;
    private String tipo;           
    private int votos;
    private LocalDateTime dataCriacao; 

   
    private int numComentarios; 
    private String tempoAtras;     

   
    public Post() {}

  

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Comunidade getComunidade() {
        return comunidade;
    }

    public void setComunidade(Comunidade comunidade) {
        this.comunidade = comunidade;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getVotos() {
        return votos;
    }

    public void setVotos(int votos) {
        this.votos = votos;
    }

    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getNumComentarios() {
        return numComentarios;
    }

    public void setNumComentarios(int numComentarios) {
        this.numComentarios = numComentarios;
    }

    public String getTempoAtras() {
        return tempoAtras;
    }

    public void setTempoAtras(String tempoAtras) {
        this.tempoAtras = tempoAtras;
    }

   
    public String getConteudoCurto() {
        if (this.conteudo == null || this.conteudo.length() <= 100) {
            return this.conteudo;
        }
        return this.conteudo.substring(0, 100) + "...";
    }
}	
	
		
	
