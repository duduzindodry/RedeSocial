package br.com.minharede.models;

import java.io.Serializable;
import java.time.LocalDateTime; // ✅ Usamos o tipo moderno (Java 8+)

/**
 * Documentação: Modelo que representa uma Comunidade (Sub-rede) na aplicação.
 */
public class Comunidade implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // --- CAMPOS DE PERSISTÊNCIA ---
    private int id;
    private String nome;
    private String slug; 
    private String descricao;
    private LocalDateTime dataCriacao; // ✅ CORRIGIDO: Usando LocalDateTime
    private Usuario moderador;         // ✅ NOVO CAMPO: Para a chave estrangeira do Moderador

    // --- CONSTRUTORES ---

    /**
     * Documentação: Construtor padrão (obrigatório para frameworks e DAO).
     */
    public Comunidade() {}

    /**
     * Documentação: Construtor útil para inicializar apenas pela chave primária (ID).
     * @param comunidadeId O ID da comunidade.
     */
    public Comunidade(int comunidadeId) {
        this.id = comunidadeId; // Inicializa o ID corretamente
    }

    // ----------------------------------------------------------------------
    // GETTERS E SETTERS
    // ----------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
  
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    
    public Usuario getModerador() {
        return moderador;
    }

    public void setModerador(Usuario moderador) {
        this.moderador = moderador;
    }

   
}