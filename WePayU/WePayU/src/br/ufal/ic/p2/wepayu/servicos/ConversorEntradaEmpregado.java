package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.exception.*;
import br.ufal.ic.p2.wepayu.models.TipoEmpregado;
import java.math.BigDecimal;

final class ConversorEntradaEmpregado {
    private ConversorEntradaEmpregado() {
    }

    static void validarDadosBasicos(String nome, String endereco)
            throws NomeNaoPodeSerNuloException, EnderecoNaoPodeSerNuloException {
        if (nome == null || nome.isEmpty()) {
            throw new NomeNaoPodeSerNuloException();
        }
        if (endereco == null || endereco.isEmpty()) {
            throw new EnderecoNaoPodeSerNuloException();
        }
    }

    static BigDecimal converterSalario(String salario)
            throws SalarioNaoPodeSerNuloException,
            SalarioDeveSerNumericoException, SalarioDeveSerNaoNegativoException {
        if (salario == null || salario.isEmpty()) {
            throw new SalarioNaoPodeSerNuloException();
        }
        BigDecimal valor;
        try {
            valor = converterValorMonetario(salario);
        } catch (NumberFormatException e) {
            throw new SalarioDeveSerNumericoException();
        }
        if (valor.signum() < 0) {
            throw new SalarioDeveSerNaoNegativoException();
        }
        return valor;
    }

    static BigDecimal converterComissao(String comissao)
            throws ComissaoNaoPodeSerNulaException,
            ComissaoDeveSerNumericaException, ComissaoDeveSerNaoNegativaException {
        if (comissao == null || comissao.isEmpty()) {
            throw new ComissaoNaoPodeSerNulaException();
        }
        BigDecimal valor;
        try {
            valor = converterValorMonetario(comissao);
        } catch (NumberFormatException e) {
            throw new ComissaoDeveSerNumericaException();
        }
        if (valor.signum() < 0) {
            throw new ComissaoDeveSerNaoNegativaException();
        }
        return valor;
    }

    static BigDecimal converterValorMonetario(String valor) {
        return new BigDecimal(valor.replace(",", "."));
    }

    static double converterNumero(String valor) {
        return Double.parseDouble(valor.replace(",", "."));
    }

    static boolean converterBooleano(String valor)
            throws ValorDeveSerTrueOuFalseException {
        if (!"true".equals(valor) && !"false".equals(valor)) {
            throw new ValorDeveSerTrueOuFalseException();
        }
        return Boolean.parseBoolean(valor);
    }

    static TipoEmpregado converterTipo(String tipo) throws TipoInvalidoException {
        try {
            return TipoEmpregado.deTexto(tipo);
        } catch (IllegalArgumentException e) {
            throw new TipoInvalidoException();
        }
    }

    static String formatarValor(BigDecimal valor) {
        return valor.setScale(2, java.math.RoundingMode.HALF_UP)
                .toPlainString().replace(".", ",");
    }
}
