package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoHoristaException;
import java.util.List;
import java.util.ArrayList;

public abstract class Empregado {
    private String id;
    private String nome;
    private String endereco;
    private String tipo;
    private double salario;
    private String sindicalizado;
    private String idSindicato;
    private double taxaSindical;
    private String metodoPagamento = "emMaos";
    private String banco = "";
    private String agencia = "";
    private String contaCorrente = "";

    public Empregado(String id, String nome, String endereco, String tipo, double salario, String sindicalizado) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.sindicalizado = sindicalizado;
        this.idSindicato = "";
        this.taxaSindical = 0.0;
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

    public String getSindicalizado() {
        return sindicalizado;
    }

    public void setSindicalizado(String sindicalizado) {
        this.sindicalizado = sindicalizado;
    }

    public String getIdSindicato() {
        return idSindicato;
    }

    public void setIdSindicato(String idSindicato) {
        this.idSindicato = idSindicato;
    }

    public double getTaxaSindical() {
        return taxaSindical;
    }

    public void setTaxaSindical(double taxaSindical) {
        this.taxaSindical = taxaSindical;
    }

    public double getComissao() {
        return 0.0;
    }

    public void adicionarHoras(double horas) throws EmpregadoNaoHoristaException {
        throw new EmpregadoNaoHoristaException();
    }

    private List<TaxaServico> taxasServico = new ArrayList<>();

    public void adicionarTaxaServico(String data, double valor) {
        taxasServico.add(new TaxaServico(data, valor));
    }

    public List<TaxaServico> getTaxasServico() {
        return taxasServico;
    }

    public String getHorasTrabalhadas() {
        return "0,0";
    }


    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}