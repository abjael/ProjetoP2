package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.persistencia.RepositorioDadosXml;
import java.util.ArrayList;
import java.util.List;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;

public class Facade {

    private final RepositorioDadosXml repositorioDadosXml =
            new RepositorioDadosXml();

    public Facade() throws Exception {
        repositorioDadosXml.carregarDados(listaEmpregados, folhasGeradas);
    }
    private final RepositorioEmpregados repositorioEmpregados =
            new RepositorioEmpregados(listaEmpregados);


    private static List<Empregado> listaEmpregados = new ArrayList<>();
    private static java.util.Map<String, String> folhasGeradas = new java.util.HashMap<>();
    private boolean sistemaEncerrado = false;

    private java.util.Deque<Estado> pilhaUndo = new java.util.ArrayDeque<>();
    private java.util.Deque<Estado> pilhaRedo = new java.util.ArrayDeque<>();

    private static class Estado {
        List<Empregado> listaEmpregados;
        java.util.Map<String, String> folhasGeradas;
        Estado(List<Empregado> lista, java.util.Map<String, String> folhas) {
            this.listaEmpregados = lista;
            this.folhasGeradas = folhas;
        }
    }

    private static List<Empregado> clonarLista(List<Empregado> original) {
        List<Empregado> copia = new ArrayList<>();
        for (Empregado e : original) {
            copia.add(e.clonar());
        }
        return copia;
    }

    private static Estado capturarEstadoAtual() {
        return new Estado(clonarLista(listaEmpregados), new java.util.HashMap<>(folhasGeradas));
    }

    private static void restaurarEstado(Estado e) {
        listaEmpregados.clear();
        listaEmpregados.addAll(e.listaEmpregados);
        folhasGeradas.clear();
        folhasGeradas.putAll(e.folhasGeradas);
    }

    private void salvarEstadoParaUndo(Estado estadoAntes) {
        pilhaUndo.push(estadoAntes);
        pilhaRedo.clear();
    }

    private void verificarSistemaAtivo() throws Exception {
        if (sistemaEncerrado) {
            throw new SistemEncerrado();
        }
    }

    public int getNumeroDeEmpregados() throws Exception {
        verificarSistemaAtivo();
        return repositorioEmpregados.listarTodos().size();
    }

    public void undo() throws Exception {
        verificarSistemaAtivo();
        if (pilhaUndo.isEmpty()) {
            throw new  NaoHaComandoParaDesfazer();
        }
        Estado estadoAtual = capturarEstadoAtual();
        pilhaRedo.push(estadoAtual);
        restaurarEstado(pilhaUndo.pop());
    }

    public void redo() throws Exception {
        verificarSistemaAtivo();
        if (pilhaRedo.isEmpty()) {
            throw new NaoHaComandoParaRefazer();
        }
        Estado estadoAtual = capturarEstadoAtual();
        pilhaUndo.push(estadoAtual);
        restaurarEstado(pilhaRedo.pop());
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

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

        repositorioEmpregados.adicionar(novoFuncionario);
        salvarEstadoParaUndo(estadoAntes);
        return idGerado;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

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

        repositorioEmpregados.adicionar(novoEmpregado);
        salvarEstadoParaUndo(estadoAntes);
        return idGerado;
    }

    public String getAtributoEmpregado(String idBuscado, String atributo) throws Exception {
        if (idBuscado == null || idBuscado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado funcionarioAtual = repositorioEmpregados.buscarPorId(idBuscado);

        if (funcionarioAtual == null) {
            throw new EmpregadoNaoExisteException();
        }

        boolean atributoExiste = atributo.equals("nome") || atributo.equals("endereco") ||
                atributo.equals("tipo") || atributo.equals("salario") ||
                atributo.equals("sindicalizado") || atributo.equals("comissao") ||
                atributo.equals("horasTrabalhadas") || atributo.equals("metodoPagamento") ||
                atributo.equals("banco") || atributo.equals("agencia") || atributo.equals("contaCorrente") ||
                atributo.equals("idSindicato") || atributo.equals("taxaSindical");

        if (!atributoExiste) {
            throw new AtributoNaoExisteException();
        }

        boolean ehAtributoBanco = atributo.equals("banco") || atributo.equals("agencia") || atributo.equals("contaCorrente");
        if (ehAtributoBanco && !funcionarioAtual.getMetodoPagamento().equals("banco")) {
            throw new EmpregadoNaoRecebeEmBanco();
        }

        if ((atributo.equals("idSindicato") || atributo.equals("taxaSindical"))
                && !funcionarioAtual.getSindicalizado().equals("true")) {
            throw new EmpregadoNaoEhSindicalizado();
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
            if (!funcionarioAtual.getTipo().equals("comissionado")) {
                throw new EmpregadoNaoEhComissionado();
            }
            return String.format("%.2f", funcionarioAtual.getComissao()).replace(".", ",");
        } else if (atributo.equals("horasTrabalhadas")) {
            return funcionarioAtual.getHorasTrabalhadas();
        } else if (atributo.equals("metodoPagamento")) {
            return funcionarioAtual.getMetodoPagamento();
        } else if (atributo.equals("banco")) {
            return funcionarioAtual.getBanco();
        } else if (atributo.equals("agencia")) {
            return funcionarioAtual.getAgencia();
        } else if (atributo.equals("contaCorrente")) {
            return funcionarioAtual.getContaCorrente();
        } else if (atributo.equals("idSindicato")) {
            return funcionarioAtual.getIdSindicato();
        } else if (atributo.equals("taxaSindical")) {
            return String.format("%.2f", funcionarioAtual.getTaxaSindical()).replace(".", ",");
        }

        return "";
    }


    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        if (nome == null || nome.isEmpty()) {
            throw new NomeNaoPodeSerNuloException();
        }

        String id = repositorioEmpregados.buscarIdPorNome(nome, indice);
        if (id == null) {
            throw new NaoHaEmpregadoComEsseNomeException();
        }
        return id;
    }

    public void removerEmpregado(String idBuscado) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        if (idBuscado == null || idBuscado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        int indice = repositorioEmpregados.indicePorId(idBuscado);

        if (indice == -1) {
            throw new EmpregadoNaoExisteException();
        }

        repositorioEmpregados.remover(indice);
        salvarEstadoParaUndo(estadoAntes);
    }

    public void lancaCartao(String idEmp, String data, String horas) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        if (idEmp == null || idEmp.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idEmp);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getTipo().equals("horista")) {
            throw new EmpregadoNaoEhHoristaException();
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
        salvarEstadoParaUndo(estadoAntes);
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

        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idEmp);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getTipo().equals("horista")) {
            throw new EmpregadoNaoEhHoristaException();
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

        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idEmp);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getTipo().equals("horista")) {
            throw new EmpregadoNaoEhHoristaException();
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
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        if (idBuscado == null || idBuscado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idBuscado);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getTipo().equals("comissionado")) {
            throw new EmpregadoNaoEhComissionado();
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
        salvarEstadoParaUndo(estadoAntes);
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

        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idEmpregado);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getTipo().equals("comissionado")) {
            throw new EmpregadoNaoEhComissionado();
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

    public void alteraEmpregado(String idEmpregado, String atributo, String valor) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        if (idEmpregado == null || idEmpregado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        int indiceEmpregado = repositorioEmpregados.indicePorId(idEmpregado);
        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idEmpregado);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (atributo.equals("nome")) {
            if (valor == null || valor.isEmpty()) throw new NomeNaoPodeSerNuloException();
            empregadoEncontrado.setNome(valor);
        } else if (atributo.equals("endereco")) {
            if (valor == null || valor.isEmpty()) throw new EnderecoNaoPodeSerNuloException();
            empregadoEncontrado.setEndereco(valor);
        } else if (atributo.equals("tipo")) {
            if (valor == null || (!valor.equals("horista") && !valor.equals("assalariado") && !valor.equals("comissionado"))) {
                throw new TipoInvalidoException();
            }

            double salarioAtual = empregadoEncontrado.getSalario();
            Empregado novo;
            if (valor.equals("horista")) {
                novo = new Horista(empregadoEncontrado.getId(), empregadoEncontrado.getNome(), empregadoEncontrado.getEndereco(), valor, salarioAtual, empregadoEncontrado.getSindicalizado());
            } else if (valor.equals("assalariado")) {
                novo = new Assalariado(empregadoEncontrado.getId(), empregadoEncontrado.getNome(), empregadoEncontrado.getEndereco(), valor, salarioAtual, empregadoEncontrado.getSindicalizado());
            } else {
                novo = new Comissionado(empregadoEncontrado.getId(), empregadoEncontrado.getNome(), empregadoEncontrado.getEndereco(), valor, salarioAtual, 0.0, empregadoEncontrado.getSindicalizado());
            }
            novo.setMetodoPagamento(empregadoEncontrado.getMetodoPagamento());
            novo.setBanco(empregadoEncontrado.getBanco());
            novo.setAgencia(empregadoEncontrado.getAgencia());
            novo.setContaCorrente(empregadoEncontrado.getContaCorrente());
            repositorioEmpregados.substituir(indiceEmpregado, novo);

        } else if (atributo.equals("salario")) {
            if (valor == null || valor.isEmpty()) throw new SalarioNaoPodeSerNuloException();

            double salarioNum;
            try {
                salarioNum = Double.parseDouble(valor.replace(",", "."));
            } catch (NumberFormatException e) {
                throw new SalarioDeveSerNumericoException();
            }
            if (salarioNum < 0) throw new SalarioDeveSerNaoNegativoException();


            Empregado novo;
            String tipoAtual = empregadoEncontrado.getTipo();
            if (tipoAtual.equals("horista")) {
                novo = new Horista(empregadoEncontrado.getId(), empregadoEncontrado.getNome(), empregadoEncontrado.getEndereco(), tipoAtual, salarioNum, empregadoEncontrado.getSindicalizado());
            } else if (tipoAtual.equals("assalariado")) {
                novo = new Assalariado(empregadoEncontrado.getId(), empregadoEncontrado.getNome(), empregadoEncontrado.getEndereco(), tipoAtual, salarioNum, empregadoEncontrado.getSindicalizado());
            } else {
                Comissionado c = (Comissionado) empregadoEncontrado;
                novo = new Comissionado(empregadoEncontrado.getId(), empregadoEncontrado.getNome(), empregadoEncontrado.getEndereco(), tipoAtual, salarioNum, c.getComissao(), empregadoEncontrado.getSindicalizado());
            }
            novo.setMetodoPagamento(empregadoEncontrado.getMetodoPagamento());
            novo.setBanco(empregadoEncontrado.getBanco());
            novo.setAgencia(empregadoEncontrado.getAgencia());
            novo.setContaCorrente(empregadoEncontrado.getContaCorrente());
            repositorioEmpregados.substituir(indiceEmpregado, novo);

        } else if (atributo.equals("comissao")) {
            if (valor == null || valor.isEmpty()) {
                throw new ComissaoNaoPodeSerNulaException();
            }
            if (!empregadoEncontrado.getTipo().equals("comissionado")) {
                throw new EmpregadoNaoEhComissionado();
            }
            double comissaoNum;
            try {
                comissaoNum = Double.parseDouble(valor.replace(",", "."));
            } catch (NumberFormatException e) {
                throw new ComissaoDeveSerNumericaException();
            }
            if (comissaoNum < 0) throw new ComissaoDeveSerNaoNegativaException();

            Comissionado comissionado = (Comissionado) empregadoEncontrado;
            comissionado.setComissao(comissaoNum);
        } else if (atributo.equals("metodoPagamento")) {
            if (valor == null || (!valor.equals("emMaos") && !valor.equals("banco") && !valor.equals("correios"))) {
                throw new MetodoDePagamentoInvalido();
            }
            empregadoEncontrado.setMetodoPagamento(valor);
        } else if (atributo.equals("sindicalizado")) {
            if (valor == null || (!valor.equals("true") && !valor.equals("false"))) {
                throw new ValorDeveSerTruOuFalse();
            }
            empregadoEncontrado.setSindicalizado(valor);
        } else {
            throw new AtributoNaoExisteException();
        }

        salvarEstadoParaUndo(estadoAntes);
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor, String valorExtra) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        if (idEmpregado == null || idEmpregado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        int indiceEmpregado = repositorioEmpregados.indicePorId(idEmpregado);
        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idEmpregado);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (atributo.equals("tipo")) {
            if (valor == null || (!valor.equals("horista") && !valor.equals("assalariado") && !valor.equals("comissionado"))) {
                throw new TipoInvalidoException();
            }

            if (valor.equals("horista") || valor.equals("assalariado")) {
                if (valorExtra == null || valorExtra.isEmpty()) {
                    throw new SalarioNaoPodeSerNuloException();
                }
                double salarioNum;
                try {
                    salarioNum = Double.parseDouble(valorExtra.replace(",", "."));
                } catch (NumberFormatException e) {
                    throw new SalarioDeveSerNumericoException();
                }
                if (salarioNum < 0) {
                    throw new SalarioDeveSerNaoNegativoException();
                }

                Empregado novo;
                if (valor.equals("horista")) {
                    novo = new Horista(empregadoEncontrado.getId(), empregadoEncontrado.getNome(), empregadoEncontrado.getEndereco(), valor, salarioNum, empregadoEncontrado.getSindicalizado());
                } else {
                    novo = new Assalariado(empregadoEncontrado.getId(), empregadoEncontrado.getNome(), empregadoEncontrado.getEndereco(), valor, salarioNum, empregadoEncontrado.getSindicalizado());
                }
                novo.setMetodoPagamento(empregadoEncontrado.getMetodoPagamento());
                novo.setBanco(empregadoEncontrado.getBanco());
                novo.setAgencia(empregadoEncontrado.getAgencia());
                novo.setContaCorrente(empregadoEncontrado.getContaCorrente());
                repositorioEmpregados.substituir(indiceEmpregado, novo);

            } else if (valor.equals("comissionado")) {
                double salarioAtual = empregadoEncontrado.getSalario();
                double comissaoNum = 0.0;
                if (valorExtra != null && !valorExtra.isEmpty()) {
                    try {
                        comissaoNum = Double.parseDouble(valorExtra.replace(",", "."));
                    } catch (NumberFormatException e) {
                        throw new ComissaoDeveSerNumericaException();
                    }
                }

                Comissionado novo = new Comissionado(empregadoEncontrado.getId(), empregadoEncontrado.getNome(), empregadoEncontrado.getEndereco(), valor, salarioAtual, comissaoNum, empregadoEncontrado.getSindicalizado());
                novo.setMetodoPagamento(empregadoEncontrado.getMetodoPagamento());
                novo.setBanco(empregadoEncontrado.getBanco());
                novo.setAgencia(empregadoEncontrado.getAgencia());
                novo.setContaCorrente(empregadoEncontrado.getContaCorrente());
                repositorioEmpregados.substituir(indiceEmpregado, novo);
            }
        }

        salvarEstadoParaUndo(estadoAntes);
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        if (idEmpregado == null || idEmpregado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idEmpregado);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (atributo.equals("sindicalizado")) {
            if (valor.equals("true")) {
                if (idSindicato == null || idSindicato.isEmpty()) {
                    throw new IdentificacaoDoSindicatoNaoPodeSerNula();
                }
                if (taxaSindical == null || taxaSindical.isEmpty()) {
                    throw new TaxaSIndicalNaoPodeSerNula();
                }

                double taxaNum;
                try {
                    taxaNum = Double.parseDouble(taxaSindical.replace(",", "."));
                } catch (NumberFormatException e) {
                    throw new TaxaSindicalDeveSerNumerica();
                }
                if (taxaNum < 0) {
                    throw new TaxaSindicalDeveSerNaoNegativa();
                }

                for (Empregado f : repositorioEmpregados.listarTodos()) {
                    if (f.getIdSindicato() != null && f.getIdSindicato().equals(idSindicato) && !f.getId().equals(idEmpregado)) {
                        throw new HaOutroEmpregadoComEstaIdentificacaoDeSindicato();
                    }
                }

                empregadoEncontrado.setSindicalizado(valor);
                empregadoEncontrado.setIdSindicato(idSindicato);
                empregadoEncontrado.setTaxaSindical(taxaNum);
            } else {
                empregadoEncontrado.setSindicalizado(valor);
                empregadoEncontrado.setIdSindicato(null);
            }
        }

        salvarEstadoParaUndo(estadoAntes);
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        if (idEmpregado == null || idEmpregado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idEmpregado);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (atributo.equals("metodoPagamento")) {
            if (valor1.equals("banco")) {
                if (banco == null || banco.isEmpty()) throw new BancoNaoPodeSerNulo();
                if (agencia == null || agencia.isEmpty()) throw new AgenciaNaoPodeSerNulo();
                if (contaCorrente == null || contaCorrente.isEmpty()) throw new ContaCorrenteNaoPodeSerNulo() ;

                empregadoEncontrado.setMetodoPagamento(valor1);
                empregadoEncontrado.setBanco(banco);
                empregadoEncontrado.setAgencia(agencia);
                empregadoEncontrado.setContaCorrente(contaCorrente);
            }
        }

        salvarEstadoParaUndo(estadoAntes);
    }

    public String getTaxasServico(String idEmpregado, String dataInicial, String dataFinal) throws Exception {
        if (idEmpregado == null || idEmpregado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idEmpregado);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (!empregadoEncontrado.getSindicalizado().equals("true")) {
            throw new EmpregadoNaoEhSindicalizado();
        }

        validarDataInicial(dataInicial);
        validarDataFinal(dataFinal);

        if (isDataPosterior(dataInicial, dataFinal)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }

        double totalTaxas = 0.0;
        java.time.LocalDate inicio = parseData(dataInicial);
        java.time.LocalDate fim = parseData(dataFinal);

        for (TaxaServico taxa : empregadoEncontrado.getTaxasServico()) {
            java.time.LocalDate dataTaxa = parseData(taxa.getData());


            if (!dataTaxa.isBefore(inicio) && dataTaxa.isBefore(fim)) {
                totalTaxas += taxa.getValor();
            }
        }

        return String.format("%.2f", totalTaxas).replace(".", ",");

    }

    public void lancaTaxaServico(String idSindicato, String data, String valor) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        if (idSindicato == null || idSindicato.isEmpty()) {
            throw new IdentificacaoDoMembroNaoPodeSerNula() ;
        }

        Empregado empregadoEncontrado = null;
        for (Empregado funcionarioAtual : repositorioEmpregados.listarTodos()) {

            if (funcionarioAtual.getIdSindicato().equals(idSindicato)) {
                empregadoEncontrado = funcionarioAtual;
                break;
            }
        }

        if (empregadoEncontrado == null) {
            throw new MembroNaoExiste();
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

        empregadoEncontrado.adicionarTaxaServico(data, valorNumero);
        salvarEstadoParaUndo(estadoAntes);
    }

    public void rodaFolha(String data, String saida) throws Exception {
        verificarSistemaAtivo();

        if (folhasGeradas.containsKey(data)) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(saida)) {
                writer.print(folhasGeradas.get(data));
            } catch (Exception e) {
                throw new ErroAoGerarArquivoDeFolha();
            }
            return;
        }
        validarData(data);
        if (saida == null || saida.isEmpty()) {
            throw new ArquivoDeSaidaNaoPodeSerNulo();
        }

        Estado estadoAntes = capturarEstadoAtual();

        java.time.LocalDate dataObj = parseData(data);
        String dataFormatada = dataObj.toString();

        StringBuilder conteudo = new StringBuilder();
        conteudo.append("FOLHA DE PAGAMENTO DO DIA ").append(dataFormatada).append("\n");
        conteudo.append("====================================\n");
        conteudo.append("\n");

        double totalFolhaGeral = 0.0;


        java.util.List<Empregado> horistasOrdenados = new java.util.ArrayList<>();
        java.util.List<Empregado> assalariadosOrdenados = new java.util.ArrayList<>();
        java.util.List<Empregado> comissionadosOrdenados = new java.util.ArrayList<>();
        for (Empregado emp : repositorioEmpregados.listarTodos()) {
            switch (emp.getTipo()) {
                case "horista": horistasOrdenados.add(emp); break;
                case "assalariado": assalariadosOrdenados.add(emp); break;
                case "comissionado": comissionadosOrdenados.add(emp); break;
            }
        }
        java.util.Comparator<Empregado> porNome = java.util.Comparator.comparing(Empregado::getNome);
        horistasOrdenados.sort(porNome);
        assalariadosOrdenados.sort(porNome);
        comissionadosOrdenados.sort(porNome);

        // =========================================================================
        // HORISTAS
        // =========================================================================
        conteudo.append("===============================================================================================================================\n");
        conteudo.append("===================== HORISTAS ================================================================================================\n");
        conteudo.append("===============================================================================================================================\n");
        conteudo.append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n");
        conteudo.append("==================================== ===== ===== ============= ========= =============== ======================================\n");

        double totalHoras = 0.0;
        double totalExtras = 0.0;
        double totalBrutoHoristas = 0.0;
        double totalDescontosHoristas = 0.0;
        double totalLiquidoHoristas = 0.0;

        for (Empregado emp : horistasOrdenados) {
            if (emp.ehDiaDePagamento(data)) {
                if (emp.getUltimaDataPagamento() != null && emp.getUltimaDataPagamento().equals(dataObj)) {
                    continue;
                }

                Horista horista = (Horista) emp;
                java.time.LocalDate dataInicio = emp.getUltimaDataPagamento().plusDays(1);
                String dataInicial = String.format("%02d/%02d/%d", dataInicio.getDayOfMonth(), dataInicio.getMonthValue(), dataInicio.getYear());

                double horas = horista.getHorasNormais(dataInicial, data);
                double extras = horista.getHorasExtras(dataInicial, data);
                double bruto = emp.calcularSalarioBruto(data);
                double liquido = emp.calcularSalarioLiquido(data);
                double descontos = bruto - liquido;

                totalHoras += horas;
                totalExtras += extras;
                totalBrutoHoristas += bruto;
                totalDescontosHoristas += descontos;
                totalLiquidoHoristas += liquido;
                totalFolhaGeral += bruto;

                String metodo = formatarMetodoPagamento(emp);


                String linhaNumeros = String.format(
                        "%-36s %5.0f %5.0f %13.2f %9.2f %15.2f ",
                        emp.getNome(), horas, extras, bruto, descontos, liquido
                ).replace(".", ",");
                conteudo.append(linhaNumeros).append(metodo).append("\n");

                if (bruto > 0) {
                    emp.setUltimaDataPagamento(dataObj);
                }
            }
        }

        conteudo.append("\n");
        conteudo.append(String.format(
                "TOTAL HORISTAS                       %5.0f %5.0f %13.2f %9.2f %15.2f\n",
                totalHoras, totalExtras, totalBrutoHoristas, totalDescontosHoristas, totalLiquidoHoristas
        ).replace(".", ","));
        conteudo.append("\n");

        // =========================================================================
        // ASSALARIADOS
        // =========================================================================
        conteudo.append("===============================================================================================================================\n");
        conteudo.append("===================== ASSALARIADOS ============================================================================================\n");
        conteudo.append("===============================================================================================================================\n");
        conteudo.append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n");
        conteudo.append("================================================ ============= ========= =============== ======================================\n");

        double totalBrutoAssalariados = 0.0;
        double totalDescontosAssalariados = 0.0;
        double totalLiquidoAssalariados = 0.0;

        for (Empregado emp : assalariadosOrdenados) {
            if (emp.ehDiaDePagamento(data)) {
                if (emp.getUltimaDataPagamento() != null && emp.getUltimaDataPagamento().equals(dataObj)) {
                    continue;
                }

                double bruto = emp.calcularSalarioBruto(data);
                double liquido = emp.calcularSalarioLiquido(data);
                double descontos = bruto - liquido;

                totalBrutoAssalariados += bruto;
                totalDescontosAssalariados += descontos;
                totalLiquidoAssalariados += liquido;
                totalFolhaGeral += bruto;

                String metodo = formatarMetodoPagamento(emp);

                String linhaNumeros = String.format(
                        "%-48s %13.2f %9.2f %15.2f ",
                        emp.getNome(), bruto, descontos, liquido
                ).replace(".", ",");
                conteudo.append(linhaNumeros).append(metodo).append("\n");

                emp.setUltimaDataPagamento(dataObj);
            }
        }

        conteudo.append("\n");
        conteudo.append(String.format(
                "TOTAL ASSALARIADOS                               %13.2f %9.2f %15.2f\n",
                totalBrutoAssalariados, totalDescontosAssalariados, totalLiquidoAssalariados
        ).replace(".", ","));
        conteudo.append("\n");

        // =========================================================================
        // COMISSIONADOS
        // =========================================================================
        conteudo.append("===============================================================================================================================\n");
        conteudo.append("===================== COMISSIONADOS ===========================================================================================\n");
        conteudo.append("===============================================================================================================================\n");
        conteudo.append("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n");
        conteudo.append("===================== ======== ======== ======== ============= ========= =============== ======================================\n");

        double totalFixo = 0.0;
        double totalVendas = 0.0;
        double totalComissao = 0.0;
        double totalBrutoComissionados = 0.0;
        double totalDescontosComissionados = 0.0;
        double totalLiquidoComissionados = 0.0;

        for (Empregado emp : comissionadosOrdenados) {
            if (emp.ehDiaDePagamento(data)) {
                if (emp.getUltimaDataPagamento() != null && emp.getUltimaDataPagamento().equals(dataObj)) {
                    continue;
                }

                Comissionado comissionado = (Comissionado) emp;
                java.time.LocalDate dataInicio = emp.getUltimaDataPagamento().plusDays(1);

                double vendas = 0.0;
                for (Venda venda : comissionado.getVendas()) {
                    java.time.LocalDate dataVenda = parseData(venda.getData());
                    if (!dataVenda.isBefore(dataInicio) && !dataVenda.isAfter(dataObj)) {
                        vendas += venda.getValor();
                    }
                }

                double comissao = Math.floor(vendas * emp.getComissao() * 100) / 100.0;
                double fixo = Math.floor(emp.getSalario() * 12.0 / 52.0 * 2.0 * 100) / 100.0;
                double bruto = emp.calcularSalarioBruto(data);
                double liquido = emp.calcularSalarioLiquido(data);
                double descontos = bruto - liquido;

                totalFixo += fixo;
                totalVendas += vendas;
                totalComissao += comissao;
                totalBrutoComissionados += bruto;
                totalDescontosComissionados += descontos;
                totalLiquidoComissionados += liquido;
                totalFolhaGeral += bruto;

                String metodo = formatarMetodoPagamento(emp);

                String linhaNumeros = String.format(
                        "%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f ",
                        emp.getNome(), fixo, vendas, comissao, bruto, descontos, liquido
                ).replace(".", ",");
                conteudo.append(linhaNumeros).append(metodo).append("\n");

                emp.setUltimaDataPagamento(dataObj);
            }
        }

        conteudo.append("\n");
        conteudo.append(String.format(
                "TOTAL COMISSIONADOS   %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f\n",
                totalFixo, totalVendas, totalComissao, totalBrutoComissionados, totalDescontosComissionados, totalLiquidoComissionados
        ).replace(".", ","));
        conteudo.append("\n");

        conteudo.append("TOTAL FOLHA: ").append(String.format("%.2f", totalFolhaGeral).replace(".", ",")).append("\n");

        folhasGeradas.put(data, conteudo.toString());
        salvarEstadoParaUndo(estadoAntes);

        try (java.io.PrintWriter writer = new java.io.PrintWriter(saida)) {
            writer.print(conteudo.toString());
        } catch (Exception e) {
            throw new ErroAoGerarArquivoDeFolha();
        }
    }

    private String formatarMetodoPagamento(Empregado emp) {
        String metodo = emp.getMetodoPagamento();
        if (metodo == null || metodo.equalsIgnoreCase("emMaos") || metodo.equalsIgnoreCase("em maos")) {
            return "Em maos";
        } else if (metodo.equalsIgnoreCase("correios")) {
            return "Correios, " + emp.getEndereco();
        } else if (metodo.equalsIgnoreCase("banco")) {
            return String.format("%s, Ag. %s CC %s", emp.getBanco(), emp.getAgencia(), emp.getContaCorrente());
        }
        return metodo;
    }

    public String totalFolha(String data) throws Exception {
        validarData(data);
        double totalGeral = 0.0;
        for (Empregado emp : repositorioEmpregados.listarTodos()) {
            if (emp.ehDiaDePagamento(data)) {
                totalGeral += emp.calcularSalarioBruto(data);
            }
        }
        return String.format("%.2f", totalGeral).replace(".", ",");
    }

    public void encerrarSistema() throws Exception {
        repositorioDadosXml.salvarDados(listaEmpregados, folhasGeradas);
        sistemaEncerrado = true;
    }

    public void zerarSistema() {
        Estado estadoAntes = capturarEstadoAtual();
        repositorioEmpregados.limpar();
        Facade.folhasGeradas.clear();
        sistemaEncerrado = false;
        salvarEstadoParaUndo(estadoAntes);
    }

}
