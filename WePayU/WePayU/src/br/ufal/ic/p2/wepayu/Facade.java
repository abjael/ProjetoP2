package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.persistencia.RepositorioDadosXml;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioFolhas;
import br.ufal.ic.p2.wepayu.servicos.ServicoEmpregados;
import br.ufal.ic.p2.wepayu.servicos.ServicoFolhaPagamento;
import br.ufal.ic.p2.wepayu.servicos.ServicoSistema;
import br.ufal.ic.p2.wepayu.servicos.ServicoUndoRedo;

public class Facade {

    private final RepositorioDadosXml repositorioDadosXml =
            new RepositorioDadosXml();
    private final RepositorioEmpregados repositorioEmpregados =
            new RepositorioEmpregados();
    private final RepositorioFolhas repositorioFolhas =
            new RepositorioFolhas();
    private final ServicoEmpregados servicoEmpregados =
            new ServicoEmpregados(repositorioEmpregados);
    private final ServicoFolhaPagamento servicoFolhaPagamento =
            new ServicoFolhaPagamento(repositorioEmpregados, repositorioFolhas);
    private final ServicoUndoRedo servicoUndoRedo =
            new ServicoUndoRedo(
                    repositorioEmpregados, repositorioFolhas, servicoEmpregados);
    private final ServicoSistema servicoSistema = new ServicoSistema(
            repositorioDadosXml, repositorioEmpregados, repositorioFolhas);

    public Facade() throws ErroAoCarregarDadosException,
            TipoDeEmpregadoPersistidoInvalidoException {
        java.util.List<Empregado> empregadosCarregados = new java.util.ArrayList<>();
        java.util.Map<String, String> folhasCarregadas = new java.util.HashMap<>();
        java.util.Set<String> agendasCarregadas = new java.util.LinkedHashSet<>();
        repositorioDadosXml.carregarDados(
                empregadosCarregados, folhasCarregadas, agendasCarregadas);
        repositorioEmpregados.substituirTodos(empregadosCarregados);
        repositorioFolhas.substituirTodas(folhasCarregadas);
        servicoEmpregados.inicializarAgendasDisponiveis(
                repositorioEmpregados.listarTodos(), agendasCarregadas);
    }


    public int getNumeroDeEmpregados() throws SistemaEncerradoException {
        servicoSistema.verificarAtivo();
        return repositorioEmpregados.listarTodos().size();
    }

    public void undo() throws SistemaEncerradoException,
            NaoHaComandoParaDesfazerException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.desfazer();
    }

    public void redo() throws SistemaEncerradoException,
            NaoHaComandoParaRefazerException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.refazer();
    }

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario)
            throws SistemaEncerradoException, NomeNaoPodeSerNuloException,
            EnderecoNaoPodeSerNuloException, TipoInvalidoException,
            TipoNaoAplicavelException, SalarioNaoPodeSerNuloException,
            SalarioDeveSerNumericoException, SalarioDeveSerNaoNegativoException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        String id = servicoEmpregados.criarEmpregado(nome, endereco, tipo, salario);
        servicoUndoRedo.registrar(estadoAnterior);
        return id;
    }

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario,
            String comissao)
            throws SistemaEncerradoException, NomeNaoPodeSerNuloException,
            EnderecoNaoPodeSerNuloException, TipoInvalidoException,
            TipoNaoAplicavelException, SalarioNaoPodeSerNuloException,
            SalarioDeveSerNumericoException, SalarioDeveSerNaoNegativoException,
            ComissaoNaoPodeSerNulaException, ComissaoDeveSerNumericaException,
            ComissaoDeveSerNaoNegativaException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        String id = servicoEmpregados.criarEmpregado(
                nome, endereco, tipo, salario, comissao);
        servicoUndoRedo.registrar(estadoAnterior);
        return id;
    }

    public String getAtributoEmpregado(String idBuscado, String atributo)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, AtributoNaoExisteException,
            EmpregadoNaoRecebeEmBancoException,
            EmpregadoNaoEhSindicalizadoException,
            EmpregadoNaoEhComissionadoException {
        return servicoEmpregados.getAtributoEmpregado(idBuscado, atributo);
    }

    public void criarAgendaDePagamentos(String descricao)
            throws SistemaEncerradoException, DescricaoDeAgendaInvalidaException,
            AgendaDePagamentosJaExisteException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        servicoEmpregados.criarAgendaDePagamentos(descricao);
        servicoUndoRedo.registrar(estadoAnterior);
    }

    public String getEmpregadoPorNome(String nome, int indice)
            throws NomeNaoPodeSerNuloException,
            NaoHaEmpregadoComEsseNomeException {
        return servicoEmpregados.getEmpregadoPorNome(nome, indice);
    }

    public void removerEmpregado(String idBuscado)
            throws SistemaEncerradoException,
            IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        servicoEmpregados.removerEmpregado(idBuscado);
        servicoUndoRedo.registrar(estadoAnterior);
    }

    public void lancaCartao(String idEmp, String data, String horas)
            throws SistemaEncerradoException,
            IdentificacaoEmpregadoNaoPodeSerNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhHoristaException, DataNaoPodeSerNulaException,
            DataInvalidaException, HorasNaoPodemSerNulasException,
            HorasDevemSerNumericasException, HorasDevemSerPositivasException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        servicoEmpregados.lancarCartao(idEmp, data, horas);
        servicoUndoRedo.registrar(estadoAnterior);
    }

    public String getHorasNormaisTrabalhadas(
            String idEmp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, EmpregadoNaoEhHoristaException,
            DataInicialNaoPodeSerNulaException, DataInicialInvalidaException,
            DataFinalNaoPodeSerNulaException, DataFinalInvalidaException,
            DataInicialNaoPodeSerPosteriorException {
        return servicoEmpregados.getHorasTrabalhadas(
                idEmp, dataInicial, dataFinal, false);
    }

    public String getHorasExtrasTrabalhadas(
            String idEmp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, EmpregadoNaoEhHoristaException,
            DataInicialNaoPodeSerNulaException, DataInicialInvalidaException,
            DataFinalNaoPodeSerNulaException, DataFinalInvalidaException,
            DataInicialNaoPodeSerPosteriorException {
        return servicoEmpregados.getHorasTrabalhadas(
                idEmp, dataInicial, dataFinal, true);
    }

    public void lancaVenda(String idBuscado, String data, String valor)
            throws SistemaEncerradoException,
            IdentificacaoEmpregadoNaoPodeSerNulaException, EmpregadoNaoExisteException,
            EmpregadoNaoEhComissionadoException, DataNaoPodeSerNulaException,
            DataInvalidaException, ValorNaoPodeSerNuloException,
            ValorDeveSerNumericoException, ValorDeveSerPositivoException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        servicoEmpregados.lancarVenda(idBuscado, data, valor);
        servicoUndoRedo.registrar(estadoAnterior);
    }

    public String getVendasRealizadas(
            String idEmpregado, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, EmpregadoNaoEhComissionadoException,
            DataInicialNaoPodeSerNulaException, DataInicialInvalidaException,
            DataFinalNaoPodeSerNulaException, DataFinalInvalidaException,
            DataInicialNaoPodeSerPosteriorException {
        return servicoEmpregados.getVendasRealizadas(
                idEmpregado, dataInicial, dataFinal);
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor)
            throws SistemaEncerradoException,
            IdentificacaoEmpregadoNaoPodeSerNulaException, EmpregadoNaoExisteException,
            AtributoNaoExisteException, NomeNaoPodeSerNuloException,
            EnderecoNaoPodeSerNuloException, TipoInvalidoException,
            SalarioNaoPodeSerNuloException, SalarioDeveSerNumericoException,
            SalarioDeveSerNaoNegativoException, EmpregadoNaoEhComissionadoException,
            ComissaoNaoPodeSerNulaException, ComissaoDeveSerNumericaException,
            ComissaoDeveSerNaoNegativaException, MetodoDePagamentoInvalidoException,
            AgendaDePagamentoNaoDisponivelException,
            ValorDeveSerTrueOuFalseException,
            IdentificacaoDoSindicatoNaoPodeSerNulaException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        servicoEmpregados.alterarAtributo(idEmpregado, atributo, valor);
        servicoUndoRedo.registrar(estadoAnterior);
    }

    public void alteraEmpregado(
            String idEmpregado, String atributo, String valor, String valorExtra)
            throws SistemaEncerradoException,
            IdentificacaoEmpregadoNaoPodeSerNulaException, EmpregadoNaoExisteException,
            TipoInvalidoException, SalarioNaoPodeSerNuloException,
            SalarioDeveSerNumericoException, SalarioDeveSerNaoNegativoException,
            ComissaoNaoPodeSerNulaException, ComissaoDeveSerNumericaException,
            ComissaoDeveSerNaoNegativaException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        servicoEmpregados.alterarTipo(idEmpregado, atributo, valor, valorExtra);
        servicoUndoRedo.registrar(estadoAnterior);
    }

    public void alteraEmpregado(
            String idEmpregado, String atributo, String valor,
            String idSindicato, String taxaSindical)
            throws SistemaEncerradoException,
            IdentificacaoEmpregadoNaoPodeSerNulaException, EmpregadoNaoExisteException,
            ValorDeveSerTrueOuFalseException,
            IdentificacaoDoSindicatoNaoPodeSerNulaException,
            TaxaSindicalNaoPodeSerNulaException,
            TaxaSindicalDeveSerNumericaException,
            TaxaSindicalDeveSerNaoNegativaException,
            HaOutroEmpregadoComEstaIdentificacaoDeSindicatoException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        servicoEmpregados.alterarSindicalizacao(
                idEmpregado, valor, idSindicato, taxaSindical);
        servicoUndoRedo.registrar(estadoAnterior);
    }

    public void alteraEmpregado(
            String idEmpregado, String atributo, String valor1,
            String banco, String agencia, String contaCorrente)
            throws SistemaEncerradoException,
            IdentificacaoEmpregadoNaoPodeSerNulaException, EmpregadoNaoExisteException,
            BancoNaoPodeSerNuloException, AgenciaNaoPodeSerNuloException,
            ContaCorrenteNaoPodeSerNuloException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        servicoEmpregados.alterarPagamentoBanco(
                idEmpregado, atributo, valor1, banco, agencia, contaCorrente);
        servicoUndoRedo.registrar(estadoAnterior);
    }

    public String getTaxasServico(
            String idEmpregado, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNaoPodeSerNulaException,
            EmpregadoNaoExisteException, EmpregadoNaoEhSindicalizadoException,
            DataInicialNaoPodeSerNulaException, DataInicialInvalidaException,
            DataFinalNaoPodeSerNulaException, DataFinalInvalidaException,
            DataInicialNaoPodeSerPosteriorException {
        return servicoEmpregados.getTaxasServico(
                idEmpregado, dataInicial, dataFinal);
    }

    public void lancaTaxaServico(String idSindicato, String data, String valor)
            throws SistemaEncerradoException,
            IdentificacaoDoMembroNaoPodeSerNulaException, MembroNaoExisteException,
            DataNaoPodeSerNulaException, DataInvalidaException,
            ValorNaoPodeSerNuloException, ValorDeveSerNumericoException,
            ValorDeveSerPositivoException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        servicoEmpregados.lancarTaxaServico(idSindicato, data, valor);
        servicoUndoRedo.registrar(estadoAnterior);
    }

    public void rodaFolha(String data, String saida)
            throws SistemaEncerradoException, DataNaoPodeSerNulaException,
            DataInvalidaException, ArquivoDeSaidaNaoPodeSerNuloException,
            DescricaoDeAgendaInvalidaException,
            ErroAoGerarArquivoDeFolhaException {
        servicoSistema.verificarAtivo();
        ServicoUndoRedo.Estado estadoAnterior = servicoUndoRedo.capturarEstadoAtual();
        if (servicoFolhaPagamento.rodaFolha(data, saida)) {
            servicoUndoRedo.registrar(estadoAnterior);
        }
    }

    public String totalFolha(String data)
            throws DataNaoPodeSerNulaException, DataInvalidaException,
            DescricaoDeAgendaInvalidaException {
        return servicoFolhaPagamento.totalFolha(data);
    }

    public void encerrarSistema() throws ErroAoSalvarDadosException {
        servicoSistema.encerrar(servicoEmpregados.listarAgendasDisponiveis());
    }

    public void zerarSistema() {
        servicoUndoRedo.executarAcaoSemErro(() -> {
            servicoSistema.zerar();
            servicoEmpregados.reiniciarAgendasDisponiveis();
        });
    }

}
