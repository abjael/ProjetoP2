package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Horista;
import br.ufal.ic.p2.wepayu.models.Assalariado;
import br.ufal.ic.p2.wepayu.models.Comissionado;
import br.ufal.ic.p2.wepayu.models.Venda;

import java.util.ArrayList;
import java.util.List;

public class Facade {

    private static List<Empregado> listaEmpregados = new ArrayList<>();

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        if (nome == null || nome.isEmpty()) {
            throw new NomeNaoPodeSerNuloException();
        }
        if (endereco == null || endereco.isEmpty()) {
            throw new EnderecoNaoPodeSerNuloException();
        }
        if (tipo == null || (!tipo.equals("horista") && !tipo.equals("assalariado") && !tipo.equals("comissionado"))) {
            throw new TipoInvalidoException();
        }
        if (tipo.equals("comissionado")) {
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

        Empregado novoFuncionario;
        if (tipo.equals("horista")) {
            novoFuncionario = new Horista(idGerado, nome, endereco, tipo, salarioNumero, "false");
        } else {
            novoFuncionario = new Assalariado(idGerado, nome, endereco, tipo, salarioNumero, "false");
        }

        listaEmpregados.add(novoFuncionario);
        return idGerado;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        if (nome == null || nome.isEmpty()) {
            throw new NomeNaoPodeSerNuloException();
        }
        if (endereco == null || endereco.isEmpty()) {
            throw new EnderecoNaoPodeSerNuloException();
        }
        if (tipo == null || (!tipo.equals("horista") && !tipo.equals("assalariado") && !tipo.equals("comissionado"))) {
            throw new TipoInvalidoException();
        }
        if (tipo.equals("horista") || tipo.equals("assalariado")) {
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
        Empregado novoEmpregado = new Comissionado(idGerado, nome, endereco, tipo, salarioNumero, comissaoNumero, "false");

        listaEmpregados.add(novoEmpregado);
        return idGerado;
    }

    public String getAtributoEmpregado(String idBuscado, String atributo) throws Exception {
        if (idBuscado == null || idBuscado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        boolean atributoExiste = atributo.equals("nome") || atributo.equals("endereco") ||
                atributo.equals("tipo") || atributo.equals("salario") ||
                atributo.equals("sindicalizado") || atributo.equals("comissao") ||
                atributo.equals("horasTrabalhadas");

        for (int i = 0; i < listaEmpregados.size(); i++) {
            Empregado funcionarioAtual = listaEmpregados.get(i);

            if (funcionarioAtual.getId().equals(idBuscado)) {
                if (!atributoExiste) {
                    throw new AtributoNaoExisteException();
                }

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
                } else if (atributo.equals("horasTrabalhadas")) {
                    return funcionarioAtual.getHorasTrabalhadas();
                }
            }
        }

        if (!atributoExiste) {
            throw new AtributoNaoExisteException();
        }

        throw new EmpregadoNaoExisteException();
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        if (nome == null || nome.isEmpty()) {
            throw new NomeNaoPodeSerNuloException();
        }

        int contador = 0;
        for (int i = 0; i < listaEmpregados.size(); i++) {
            Empregado funcionarioAtual = listaEmpregados.get(i);

            if (funcionarioAtual.getNome().equals(nome)) {
                contador++;
                if (contador == indice) {
                    return funcionarioAtual.getId();
                }
            }
        }

        throw new NaoHaEmpregadoComEsseNomeException();
    }

    public void removerEmpregado(String idBuscado) throws Exception {
        if (idBuscado == null || idBuscado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        for (int i = 0; i < listaEmpregados.size(); i++) {
            Empregado funcionarioAtual = listaEmpregados.get(i);
            if (funcionarioAtual.getId().equals(idBuscado)) {
                listaEmpregados.remove(i);
                return;
            }
        }

        throw new EmpregadoNaoExisteException();
    }

    public void lancaCartao(String idEmp, String data, String horas) throws Exception {
        if (idEmp == null || idEmp.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado = null;
        for (int i = 0; i < listaEmpregados.size(); i++) {
            Empregado funcionarioAtual = listaEmpregados.get(i);
            if (funcionarioAtual.getId().equals(idEmp)) {
                empregadoEncontrado = funcionarioAtual;
                break;
            }
        }

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getTipo().equals("horista")) {
            throw new RuntimeException("Empregado nao eh horista.");
        }

        validarData(data);

        if (horas == null || horas.isEmpty()) {
            throw new HorasNaoPodemSerNulasException();
        }

        double horasNumero;
        try {
            horasNumero = Double.parseDouble(horas.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new HorasDevemSerNumericasException();
        }

        if (horasNumero <= 0) {
            throw new HorasDevemSerPositivasException();
        }

        Horista horista = (Horista) empregadoEncontrado;
        horista.lancarCartao(data, horasNumero);
    }

    private void validarData(String data) throws Exception {
        if (data == null || data.isEmpty()) {
            throw new DataNaoPodeSerNulaException();
        }
        String[] partes = data.split("/");
        if (partes.length != 3) {
            throw new DataInvalidaException();
        }
        try {
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int ano = Integer.parseInt(partes[2]);

            java.time.LocalDate.of(ano, mes, dia);
        } catch (Exception e) {
            throw new DataInvalidaException();
        }
    }

    private void validarDataInicial(String data) throws Exception {
        if (data == null || data.isEmpty()) {
            throw new DataInicialNaoPodeSerNulaException();
        }
        String[] partes = data.split("/");
        if (partes.length != 3) {
            throw new DataInicialInvalidaException();
        }
        try {
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int ano = Integer.parseInt(partes[2]);

            java.time.LocalDate.of(ano, mes, dia);
        } catch (Exception e) {
            throw new DataInicialInvalidaException();
        }
    }

    private void validarDataFinal(String data) throws Exception {
        if (data == null || data.isEmpty()) {
            throw new DataFinalNaoPodeSerNulaException();
        }
        String[] partes = data.split("/");
        if (partes.length != 3) {
            throw new DataFinalInvalidaException();
        }
        try {
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int ano = Integer.parseInt(partes[2]);

            java.time.LocalDate.of(ano, mes, dia);
        } catch (Exception e) {
            throw new DataFinalInvalidaException();
        }
    }

    private boolean isDataPosterior(String d1, String d2) {
        String[] p1 = d1.split("/");
        String[] p2 = d2.split("/");

        int dia1 = Integer.parseInt(p1[0]), mes1 = Integer.parseInt(p1[1]), ano1 = Integer.parseInt(p1[2]);
        int dia2 = Integer.parseInt(p2[0]), mes2 = Integer.parseInt(p2[1]), ano2 = Integer.parseInt(p2[2]);

        java.time.LocalDate data1 = java.time.LocalDate.of(ano1, mes1, dia1);
        java.time.LocalDate data2 = java.time.LocalDate.of(ano2, mes2, dia2);

        return data1.isAfter(data2);
    }

    public String getHorasNormaisTrabalhadas(String idEmp, String dataInicial, String dataFinal) throws Exception {
        if (idEmp == null || idEmp.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado = null;
        for (int i = 0; i < listaEmpregados.size(); i++) {
            Empregado funcionarioAtual = listaEmpregados.get(i);
            if (funcionarioAtual.getId().equals(idEmp)) {
                empregadoEncontrado = funcionarioAtual;
                break;
            }
        }

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getTipo().equals("horista")) {
            throw new RuntimeException("Empregado nao eh horista.");
        }

        validarDataInicial(dataInicial);
        validarDataFinal(dataFinal);

        if (isDataPosterior(dataInicial, dataFinal)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }

        Horista horista = (Horista) empregadoEncontrado;
        double horas = horista.getHorasNormais(dataInicial, dataFinal);

        if (horas == (int) horas) {
            return String.valueOf((int) horas);
        }
        return String.format("%.1f", horas).replace(".", ",");
    }

    public String getHorasExtrasTrabalhadas(String idEmp, String dataInicial, String dataFinal) throws Exception {
        if (idEmp == null || idEmp.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado = null;
        for (int i = 0; i < listaEmpregados.size(); i++) {
            Empregado funcionarioAtual = listaEmpregados.get(i);
            if (funcionarioAtual.getId().equals(idEmp)) {
                empregadoEncontrado = funcionarioAtual;
                break;
            }
        }

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getTipo().equals("horista")) {
            throw new RuntimeException("Empregado nao eh horista.");
        }

        validarDataInicial(dataInicial);
        validarDataFinal(dataFinal);

        if (isDataPosterior(dataInicial, dataFinal)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }

        Horista horista = (Horista) empregadoEncontrado;
        double horas = horista.getHorasExtras(dataInicial, dataFinal);

        if (horas == (int) horas) {
            return String.valueOf((int) horas);
        }
        return String.format("%.1f", horas).replace(".", ",");
    }
    public void lancaVenda(String idBuscado, String data, String valor) throws Exception {
        if (idBuscado == null || idBuscado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado = null;
        for (int i = 0; i < listaEmpregados.size(); i++) {
            Empregado funcionarioAtual = listaEmpregados.get(i);
            if (funcionarioAtual.getId().equals(idBuscado)) {
                empregadoEncontrado = funcionarioAtual;
                break;
            }
        }

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getTipo().equals("comissionado")) {
            throw new RuntimeException("Empregado nao eh comissionado.");
        }

        validarData(data);

        if (valor == null || valor.isEmpty()) {
            throw new ValorNaoPodeSerNuloException();
        }

        double valorNumero;
        try {
            valorNumero = Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValorDeveSerNumericoException();
        }

        if (valorNumero <= 0) {
            throw new ValorDeveSerPositivoException();
        }

        Comissionado comissionado = (Comissionado) empregadoEncontrado;
        comissionado.adicionarVenda(data, valorNumero);
    }
    private java.time.LocalDate parseData(String data) {
        String[] partes = data.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int ano = Integer.parseInt(partes[2]);
        return java.time.LocalDate.of(ano, mes, dia);
    }

    public String getVendasRealizadas(String idEmpregado, String dataInicial, String dataFinal) throws Exception {
        if (idEmpregado == null || idEmpregado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado = null;
        for (int i = 0; i < listaEmpregados.size(); i++) {
            Empregado funcionarioAtual = listaEmpregados.get(i);
            if (funcionarioAtual.getId().equals(idEmpregado)) {
                empregadoEncontrado = funcionarioAtual;
                break;
            }
        }

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getTipo().equals("comissionado")) {
            throw new RuntimeException("Empregado nao eh comissionado.");
        }

        validarDataInicial(dataInicial);
        validarDataFinal(dataFinal);

        if (isDataPosterior(dataInicial, dataFinal)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }

        Comissionado comissionado = (Comissionado) empregadoEncontrado;
        double totalVendas = 0.0;

        java.time.LocalDate inicio = parseData(dataInicial);
        java.time.LocalDate fim = parseData(dataFinal);

        for (Venda venda : comissionado.getVendas()) {
            java.time.LocalDate dataVenda = parseData(venda.getData());
            if (!dataVenda.isBefore(inicio) && dataVenda.isBefore(fim)) {
                totalVendas += venda.getValor();
            }
        }

        return String.format("%.2f", totalVendas).replace(".", ",");
    }

    public void zerarSistema() {
        Facade.listaEmpregados.clear();
    }

    public void encerrarSistema() {
    }
}