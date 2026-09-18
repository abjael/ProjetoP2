package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;

public class Empregado {
    private String id;
    private String nome;
    private String endereco;
    private String tipo;
    private Double salario;
    private Double comissao;
    private String sindicalizado;

    public Empregado(String id, String nome, String endereco, String tipo, double salario, String sindicalizado) {
        this.id =  id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.comissao = 0.0;
        this.salario = salario;
        this.sindicalizado = sindicalizado;

    }
    public Empregado(String id, String nome, String endereco, String tipo, double salario, double comissao,  String sindicalizado) {
        this.id =  id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.comissao = comissao;
        this.salario = salario;
        this.sindicalizado = sindicalizado;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTipo() {
        return tipo;
    }

    public double getSalario() {
        return salario;
    }

    public String getSindicalizado() {return sindicalizado;}

    public Double getComissao() {return comissao;}



}
