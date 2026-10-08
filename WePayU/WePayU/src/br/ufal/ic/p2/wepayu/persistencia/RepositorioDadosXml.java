package br.ufal.ic.p2.wepayu.persistencia;
import br.ufal.ic.p2.wepayu.exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class RepositorioDadosXml {
    private static final Path ARQUIVO_DADOS =
            Paths.get("wepayu-dados.xml");

    private void adicionarTexto(
            org.w3c.dom.Document documento,
            org.w3c.dom.Element pai,
            String nome,
            String valor) {
        org.w3c.dom.Element elemento = documento.createElement(nome);
        elemento.appendChild(documento.createTextNode(valor == null ? "" : valor));
        pai.appendChild(elemento);
    }

    public void salvarDados(List<Empregado> empregados, Map<String, String> folhasGeradas) throws ErroWePayUException {
        try {
            org.w3c.dom.Document documento =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder()
                            .newDocument();

            org.w3c.dom.Element raiz = documento.createElement("wepayu");
            documento.appendChild(raiz);

            org.w3c.dom.Element empregadosXml = documento.createElement("empregados");
            raiz.appendChild(empregadosXml);

            for (Empregado emp : empregados) {
                org.w3c.dom.Element empregadoXml = documento.createElement("empregado");
                empregadoXml.setAttribute("tipo", emp.getTipo().paraTexto());
                empregadosXml.appendChild(empregadoXml);

                adicionarTexto(documento, empregadoXml, "id", emp.getId());
                adicionarTexto(documento, empregadoXml, "nome", emp.getNome());
                adicionarTexto(documento, empregadoXml, "endereco", emp.getEndereco());
                adicionarTexto(documento, empregadoXml, "salario",
                        Double.toString(emp.getSalario()));
                adicionarTexto(documento, empregadoXml, "sindicalizado",
                        emp.getSindicalizado());
                adicionarTexto(documento, empregadoXml, "idSindicato",
                        emp.getIdSindicato());
                adicionarTexto(documento, empregadoXml, "taxaSindical",
                        Double.toString(emp.getTaxaSindical()));
                adicionarTexto(documento, empregadoXml, "metodoPagamento",
                        emp.getMetodoPagamento());
                adicionarTexto(documento, empregadoXml, "banco", emp.getBanco());
                adicionarTexto(documento, empregadoXml, "agencia", emp.getAgencia());
                adicionarTexto(documento, empregadoXml, "contaCorrente",
                        emp.getContaCorrente());
                adicionarTexto(documento, empregadoXml, "ultimaDataPagamento",
                        emp.getUltimaDataPagamento().toString());
                adicionarTexto(documento, empregadoXml, "dataPagamentoAnterior",
                        emp.getDataPagamentoAnterior() == null
                                ? "" : emp.getDataPagamentoAnterior().toString());
                adicionarTexto(documento, empregadoXml, "dividaDescontos",
                        Double.toString(emp.getDividaDescontos()));
                adicionarTexto(documento, empregadoXml, "dividaDescontosAnterior",
                        Double.toString(emp.getDividaDescontosAnterior()));
                adicionarTexto(documento, empregadoXml, "agendaPagamento",
                        emp.getAgendaPagamento());

                org.w3c.dom.Element taxasXml = documento.createElement("taxasServico");
                empregadoXml.appendChild(taxasXml);
                for (TaxaServico taxa : emp.getTaxasServico()) {
                    org.w3c.dom.Element taxaXml = documento.createElement("taxa");
                    taxaXml.setAttribute("data", taxa.getData());
                    taxaXml.setAttribute("valor", Double.toString(taxa.getValor()));
                    taxasXml.appendChild(taxaXml);
                }

                emp.aceitar(new EscritorDadosEspecificos(
                        documento, empregadoXml));
            }

            org.w3c.dom.Element folhasXml = documento.createElement("folhas");
            raiz.appendChild(folhasXml);

            for (java.util.Map.Entry<String, String> folha : folhasGeradas.entrySet()) {
                org.w3c.dom.Element folhaXml = documento.createElement("folha");
                folhaXml.setAttribute("data", folha.getKey());
                folhaXml.appendChild(documento.createTextNode(folha.getValue()));
                folhasXml.appendChild(folhaXml);
            }

            javax.xml.transform.Transformer transformador =
                    javax.xml.transform.TransformerFactory.newInstance()
                            .newTransformer();
            transformador.setOutputProperty(
                    javax.xml.transform.OutputKeys.INDENT, "yes");
            transformador.transform(
                    new javax.xml.transform.dom.DOMSource(documento),
                    new javax.xml.transform.stream.StreamResult(ARQUIVO_DADOS.toFile()));

        } catch (javax.xml.parsers.ParserConfigurationException
                 | javax.xml.transform.TransformerException
                 | RuntimeException e) {
            throw new ErroAoSalvarDadosException(e);
        }
    }
    private static String lerTexto(
            org.w3c.dom.Element elemento,
            String nome) {
        org.w3c.dom.NodeList lista = elemento.getElementsByTagName(nome);
        if (lista.getLength() == 0) {
            return "";
        }
        return lista.item(0).getTextContent();
    }

    private static org.w3c.dom.Element primeiroFilho(
            org.w3c.dom.Element elemento,
            String nome) {
        org.w3c.dom.NodeList lista = elemento.getElementsByTagName(nome);
        if (lista.getLength() == 0) {
            return null;
        }
        return (org.w3c.dom.Element) lista.item(0);
    }

    public void carregarDados(
            List<Empregado> empregados,
            Map<String, String> folhasGeradas) throws ErroWePayUException {
        if (!java.nio.file.Files.exists(ARQUIVO_DADOS)) {
            return;
        }

        try {
            org.w3c.dom.Document documento =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder()
                            .parse(ARQUIVO_DADOS.toFile());

            java.util.List<Empregado> empregadosCarregados =
                    new java.util.ArrayList<>();
            java.util.Map<String, String> folhasCarregadas =
                    new java.util.HashMap<>();

            org.w3c.dom.NodeList empregadosXml =
                    documento.getElementsByTagName("empregado");

            for (int i = 0; i < empregadosXml.getLength(); i++) {
                org.w3c.dom.Element elemento =
                        (org.w3c.dom.Element) empregadosXml.item(i);

                String tipoTexto = elemento.getAttribute("tipo");
                String id = lerTexto(elemento, "id");
                String nome = lerTexto(elemento, "nome");
                String endereco = lerTexto(elemento, "endereco");
                double salario = Double.parseDouble(lerTexto(elemento, "salario"));
                boolean sindicalizado =
                        Boolean.parseBoolean(lerTexto(elemento, "sindicalizado"));
                String idSindicato = lerTexto(elemento, "idSindicato");
                double taxaSindical = Double.parseDouble(
                        lerTexto(elemento, "taxaSindical"));

                TipoEmpregado tipo;
                try {
                    tipo = TipoEmpregado.deTexto(tipoTexto);
                } catch (IllegalArgumentException e) {
                    throw new TipoDeEmpregadoPersistidoInvalidoException(tipoTexto);
                }

                Empregado emp = switch (tipo) {
                    case HORISTA -> new Horista(
                            id, nome, endereco, salario, false);
                    case ASSALARIADO -> new Assalariado(
                            id, nome, endereco, salario, false);
                    case COMISSIONADO -> {
                        double comissao = Double.parseDouble(
                                lerTexto(elemento, "comissao"));
                        yield new Comissionado(
                                id, nome, endereco, salario, comissao, false);
                    }
                };

                if (sindicalizado) {
                    emp.sindicalizar(idSindicato, taxaSindical);
                }
                emp.setMetodoPagamento(lerTexto(elemento, "metodoPagamento"));
                emp.setBanco(lerTexto(elemento, "banco"));
                emp.setAgencia(lerTexto(elemento, "agencia"));
                emp.setContaCorrente(lerTexto(elemento, "contaCorrente"));
                emp.setUltimaDataPagamento(
                        java.time.LocalDate.parse(
                                lerTexto(elemento, "ultimaDataPagamento")));
                String dataPagamentoAnterior =
                        lerTexto(elemento, "dataPagamentoAnterior");
                if (!dataPagamentoAnterior.isEmpty()) {
                    emp.setDataPagamentoAnterior(
                            java.time.LocalDate.parse(dataPagamentoAnterior));
                }
                String dividaDescontos = lerTexto(elemento, "dividaDescontos");
                if (!dividaDescontos.isEmpty()) {
                    emp.setDividaDescontos(Double.parseDouble(dividaDescontos));
                }
                String dividaDescontosAnterior =
                        lerTexto(elemento, "dividaDescontosAnterior");
                if (!dividaDescontosAnterior.isEmpty()) {
                    emp.setDividaDescontosAnterior(
                            Double.parseDouble(dividaDescontosAnterior));
                }
                String agendaPagamento = lerTexto(elemento, "agendaPagamento");
                if (!agendaPagamento.isEmpty()) {
                    emp.setAgendaPagamento(agendaPagamento);
                } else {
                    emp.setAgendaPagamento(AgendaPagamento.defaultAgenda(tipo));
                }

                org.w3c.dom.Element taxasXml =
                        primeiroFilho(elemento, "taxasServico");
                if (taxasXml != null) {
                    org.w3c.dom.NodeList taxas = taxasXml.getElementsByTagName("taxa");
                    for (int j = 0; j < taxas.getLength(); j++) {
                        org.w3c.dom.Element taxa =
                                (org.w3c.dom.Element) taxas.item(j);
                        emp.adicionarTaxaServico(
                                taxa.getAttribute("data"),
                                Double.parseDouble(taxa.getAttribute("valor")));
                    }
                }

                emp.aceitar(new LeitorDadosEspecificos(elemento));

                empregadosCarregados.add(emp);
            }

            org.w3c.dom.NodeList folhasXml =
                    documento.getElementsByTagName("folha");
            for (int i = 0; i < folhasXml.getLength(); i++) {
                org.w3c.dom.Element folha =
                        (org.w3c.dom.Element) folhasXml.item(i);
                folhasCarregadas.put(
                        folha.getAttribute("data"),
                        folha.getTextContent());
            }

            empregados.clear();
            empregados.addAll(empregadosCarregados);

            folhasGeradas.clear();
            folhasGeradas.putAll(folhasCarregadas);

        } catch (TipoDeEmpregadoPersistidoInvalidoException e) {
            throw e;
        } catch (javax.xml.parsers.ParserConfigurationException
                 | org.xml.sax.SAXException
                 | java.io.IOException
                 | RuntimeException e) {
            throw new ErroAoCarregarDadosException(e);
        }
    }

    private class EscritorDadosEspecificos implements VisitanteDadosEmpregado {
        private final org.w3c.dom.Document documento;
        private final org.w3c.dom.Element empregadoXml;

        private EscritorDadosEspecificos(
                org.w3c.dom.Document documento,
                org.w3c.dom.Element empregadoXml) {
            this.documento = documento;
            this.empregadoXml = empregadoXml;
        }

        @Override
        public void visitar(Horista horista) {
            adicionarRegistros(horista);
        }

        @Override
        public void visitar(Assalariado assalariado) {
            adicionarRegistros(assalariado);
        }

        @Override
        public void visitar(Comissionado comissionado) {
            adicionarTexto(documento, empregadoXml, "comissao",
                    Double.toString(comissionado.getComissao()));
            adicionarRegistros(comissionado);
        }

        private void adicionarRegistros(Empregado empregado) {
            org.w3c.dom.Element cartoesXml = documento.createElement("cartoes");
            empregadoXml.appendChild(cartoesXml);

            for (CartaoPonto cartao : empregado.getCartoes()) {
                org.w3c.dom.Element cartaoXml = documento.createElement("cartao");
                cartaoXml.setAttribute("data", cartao.getData());
                cartaoXml.setAttribute("horas", Double.toString(cartao.getHoras()));
                cartoesXml.appendChild(cartaoXml);
            }

            org.w3c.dom.Element vendasXml = documento.createElement("vendas");
            empregadoXml.appendChild(vendasXml);

            for (Venda venda : empregado.getVendas()) {
                org.w3c.dom.Element vendaXml = documento.createElement("venda");
                vendaXml.setAttribute("data", venda.getData());
                vendaXml.setAttribute("valor", Double.toString(venda.getValor()));
                vendasXml.appendChild(vendaXml);
            }
        }
    }

    private class LeitorDadosEspecificos implements VisitanteDadosEmpregado {
        private final org.w3c.dom.Element empregadoXml;

        private LeitorDadosEspecificos(org.w3c.dom.Element empregadoXml) {
            this.empregadoXml = empregadoXml;
        }

        @Override
        public void visitar(Horista horista) {
            lerRegistros(horista);
        }

        @Override
        public void visitar(Assalariado assalariado) {
            lerRegistros(assalariado);
        }

        @Override
        public void visitar(Comissionado comissionado) {
            lerRegistros(comissionado);
        }

        private void lerRegistros(Empregado empregado) {
            org.w3c.dom.Element cartoesXml =
                    primeiroFilho(empregadoXml, "cartoes");
            if (cartoesXml != null) {
                org.w3c.dom.NodeList cartoes =
                        cartoesXml.getElementsByTagName("cartao");
                for (int i = 0; i < cartoes.getLength(); i++) {
                    org.w3c.dom.Element cartao =
                            (org.w3c.dom.Element) cartoes.item(i);
                    empregado.carregarCartaoPersistido(
                            cartao.getAttribute("data"),
                            Double.parseDouble(cartao.getAttribute("horas")));
                }
            }

            org.w3c.dom.Element vendasXml =
                    primeiroFilho(empregadoXml, "vendas");
            if (vendasXml != null) {
                org.w3c.dom.NodeList vendas =
                        vendasXml.getElementsByTagName("venda");
                for (int i = 0; i < vendas.getLength(); i++) {
                    org.w3c.dom.Element venda =
                            (org.w3c.dom.Element) vendas.item(i);
                    empregado.carregarVendaPersistida(
                            venda.getAttribute("data"),
                            Double.parseDouble(venda.getAttribute("valor")));
                }
            }
        }
    }
}
