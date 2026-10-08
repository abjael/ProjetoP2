package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;

public class ServicoEmpregados {
    private final RepositorioEmpregados repositorioEmpregados;

    public ServicoEmpregados(RepositorioEmpregados repositorioEmpregados) {
        this.repositorioEmpregados = repositorioEmpregados;
    }

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario) throws Exception {

        if (nome == null || nome.isEmpty()) {
            throw new NomeNaoPodeSerNuloException();
        }
        if (endereco == null || endereco.isEmpty()) {
            throw new EnderecoNaoPodeSerNuloException();
        }
        TipoEmpregado tipoConvertido = converterTipo(tipo);
        if (tipoConvertido == TipoEmpregado.COMISSIONADO) {
            throw new TipoNaoAplicavelException();
        }
        if (salario == null || salario.isEmpty()) {
            throw new SalarioNaoPodeSerNuloException();
        }

        double salarioNumero;
        try {
            salarioNumero = Double.parseDouble(salario.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new SalarioDeveSerNumericoException();
        }

        if (salarioNumero < 0) {
            throw new SalarioDeveSerNaoNegativoException();
        }

        String idGerado = java.util.UUID.randomUUID().toString();
        Empregado novoEmpregado = switch (tipoConvertido) {
            case HORISTA -> new Horista(
                    idGerado, nome, endereco, salarioNumero, false);
            case ASSALARIADO -> new Assalariado(
                    idGerado, nome, endereco, salarioNumero, false);
            case COMISSIONADO -> throw new TipoNaoAplicavelException();
        };

        repositorioEmpregados.adicionar(novoEmpregado);
        return idGerado;
    }

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario,
            String comissao) throws Exception {

        if (nome == null || nome.isEmpty()) {
            throw new NomeNaoPodeSerNuloException();
        }
        if (endereco == null || endereco.isEmpty()) {
            throw new EnderecoNaoPodeSerNuloException();
        }
        TipoEmpregado tipoConvertido = converterTipo(tipo);
        if (tipoConvertido == TipoEmpregado.HORISTA
                || tipoConvertido == TipoEmpregado.ASSALARIADO) {
            throw new TipoNaoAplicavelException();
        }
        if (salario == null || salario.isEmpty()) {
            throw new SalarioNaoPodeSerNuloException();
        }

        double salarioNumero;
        try {
            salarioNumero = Double.parseDouble(salario.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new SalarioDeveSerNumericoException();
        }

        if (salarioNumero < 0) {
            throw new SalarioDeveSerNaoNegativoException();
        }

        if (comissao == null || comissao.isEmpty()) {
            throw new ComissaoNaoPodeSerNulaException();
        }

        double comissaoNumero;
        try {
            comissaoNumero = Double.parseDouble(comissao.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ComissaoDeveSerNumericaException();
        }

        if (comissaoNumero < 0) {
            throw new ComissaoDeveSerNaoNegativaException();
        }

        String idGerado = java.util.UUID.randomUUID().toString();
        Empregado novoEmpregado = switch (tipoConvertido) {
            case HORISTA -> throw new TipoNaoAplicavelException();
            case ASSALARIADO -> throw new TipoNaoAplicavelException();
            case COMISSIONADO -> new Comissionado(
                    idGerado, nome, endereco, salarioNumero, comissaoNumero, false);
        };

        repositorioEmpregados.adicionar(novoEmpregado);
        return idGerado;
    }

    public void alterarTipo(Empregado empregado, String tipo) throws Exception {
        TipoEmpregado tipoConvertido = converterTipo(tipo);
        Empregado novo = criarComTipo(
                empregado, tipoConvertido, empregado.getSalario(), 0.0);
        substituir(empregado, novo);
    }

    public void alterarTipo(
            Empregado empregado, String tipo, String valorExtra) throws Exception {
        TipoEmpregado tipoConvertido = converterTipo(tipo);
        ValoresAlteracao valores = switch (tipoConvertido) {
            case HORISTA, ASSALARIADO -> new ValoresAlteracao(
                    converterSalarioAlteracao(valorExtra), 0.0);
            case COMISSIONADO -> new ValoresAlteracao(
                    empregado.getSalario(), converterComissaoAlteracao(valorExtra));
        };

        Empregado novo = criarComTipo(
                empregado, tipoConvertido, valores.salario(), valores.comissao());
        substituir(empregado, novo);
    }

    private double converterSalarioAlteracao(String valor) throws Exception {
        if (valor == null || valor.isEmpty()) {
            throw new SalarioNaoPodeSerNuloException();
        }
        double salario;
        try {
            salario = Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new SalarioDeveSerNumericoException();
        }
        if (salario < 0) {
            throw new SalarioDeveSerNaoNegativoException();
        }
        return salario;
    }

    private double converterComissaoAlteracao(String valor) throws Exception {
        if (valor == null || valor.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ComissaoDeveSerNumericaException();
        }
    }

    private TipoEmpregado converterTipo(String tipo) throws TipoInvalidoException {
        try {
            return TipoEmpregado.deTexto(tipo);
        } catch (IllegalArgumentException e) {
            throw new TipoInvalidoException();
        }
    }

    private Empregado criarComTipo(
            Empregado atual, TipoEmpregado tipo, double salario, double comissao) {
        Empregado novo = switch (tipo) {
            case HORISTA -> new Horista(
                    atual.getId(), atual.getNome(), atual.getEndereco(),
                    salario, atual.isSindicalizado());
            case ASSALARIADO -> new Assalariado(
                    atual.getId(), atual.getNome(), atual.getEndereco(),
                    salario, atual.isSindicalizado());
            case COMISSIONADO -> new Comissionado(
                    atual.getId(), atual.getNome(), atual.getEndereco(),
                    salario, comissao, atual.isSindicalizado());
        };

        novo.setMetodoPagamento(atual.getMetodoPagamento());
        novo.setBanco(atual.getBanco());
        novo.setAgencia(atual.getAgencia());
        novo.setContaCorrente(atual.getContaCorrente());
        return novo;
    }

    private void substituir(Empregado atual, Empregado novo) {
        int indice = repositorioEmpregados.indicePorId(atual.getId());
        repositorioEmpregados.substituir(indice, novo);
    }

    private record ValoresAlteracao(double salario, double comissao) {
    }
}
