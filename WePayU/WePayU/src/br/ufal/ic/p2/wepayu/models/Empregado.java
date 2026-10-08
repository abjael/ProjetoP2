package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.Exception.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public abstract class Empregado {
    private String id;
    private String nome;
    private String endereco;
    private double salario;
    private boolean sindicalizado;
    private String idSindicato;
    private double taxaSindical;
    private String metodoPagamento = "emMaos";
    private String banco = "";
    private String agencia = "";
    private String contaCorrente = "";
    private LocalDate ultimaDataPagamento = LocalDate.of(2004, 12, 31);

    public Empregado(String id, String nome, String endereco, double salario, boolean sindicalizado) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.salario = salario;
        this.sindicalizado = sindicalizado;
        this.idSindicato = "";
        this.taxaSindical = 0.0;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public abstract TipoEmpregado getTipo();
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
    public String getSindicalizado() {
        return Boolean.toString(sindicalizado);
    }
    public boolean isSindicalizado() { return sindicalizado; }
    public void setSindicalizado(boolean sindicalizado) { this.sindicalizado = sindicalizado; }
    public String getIdSindicato() { return idSindicato; }
    public void setIdSindicato(String idSindicato) { this.idSindicato = idSindicato; }
    public double getTaxaSindical() { return taxaSindical; }
    public void setTaxaSindical(double taxaSindical) { this.taxaSindical = taxaSindical; }
    public abstract void validarComissao() throws EmpregadoNaoEhComissionado;
    public abstract double getComissao() throws EmpregadoNaoEhComissionado;
    public abstract void setComissao(double comissao) throws EmpregadoNaoEhComissionado;

    public void adicionarHoras(double horas) throws EmpregadoNaoHoristaException {
        throw new EmpregadoNaoHoristaException();
    }

    private List<TaxaServico> taxasServico = new ArrayList<>();

    public void adicionarTaxaServico(String data, double valor) {
        taxasServico.add(new TaxaServico(data, valor));
    }

    public List<TaxaServico> getTaxasServico() { return taxasServico; }
    public String getHorasTrabalhadas() { return "0,0"; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    public String getContaCorrente() { return contaCorrente; }
    public void setContaCorrente(String contaCorrente) { this.contaCorrente = contaCorrente; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public LocalDate getUltimaDataPagamento() { return ultimaDataPagamento; }
    public void setUltimaDataPagamento(LocalDate ultimaDataPagamento) { this.ultimaDataPagamento = ultimaDataPagamento; }

    public abstract boolean ehDiaDePagamento(String data);
    public abstract double calcularSalarioBruto(String data);
    public abstract double calcularSalarioLiquido(String data);
    public abstract void aceitar(VisitanteEmpregado visitante);
    public abstract void aceitar(VisitanteDadosEmpregado visitante);
    protected LocalDate parseData(String data) {
        String[] partes = data.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int ano = Integer.parseInt(partes[2]);
        return LocalDate.of(ano, mes, dia);
    }
    public abstract Empregado clonar();

    protected void copiarCamposComunsPara(Empregado destino) {
        destino.setMetodoPagamento(this.getMetodoPagamento());
        destino.setBanco(this.getBanco());
        destino.setAgencia(this.getAgencia());
        destino.setContaCorrente(this.getContaCorrente());
        destino.setUltimaDataPagamento(this.getUltimaDataPagamento());
        if (isSindicalizado()) {
            destino.setIdSindicato(this.getIdSindicato());
            destino.setTaxaSindical(this.getTaxaSindical());
        }
        for (TaxaServico ts : this.getTaxasServico()) {
            destino.adicionarTaxaServico(ts.getData(), ts.getValor());
        }
    }
    public void validarLancamentoCartao() throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    public void lancarCartao(String data, double horas)
            throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }
    public void validarLancamentoVenda() throws EmpregadoNaoEhComissionado {
        throw new EmpregadoNaoEhComissionado();
    }

    public void adicionarVenda(String data, double valor)
            throws EmpregadoNaoEhComissionado {
        throw new EmpregadoNaoEhComissionado();
    }
    public void validarConsultaVendas() throws EmpregadoNaoEhComissionado {
        throw new EmpregadoNaoEhComissionado();
    }

    public double calcularVendasRealizadas(
            java.time.LocalDate inicio, java.time.LocalDate fim)
            throws EmpregadoNaoEhComissionado {
        throw new EmpregadoNaoEhComissionado();
    }
    public void validarConsultaHoras() throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    public double getHorasNormais(String dataInicial, String dataFinal)
            throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    public double getHorasExtras(String dataInicial, String dataFinal)
            throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }
}

