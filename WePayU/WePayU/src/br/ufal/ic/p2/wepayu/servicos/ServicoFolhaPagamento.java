package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioFolhas;

public class ServicoFolhaPagamento {
    private final RepositorioEmpregados repositorioEmpregados;
    private final RepositorioFolhas repositorioFolhas;

    public ServicoFolhaPagamento(
            RepositorioEmpregados repositorioEmpregados,
            RepositorioFolhas repositorioFolhas) {
        this.repositorioEmpregados = repositorioEmpregados;
        this.repositorioFolhas = repositorioFolhas;
    }
    public boolean rodaFolha(String data, String saida) throws Exception {


        if (repositorioFolhas.contem(data)) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(saida)) {
                writer.print(repositorioFolhas.buscar(data));
            } catch (Exception e) {
                throw new ErroAoGerarArquivoDeFolha();
            }
            return false;
        }
        validarData(data);
        if (saida == null || saida.isEmpty()) {
            throw new ArquivoDeSaidaNaoPodeSerNulo();
        }



        java.time.LocalDate dataObj = parseData(data);
        String dataFormatada = dataObj.toString();

        StringBuilder conteudo = new StringBuilder();
        conteudo.append("FOLHA DE PAGAMENTO DO DIA ").append(dataFormatada).append("\n");
        conteudo.append("====================================\n");
        conteudo.append("\n");

        double totalFolhaGeral = 0.0;


        SeparadorEmpregadosFolha separador = new SeparadorEmpregadosFolha();
        for (Empregado emp : repositorioEmpregados.listarTodos()) {
            emp.aceitar(separador);
        }
        java.util.List<Horista> horistasOrdenados = separador.getHoristas();
        java.util.List<Assalariado> assalariadosOrdenados = separador.getAssalariados();
        java.util.List<Comissionado> comissionadosOrdenados = separador.getComissionados();
        horistasOrdenados.sort(java.util.Comparator.comparing(Horista::getNome));
        assalariadosOrdenados.sort(java.util.Comparator.comparing(Assalariado::getNome));
        comissionadosOrdenados.sort(java.util.Comparator.comparing(Comissionado::getNome));
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

        for (Horista emp : horistasOrdenados) {
            if (emp.ehDiaDePagamento(data)) {
                if (emp.getUltimaDataPagamento() != null && emp.getUltimaDataPagamento().equals(dataObj)) {
                    continue;
                }

                java.time.LocalDate dataInicio = emp.getUltimaDataPagamento().plusDays(1);
                String dataInicial = String.format("%02d/%02d/%d", dataInicio.getDayOfMonth(), dataInicio.getMonthValue(), dataInicio.getYear());

                double horas = emp.getHorasNormais(dataInicial, data);
                double extras = emp.getHorasExtras(dataInicial, data);
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
        conteudo.append("===============================================================================================================================\n");
        conteudo.append("===================== ASSALARIADOS ============================================================================================\n");
        conteudo.append("===============================================================================================================================\n");
        conteudo.append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n");
        conteudo.append("================================================ ============= ========= =============== ======================================\n");

        double totalBrutoAssalariados = 0.0;
        double totalDescontosAssalariados = 0.0;
        double totalLiquidoAssalariados = 0.0;

        for (Assalariado emp : assalariadosOrdenados) {
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

        for (Comissionado emp : comissionadosOrdenados) {
            if (emp.ehDiaDePagamento(data)) {
                if (emp.getUltimaDataPagamento() != null && emp.getUltimaDataPagamento().equals(dataObj)) {
                    continue;
                }

                java.time.LocalDate dataInicio = emp.getUltimaDataPagamento().plusDays(1);

                double vendas = 0.0;
                for (Venda venda : emp.getVendas()) {
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

        repositorioFolhas.adicionar(data, conteudo.toString());

        try (java.io.PrintWriter writer = new java.io.PrintWriter(saida)) {
            writer.print(conteudo.toString());
        } catch (Exception e) {
            throw new ErroAoGerarArquivoDeFolha();
        }
        return true;
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

    private java.time.LocalDate parseData(String data) {
        String[] partes = data.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int ano = Integer.parseInt(partes[2]);
        return java.time.LocalDate.of(ano, mes, dia);
    }
}
