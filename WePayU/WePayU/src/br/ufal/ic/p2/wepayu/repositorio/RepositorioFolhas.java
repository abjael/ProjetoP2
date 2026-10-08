package br.ufal.ic.p2.wepayu.repositorio;

import java.util.HashMap;
import java.util.Map;

public class RepositorioFolhas {
    private final Map<String, String> folhas = new HashMap<>();

    public boolean contem(String data) {
        return folhas.containsKey(data);
    }

    public String buscar(String data) {
        return folhas.get(data);
    }

    public void adicionar(String data, String conteudo) {
        folhas.put(data, conteudo);
    }

    public Map<String, String> listarTodas() {
        return folhas;
    }

    public void limpar() {
        folhas.clear();
    }

    public void substituirTodas(Map<String, String> novasFolhas) {
        folhas.clear();
        folhas.putAll(novasFolhas);
    }
}
