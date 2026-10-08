package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.exception.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class Empregado {
    private String id;
    private String nome;
    private String endereco;
    private double salario;
    private boolean sindicalizado;
    private String idSindicato;
    private double taxaSindical;
    private MetodoPagamento metodoPagamento = MetodoPagamento.EM_MAOS;
    private String banco = "";
    private String agencia = "";
    private String contaCorrente = "";
    private LocalDate ultimaDataPagamento = LocalDate.of(2004, 12, 31);
    private LocalDate dataPagamentoAnterior;
    private double dividaDescontos;
    private double dividaDescontosAnterior;
    private AgendaPagamento agendaPagamento;
    private final List<CartaoPonto> cartoes = new ArrayList<>();
    private final List<Venda> vendas = new ArrayList<>();

    public Empregado(String id, String nome, String endereco, double salario, boolean sindicalizado) {
        validarTexto(id);
        validarTexto(nome);
        validarTexto(endereco);
        validarValorNaoNegativo(salario);
        if (sindicalizado) {
            throw new IllegalArgumentException();
        }
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
    public void setSalario(double salario) {
        validarValorNaoNegativo(salario);
        this.salario = salario;
    }
    public String getSindicalizado() {
        return Boolean.toString(sindicalizado);
    }
    public boolean isSindicalizado() { return sindicalizado; }
    public void setSindicalizado(boolean sindicalizado) {
        if (sindicalizado) {
            validarTexto(idSindicato);
            validarValorNaoNegativo(taxaSindical);
            this.sindicalizado = true;
        } else {
            dessindicalizar();
        }
    }
    public void sindicalizar(String idSindicato, double taxaSindical) {
        validarTexto(idSindicato);
        validarValorNaoNegativo(taxaSindical);
        this.idSindicato = idSindicato;
        this.taxaSindical = taxaSindical;
        this.sindicalizado = true;
    }
    public void dessindicalizar() {
        this.sindicalizado = false;
        this.idSindicato = "";
        this.taxaSindical = 0.0;
    }
    public String getIdSindicato() { return idSindicato; }
    public void setIdSindicato(String idSindicato) {
        validarTexto(idSindicato);
        this.idSindicato = idSindicato;
    }
    public double getTaxaSindical() { return taxaSindical; }
    public void setTaxaSindical(double taxaSindical) {
        validarValorNaoNegativo(taxaSindical);
        this.taxaSindical = taxaSindical;
    }
    public abstract void validarComissao() throws EmpregadoNaoEhComissionadoException;
    public abstract double getComissao() throws EmpregadoNaoEhComissionadoException;
    public abstract void setComissao(double comissao) throws EmpregadoNaoEhComissionadoException;

    private final List<TaxaServico> taxasServico = new ArrayList<>();

    public void adicionarTaxaServico(String data, double valor) {
        taxasServico.add(new TaxaServico(data, valor));
    }

    public List<TaxaServico> getTaxasServico() { return List.copyOf(taxasServico); }
    public List<CartaoPonto> getCartoes() { return List.copyOf(cartoes); }
    public List<Venda> getVendas() { return List.copyOf(vendas); }
    public String getHorasTrabalhadas() { return "0,0"; }

    protected void registrarCartao(String data, double horas) {
        cartoes.add(new CartaoPonto(data, horas));
    }

    protected void registrarVenda(String data, double valor) {
        vendas.add(new Venda(data, valor));
    }

    public void carregarCartaoPersistido(String data, double horas) {
        cartoes.add(new CartaoPonto(data, horas));
    }

    public void carregarVendaPersistida(String data, double valor) {
        vendas.add(new Venda(data, valor));
    }

    public String getMetodoPagamento() { return metodoPagamento.paraTexto(); }
    public MetodoPagamento getTipoMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = MetodoPagamento.deTexto(metodoPagamento);
    }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = java.util.Objects.requireNonNull(metodoPagamento);
    }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = java.util.Objects.requireNonNull(banco); }
    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = java.util.Objects.requireNonNull(agencia); }
    public String getContaCorrente() { return contaCorrente; }
    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = java.util.Objects.requireNonNull(contaCorrente);
    }
    public void setNome(String nome) {
        validarTexto(nome);
        this.nome = nome;
    }
    public void setEndereco(String endereco) {
        validarTexto(endereco);
        this.endereco = endereco;
    }

    public LocalDate getUltimaDataPagamento() { return ultimaDataPagamento; }
    public void setUltimaDataPagamento(LocalDate ultimaDataPagamento) {
        this.ultimaDataPagamento = java.util.Objects.requireNonNull(ultimaDataPagamento);
    }
    public LocalDate getDataPagamentoAnterior() { return dataPagamentoAnterior; }
    public void setDataPagamentoAnterior(LocalDate dataPagamentoAnterior) {
        this.dataPagamentoAnterior = dataPagamentoAnterior;
    }
    public double getDividaDescontos() { return dividaDescontos; }
    public void setDividaDescontos(double dividaDescontos) {
        validarValorNaoNegativo(dividaDescontos);
        this.dividaDescontos = dividaDescontos;
    }
    public double getDividaDescontosAnterior() { return dividaDescontosAnterior; }
    public void setDividaDescontosAnterior(double dividaDescontosAnterior) {
        validarValorNaoNegativo(dividaDescontosAnterior);
        this.dividaDescontosAnterior = dividaDescontosAnterior;
    }
    public void reabrirPagamento(LocalDate data) {
        if (data.equals(ultimaDataPagamento) && dataPagamentoAnterior != null) {
            ultimaDataPagamento = dataPagamentoAnterior;
            dividaDescontos = dividaDescontosAnterior;
        }
    }
    public void registrarPagamento(LocalDate data, LocalDate dataAnterior,
            double dividaAnterior) {
        dataPagamentoAnterior = dataAnterior;
        dividaDescontosAnterior = dividaAnterior;
        ultimaDataPagamento = data;
    }
    public String getAgendaPagamento() {
        if (agendaPagamento == null) {
            return AgendaPagamento.defaultAgenda(getTipo());
        }
        return agendaPagamento.toString();
    }
    public void setAgendaPagamento(String descricao) {
        try {
            agendaPagamento = AgendaPagamento.parse(descricao);
        } catch (DescricaoDeAgendaInvalidaException e) {
            throw new IllegalArgumentException(e);
        }
    }
    public void setAgendaPagamento(AgendaPagamento agendaPagamento) {
        this.agendaPagamento = java.util.Objects.requireNonNull(agendaPagamento);
    }

    public abstract boolean ehDiaDePagamento(String data);
    public abstract double calcularSalarioBruto(String data);
    public double calcularSalarioLiquido(String data) {
        LocalDate dataFim = parseData(data);
        LocalDate dataInicio = getUltimaDataPagamento().plusDays(1);
        long diasDecorridos = ChronoUnit.DAYS.between(
                getUltimaDataPagamento(), dataFim);
        double bruto = calcularSalarioBruto(data);
        double descontoSindical = isSindicalizado()
                ? getTaxaSindical() * diasDecorridos
                : 0.0;
        double totalTaxasServico = 0.0;
        for (TaxaServico taxa : getTaxasServico()) {
            LocalDate dataTaxa = parseData(taxa.getData());
            if (!dataTaxa.isBefore(dataInicio) && !dataTaxa.isAfter(dataFim)) {
                totalTaxasServico += taxa.getValor();
            }
        }
        double descontos = dividaDescontos + descontoSindical + totalTaxasServico;
        double liquido = Math.max(0.0, bruto - descontos);
        dividaDescontos = Math.max(0.0, descontos - bruto);
        return liquido;
    }
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
        destino.setDataPagamentoAnterior(this.getDataPagamentoAnterior());
        destino.setDividaDescontos(this.getDividaDescontos());
        destino.setDividaDescontosAnterior(this.getDividaDescontosAnterior());
        destino.setAgendaPagamento(this.getAgendaPagamento());
        if (isSindicalizado()) {
            destino.sindicalizar(this.getIdSindicato(), this.getTaxaSindical());
        }
        for (TaxaServico ts : this.getTaxasServico()) {
            destino.adicionarTaxaServico(ts.getData(), ts.getValor());
        }
        destino.cartoes.addAll(cartoes);
        destino.vendas.addAll(vendas);
    }

    public Empregado mudarTipo(TipoEmpregado tipo, double salario, double comissao) {
        Empregado destino = switch (tipo) {
            case HORISTA -> new Horista(
                    getId(), getNome(), getEndereco(), salario, false);
            case ASSALARIADO -> new Assalariado(
                    getId(), getNome(), getEndereco(), salario, false);
            case COMISSIONADO -> new Comissionado(
                    getId(), getNome(), getEndereco(), salario, comissao, false);
        };
        copiarCamposComunsPara(destino);
        return destino;
    }

    protected static void validarTexto(String valor) {
        if (valor == null || valor.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    protected static void validarValorNaoNegativo(double valor) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException();
        }
    }
    public void validarLancamentoCartao() throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }

    public void lancarCartao(String data, double horas)
            throws EmpregadoNaoEhHoristaException {
        throw new EmpregadoNaoEhHoristaException();
    }
    public void validarLancamentoVenda() throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }

    public void adicionarVenda(String data, double valor)
            throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }
    public void validarConsultaVendas() throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
    }

    public double calcularVendasRealizadas(
            java.time.LocalDate inicio, java.time.LocalDate fim)
            throws EmpregadoNaoEhComissionadoException {
        throw new EmpregadoNaoEhComissionadoException();
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
