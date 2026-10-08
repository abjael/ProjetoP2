package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.exception.ErroWePayUException;
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
    private final Deque<Estado> estadosDesfeitos = new ArrayDeque<>();
    private final Deque<Estado> estadosRefeitos = new ArrayDeque<>();

    public ServicoUndoRedo(
            RepositorioEmpregados repositorioEmpregados,
            RepositorioFolhas repositorioFolhas) {
        this.repositorioEmpregados = repositorioEmpregados;
        this.repositorioFolhas = repositorioFolhas;
    }

    public <T> T executarComando(ComandoComRetorno<T> comando)
            throws ErroWePayUException {
        Estado estadoAnterior = capturarEstadoAtual();
        T resultado = comando.executar();
        registrar(estadoAnterior);
        return resultado;
    }

    public void executarAcao(AcaoComando comando)
            throws ErroWePayUException {
        executarComando(() -> {
            comando.executar();
            return null;
        });
    }

    public boolean executarComandoSeNecessario(
            ComandoComRetorno<Boolean> comando) throws ErroWePayUException {
        Estado estadoAnterior = capturarEstadoAtual();
        boolean alterouEstado = comando.executar();
        if (alterouEstado) {
            registrar(estadoAnterior);
        }
        return alterouEstado;
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
        return new Estado(empregados, repositorioFolhas.listarTodas());
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
    }

    public static final class Estado {
        private final List<Empregado> empregados;
        private final Map<String, String> folhas;

        private Estado(List<Empregado> empregados, Map<String, String> folhas) {
            this.empregados = empregados;
            this.folhas = new HashMap<>(folhas);
        }
    }

    @FunctionalInterface
    public interface ComandoComRetorno<T> {
        T executar() throws ErroWePayUException;
    }

    @FunctionalInterface
    public interface AcaoComando {
        void executar() throws ErroWePayUException;
    }
}
