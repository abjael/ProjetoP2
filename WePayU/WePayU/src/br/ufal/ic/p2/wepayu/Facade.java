package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facade {

    private List<Empregado> listaEmpregados = new ArrayList<>();

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        String idGerado = java.util.UUID.randomUUID().toString();
        double salarioNumero = Double.parseDouble(salario.replace(",", "."));
        Empregado novoFuncionario = new Empregado(idGerado, nome, endereco, tipo, salarioNumero,"false");
        listaEmpregados.add(novoFuncionario);
        return idGerado;
    }

    public String getAtributoEmpregado(String idBuscado, String atributo) throws Exception {


        for (int i = 0; i < listaEmpregados.size(); i++) {

            Empregado funcionarioAtual = listaEmpregados.get(i);

            if (funcionarioAtual.getId().equals(idBuscado)) {

                if(atributo.equals("nome")){
                    return funcionarioAtual.getNome();
               }
               else if(atributo.equals("endereco")){
                   return funcionarioAtual.getEndereco();
                }
               else if(atributo.equals("tipo")){
                   return funcionarioAtual.getTipo();

                }
               else if (atributo.equals("salario")){
                    return String.format("%.2f", funcionarioAtual.getSalario()).replace(".", ",");
               }

               else if (atributo.equals("sindicalizado")){
                    return funcionarioAtual.getSindicalizado();


                }
            }

        }
        throw new EmpregadoNaoExisteException();

    }

    public void zerarSistema(){
        listaEmpregados.clear();
    }
}
