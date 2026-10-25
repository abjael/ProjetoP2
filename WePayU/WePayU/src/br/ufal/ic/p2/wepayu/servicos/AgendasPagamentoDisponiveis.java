package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.exception.AgendaDePagamentosJaExisteException;
import br.ufal.ic.p2.wepayu.exception.DescricaoDeAgendaInvalidaException;
import br.ufal.ic.p2.wepayu.models.AgendaPagamento;
import br.ufal.ic.p2.wepayu.models.Empregado;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class AgendasPagamentoDisponiveis {
    private final Set<String> agendas = new LinkedHashSet<>();

    AgendasPagamentoDisponiveis() {
        reiniciar();
    }

    void reiniciar() {
        agendas.clear();
        agendas.add("semanal 5");
        agendas.add("mensal $");
        agendas.add("semanal 2 5");
    }

    void inicializar(List<Empregado> empregados, Collection<String> agendasPersistidas) {
        reiniciar();
        for (String agenda : agendasPersistidas) {
            if (agenda != null && !agenda.isEmpty()) {
                agendas.add(AgendaPagamento.normalizar(agenda));
            }
        }
        for (Empregado empregado : empregados) {
            if (empregado != null && empregado.getAgendaPagamento() != null
                    && !empregado.getAgendaPagamento().isEmpty()) {
                agendas.add(AgendaPagamento.normalizar(empregado.getAgendaPagamento()));
            }
        }
    }

    Set<String> listar() {
        return Set.copyOf(agendas);
    }

    void substituir(Collection<String> novasAgendas) {
        agendas.clear();
        agendas.addAll(novasAgendas);
    }

    boolean contem(String descricao) {
        return descricao != null && agendas.contains(AgendaPagamento.normalizar(descricao));
    }

    void adicionar(String descricao)
            throws DescricaoDeAgendaInvalidaException,
            AgendaDePagamentosJaExisteException {
        String agenda = AgendaPagamento.normalizar(descricao);
        if (agenda.isEmpty()) {
            throw new DescricaoDeAgendaInvalidaException();
        }
        if (agendas.contains(agenda)) {
            throw new AgendaDePagamentosJaExisteException();
        }
        AgendaPagamento.parse(agenda);
        agendas.add(agenda);
    }
}
