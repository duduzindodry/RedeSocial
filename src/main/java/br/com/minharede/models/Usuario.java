package br.com.minharede.models; 

import java.io.Serializable;
import java.time.LocalDateTime;


public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int id; 
    private String nome;
    private String nomeUsuario;
    private String email;
    private LocalDateTime dataRegistro; 
    
   
    public Usuario() {
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

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

   
    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

   
    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

   
    public void setUsuario(String nomeUsuario) {
         this.nomeUsuario = nomeUsuario;
    }
}