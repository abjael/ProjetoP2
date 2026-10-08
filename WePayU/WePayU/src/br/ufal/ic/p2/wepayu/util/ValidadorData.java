package br.ufal.ic.p2.wepayu.util;

import br.ufal.ic.p2.wepayu.exception.DataFinalInvalidaException;
import br.ufal.ic.p2.wepayu.exception.DataFinalNaoPodeSerNulaException;
import br.ufal.ic.p2.wepayu.exception.DataInicialInvalidaException;
import br.ufal.ic.p2.wepayu.exception.DataInicialNaoPodeSerNulaException;
import br.ufal.ic.p2.wepayu.exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.exception.DataNaoPodeSerNulaException;
import java.time.DateTimeException;
import java.time.LocalDate;

public final class ValidadorData {
    private ValidadorData() {
    }

    public static LocalDate validar(String data)
            throws DataNaoPodeSerNulaException, DataInvalidaException {
        if (data == null || data.isEmpty()) {
            throw new DataNaoPodeSerNulaException();
        }
        try {
            return parse(data);
        } catch (IllegalArgumentException e) {
            throw new DataInvalidaException();
        }
    }

    public static LocalDate validarInicial(String data)
            throws DataInicialNaoPodeSerNulaException, DataInicialInvalidaException {
        if (data == null || data.isEmpty()) {
            throw new DataInicialNaoPodeSerNulaException();
        }
        try {
            return parse(data);
        } catch (IllegalArgumentException e) {
            throw new DataInicialInvalidaException();
        }
    }

    public static LocalDate validarFinal(String data)
            throws DataFinalNaoPodeSerNulaException, DataFinalInvalidaException {
        if (data == null || data.isEmpty()) {
            throw new DataFinalNaoPodeSerNulaException();
        }
        try {
            return parse(data);
        } catch (IllegalArgumentException e) {
            throw new DataFinalInvalidaException();
        }
    }

    public static LocalDate parse(String data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        String[] partes = data.split("/");
        if (partes.length != 3) {
            throw new IllegalArgumentException();
        }
        try {
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int ano = Integer.parseInt(partes[2]);
            return LocalDate.of(ano, mes, dia);
        } catch (NumberFormatException | DateTimeException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
