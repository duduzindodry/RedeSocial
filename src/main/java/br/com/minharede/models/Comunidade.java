package br.com.minharede.models;

public class Comunidade {

    private int id;
    private String nome;
    private String slug; // O mais importante para as URLs (r/slug)
    private String descricao;
    // ... outros atributos

    public Comunidade() {}

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

}