package br.com.minharede.models;

import java.util.Date;

public class Post {

    private int id;
    private Comunidade comunidade; // Objeto para a chave estrangeira
    private Usuario usuario;       // Objeto para a chave estrangeira
    private String titulo;
    private String conteudo;
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

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getTempoAtras() {
		return tempoAtras;
	}

	public void setTempoAtras(String tempoAtras) {
		this.tempoAtras = tempoAtras;
	}

	private String tipo;           // TEXTO, LINK, IMAGEM
    private int votos;
    private Date dataCriacao;
    
    // Propriedades úteis para o Feed
    private int numComentarios; 
    private String tempoAtras;     // Ex: "5 horas atrás"

    // Construtor padrão (obrigatório para muitos frameworks e para o DAO)
    public Post() {}

    // --- GETTERS E SETTERS (Mínimo necessário para o seu projeto) ---

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
    
    // ... Incluir todos os outros Getters e Setters
    // (Ex: getUsuario(), setUsuario(), getTitulo(), setTitulo(), etc.)
    
    // Exemplo de Getters e Setters de suporte para o JSP:
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
    
    public int getVotos() {
        return votos;
    }

    public void setVotos(int votos) {
        this.votos = votos;
    }

    public int getNumComentarios() {
        return numComentarios;
    }

    public void setNumComentarios(int numComentarios) {
        this.numComentarios = numComentarios;
    }

    // Você pode adicionar um método de conveniência para pegar um resumo do conteúdo
    public String getConteudoCurto() {
        if (this.conteudo == null || this.conteudo.length() <= 100) {
            return this.conteudo;
        }
        return this.conteudo.substring(0, 100) + "...";
    }

}
		
	
		
	
