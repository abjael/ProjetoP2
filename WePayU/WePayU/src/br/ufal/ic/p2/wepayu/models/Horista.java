package br.ufal.ic.p2.wepayu.models;

public class Horista extends Empregado {

    private double horasTrabalhadas = 0.0;

    public Horista(String id, String nome, String endereco, String tipo, double salario, String sindicalizado) {
        super(id, nome, endereco, tipo, salario, sindicalizado);
    }

    @Override
    public void adicionarHoras(double horas) {
        this.horasTrabalhadas += horas;
    }

    @Override
    public String getHorasTrabalhadas() {
        return String.format("%.1f", horasTrabalhadas).replace(".", ",");
    }
}