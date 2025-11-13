package br.com.minharede.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

	    private static final String URL = " jdbc:mysql://127.0.0.1:33006/bd?useTimezone=true&serverTimezone=UTC"; 
	    private static final String USUARIO = "root"; 
	    private static final String SENHA = "12345"; 
	    public static Connection getConnection() throws SQLException {
	      

	        return DriverManager.getConnection(URL, USUARIO, SENHA);
	    }
	    
	    public static void main(String[] args) {
	        try (Connection conn = getConnection()) {
	            if (conn != null) {
	                System.out.println("✅ Conexão com o banco de dados bem-sucedida!");
	            } else {
	                System.err.println("❌ Falha ao obter a conexão. Verifique as credenciais.");
	            }
	        } catch (SQLException e) {
	            System.err.println("❌ Erro de SQL durante o teste: " + e.getMessage());
	}
	        
	        
	    }}
	        
	    

