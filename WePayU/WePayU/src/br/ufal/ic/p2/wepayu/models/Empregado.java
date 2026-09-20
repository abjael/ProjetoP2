package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoHoristaException;

public abstract class Empregado {
    private String id;
    private String nome;
    private String endereco;
    private String tipo;
    private double salario;
    private String sindicalizado;

    public Empregado(String id, String nome, String endereco, String tipo, double salario, String sindicalizado) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.sindicalizado = sindicalizado;
    }

    public String getId() {return id;}

    public String getNome() {return nome;}

    public String getEndereco() {return endereco;}

    public String getTipo() {return tipo;}

    public String getSindicalizado() {return sindicalizado;}

    public double getSalario() {return salario;}

    public double getComissao() {return 0.0;}

    public void adicionarHoras(double horas) throws EmpregadoNaoHoristaException {throw new EmpregadoNaoHoristaException();}

    public String getHorasTrabalhadas() {return "0,0";}
}