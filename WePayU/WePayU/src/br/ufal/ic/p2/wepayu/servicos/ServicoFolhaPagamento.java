package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioFolhas;
import br.ufal.ic.p2.wepayu.util.ValidadorData;

public class ServicoFolhaPagamento {
    private final RepositorioEmpregados repositorioEmpregados;
    private final RepositorioFolhas repositorioFolhas;

    public ServicoFolhaPagamento(
            RepositorioEmpregados repositorioEmpregados,
            RepositorioFolhas repositorioFolhas) {
        this.repositorioEmpregados = repositorioEmpregados;
        this.repositorioFolhas = repositorioFolhas;
    }
    public boolean rodaFolha(String data, String saida) throws ErroWePayUException {
        validarData(data);
        if (saida == null || saida.isEmpty()) {
            throw new ArquivoDeSaidaNaoPodeSerNuloException();
        }

        String relatorioAnterior = repositorioFolhas.buscar(data);
        java.time.LocalDate dataObj = parseData(data);
        java.util.List<Empregado> empregadosProcessados = new java.util.ArrayList<>();
        for (Empregado empregado : repositorioEmpregados.listarTodos()) {
            Empregado copia = empregado.clonar();
            if (repositorioFolhas.contem(data)) {
                copia.reabrirPagamento(dataObj);
            }
            empregadosProcessados.add(copia);
        }
        StringBuilder conteudo = new StringBuilder(cabecalho(dataObj));
        SeparadorEmpregadosFolha separador = separarEmpregados(empregadosProcessados);
        java.util.List<Horista> horistas = new java.util.ArrayList<>(separador.getHoristas());
        horistas.sort(java.util.Comparator.comparing(Horista::getNome));
        java.util.List<Assalariado> assalariados = new java.util.ArrayList<>(separador.getAssalariados());
        assalariados.sort(java.util.Comparator.comparing(Assalariado::getNome));
        java.util.List<Comissionado> comissionados = new java.util.ArrayList<>(separador.getComissionados());
        comissionados.sort(java.util.Comparator.comparing(Comissionado::getNome));

        double[] totalGeral = {0.0};
        escreverHoristas(conteudo, horistas, data, dataObj, totalGeral);
        escreverAssalariados(conteudo, assalariados, data, dataObj, totalGeral);
        escreverComissionados(conteudo, comissionados, data, dataObj, totalGeral);
        conteudo.append("TOTAL FOLHA: ")
                .append(formatarLinha("%.2f", totalGeral[0])).append("\n");

        escreverArquivo(saida, conteudo.toString());
        repositorioEmpregados.substituirTodos(empregadosProcessados);
        repositorioFolhas.adicionar(data, conteudo.toString());
        return !conteudo.toString().equals(relatorioAnterior);
    }

    private String cabecalho(java.time.LocalDate data) {
        return "FOLHA DE PAGAMENTO DO DIA " + data + "\n"
                + "====================================\n\n";
    }

    private SeparadorEmpregadosFolha separarEmpregados(
            java.util.List<Empregado> empregados) {
        SeparadorEmpregadosFolha separador = new SeparadorEmpregadosFolha();
        for (Empregado empregado : empregados) {
            empregado.aceitar(separador);
        }
        return separador;
    }

    private void escreverHoristas(StringBuilder conteudo, java.util.List<Horista> empregados,
                                  String data, java.time.LocalDate dataObj, double[] totalGeral) {
        conteudo.append("===============================================================================================================================\n")
                .append("===================== HORISTAS ================================================================================================\n")
                .append("===============================================================================================================================\n")
                .append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n")
                .append("==================================== ===== ===== ============= ========= =============== ======================================\n");
        double horasTotal = 0.0, extrasTotal = 0.0, brutoTotal = 0.0, descontosTotal = 0.0, liquidoTotal = 0.0;
        for (Horista empregado : empregados) {
            if (!empregado.ehDiaDePagamento(data) || jaPago(empregado, dataObj)) continue;
            java.time.LocalDate inicio = empregado.getUltimaDataPagamento().plusDays(1);
            String dataInicial = String.format("%02d/%02d/%d", inicio.getDayOfMonth(), inicio.getMonthValue(), inicio.getYear());
            double horas = empregado.getHorasNormais(dataInicial, data);
            double extras = empregado.getHorasExtras(dataInicial, data);
            double bruto = empregado.calcularSalarioBruto(data);
            java.time.LocalDate dataAnterior = empregado.getUltimaDataPagamento();
            double dividaAnterior = empregado.getDividaDescontos();
            double liquido = empregado.calcularSalarioLiquido(data);
            double descontos = bruto - liquido;
            horasTotal += horas;
            extrasTotal += extras;
            brutoTotal += bruto;
            descontosTotal += descontos;
            liquidoTotal += liquido;
            totalGeral[0] += bruto;
            conteudo.append(formatarLinha(
                    "%-36s %5.0f %5.0f %13.2f %9.2f %15.2f ",
                    empregado.getNome(), horas, extras, bruto, descontos, liquido))
                    .append(formatarMetodoPagamento(empregado)).append("\n");
            empregado.registrarPagamento(dataObj, dataAnterior, dividaAnterior);
        }
        conteudo.append("\n").append(formatarLinha(
                "TOTAL HORISTAS                       %5.0f %5.0f %13.2f %9.2f %15.2f\n",
                horasTotal, extrasTotal, brutoTotal, descontosTotal, liquidoTotal))
                .append("\n");
    }

    private void escreverAssalariados(StringBuilder conteudo, java.util.List<Assalariado> empregados,
                                      String data, java.time.LocalDate dataObj, double[] totalGeral) {
        conteudo.append("===============================================================================================================================\n")
                .append("===================== ASSALARIADOS ============================================================================================\n")
                .append("===============================================================================================================================\n")
                .append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n")
                .append("================================================ ============= ========= =============== ======================================\n");
        double brutoTotal = 0.0, descontosTotal = 0.0, liquidoTotal = 0.0;
        for (Assalariado empregado : empregados) {
            if (!empregado.ehDiaDePagamento(data) || jaPago(empregado, dataObj)) continue;
            double bruto = empregado.calcularSalarioBruto(data);
            java.time.LocalDate dataAnterior = empregado.getUltimaDataPagamento();
            double dividaAnterior = empregado.getDividaDescontos();
            double liquido = empregado.calcularSalarioLiquido(data);
            double descontos = bruto - liquido;
            brutoTotal += bruto;
            descontosTotal += descontos;
            liquidoTotal += liquido;
            totalGeral[0] += bruto;
            conteudo.append(formatarLinha(
                    "%-48s %13.2f %9.2f %15.2f ",
                    empregado.getNome(), bruto, descontos, liquido))
                    .append(formatarMetodoPagamento(empregado)).append("\n");
            empregado.registrarPagamento(dataObj, dataAnterior, dividaAnterior);
        }
        conteudo.append("\n").append(formatarLinha(
                "TOTAL ASSALARIADOS                               %13.2f %9.2f %15.2f\n",
                brutoTotal, descontosTotal, liquidoTotal))
                .append("\n");
    }

    private void escreverComissionados(StringBuilder conteudo, java.util.List<Comissionado> empregados,
                                       String data, java.time.LocalDate dataObj, double[] totalGeral) {
        conteudo.append("===============================================================================================================================\n")
                .append("===================== COMISSIONADOS ===========================================================================================\n")
                .append("===============================================================================================================================\n")
                .append("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n")
                .append("===================== ======== ======== ======== ============= ========= =============== ======================================\n");
        double fixoTotal = 0.0, vendasTotal = 0.0, comissaoTotal = 0.0;
        double brutoTotal = 0.0, descontosTotal = 0.0, liquidoTotal = 0.0;
        for (Comissionado empregado : empregados) {
            if (!empregado.ehDiaDePagamento(data) || jaPago(empregado, dataObj)) continue;
            java.time.LocalDate inicio = empregado.getUltimaDataPagamento().plusDays(1);
            double vendas = totalVendas(empregado, inicio, dataObj);
            double comissao = Math.floor(vendas * empregado.getComissao() * 100) / 100.0;
            double fixo = Math.floor(empregado.getSalario() * 12.0 / 52.0 * 2.0 * 100) / 100.0;
            double bruto = empregado.calcularSalarioBruto(data);
            java.time.LocalDate dataAnterior = empregado.getUltimaDataPagamento();
            double dividaAnterior = empregado.getDividaDescontos();
            double liquido = empregado.calcularSalarioLiquido(data);
            double descontos = bruto - liquido;
            fixoTotal += fixo;
            vendasTotal += vendas;
            comissaoTotal += comissao;
            brutoTotal += bruto;
            descontosTotal += descontos;
            liquidoTotal += liquido;
            totalGeral[0] += bruto;
            conteudo.append(formatarLinha(
                    "%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f ",
                    empregado.getNome(), fixo, vendas, comissao,
                    bruto, descontos, liquido))
                    .append(formatarMetodoPagamento(empregado)).append("\n");
            empregado.registrarPagamento(dataObj, dataAnterior, dividaAnterior);
        }
        conteudo.append("\n").append(formatarLinha(
                "TOTAL COMISSIONADOS   %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f\n",
                fixoTotal, vendasTotal, comissaoTotal,
                brutoTotal, descontosTotal, liquidoTotal))
                .append("\n");
    }

    private boolean jaPago(Empregado empregado, java.time.LocalDate data) {
        return empregado.getUltimaDataPagamento() != null && empregado.getUltimaDataPagamento().equals(data);
    }

    private double totalVendas(Comissionado empregado, java.time.LocalDate inicio, java.time.LocalDate fim) {
        double total = 0.0;
        for (Venda venda : empregado.getVendas()) {
            java.time.LocalDate dataVenda = parseData(venda.getData());
            if (!dataVenda.isBefore(inicio) && !dataVenda.isAfter(fim)) total += venda.getValor();
        }
        return total;
    }

    private void escreverArquivo(String saida, String conteudo)
            throws ErroAoGerarArquivoDeFolhaException {
        java.nio.file.Path temporario = null;
        try {
            java.nio.file.Path arquivo = java.nio.file.Paths.get(saida).toAbsolutePath();
            java.nio.file.Path diretorio = arquivo.getParent();
            String prefixo = arquivo.getFileName().toString();
            if (prefixo.length() < 3) {
                prefixo = (prefixo + "___").substring(0, 3);
            }
            temporario = java.nio.file.Files.createTempFile(
                    diretorio, prefixo, ".tmp");
            java.nio.file.Files.writeString(temporario, conteudo);
            try {
                java.nio.file.Files.move(
                        temporario, arquivo,
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                java.nio.file.Files.move(
                        temporario, arquivo,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (java.io.IOException
                 | java.nio.file.InvalidPathException
                 | SecurityException e) {
            if (temporario != null) {
                try {
                    java.nio.file.Files.deleteIfExists(temporario);
                } catch (java.io.IOException | SecurityException cleanupException) {
                    e.addSuppressed(cleanupException);
                }
            }
            throw new ErroAoGerarArquivoDeFolhaException(e);
        }
    }

    private String formatarMetodoPagamento(Empregado emp) {
        return switch (emp.getTipoMetodoPagamento()) {
            case EM_MAOS -> "Em maos";
            case CORREIOS -> "Correios, " + emp.getEndereco();
            case BANCO -> String.format(
                    "%s, Ag. %s CC %s",
                    emp.getBanco(), emp.getAgencia(), emp.getContaCorrente());
        };
    }

    private String formatarLinha(String formato, Object... valores) {
        return String.format(formato, valores).replace(".", ",");
    }

    public String totalFolha(String data) throws ErroWePayUException {
        validarData(data);
        double totalGeral = 0.0;
        for (Empregado emp : repositorioEmpregados.listarTodos()) {
            if (emp.ehDiaDePagamento(data)) {
                totalGeral += emp.calcularSalarioBruto(data);
            }
        }
        return formatarLinha("%.2f", totalGeral);
    }

    private void validarData(String data) throws ErroWePayUException {
        ValidadorData.validar(data);
    }

    private java.time.LocalDate parseData(String data) {
        return ValidadorData.parse(data);
    }
}
