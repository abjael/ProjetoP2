package br.ufal.ic.p2.wepayu.repositorio;

import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.ArrayList;
import java.util.List;

public class RepositorioEmpregados {
    private final List<Empregado> empregados = new ArrayList<>();

    public RepositorioEmpregados() {
    }

    public List<Empregado> listarTodos() {
        return empregados;
    }

    public void adicionar(Empregado empregado) {
        empregados.add(empregado);
    }

    public Empregado buscarPorId(String id) {
        for (Empregado empregado : empregados) {
            if (empregado.getId().equals(id)) {
                return empregado;
            }
        }
        return null;
    }

    public int indicePorId(String id) {
        for (int i = 0; i < empregados.size(); i++) {
            if (empregados.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public String buscarIdPorNome(String nome, int indice) {
        int encontrados = 0;
        for (Empregado empregado : empregados) {
            if (empregado.getNome().equals(nome) && ++encontrados == indice) {
                return empregado.getId();
            }
        }
        return null;
    }

    public void remover(int indice) {
        empregados.remove(indice);
    }

    public void substituir(int indice, Empregado empregado) {
        empregados.set(indice, empregado);
    }

    public void limpar() {
        empregados.clear();
    }

    public void substituirTodos(List<Empregado> novosEmpregados) {
        empregados.clear();
        empregados.addAll(novosEmpregados);
    }
}
