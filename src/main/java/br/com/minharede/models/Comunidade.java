package br.com.minharede.models;

import java.sql.Date;

public class Comunidade {

    private int id;
    private String nome;
    private String slug; // O mais importante para as URLs (r/slug)
    private String descricao;
    private Date DataCriacao;
    // ... outros atributos

    public Comunidade(int comunidadeId) {}

	public Comunidade() {
		// TODO Auto-generated constructor stub
	}

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

	public void setModerador(Usuario usuarioLogado) {
		// TODO Auto-generated method stub
		
	}

	public Comunidade getModerador() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDataCriacao() {
		return DataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		DataCriacao = dataCriacao;
	}

}