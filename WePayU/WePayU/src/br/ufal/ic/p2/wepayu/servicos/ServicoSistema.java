package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.exception.ErroAoSalvarDadosException;
import br.ufal.ic.p2.wepayu.exception.SistemaEncerradoException;
import br.ufal.ic.p2.wepayu.persistencia.RepositorioDadosXml;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioFolhas;

public class ServicoSistema {
    private final RepositorioDadosXml repositorioDadosXml;
    private final RepositorioEmpregados repositorioEmpregados;
    private final RepositorioFolhas repositorioFolhas;
    private boolean encerrado;

    public ServicoSistema(
            RepositorioDadosXml repositorioDadosXml,
            RepositorioEmpregados repositorioEmpregados,
            RepositorioFolhas repositorioFolhas) {
        this.repositorioDadosXml = repositorioDadosXml;
        this.repositorioEmpregados = repositorioEmpregados;
        this.repositorioFolhas = repositorioFolhas;
    }

    public void verificarAtivo() throws SistemaEncerradoException {
        if (encerrado) {
            throw new SistemaEncerradoException();
        }
    }

    public void encerrar() throws ErroAoSalvarDadosException {
        encerrar(java.util.List.of());
    }

    public void encerrar(java.util.Collection<String> agendasDisponiveis)
            throws ErroAoSalvarDadosException {
        repositorioDadosXml.salvarDados(
                repositorioEmpregados.listarTodos(),
                repositorioFolhas.listarTodas(),
                agendasDisponiveis);
        encerrado = true;
    }

    public void zerar() {
        repositorioEmpregados.limpar();
        repositorioFolhas.limpar();
        encerrado = false;
    }
}
