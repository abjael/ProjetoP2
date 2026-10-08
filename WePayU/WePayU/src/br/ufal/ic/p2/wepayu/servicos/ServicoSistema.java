package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.exception.ErroWePayUException;
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

    public void encerrar() throws ErroWePayUException {
        repositorioDadosXml.salvarDados(
                repositorioEmpregados.listarTodos(),
                repositorioFolhas.listarTodas());
        encerrado = true;
    }

    public void zerar() {
        repositorioEmpregados.limpar();
        repositorioFolhas.limpar();
        encerrado = false;
    }
}
