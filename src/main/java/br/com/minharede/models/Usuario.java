package br.com.minharede.models;

import java.io.Serializable; 

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nome;
    private String email;
    
    // Construtor usado após o login bem-sucedido
    public Usuario(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

	public Usuario() {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

  
}