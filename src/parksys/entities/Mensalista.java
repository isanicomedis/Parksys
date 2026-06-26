package parksys.entities;

import java.io.Serializable;


public class Mensalista implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String cpf;
    private String telefone;
    private Veiculo veiculo;
    private double valorMensalidade;

    public Mensalista(String nome, String cpf, String telefone, Veiculo veiculo, double valorMensalidade) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.veiculo = veiculo;
        this.valorMensalidade = valorMensalidade;
    }

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getTelefone() { return telefone; }
    public Veiculo getVeiculo() { return veiculo; } 
    public double getValorMensalidade() { return valorMensalidade; }
}