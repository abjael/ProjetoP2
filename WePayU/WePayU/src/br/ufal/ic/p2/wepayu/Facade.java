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
            new ServicoUndoRedo(repositorioEmpregados, repositorioFolhas);
    private final ServicoSistema servicoSistema = new ServicoSistema(
            repositorioDadosXml, repositorioEmpregados, repositorioFolhas);

    public Facade() throws ErroWePayUException {
        java.util.List<Empregado> empregadosCarregados = new java.util.ArrayList<>();
        java.util.Map<String, String> folhasCarregadas = new java.util.HashMap<>();
        repositorioDadosXml.carregarDados(empregadosCarregados, folhasCarregadas);
        repositorioEmpregados.substituirTodos(empregadosCarregados);
        repositorioFolhas.substituirTodas(folhasCarregadas);
        servicoEmpregados.inicializarAgendasDisponiveis(repositorioEmpregados.listarTodos());
    }


    public int getNumeroDeEmpregados() throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        return repositorioEmpregados.listarTodos().size();
    }

    public void undo() throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.desfazer();
    }

    public void redo() throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.refazer();
    }

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        return servicoUndoRedo.executarComando(
                () -> servicoEmpregados.criarEmpregado(
                        nome, endereco, tipo, salario));
    }

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario,
            String comissao) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        return servicoUndoRedo.executarComando(
                () -> servicoEmpregados.criarEmpregado(
                        nome, endereco, tipo, salario, comissao));
    }

    public String getAtributoEmpregado(String idBuscado, String atributo) throws ErroWePayUException {
        return servicoEmpregados.getAtributoEmpregado(idBuscado, atributo);
    }

    public void criarAgendaDePagamentos(String descricao) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoEmpregados.criarAgendaDePagamentos(descricao);
    }

    public String getEmpregadoPorNome(String nome, int indice) throws ErroWePayUException {
        return servicoEmpregados.getEmpregadoPorNome(nome, indice);
    }

    public void removerEmpregado(String idBuscado) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.executarAcao(
                () -> servicoEmpregados.removerEmpregado(idBuscado));
    }

    public void lancaCartao(String idEmp, String data, String horas) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.executarAcao(
                () -> servicoEmpregados.lancarCartao(idEmp, data, horas));
    }

    public String getHorasNormaisTrabalhadas(
            String idEmp, String dataInicial, String dataFinal) throws ErroWePayUException {
        return servicoEmpregados.getHorasTrabalhadas(
                idEmp, dataInicial, dataFinal, false);
    }

    public String getHorasExtrasTrabalhadas(
            String idEmp, String dataInicial, String dataFinal) throws ErroWePayUException {
        return servicoEmpregados.getHorasTrabalhadas(
                idEmp, dataInicial, dataFinal, true);
    }

    public void lancaVenda(String idBuscado, String data, String valor) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.executarAcao(
                () -> servicoEmpregados.lancarVenda(idBuscado, data, valor));
    }

    public String getVendasRealizadas(
            String idEmpregado, String dataInicial, String dataFinal) throws ErroWePayUException {
        return servicoEmpregados.getVendasRealizadas(
                idEmpregado, dataInicial, dataFinal);
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.executarAcao(
                () -> servicoEmpregados.alterarAtributo(
                        idEmpregado, atributo, valor));
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor, String valorExtra) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.executarAcao(
                () -> servicoEmpregados.alterarTipo(
                        idEmpregado, atributo, valor, valorExtra));
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor, String idSindicato, String taxaSindical) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.executarAcao(
                () -> servicoEmpregados.alterarSindicalizacao(
                        idEmpregado, valor, idSindicato, taxaSindical));
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.executarAcao(
                () -> servicoEmpregados.alterarPagamentoBanco(
                        idEmpregado, atributo, valor1, banco, agencia, contaCorrente));
    }

    public String getTaxasServico(String idEmpregado, String dataInicial, String dataFinal) throws ErroWePayUException {
        return servicoEmpregados.getTaxasServico(
                idEmpregado, dataInicial, dataFinal);
    }

    public void lancaTaxaServico(String idSindicato, String data, String valor) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.executarAcao(
                () -> servicoEmpregados.lancarTaxaServico(
                        idSindicato, data, valor));
    }

    public void rodaFolha(String data, String saida) throws ErroWePayUException {
        servicoSistema.verificarAtivo();
        servicoUndoRedo.executarComandoSeNecessario(
                () -> servicoFolhaPagamento.rodaFolha(data, saida));
    }

    public String totalFolha(String data) throws ErroWePayUException {
        return servicoFolhaPagamento.totalFolha(data);
    }
    public void encerrarSistema() throws ErroWePayUException {
        servicoSistema.encerrar();
    }

    public void zerarSistema() {
        servicoUndoRedo.executarAcaoSemErro(() -> {
            servicoSistema.zerar();
            servicoEmpregados.reiniciarAgendasDisponiveis();
        });
    }

}
