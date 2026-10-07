package br.ufal.ic.p2.wepayu.persistencia;
import br.ufal.ic.p2.wepayu.Exception.*;
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

    public void salvarDados(List<Empregado> empregados, Map<String, String> folhasGeradas) throws Exception {
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
                empregadoXml.setAttribute("tipo", emp.getTipo());
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

                org.w3c.dom.Element taxasXml = documento.createElement("taxasServico");
                empregadoXml.appendChild(taxasXml);
                for (TaxaServico taxa : emp.getTaxasServico()) {
                    org.w3c.dom.Element taxaXml = documento.createElement("taxa");
                    taxaXml.setAttribute("data", taxa.getData());
                    taxaXml.setAttribute("valor", Double.toString(taxa.getValor()));
                    taxasXml.appendChild(taxaXml);
                }

                if (emp instanceof Horista) {
                    Horista horista = (Horista) emp;
                    org.w3c.dom.Element cartoesXml = documento.createElement("cartoes");
                    empregadoXml.appendChild(cartoesXml);

                    for (br.ufal.ic.p2.wepayu.models.CartaoPonto cartao
                            : horista.getCartoes()) {
                        org.w3c.dom.Element cartaoXml = documento.createElement("cartao");
                        cartaoXml.setAttribute("data", cartao.getData());
                        cartaoXml.setAttribute("horas",
                                Double.toString(cartao.getHoras()));
                        cartoesXml.appendChild(cartaoXml);
                    }
                }

                if (emp instanceof Comissionado) {
                    Comissionado comissionado = (Comissionado) emp;
                    adicionarTexto(documento, empregadoXml, "comissao",
                            Double.toString(comissionado.getComissao()));

                    org.w3c.dom.Element vendasXml = documento.createElement("vendas");
                    empregadoXml.appendChild(vendasXml);

                    for (Venda venda : comissionado.getVendas()) {
                        org.w3c.dom.Element vendaXml = documento.createElement("venda");
                        vendaXml.setAttribute("data", venda.getData());
                        vendaXml.setAttribute("valor",
                                Double.toString(venda.getValor()));
                        vendasXml.appendChild(vendaXml);
                    }
                }
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

        } catch (Exception e) {
            throw new ErroAoSalvarDadosException();
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
            Map<String, String> folhasGeradas) throws Exception {
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

                String tipo = elemento.getAttribute("tipo");
                String id = lerTexto(elemento, "id");
                String nome = lerTexto(elemento, "nome");
                String endereco = lerTexto(elemento, "endereco");
                double salario = Double.parseDouble(lerTexto(elemento, "salario"));
                String sindicalizado = lerTexto(elemento, "sindicalizado");

                Empregado emp;
                if (tipo.equals("horista")) {
                    emp = new Horista(id, nome, endereco, tipo, salario, sindicalizado);
                } else if (tipo.equals("assalariado")) {
                    emp = new Assalariado(id, nome, endereco, tipo, salario, sindicalizado);
                } else if (tipo.equals("comissionado")) {
                    double comissao =
                            Double.parseDouble(lerTexto(elemento, "comissao"));
                    emp = new Comissionado(
                            id, nome, endereco, tipo, salario, comissao, sindicalizado);
                } else {
                    throw new TipoDeEmpregadoPersistidoInvalidoException(tipo);
                }

                emp.setIdSindicato(lerTexto(elemento, "idSindicato"));
                emp.setTaxaSindical(
                        Double.parseDouble(lerTexto(elemento, "taxaSindical")));
                emp.setMetodoPagamento(lerTexto(elemento, "metodoPagamento"));
                emp.setBanco(lerTexto(elemento, "banco"));
                emp.setAgencia(lerTexto(elemento, "agencia"));
                emp.setContaCorrente(lerTexto(elemento, "contaCorrente"));
                emp.setUltimaDataPagamento(
                        java.time.LocalDate.parse(
                                lerTexto(elemento, "ultimaDataPagamento")));

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

                if (emp instanceof Horista) {
                    org.w3c.dom.Element cartoesXml =
                            primeiroFilho(elemento, "cartoes");
                    if (cartoesXml != null) {
                        org.w3c.dom.NodeList cartoes =
                                cartoesXml.getElementsByTagName("cartao");
                        for (int j = 0; j < cartoes.getLength(); j++) {
                            org.w3c.dom.Element cartao =
                                    (org.w3c.dom.Element) cartoes.item(j);
                            ((Horista) emp).lancarCartao(
                                    cartao.getAttribute("data"),
                                    Double.parseDouble(cartao.getAttribute("horas")));
                        }
                    }
                }

                if (emp instanceof Comissionado) {
                    org.w3c.dom.Element vendasXml =
                            primeiroFilho(elemento, "vendas");
                    if (vendasXml != null) {
                        org.w3c.dom.NodeList vendas =
                                vendasXml.getElementsByTagName("venda");
                        for (int j = 0; j < vendas.getLength(); j++) {
                            org.w3c.dom.Element venda =
                                    (org.w3c.dom.Element) vendas.item(j);
                            ((Comissionado) emp).adicionarVenda(
                                    venda.getAttribute("data"),
                                    Double.parseDouble(venda.getAttribute("valor")));
                        }
                    }
                }

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
        } catch (Exception e) {
            throw new ErroAoCarregarDadosException();
        }
    }
}


