package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exception.DescricaoDeAgendaInvalidaException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public final class AgendaPagamento {
    private final TipoAgenda tipo;
    private final int intervaloSemanas;
    private final int dia;

    public AgendaPagamento(TipoAgenda tipo, int intervaloSemanas, int dia) {
        this.tipo = tipo;
        this.intervaloSemanas = intervaloSemanas;
        this.dia = dia;
    }

    public static AgendaPagamento parse(String descricao)
            throws DescricaoDeAgendaInvalidaException {
        if (descricao == null) {
            throw new DescricaoDeAgendaInvalidaException();
        }

        String normalizado = normalizar(descricao);
        if (normalizado.isEmpty()) {
            throw new DescricaoDeAgendaInvalidaException();
        }

        if (normalizado.startsWith("mensal ")) {
            String valor = normalizado.substring("mensal ".length());
            if ("$".equals(valor)) {
                return new AgendaPagamento(TipoAgenda.MENSAL, 1, 0);
            }
            try {
                int diaMes = Integer.parseInt(valor);
                if (diaMes < 1 || diaMes > 28) {
                    throw new DescricaoDeAgendaInvalidaException();
                }
                return new AgendaPagamento(TipoAgenda.MENSAL, 1, diaMes);
            } catch (NumberFormatException e) {
                throw new DescricaoDeAgendaInvalidaException();
            }
        }

        if (normalizado.startsWith("semanal ")) {
            String restante = normalizado.substring("semanal ".length());
            String[] partes = restante.split(" ");
            if (partes.length == 1) {
                int diaSemana = parseDiaSemana(partes[0]);
                return new AgendaPagamento(TipoAgenda.SEMANAL, 1, diaSemana);
            }
            if (partes.length == 2) {
                try {
                    int intervalo = Integer.parseInt(partes[0]);
                    int diaSemana = parseDiaSemana(partes[1]);
                    if (intervalo < 1 || intervalo > 52) {
                        throw new DescricaoDeAgendaInvalidaException();
                    }
                    return new AgendaPagamento(TipoAgenda.SEMANAL, intervalo, diaSemana);
                } catch (NumberFormatException e) {
                    throw new DescricaoDeAgendaInvalidaException();
                }
            }
            throw new DescricaoDeAgendaInvalidaException();
        }

        throw new DescricaoDeAgendaInvalidaException();
    }

    public static String normalizar(String descricao) {
        if (descricao == null) {
            return "";
        }
        return descricao.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    public static String defaultAgenda(TipoEmpregado tipo) {
        return switch (tipo) {
            case HORISTA -> "semanal 5";
            case ASSALARIADO -> "mensal $";
            case COMISSIONADO -> "semanal 2 5";
        };
    }

    public boolean ehDiaDePagamento(LocalDate data) {
        if (tipo == TipoAgenda.MENSAL) {
            if (dia == 0) {
                LocalDate ultimoDiaUtil = data.withDayOfMonth(data.lengthOfMonth());
                while (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY
                        || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
                }
                return data.equals(ultimoDiaUtil);
            }
            return data.getDayOfMonth() == dia;
        }

        if (intervaloSemanas <= 1) {
            return data.getDayOfWeek().getValue() == dia;
        }

        LocalDate inicio = LocalDate.of(2005, 1, 1);
        LocalDate primeiroComDia = primeiroDiaDaSemana(inicio, dia);
        LocalDate base = primeiroComDia.plusWeeks(intervaloSemanas - 1);
        if (data.isBefore(base)) {
            return false;
        }

        long diferencaDias = ChronoUnit.DAYS.between(base, data);
        return data.getDayOfWeek().getValue() == dia && diferencaDias % (intervaloSemanas * 7L) == 0;
    }

    private static int parseDiaSemana(String valor) throws DescricaoDeAgendaInvalidaException {
        try {
            int diaSemana = Integer.parseInt(valor);
            if (diaSemana < 1 || diaSemana > 7) {
                throw new DescricaoDeAgendaInvalidaException();
            }
            return diaSemana;
        } catch (NumberFormatException e) {
            throw new DescricaoDeAgendaInvalidaException();
        }
    }

    private static LocalDate primeiroDiaDaSemana(LocalDate data, int diaSemana) {
        DayOfWeek alvo = DayOfWeek.of(diaSemana);
        LocalDate cursor = data;
        while (cursor.getDayOfWeek() != alvo) {
            cursor = cursor.plusDays(1);
        }
        return cursor;
    }

    @Override
    public String toString() {
        if (tipo == TipoAgenda.MENSAL) {
            return dia == 0 ? "mensal $" : "mensal " + dia;
        }
        if (intervaloSemanas <= 1) {
            return "semanal " + dia;
        }
        return "semanal " + intervaloSemanas + " " + dia;
    }

    private enum TipoAgenda {
        MENSAL,
        SEMANAL
    }
}
