package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.persistencia.RepositorioDadosXml;
import java.util.ArrayList;
import java.util.List;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioFolhas;
import br.ufal.ic.p2.wepayu.servicos.ServicoEmpregados;
import br.ufal.ic.p2.wepayu.servicos.ServicoFolhaPagamento;

public class Facade {

    private final RepositorioDadosXml repositorioDadosXml =
            new RepositorioDadosXml();
    private final RepositorioEmpregados repositorioEmpregados =
            new RepositorioEmpregados();
    private final RepositorioFolhas repositorioFolhas =
            new RepositorioFolhas();
    private final ServicoEmpregados servicoEmpregados =
            new ServicoEmpregados(repositorioEmpregados);
    private final ServicoFolhaPagamento servicoFolhaPagamento =
            new ServicoFolhaPagamento(repositorioEmpregados, repositorioFolhas);

    public Facade() throws Exception {
        repositorioDadosXml.carregarDados(
                repositorioEmpregados.listarTodos(), repositorioFolhas.listarTodas());
    }


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

    private Estado capturarEstadoAtual() {
        return new Estado(
                clonarLista(repositorioEmpregados.listarTodos()),
                new java.util.HashMap<>(repositorioFolhas.listarTodas()));
    }

    private void restaurarEstado(Estado e) {
        repositorioEmpregados.substituirTodos(e.listaEmpregados);
        repositorioFolhas.substituirTodas(e.folhasGeradas);
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

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        String idGerado =
                servicoEmpregados.criarEmpregado(nome, endereco, tipo, salario);

        salvarEstadoParaUndo(estadoAntes);
        return idGerado;
    }

    public String criarEmpregado(
            String nome, String endereco, String tipo, String salario,
            String comissao) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        String idGerado =
                servicoEmpregados.criarEmpregado(
                        nome, endereco, tipo, salario, comissao);

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
                && !funcionarioAtual.isSindicalizado()) {
            throw new EmpregadoNaoEhSindicalizado();
        }

        if (atributo.equals("nome")) {
            return funcionarioAtual.getNome();
        } else if (atributo.equals("endereco")) {
            return funcionarioAtual.getEndereco();
        } else if (atributo.equals("tipo")) {
            return funcionarioAtual.getTipo().paraTexto();
        } else if (atributo.equals("salario")) {
            return String.format("%.2f", funcionarioAtual.getSalario()).replace(".", ",");
        } else if (atributo.equals("sindicalizado")) {
            return funcionarioAtual.getSindicalizado();
        } else if (atributo.equals("comissao")) {
            funcionarioAtual.validarComissao();
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

        empregadoEncontrado.validarLancamentoCartao();

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

        empregadoEncontrado.lancarCartao(data, horasNumero);
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

    public String getHorasNormaisTrabalhadas(
            String idEmp, String dataInicial, String dataFinal) throws Exception {
        if (idEmp == null || idEmp.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado =
                repositorioEmpregados.buscarPorId(idEmp);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        empregadoEncontrado.validarConsultaHoras();

        validarDataInicial(dataInicial);
        validarDataFinal(dataFinal);

        if (isDataPosterior(dataInicial, dataFinal)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }

        double horas =
                empregadoEncontrado.getHorasNormais(dataInicial, dataFinal);

        if (horas == (int) horas) {
            return String.valueOf((int) horas);
        }
        return String.format("%.1f", horas).replace(".", ",");
    }

    public String getHorasExtrasTrabalhadas(
            String idEmp, String dataInicial, String dataFinal) throws Exception {
        if (idEmp == null || idEmp.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado =
                repositorioEmpregados.buscarPorId(idEmp);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        empregadoEncontrado.validarConsultaHoras();

        validarDataInicial(dataInicial);
        validarDataFinal(dataFinal);

        if (isDataPosterior(dataInicial, dataFinal)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }

        double horas =
                empregadoEncontrado.getHorasExtras(dataInicial, dataFinal);

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

        empregadoEncontrado.validarLancamentoVenda();

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

        empregadoEncontrado.adicionarVenda(data, valorNumero);
        salvarEstadoParaUndo(estadoAntes);
    }

    private java.time.LocalDate parseData(String data) {
        String[] partes = data.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int ano = Integer.parseInt(partes[2]);
        return java.time.LocalDate.of(ano, mes, dia);
    }

    public String getVendasRealizadas(
            String idEmpregado, String dataInicial, String dataFinal) throws Exception {
        if (idEmpregado == null || idEmpregado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

        Empregado empregadoEncontrado =
                repositorioEmpregados.buscarPorId(idEmpregado);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        empregadoEncontrado.validarConsultaVendas();

        validarDataInicial(dataInicial);
        validarDataFinal(dataFinal);

        if (isDataPosterior(dataInicial, dataFinal)) {
            throw new DataInicialNaoPodeSerPosteriorException();
        }

        java.time.LocalDate inicio = parseData(dataInicial);
        java.time.LocalDate fim = parseData(dataFinal);

        double totalVendas =
                empregadoEncontrado.calcularVendasRealizadas(inicio, fim);

        return String.format("%.2f", totalVendas).replace(".", ",");
    }

    public void alteraEmpregado(String idEmpregado, String atributo, String valor) throws Exception {
        verificarSistemaAtivo();
        Estado estadoAntes = capturarEstadoAtual();

        if (idEmpregado == null || idEmpregado.isEmpty()) {
            throw new IdentificacaoEmpregadoNaoPodeSerNulaException();
        }

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
            servicoEmpregados.alterarTipo(empregadoEncontrado, valor);

        } else if (atributo.equals("salario")) {
            if (valor == null || valor.isEmpty()) throw new SalarioNaoPodeSerNuloException();

            double salarioNum;
            try {
                salarioNum = Double.parseDouble(valor.replace(",", "."));
            } catch (NumberFormatException e) {
                throw new SalarioDeveSerNumericoException();
            }
            if (salarioNum < 0) throw new SalarioDeveSerNaoNegativoException();


            empregadoEncontrado.setSalario(salarioNum);

        } else if (atributo.equals("comissao")) {
            if (valor == null || valor.isEmpty()) {
                throw new ComissaoNaoPodeSerNulaException();
            }
            empregadoEncontrado.validarComissao();
            double comissaoNum;
            try {
                comissaoNum = Double.parseDouble(valor.replace(",", "."));
            } catch (NumberFormatException e) {
                throw new ComissaoDeveSerNumericaException();
            }
            if (comissaoNum < 0) throw new ComissaoDeveSerNaoNegativaException();

            empregadoEncontrado.setComissao(comissaoNum);
        } else if (atributo.equals("metodoPagamento")) {
            if (valor == null || (!valor.equals("emMaos") && !valor.equals("banco") && !valor.equals("correios"))) {
                throw new MetodoDePagamentoInvalido();
            }
            empregadoEncontrado.setMetodoPagamento(valor);
        } else if (atributo.equals("sindicalizado")) {
            if (valor == null || (!valor.equals("true") && !valor.equals("false"))) {
                throw new ValorDeveSerTruOuFalse();
            }
            empregadoEncontrado.setSindicalizado(Boolean.parseBoolean(valor));
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

        Empregado empregadoEncontrado = repositorioEmpregados.buscarPorId(idEmpregado);

        if (empregadoEncontrado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (atributo.equals("tipo")) {
            servicoEmpregados.alterarTipo(
                    empregadoEncontrado, valor, valorExtra);
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
            if (valor == null || (!valor.equals("true") && !valor.equals("false"))) {
                throw new ValorDeveSerTruOuFalse();
            }

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

                empregadoEncontrado.setSindicalizado(true);
                empregadoEncontrado.setIdSindicato(idSindicato);
                empregadoEncontrado.setTaxaSindical(taxaNum);
            } else {
                empregadoEncontrado.setSindicalizado(false);
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

        if (!empregadoEncontrado.isSindicalizado()) {
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
        Estado estadoAntes = capturarEstadoAtual();
        if (servicoFolhaPagamento.rodaFolha(data, saida)) {
            salvarEstadoParaUndo(estadoAntes);
        }
    }

    public String totalFolha(String data) throws Exception {
        return servicoFolhaPagamento.totalFolha(data);
    }
    public void encerrarSistema() throws Exception {
        repositorioDadosXml.salvarDados(
                repositorioEmpregados.listarTodos(), repositorioFolhas.listarTodas());
        sistemaEncerrado = true;
    }

    public void zerarSistema() {
        Estado estadoAntes = capturarEstadoAtual();
        repositorioEmpregados.limpar();
        repositorioFolhas.limpar();
        sistemaEncerrado = false;
        salvarEstadoParaUndo(estadoAntes);
    }

}
