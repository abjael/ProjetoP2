package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facade {

    private List<Empregado> listaEmpregados = new ArrayList<>();

    public String getAtributoEmpregado(String nomeBusacado, String atributo) throws Exception {


        for (int i = 0; i < listaEmpregados.size(); i++) {

            Empregado funcionarioAtual = listaEmpregados.get(i);

            if (funcionarioAtual.getNome().equals(nomeBusacado)) {
                return funcionarioAtual.getNome();

            }

        }
        throw new EmpregadoNaoExisteException();

    }

    public void zerarSistema(){
        listaEmpregados.clear();
    }
}
