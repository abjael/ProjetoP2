package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.exception.*;
import br.ufal.ic.p2.wepayu.models.AgendaPagamento;
import br.ufal.ic.p2.wepayu.models.Assalariado;
import br.ufal.ic.p2.wepayu.models.Comissionado;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Horista;
import br.ufal.ic.p2.wepayu.models.MetodoPagamento;
import br.ufal.ic.p2.wepayu.models.TaxaServico;
import br.ufal.ic.p2.wepayu.models.TipoEmpregado;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.util.ValidadorData;

import java.math.BigDecimal;

public class ServicoEmpregados {
    private final RepositorioEmpregados repositorioEmpregados;
    private final AgendasPagamentoDisponiveis agendasDisponiveis =
            new AgendasPagamentoDisponiveis();

    public ServicoEmpregados(RepositorioEmpregados repositorioEmpregados) {
        this.repositorioEmpregados = repositorioEmpregados;
    }

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario)
            throws NomeNaoPodeSerNuloException, EnderecoNaoPodeSerNuloException,
            TipoInvalidoException, TipoNaoAplicavelException,
            SalarioNaoPodeSerNuloException, SalarioDeveSerNumericoException,
            SalarioDeveSerNaoNegativoException {

        ConversorEntradaEmpregado.validarDadosBasicos(nome, endereco);
        TipoEmpregado tipoConvertido = ConversorEntradaEmpregado.converterTipo(tipo);
        if (tipoConvertido == TipoEmpregado.COMISSIONADO) {
            throw new TipoNaoAplicavelException();
        }
        BigDecimal salarioNumero = ConversorEntradaEmpregado.converterSalario(salario);

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

    public Empregado buscarEmpregado(String id)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException {
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
            throws NomeNaoPodeSerNuloException,
            NaoHaEmpregadoComEsseNomeException {
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
        agendasDisponiveis.reiniciar();
    }

    public void inicializarAgendasDisponiveis(java.util.List<Empregado> empregados) {
        inicializarAgendasDisponiveis(empregados, java.util.List.of());
    }

    public void inicializarAgendasDisponiveis(
            java.util.List<Empregado> empregados,
            java.util.Collection<String> agendasPersistidas) {
        agendasDisponiveis.inicializar(empregados, agendasPersistidas);
    }

    public java.util.Set<String> listarAgendasDisponiveis() {
        return agendasDisponiveis.listar();
    }

    public void substituirAgendasDisponiveis(java.util.Collection<String> agendas) {
        agendasDisponiveis.substituir(agendas);
    }

    public boolean agendaDisponivel(String descricao) {
        return agendasDisponiveis.contem(descricao);
    }

    public void criarAgendaDePagamentos(String descricao)
            throws DescricaoDeAgendaInvalidaException,
            AgendaDePagamentosJaExisteException {
        agendasDisponiveis.adicionar(descricao);
    }

    public String getAtributoEmpregado(String id, String atributo)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, AtributoNaoExisteException,
            EmpregadoNaoRecebeEmBancoException,
            EmpregadoNaoEhSindicalizadoException,
            EmpregadoNaoEhComissionadoException {
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
            case "salario" -> ConversorEntradaEmpregado.formatarValor(
                    empregado.getSalarioMonetario());
            case "sindicalizado" -> empregado.getSindicalizado();
            case "comissao" -> ConversorEntradaEmpregado.formatarValor(
                    obterComissionado(empregado).getComissaoMonetario());
            case "horasTrabalhadas" -> empregado.getHorasTrabalhadas();
            case "metodoPagamento" -> empregado.getMetodoPagamento();
            case "agendaPagamento" -> empregado.getAgendaPagamento();
            case "banco" -> empregado.getBanco();
            case "agencia" -> empregado.getAgencia();
            case "contaCorrente" -> empregado.getContaCorrente();
            case "idSindicato" -> empregado.getIdSindicato();
            case "taxaSindical" -> ConversorEntradaEmpregado.formatarValor(
                    empregado.getTaxaSindicalMonetario());
            default -> throw new AtributoNaoExisteException();
        };
    }

    public String getHorasTrabalhadas(String id, String dataInicial,
                                      String dataFinal, boolean extras)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, EmpregadoNaoEhHoristaException,
            DataInicialNaoPodeSerNulaException, DataInicialInvalidaException,
            DataFinalNaoPodeSerNulaException, DataFinalInvalidaException,
            DataInicialNaoPodeSerPosteriorException {
        Horista empregado = obterHorista(buscarEmpregado(id));
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
                                      String dataFinal)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, EmpregadoNaoEhComissionadoException,
            DataInicialNaoPodeSerNulaException, DataInicialInvalidaException,
            DataFinalNaoPodeSerNulaException, DataFinalInvalidaException,
            DataInicialNaoPodeSerPosteriorException {
        Comissionado empregado = obterComissionado(buscarEmpregado(id));
        java.time.LocalDate inicio = ValidadorData.validarInicial(dataInicial);
        java.time.LocalDate fim = ValidadorData.validarFinal(dataFinal);
        if (inicio.isAfter(fim)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }
        return ConversorEntradaEmpregado.formatarValor(
                empregado.calcularVendasRealizadasMonetario(inicio, fim));
    }

    public String getTaxasServico(String id, String dataInicial,
                                  String dataFinal)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, EmpregadoNaoEhSindicalizadoException,
            DataInicialNaoPodeSerNulaException, DataInicialInvalidaException,
            DataFinalNaoPodeSerNulaException, DataFinalInvalidaException,
            DataInicialNaoPodeSerPosteriorException {
        Empregado empregado = buscarEmpregado(id);
        if (!empregado.isSindicalizado()) {
            throw new EmpregadoNaoEhSindicalizadoException();
        }
        java.time.LocalDate inicio = ValidadorData.validarInicial(dataInicial);
        java.time.LocalDate fim = ValidadorData.validarFinal(dataFinal);
        if (inicio.isAfter(fim)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }
        BigDecimal total = BigDecimal.ZERO;
        for (TaxaServico taxa : empregado.getTaxasServico()) {
            java.time.LocalDate dataTaxa = ValidadorData.parse(taxa.getData());
            if (!dataTaxa.isBefore(inicio) && dataTaxa.isBefore(fim)) {
                total = total.add(taxa.getValorMonetario());
            }
        }
        return ConversorEntradaEmpregado.formatarValor(total);
    }

    public void alterarAtributo(String id, String atributo, String valor)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, AtributoNaoExisteException,
            NomeNaoPodeSerNuloException, EnderecoNaoPodeSerNuloException,
            TipoInvalidoException, SalarioNaoPodeSerNuloException,
            SalarioDeveSerNumericoException, SalarioDeveSerNaoNegativoException,
            EmpregadoNaoEhComissionadoException, ComissaoNaoPodeSerNulaException,
            ComissaoDeveSerNumericaException,
            ComissaoDeveSerNaoNegativaException,
            MetodoDePagamentoInvalidoException,
            AgendaDePagamentoNaoDisponivelException,
            ValorDeveSerTrueOuFalseException,
            IdentificacaoDoSindicatoNaoPodeSerNulaException {
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
            case "salario" -> empregado.setSalario(
                    ConversorEntradaEmpregado.converterSalario(valor));
            case "comissao" -> obterComissionado(empregado).setComissao(
                    ConversorEntradaEmpregado.converterComissao(valor));
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
                boolean sindicalizado =
                        ConversorEntradaEmpregado.converterBooleano(valor);
                if (sindicalizado) {
                    throw new IdentificacaoDoSindicatoNaoPodeSerNulaException();
                }
                empregado.dessindicalizar();
            }
            default -> throw new AtributoNaoExisteException();
        }
    }

    public void alterarTipo(String id, String atributo, String tipo,
                            String valorExtra)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, TipoInvalidoException,
            SalarioNaoPodeSerNuloException, SalarioDeveSerNumericoException,
            SalarioDeveSerNaoNegativoException, ComissaoNaoPodeSerNulaException,
            ComissaoDeveSerNumericaException,
            ComissaoDeveSerNaoNegativaException {
        if ("tipo".equals(atributo)) {
            alterarTipo(buscarEmpregado(id), tipo, valorExtra);
        }
    }

    public void alterarSindicalizacao(String id, String valor,
                                      String idSindicato, String taxaSindical)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, ValorDeveSerTrueOuFalseException,
            IdentificacaoDoSindicatoNaoPodeSerNulaException,
            TaxaSindicalNaoPodeSerNulaException,
            TaxaSindicalDeveSerNumericaException,
            TaxaSindicalDeveSerNaoNegativaException,
            HaOutroEmpregadoComEstaIdentificacaoDeSindicatoException {
        Empregado empregado = buscarEmpregado(id);
        boolean sindicalizado = ConversorEntradaEmpregado.converterBooleano(valor);
        if (!sindicalizado) {
            empregado.setSindicalizado(false);
            return;
        }
        if (idSindicato == null || idSindicato.isEmpty()) {
            throw new IdentificacaoDoSindicatoNaoPodeSerNulaException();
        }
        if (taxaSindical == null || taxaSindical.isEmpty()) {
            throw new TaxaSindicalNaoPodeSerNulaException();
        }
        BigDecimal taxa;
        try {
            taxa = ConversorEntradaEmpregado.converterValorMonetario(taxaSindical);
        } catch (NumberFormatException e) {
            throw new TaxaSindicalDeveSerNumericaException();
        }
        if (taxa.signum() < 0) {
            throw new TaxaSindicalDeveSerNaoNegativaException();
        }
        Empregado outroMembro = repositorioEmpregados.buscarPorIdSindicato(idSindicato);
        if (outroMembro != null && !outroMembro.getId().equals(id)) {
            throw new HaOutroEmpregadoComEstaIdentificacaoDeSindicatoException();
        }
        empregado.setSindicalizado(true, idSindicato, taxa);
    }

    public void alterarPagamentoBanco(String id, String atributo,
                                      String metodo, String banco, String agencia, String contaCorrente)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, BancoNaoPodeSerNuloException,
            AgenciaNaoPodeSerNuloException, ContaCorrenteNaoPodeSerNuloException {
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

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario,
            String comissao)
            throws NomeNaoPodeSerNuloException, EnderecoNaoPodeSerNuloException,
            TipoInvalidoException, TipoNaoAplicavelException,
            SalarioNaoPodeSerNuloException, SalarioDeveSerNumericoException,
            SalarioDeveSerNaoNegativoException, ComissaoNaoPodeSerNulaException,
            ComissaoDeveSerNumericaException,
            ComissaoDeveSerNaoNegativaException {

        ConversorEntradaEmpregado.validarDadosBasicos(nome, endereco);
        TipoEmpregado tipoConvertido = ConversorEntradaEmpregado.converterTipo(tipo);
        if (tipoConvertido == TipoEmpregado.HORISTA
                || tipoConvertido == TipoEmpregado.ASSALARIADO) {
            throw new TipoNaoAplicavelException();
        }
        BigDecimal salarioNumero = ConversorEntradaEmpregado.converterSalario(salario);
        BigDecimal comissaoNumero = ConversorEntradaEmpregado.converterComissao(comissao);

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

    public void alterarTipo(Empregado empregado, String tipo)
            throws TipoInvalidoException {
        TipoEmpregado tipoConvertido = ConversorEntradaEmpregado.converterTipo(tipo);
        Empregado novo = criarComTipo(
                empregado, tipoConvertido, empregado.getSalarioMonetario(),
                BigDecimal.ZERO);
        substituir(empregado, novo);
    }

    public void removerEmpregado(String idBuscado)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException {
        Empregado empregado = buscarEmpregado(idBuscado);
        repositorioEmpregados.remover(
                repositorioEmpregados.indicePorId(empregado.getId()));
    }

    public void lancarCartao(String idEmp, String data, String horas)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, EmpregadoNaoEhHoristaException,
            DataNaoPodeSerNulaException, DataInvalidaException,
            HorasNaoPodemSerNulasException, HorasDevemSerNumericasException,
            HorasDevemSerPositivasException {
        Horista empregado = obterHorista(buscarEmpregado(idEmp));
        validarData(data);

        if (horas == null || horas.isEmpty()) {
            throw new HorasNaoPodemSerNulasException();
        }

        double horasNumericas;
        try {
            horasNumericas = ConversorEntradaEmpregado.converterNumero(horas);
        } catch (NumberFormatException e) {
            throw new HorasDevemSerNumericasException();
        }

        if (horasNumericas <= 0) {
            throw new HorasDevemSerPositivasException();
        }

        empregado.lancarCartao(data, horasNumericas);
    }

    public void lancarVenda(String idEmp, String data, String valor)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, EmpregadoNaoEhComissionadoException,
            DataNaoPodeSerNulaException, DataInvalidaException,
            ValorNaoPodeSerNuloException, ValorDeveSerNumericoException,
            ValorDeveSerPositivoException {
        Comissionado empregado = obterComissionado(buscarEmpregado(idEmp));
        validarData(data);

        if (valor == null || valor.isEmpty()) {
            throw new ValorNaoPodeSerNuloException();
        }

        BigDecimal valorNumerico;
        try {
            valorNumerico = ConversorEntradaEmpregado.converterValorMonetario(valor);
        } catch (NumberFormatException e) {
            throw new ValorDeveSerNumericoException();
        }

        if (valorNumerico.signum() <= 0) {
            throw new ValorDeveSerPositivoException();
        }

        empregado.adicionarVenda(data, valorNumerico);
    }

    public void lancarTaxaServico(
            String idSindicato, String data, String valor)
            throws IdentificacaoDoMembroNaoPodeSerNulaException,
            MembroNaoExisteException, DataNaoPodeSerNulaException,
            DataInvalidaException, ValorNaoPodeSerNuloException,
            ValorDeveSerNumericoException, ValorDeveSerPositivoException {
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

        BigDecimal valorNumerico;
        try {
            valorNumerico = ConversorEntradaEmpregado.converterValorMonetario(valor);
        } catch (NumberFormatException e) {
            throw new ValorDeveSerNumericoException();
        }

        if (valorNumerico.signum() <= 0) {
            throw new ValorDeveSerPositivoException();
        }

        empregado.adicionarTaxaServico(data, valorNumerico);
    }

    public void alterarTipo(
            Empregado empregado, String tipo, String valorExtra)
            throws TipoInvalidoException, SalarioNaoPodeSerNuloException,
            SalarioDeveSerNumericoException, SalarioDeveSerNaoNegativoException,
            ComissaoNaoPodeSerNulaException, ComissaoDeveSerNumericaException,
            ComissaoDeveSerNaoNegativaException {
        TipoEmpregado tipoConvertido = ConversorEntradaEmpregado.converterTipo(tipo);
        ValoresAlteracao valores = switch (tipoConvertido) {
            case HORISTA, ASSALARIADO -> new ValoresAlteracao(
                    ConversorEntradaEmpregado.converterSalario(valorExtra),
                    BigDecimal.ZERO);
            case COMISSIONADO -> new ValoresAlteracao(
                    empregado.getSalarioMonetario(),
                    ConversorEntradaEmpregado.converterComissao(valorExtra));
        };

        Empregado novo = criarComTipo(
                empregado, tipoConvertido, valores.salario(), valores.comissao());
        substituir(empregado, novo);
    }

    private void validarData(String data)
            throws DataNaoPodeSerNulaException, DataInvalidaException {
        ValidadorData.validar(data);
    }

    private Empregado criarComTipo(
            Empregado atual, TipoEmpregado tipo,
            BigDecimal salario, BigDecimal comissao) {
        return atual.mudarTipo(tipo, salario, comissao);
    }

    private Horista obterHorista(Empregado empregado)
            throws EmpregadoNaoEhHoristaException {
        if (empregado instanceof Horista horista) {
            return horista;
        }
        throw new EmpregadoNaoEhHoristaException();
    }

    private Comissionado obterComissionado(Empregado empregado)
            throws EmpregadoNaoEhComissionadoException {
        if (empregado instanceof Comissionado comissionado) {
            return comissionado;
        }
        throw new EmpregadoNaoEhComissionadoException();
    }

    private void substituir(Empregado atual, Empregado novo) {
        int indice = repositorioEmpregados.indicePorId(atual.getId());
        repositorioEmpregados.substituir(indice, novo);
    }

    private record ValoresAlteracao(BigDecimal salario, BigDecimal comissao) {
    }
}
