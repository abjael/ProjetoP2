package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.ArrayList;
import java.util.List;

public class Facade {

    private List<Empregado> listaEmpregados = new ArrayList<>();

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        if (nome == null || nome.isEmpty()) {
            throw new Exception("Nome nao pode ser nulo.");
        }
        if (endereco == null || endereco.isEmpty()) {
            throw new Exception("Endereco nao pode ser nulo.");
        }
        if (tipo == null || (!tipo.equals("horista") && !tipo.equals("assalariado") && !tipo.equals("comissionado"))) {
            throw new Exception("Tipo invalido.");
        }
        if (tipo.equals("comissionado")) {
            throw new Exception("Tipo nao aplicavel.");
        }
        if (salario == null || salario.isEmpty()) {
            throw new Exception("Salario nao pode ser nulo.");
        }

        double salarioNumero;
        try {
            salarioNumero = Double.parseDouble(salario.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Salario deve ser numerico.");
        }

        if (salarioNumero < 0) {
            throw new Exception("Salario deve ser nao-negativo.");
        }

        String idGerado = java.util.UUID.randomUUID().toString();

        Empregado novoFuncionario = new Empregado(idGerado, nome, endereco, tipo, salarioNumero, "false");
        listaEmpregados.add(novoFuncionario);
        return idGerado;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        if (nome == null || nome.isEmpty()) {
            throw new Exception("Nome nao pode ser nulo.");
        }
        if (endereco == null || endereco.isEmpty()) {
            throw new Exception("Endereco nao pode ser nulo.");
        }
        if (tipo == null || (!tipo.equals("horista") && !tipo.equals("assalariado") && !tipo.equals("comissionado"))) {
            throw new Exception("Tipo invalido.");
        }
        if (tipo.equals("horista") || tipo.equals("assalariado")) {
            throw new Exception("Tipo nao aplicavel.");
        }
        if (salario == null || salario.isEmpty()) {
            throw new Exception("Salario nao pode ser nulo.");
        }

        double salarioNumero;
        try {
            salarioNumero = Double.parseDouble(salario.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Salario deve ser numerico.");
        }

        if (salarioNumero < 0) {
            throw new Exception("Salario deve ser nao-negativo.");
        }

        if (comissao == null || comissao.isEmpty()) {
            throw new Exception("Comissao nao pode ser nula.");
        }

        double comissaoNumero;
        try {
            comissaoNumero = Double.parseDouble(comissao.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Comissao deve ser numerica.");
        }

        if (comissaoNumero < 0) {
            throw new Exception("Comissao deve ser nao-negativa.");
        }

        String idGerado = java.util.UUID.randomUUID().toString();
        Empregado novoFuncionario = new Empregado(idGerado, nome, endereco, tipo, salarioNumero, comissaoNumero, "false");
        listaEmpregados.add(novoFuncionario);
        return idGerado;
    }

    public String getAtributoEmpregado(String idBuscado, String atributo) throws Exception {
        if (idBuscado == null || idBuscado.isEmpty()) {
            throw new Exception("Identificacao do empregado nao pode ser nula.");
        }
        for (int i = 0; i < listaEmpregados.size(); i++) {
            Empregado funcionarioAtual = listaEmpregados.get(i);

            if (funcionarioAtual.getId().equals(idBuscado)) {
                if (atributo.equals("nome")) {
                    return funcionarioAtual.getNome();
                } else if (atributo.equals("endereco")) {
                    return funcionarioAtual.getEndereco();
                } else if (atributo.equals("tipo")) {
                    return funcionarioAtual.getTipo();
                } else if (atributo.equals("salario")) {
                    return String.format("%.2f", funcionarioAtual.getSalario()).replace(".", ",");
                } else if (atributo.equals("sindicalizado")) {
                    return funcionarioAtual.getSindicalizado();
                } else if (atributo.equals("comissao")) {
                    return String.format("%.2f", funcionarioAtual.getComissao()).replace(".", ",");
                }
            }
        }
        if (!atributo.equals("nome") && !atributo.equals("endereco") && !atributo.equals("tipo") &&
                !atributo.equals("salario") && !atributo.equals("sindicalizado") && !atributo.equals("comissao")) {
            throw new Exception("Atributo nao existe.");
        }
        throw new EmpregadoNaoExisteException();
    }

    public void zerarSistema() {
        listaEmpregados.clear();
    }
    public void encerrarSistema() {

    }
}
