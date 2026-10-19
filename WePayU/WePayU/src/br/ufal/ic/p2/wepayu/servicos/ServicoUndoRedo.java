package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.exception.NaoHaComandoParaDesfazerException;
import br.ufal.ic.p2.wepayu.exception.NaoHaComandoParaRefazerException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioFolhas;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicoUndoRedo {
    private final RepositorioEmpregados repositorioEmpregados;
    private final RepositorioFolhas repositorioFolhas;
    private final ServicoEmpregados servicoEmpregados;
    private final Deque<Estado> estadosDesfeitos = new ArrayDeque<>();
    private final Deque<Estado> estadosRefeitos = new ArrayDeque<>();

    public ServicoUndoRedo(
            RepositorioEmpregados repositorioEmpregados,
            RepositorioFolhas repositorioFolhas,
            ServicoEmpregados servicoEmpregados) {
        this.repositorioEmpregados = repositorioEmpregados;
        this.repositorioFolhas = repositorioFolhas;
        this.servicoEmpregados = servicoEmpregados;
    }

    public void executarAcaoSemErro(Runnable comando) {
        Estado estadoAnterior = capturarEstadoAtual();
        comando.run();
        registrar(estadoAnterior);
    }

    public Estado capturarEstadoAtual() {
        List<Empregado> empregados = new ArrayList<>();
        for (Empregado empregado : repositorioEmpregados.listarTodos()) {
            empregados.add(empregado.clonar());
        }
        return new Estado(
                empregados,
                repositorioFolhas.listarTodas(),
                servicoEmpregados.listarAgendasDisponiveis());
    }

    public void registrar(Estado estadoAnterior) {
        estadosDesfeitos.push(estadoAnterior);
        estadosRefeitos.clear();
    }

    public void desfazer() throws NaoHaComandoParaDesfazerException {
        if (estadosDesfeitos.isEmpty()) {
            throw new NaoHaComandoParaDesfazerException();
        }
        estadosRefeitos.push(capturarEstadoAtual());
        restaurar(estadosDesfeitos.pop());
    }

    public void refazer() throws NaoHaComandoParaRefazerException {
        if (estadosRefeitos.isEmpty()) {
            throw new NaoHaComandoParaRefazerException();
        }
        estadosDesfeitos.push(capturarEstadoAtual());
        restaurar(estadosRefeitos.pop());
    }

    private void restaurar(Estado estado) {
        repositorioEmpregados.substituirTodos(estado.empregados);
        repositorioFolhas.substituirTodas(estado.folhas);
        servicoEmpregados.substituirAgendasDisponiveis(estado.agendasDisponiveis);
    }

    public static final class Estado {
        private final List<Empregado> empregados;
        private final Map<String, String> folhas;
        private final List<String> agendasDisponiveis;

        private Estado(
                List<Empregado> empregados,
                Map<String, String> folhas,
                java.util.Collection<String> agendasDisponiveis) {
            this.empregados = empregados;
            this.folhas = new HashMap<>(folhas);
            this.agendasDisponiveis = new ArrayList<>(agendasDisponiveis);
        }
    }

}
