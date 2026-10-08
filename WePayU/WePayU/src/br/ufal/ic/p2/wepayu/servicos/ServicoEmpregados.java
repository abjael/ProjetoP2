package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.util.ValidadorData;
import java.util.LinkedHashSet;
import java.util.Set;

public class ServicoEmpregados {
    private final RepositorioEmpregados repositorioEmpregados;
    private final Set<String> agendasDisponiveis = new LinkedHashSet<>();

    public ServicoEmpregados(RepositorioEmpregados repositorioEmpregados) {
        this.repositorioEmpregados = repositorioEmpregados;
        reiniciarAgendasDisponiveis();
    }

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario) throws ErroWePayUException {

        validarDadosBasicosEmpregado(nome, endereco);
        TipoEmpregado tipoConvertido = converterTipo(tipo);
        if (tipoConvertido == TipoEmpregado.COMISSIONADO) {
            throw new TipoNaoAplicavelException();
        }
        double salarioNumero = converterSalarioCriacao(salario);

        String idGerado = java.util.UUID.randomUUID().toString();
        Empregado novoEmpregado = switch (tipoConvertido) {
            case HORISTA -> new Horista(
                    idGerado, nome, endereco, salarioNumero, false);
            case ASSALARIADO -> new Assalariado(
                    idGerado, nome, endereco, salarioNumero, false);
            case COMISSIONADO -> throw new TipoNaoAplicavelException();
        };

        repositorioEmpregados.adicionar(novoEmpregado);
        return idGerado;
    }

    public Empregado buscarEmpregado(String id) throws ErroWePayUException {
        if (id == null || id.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }
        Empregado empregado = repositorioEmpregados.buscarPorId(id);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }
        return empregado;
    }

    public String getEmpregadoPorNome(String nome, int indice)
            throws ErroWePayUException {
        if (nome == null || nome.isEmpty()) {
            throw new NomeNaoPodeSerNuloException();
        }
        String id = repositorioEmpregados.buscarIdPorNome(nome, indice);
        if (id == null) {
            throw new NaoHaEmpregadoComEsseNomeException();
        }
        return id;
    }

    public void reiniciarAgendasDisponiveis() {
        agendasDisponiveis.clear();
        agendasDisponiveis.add("semanal 5");
        agendasDisponiveis.add("mensal $");
        agendasDisponiveis.add("semanal 2 5");
    }

    public void inicializarAgendasDisponiveis(java.util.List<Empregado> empregados) {
        reiniciarAgendasDisponiveis();
        for (Empregado empregado : empregados) {
            if (empregado != null && empregado.getAgendaPagamento() != null
                    && !empregado.getAgendaPagamento().isEmpty()) {
                agendasDisponiveis.add(AgendaPagamento.normalizar(empregado.getAgendaPagamento()));
            }
        }
    }

    public boolean agendaDisponivel(String descricao) {
        if (descricao == null) {
            return false;
        }
        return agendasDisponiveis.contains(AgendaPagamento.normalizar(descricao));
    }

    public void criarAgendaDePagamentos(String descricao) throws ErroWePayUException {
        String agenda = AgendaPagamento.normalizar(descricao);
        if (agenda.isEmpty()) {
            throw new DescricaoDeAgendaInvalidaException();
        }
        if (agendasDisponiveis.contains(agenda)) {
            throw new AgendaDePagamentosJaExisteException();
        }
        AgendaPagamento.parse(agenda);
        agendasDisponiveis.add(agenda);
    }

    public String getAtributoEmpregado(String id, String atributo)
            throws ErroWePayUException {
        Empregado empregado = buscarEmpregado(id);
        if (atributo == null) {
            throw new AtributoNaoExisteException();
        }
        boolean existe = switch (atributo) {
            case "nome", "endereco", "tipo", "salario", "sindicalizado",
                    "comissao", "horasTrabalhadas", "metodoPagamento",
                    "agendaPagamento", "banco", "agencia", "contaCorrente",
                    "idSindicato", "taxaSindical" -> true;
            default -> false;
        };
        if (!existe) {
            throw new AtributoNaoExisteException();
        }
        if ((atributo.equals("banco") || atributo.equals("agencia")
                || atributo.equals("contaCorrente"))
                && empregado.getTipoMetodoPagamento() != MetodoPagamento.BANCO) {
            throw new EmpregadoNaoRecebeEmBancoException();
        }
        if ((atributo.equals("idSindicato") || atributo.equals("taxaSindical"))
                && !empregado.isSindicalizado()) {
            throw new EmpregadoNaoEhSindicalizadoException();
        }
        return switch (atributo) {
            case "nome" -> empregado.getNome();
            case "endereco" -> empregado.getEndereco();
            case "tipo" -> empregado.getTipo().paraTexto();
            case "salario" -> formatarValor(empregado.getSalario());
            case "sindicalizado" -> empregado.getSindicalizado();
            case "comissao" -> {
                empregado.validarComissao();
                yield formatarValor(empregado.getComissao());
            }
            case "horasTrabalhadas" -> empregado.getHorasTrabalhadas();
            case "metodoPagamento" -> empregado.getMetodoPagamento();
            case "agendaPagamento" -> empregado.getAgendaPagamento();
            case "banco" -> empregado.getBanco();
            case "agencia" -> empregado.getAgencia();
            case "contaCorrente" -> empregado.getContaCorrente();
            case "idSindicato" -> empregado.getIdSindicato();
            case "taxaSindical" -> formatarValor(empregado.getTaxaSindical());
            default -> throw new AtributoNaoExisteException();
        };
    }

    public String getHorasTrabalhadas(String id, String dataInicial,
            String dataFinal, boolean extras) throws ErroWePayUException {
        Empregado empregado = buscarEmpregado(id);
        empregado.validarConsultaHoras();
        java.time.LocalDate inicio = ValidadorData.validarInicial(dataInicial);
        java.time.LocalDate fim = ValidadorData.validarFinal(dataFinal);
        if (inicio.isAfter(fim)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }
        double horas = extras
                ? empregado.getHorasExtras(dataInicial, dataFinal)
                : empregado.getHorasNormais(dataInicial, dataFinal);
        if (horas == (int) horas) {
            return String.valueOf((int) horas);
        }
        return String.format("%.1f", horas).replace(".", ",");
    }

    public String getVendasRealizadas(String id, String dataInicial,
            String dataFinal) throws ErroWePayUException {
        Empregado empregado = buscarEmpregado(id);
        empregado.validarConsultaVendas();
        java.time.LocalDate inicio = ValidadorData.validarInicial(dataInicial);
        java.time.LocalDate fim = ValidadorData.validarFinal(dataFinal);
        if (inicio.isAfter(fim)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }
        return formatarValor(empregado.calcularVendasRealizadas(inicio, fim));
    }

    public String getTaxasServico(String id, String dataInicial,
            String dataFinal) throws ErroWePayUException {
        Empregado empregado = buscarEmpregado(id);
        if (!empregado.isSindicalizado()) {
            throw new EmpregadoNaoEhSindicalizadoException();
        }
        java.time.LocalDate inicio = ValidadorData.validarInicial(dataInicial);
        java.time.LocalDate fim = ValidadorData.validarFinal(dataFinal);
        if (inicio.isAfter(fim)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }
        double total = 0.0;
        for (TaxaServico taxa : empregado.getTaxasServico()) {
            java.time.LocalDate dataTaxa = ValidadorData.parse(taxa.getData());
            if (!dataTaxa.isBefore(inicio) && dataTaxa.isBefore(fim)) {
                total += taxa.getValor();
            }
        }
        return formatarValor(total);
    }

    public void alterarAtributo(String id, String atributo, String valor)
            throws ErroWePayUException {
        Empregado empregado = buscarEmpregado(id);
        if (atributo == null) {
            throw new AtributoNaoExisteException();
        }
        switch (atributo) {
            case "nome" -> {
                if (valor == null || valor.isEmpty()) {
                    throw new NomeNaoPodeSerNuloException();
                }
                empregado.setNome(valor);
            }
            case "endereco" -> {
                if (valor == null || valor.isEmpty()) {
                    throw new EnderecoNaoPodeSerNuloException();
                }
                empregado.setEndereco(valor);
            }
            case "tipo" -> alterarTipo(empregado, valor);
            case "salario" -> empregado.setSalario(converterSalarioAlteracao(valor));
            case "comissao" -> {
                empregado.validarComissao();
                empregado.setComissao(converterComissaoAlteracao(valor));
            }
            case "metodoPagamento" -> {
                try {
                    MetodoPagamento.deTexto(valor);
                } catch (IllegalArgumentException e) {
                    throw new MetodoDePagamentoInvalidoException();
                }
                empregado.setMetodoPagamento(valor);
            }
            case "agendaPagamento" -> {
                if (valor == null || valor.isEmpty()) {
                    throw new AgendaDePagamentoNaoDisponivelException();
                }
                String agenda = AgendaPagamento.normalizar(valor);
                if (!agendaDisponivel(agenda)) {
                    throw new AgendaDePagamentoNaoDisponivelException();
                }
                try {
                    empregado.setAgendaPagamento(agenda);
                } catch (IllegalArgumentException e) {
                    throw new AgendaDePagamentoNaoDisponivelException();
                }
            }
            case "sindicalizado" -> {
                boolean sindicalizado = converterBooleano(valor);
                if (sindicalizado) {
                    throw new IdentificacaoDoSindicatoNaoPodeSerNulaException();
                }
                empregado.dessindicalizar();
            }
            default -> throw new AtributoNaoExisteException();
        }
    }

    public void alterarTipo(String id, String atributo, String tipo,
            String valorExtra) throws ErroWePayUException {
        if ("tipo".equals(atributo)) {
            alterarTipo(buscarEmpregado(id), tipo, valorExtra);
        }
    }

    public void alterarSindicalizacao(String id, String valor,
            String idSindicato, String taxaSindical) throws ErroWePayUException {
        Empregado empregado = buscarEmpregado(id);
        boolean sindicalizado = converterBooleano(valor);
        if (!sindicalizado) {
            empregado.dessindicalizar();
            return;
        }
        if (idSindicato == null || idSindicato.isEmpty()) {
            throw new IdentificacaoDoSindicatoNaoPodeSerNulaException();
        }
        if (taxaSindical == null || taxaSindical.isEmpty()) {
            throw new TaxaSindicalNaoPodeSerNulaException();
        }
        double taxa;
        try {
            taxa = parseNumero(taxaSindical);
        } catch (NumberFormatException e) {
            throw new TaxaSindicalDeveSerNumericaException();
        }
        if (!Double.isFinite(taxa) || taxa < 0) {
            throw new TaxaSindicalDeveSerNaoNegativaException();
        }
        Empregado outroMembro = repositorioEmpregados.buscarPorIdSindicato(idSindicato);
        if (outroMembro != null && !outroMembro.getId().equals(id)) {
            throw new HaOutroEmpregadoComEstaIdentificacaoDeSindicatoException();
        }
        empregado.sindicalizar(idSindicato, taxa);
    }

    public void alterarPagamentoBanco(String id, String atributo,
            String metodo, String banco, String agencia, String contaCorrente)
            throws ErroWePayUException {
        Empregado empregado = buscarEmpregado(id);
        if ("metodoPagamento".equals(atributo) && "banco".equals(metodo)) {
            if (banco == null || banco.isEmpty()) {
                throw new BancoNaoPodeSerNuloException();
            }
            if (agencia == null || agencia.isEmpty()) {
                throw new AgenciaNaoPodeSerNuloException();
            }
            if (contaCorrente == null || contaCorrente.isEmpty()) {
                throw new ContaCorrenteNaoPodeSerNuloException();
            }
            empregado.setMetodoPagamento(MetodoPagamento.BANCO);
            empregado.setBanco(banco);
            empregado.setAgencia(agencia);
            empregado.setContaCorrente(contaCorrente);
        }
    }

    private boolean converterBooleano(String valor) throws ValorDeveSerTrueOuFalseException {
        if (!"true".equals(valor) && !"false".equals(valor)) {
            throw new ValorDeveSerTrueOuFalseException();
        }
        return Boolean.parseBoolean(valor);
    }

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario,
            String comissao) throws ErroWePayUException {

        validarDadosBasicosEmpregado(nome, endereco);
        TipoEmpregado tipoConvertido = converterTipo(tipo);
        if (tipoConvertido == TipoEmpregado.HORISTA
                || tipoConvertido == TipoEmpregado.ASSALARIADO) {
            throw new TipoNaoAplicavelException();
        }
        double salarioNumero = converterSalarioCriacao(salario);
        double comissaoNumero = converterComissaoCriacao(comissao);

        String idGerado = java.util.UUID.randomUUID().toString();
        Empregado novoEmpregado = switch (tipoConvertido) {
            case HORISTA -> throw new TipoNaoAplicavelException();
            case ASSALARIADO -> throw new TipoNaoAplicavelException();
            case COMISSIONADO -> new Comissionado(
                    idGerado, nome, endereco, salarioNumero, comissaoNumero, false);
        };

        repositorioEmpregados.adicionar(novoEmpregado);
        return idGerado;
    }

    public void alterarTipo(Empregado empregado, String tipo) throws ErroWePayUException {
        TipoEmpregado tipoConvertido = converterTipo(tipo);
        Empregado novo = criarComTipo(
                empregado, tipoConvertido, empregado.getSalario(), 0.0);
        substituir(empregado, novo);
    }

    public void removerEmpregado(String idBuscado) throws ErroWePayUException {
        Empregado empregado = buscarEmpregado(idBuscado);
        repositorioEmpregados.remover(
                repositorioEmpregados.indicePorId(empregado.getId()));
    }

    public void lancarCartao(String idEmp, String data, String horas) throws ErroWePayUException {
        Empregado empregado = buscarEmpregado(idEmp);
        empregado.validarLancamentoCartao();
        validarData(data);

        if (horas == null || horas.isEmpty()) {
            throw new HorasNaoPodemSerNulasException();
        }

        double horasNumericas;
        try {
            horasNumericas = parseNumero(horas);
        } catch (NumberFormatException e) {
            throw new HorasDevemSerNumericasException();
        }

        if (horasNumericas <= 0) {
            throw new HorasDevemSerPositivasException();
        }

        empregado.lancarCartao(data, horasNumericas);
    }

    public void lancarVenda(String idEmp, String data, String valor) throws ErroWePayUException {
        Empregado empregado = buscarEmpregado(idEmp);
        empregado.validarLancamentoVenda();
        validarData(data);

        if (valor == null || valor.isEmpty()) {
            throw new ValorNaoPodeSerNuloException();
        }

        double valorNumerico;
        try {
            valorNumerico = parseNumero(valor);
        } catch (NumberFormatException e) {
            throw new ValorDeveSerNumericoException();
        }

        if (!Double.isFinite(valorNumerico) || valorNumerico <= 0) {
            throw new ValorDeveSerPositivoException();
        }

        empregado.adicionarVenda(data, valorNumerico);
    }

    public void lancarTaxaServico(
            String idSindicato, String data, String valor) throws ErroWePayUException {
        if (idSindicato == null || idSindicato.isEmpty()) {
            throw new IdentificacaoDoMembroNaoPodeSerNulaException();
        }

        Empregado empregado = repositorioEmpregados.buscarPorIdSindicato(idSindicato);
        if (empregado == null) {
            throw new MembroNaoExisteException();
        }

        validarData(data);

        if (valor == null || valor.isEmpty()) {
            throw new ValorNaoPodeSerNuloException();
        }

        double valorNumerico;
        try {
            valorNumerico = parseNumero(valor);
        } catch (NumberFormatException e) {
            throw new ValorDeveSerNumericoException();
        }

        if (!Double.isFinite(valorNumerico) || valorNumerico <= 0) {
            throw new ValorDeveSerPositivoException();
        }

        empregado.adicionarTaxaServico(data, valorNumerico);
    }

    public void alterarTipo(
            Empregado empregado, String tipo, String valorExtra) throws ErroWePayUException {
        TipoEmpregado tipoConvertido = converterTipo(tipo);
        ValoresAlteracao valores = switch (tipoConvertido) {
            case HORISTA, ASSALARIADO -> new ValoresAlteracao(
                    converterSalarioAlteracao(valorExtra), 0.0);
            case COMISSIONADO -> new ValoresAlteracao(
                    empregado.getSalario(), converterComissaoAlteracao(valorExtra));
        };

        Empregado novo = criarComTipo(
                empregado, tipoConvertido, valores.salario(), valores.comissao());
        substituir(empregado, novo);
    }

    private double converterSalarioAlteracao(String valor) throws ErroWePayUException {
        if (valor == null || valor.isEmpty()) {
            throw new SalarioNaoPodeSerNuloException();
        }
        double salario;
        try {
            salario = parseNumero(valor);
        } catch (NumberFormatException e) {
            throw new SalarioDeveSerNumericoException();
        }
        if (!Double.isFinite(salario) || salario < 0) {
            throw new SalarioDeveSerNaoNegativoException();
        }
        return salario;
    }

    private double converterComissaoAlteracao(String valor) throws ErroWePayUException {
        if (valor == null || valor.isEmpty()) {
            throw new ComissaoNaoPodeSerNulaException();
        }
        try {
            double comissao = parseNumero(valor);
            if (!Double.isFinite(comissao) || comissao < 0) {
                throw new ComissaoDeveSerNaoNegativaException();
            }
            return comissao;
        } catch (NumberFormatException e) {
            throw new ComissaoDeveSerNumericaException();
        }
    }

    private void validarDadosBasicosEmpregado(String nome, String endereco)
            throws NomeNaoPodeSerNuloException, EnderecoNaoPodeSerNuloException {
        if (nome == null || nome.isEmpty()) {
            throw new NomeNaoPodeSerNuloException();
        }
        if (endereco == null || endereco.isEmpty()) {
            throw new EnderecoNaoPodeSerNuloException();
        }
    }

    private double converterSalarioCriacao(String salario)
            throws SalarioNaoPodeSerNuloException,
            SalarioDeveSerNumericoException, SalarioDeveSerNaoNegativoException {
        if (salario == null || salario.isEmpty()) {
            throw new SalarioNaoPodeSerNuloException();
        }
        double valor;
        try {
            valor = parseNumero(salario);
        } catch (NumberFormatException e) {
            throw new SalarioDeveSerNumericoException();
        }
        if (!Double.isFinite(valor) || valor < 0) {
            throw new SalarioDeveSerNaoNegativoException();
        }
        return valor;
    }

    private double converterComissaoCriacao(String comissao)
            throws ComissaoNaoPodeSerNulaException,
            ComissaoDeveSerNumericaException, ComissaoDeveSerNaoNegativaException {
        if (comissao == null || comissao.isEmpty()) {
            throw new ComissaoNaoPodeSerNulaException();
        }
        double valor;
        try {
            valor = parseNumero(comissao);
        } catch (NumberFormatException e) {
            throw new ComissaoDeveSerNumericaException();
        }
        if (!Double.isFinite(valor) || valor < 0) {
            throw new ComissaoDeveSerNaoNegativaException();
        }
        return valor;
    }

    private double parseNumero(String valor) {
        return Double.parseDouble(valor.replace(",", "."));
    }

    private TipoEmpregado converterTipo(String tipo) throws TipoInvalidoException {
        try {
            return TipoEmpregado.deTexto(tipo);
        } catch (IllegalArgumentException e) {
            throw new TipoInvalidoException();
        }
    }

    private void validarData(String data) throws ErroWePayUException {
        ValidadorData.validar(data);
    }

    private Empregado criarComTipo(
            Empregado atual, TipoEmpregado tipo, double salario, double comissao) {
        return atual.mudarTipo(tipo, salario, comissao);
    }

    private void substituir(Empregado atual, Empregado novo) {
        int indice = repositorioEmpregados.indicePorId(atual.getId());
        repositorioEmpregados.substituir(indice, novo);
    }

    private static String formatarValor(double valor) {
        return String.format("%.2f", valor).replace(".", ",");
    }

    private record ValoresAlteracao(double salario, double comissao) {
    }
}
