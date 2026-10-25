package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.exception.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Objects;

public abstract class Empregado {
    private String id;
    private String nome;
    private String endereco;
    private BigDecimal salario;
    private boolean sindicalizado;
    private String idSindicato;
    private BigDecimal taxaSindical;
    private MetodoPagamento metodoPagamento = MetodoPagamento.EM_MAOS;
    private String banco = "";
    private String agencia = "";
    private String contaCorrente = "";
    private LocalDate ultimaDataPagamento = LocalDate.of(2004, 12, 31);
    private LocalDate dataPagamentoAnterior;
    private BigDecimal dividaDescontos = BigDecimal.ZERO;
    private BigDecimal dividaDescontosAnterior = BigDecimal.ZERO;
    private AgendaPagamento agendaPagamento;
    private final HistoricoTrabalho historicoTrabalho = new HistoricoTrabalho();

    public Empregado(String id, String nome, String endereco, double salario, boolean sindicalizado) {
        this(id, nome, endereco, BigDecimal.valueOf(salario), sindicalizado);
    }

    public Empregado(
            String id, String nome, String endereco, BigDecimal salario,
            boolean sindicalizado) {
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
        this.taxaSindical = BigDecimal.ZERO;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public abstract TipoEmpregado getTipo();
    public double getSalario() { return salario.doubleValue(); }
    public BigDecimal getSalarioMonetario() { return salario; }
    public void setSalario(double salario) {
        setSalario(BigDecimal.valueOf(salario));
    }
    public void setSalario(BigDecimal salario) {
        validarValorNaoNegativo(salario);
        this.salario = salario;
    }
    public String getSindicalizado() {
        return Boolean.toString(sindicalizado);
    }
    public boolean isSindicalizado() { return sindicalizado; }
    public void setSindicalizado(boolean sindicalizado) {
        if (!sindicalizado) {
            dessindicalizar();
            return;
        }
        if (!this.sindicalizado) {
            throw new IllegalArgumentException(
                    "Os dados sindicais devem ser informados para sindicalizar o empregado.");
        }
    }
    public void setSindicalizado(
            boolean sindicalizado, String idSindicato, BigDecimal taxaSindical) {
        if (sindicalizado) {
            sindicalizar(idSindicato, taxaSindical);
        } else {
            dessindicalizar();
        }
    }
    public void sindicalizar(String idSindicato, double taxaSindical) {
        sindicalizar(idSindicato, BigDecimal.valueOf(taxaSindical));
    }
    public void sindicalizar(String idSindicato, BigDecimal taxaSindical) {
        validarTexto(idSindicato);
        validarValorNaoNegativo(taxaSindical);
        this.idSindicato = idSindicato;
        this.taxaSindical = taxaSindical;
        this.sindicalizado = true;
    }
    public void dessindicalizar() {
        this.sindicalizado = false;
        this.idSindicato = "";
        this.taxaSindical = BigDecimal.ZERO;
    }
    public String getIdSindicato() { return idSindicato; }
    public void setIdSindicato(String idSindicato) throws EmpregadoNaoEhSindicalizadoException {
        if (!sindicalizado) {
            throw new EmpregadoNaoEhSindicalizadoException();
        }
        validarTexto(idSindicato);
        this.idSindicato = idSindicato;
    }
    public double getTaxaSindical() { return taxaSindical.doubleValue(); }
    public BigDecimal getTaxaSindicalMonetario() { return taxaSindical; }
    public void setTaxaSindical(double taxaSindical) throws EmpregadoNaoEhSindicalizadoException {
        setTaxaSindical(BigDecimal.valueOf(taxaSindical));
    }
    public void setTaxaSindical(BigDecimal taxaSindical) throws EmpregadoNaoEhSindicalizadoException {
        if (!sindicalizado) {
            throw new EmpregadoNaoEhSindicalizadoException();
        }
        validarValorNaoNegativo(taxaSindical);
        this.taxaSindical = taxaSindical;
    }
    private final List<TaxaServico> taxasServico = new ArrayList<>();
    public void adicionarTaxaServico(String data, double valor) {
        taxasServico.add(new TaxaServico(data, valor));
    }
    public void adicionarTaxaServico(String data, BigDecimal valor) {
        taxasServico.add(new TaxaServico(data, valor));
    }

    public List<TaxaServico> getTaxasServico() { return List.copyOf(taxasServico); }
    public HistoricoTrabalho getHistoricoTrabalho() { return historicoTrabalho; }
    public String getHorasTrabalhadas() { return "0,0"; }

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
    public double getDividaDescontos() { return dividaDescontos.doubleValue(); }
    public BigDecimal getDividaDescontosMonetario() { return dividaDescontos; }
    public void setDividaDescontos(double dividaDescontos) {
        setDividaDescontos(BigDecimal.valueOf(dividaDescontos));
    }
    public void setDividaDescontos(BigDecimal dividaDescontos) {
        validarValorNaoNegativo(dividaDescontos);
        this.dividaDescontos = dividaDescontos;
    }
    public double getDividaDescontosAnterior() { return dividaDescontosAnterior.doubleValue(); }
    public BigDecimal getDividaDescontosAnteriorMonetario() { return dividaDescontosAnterior; }
    public void setDividaDescontosAnterior(double dividaDescontosAnterior) {
        setDividaDescontosAnterior(BigDecimal.valueOf(dividaDescontosAnterior));
    }
    public void setDividaDescontosAnterior(BigDecimal dividaDescontosAnterior) {
        validarValorNaoNegativo(dividaDescontosAnterior);
        this.dividaDescontosAnterior = dividaDescontosAnterior;
    }
    public void reabrirPagamento(LocalDate data) {
        if (data.equals(ultimaDataPagamento) && dataPagamentoAnterior != null) {
            ultimaDataPagamento = dataPagamentoAnterior;
            dividaDescontos = dividaDescontosAnterior;
        }
    }
    public void registrarPagamento(
            LocalDate data, LocalDate dataAnterior, BigDecimal dividaAnterior) {
        dataPagamentoAnterior = dataAnterior;
        dividaDescontosAnterior = dividaAnterior;
        ultimaDataPagamento = data;
    }
    public void registrarPagamento(
            LocalDate data, LocalDate dataAnterior, double dividaAnterior) {
        registrarPagamento(data, dataAnterior, BigDecimal.valueOf(dividaAnterior));
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
    public abstract double calcularSalarioBruto(String data)
            throws DescricaoDeAgendaInvalidaException;
    public BigDecimal calcularSalarioBrutoMonetario(String data)
            throws DescricaoDeAgendaInvalidaException {
        return BigDecimal.valueOf(calcularSalarioBruto(data));
    }
    public double calcularSalarioBruto(
            String data, LocalDate inicioPeriodo)
            throws DescricaoDeAgendaInvalidaException {
        return calcularSalarioBruto(data);
    }
    public BigDecimal calcularSalarioBrutoMonetario(
            String data, LocalDate inicioPeriodo)
            throws DescricaoDeAgendaInvalidaException {
        return BigDecimal.valueOf(calcularSalarioBruto(data, inicioPeriodo));
    }
    public double calcularSalarioLiquido(String data)
            throws DescricaoDeAgendaInvalidaException {
        return calcularSalarioLiquidoMonetario(data).doubleValue();
    }
    public BigDecimal calcularSalarioLiquidoMonetario(String data)
            throws DescricaoDeAgendaInvalidaException {
        return CalculadoraDescontos.calcularSalarioLiquido(
                this, data, calcularSalarioBrutoMonetario(data));
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
        destino.setDividaDescontos(this.getDividaDescontosMonetario());
        destino.setDividaDescontosAnterior(this.getDividaDescontosAnteriorMonetario());
        destino.setAgendaPagamento(this.getAgendaPagamento());
        if (isSindicalizado()) {
            destino.setSindicalizado(
                    true, this.getIdSindicato(), this.getTaxaSindicalMonetario());
        }
        for (TaxaServico ts : this.getTaxasServico()) {
            destino.adicionarTaxaServico(ts.getData(), ts.getValorMonetario());
        }
        historicoTrabalho.copiarPara(destino.historicoTrabalho);
    }

    public Empregado mudarTipo(TipoEmpregado tipo, double salario, double comissao) {
        return mudarTipo(tipo, BigDecimal.valueOf(salario), BigDecimal.valueOf(comissao));
    }

    public Empregado mudarTipo(
            TipoEmpregado tipo, BigDecimal salario, BigDecimal comissao) {
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

    protected static void validarValorNaoNegativo(BigDecimal valor) {
        Objects.requireNonNull(valor);
        if (valor.signum() < 0) {
            throw new IllegalArgumentException();
        }
    }
}
