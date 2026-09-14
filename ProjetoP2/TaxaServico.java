public class TaxaServico {
    private  double valor;
    private String data;

    public TaxaServico (double valor, String data){
        this.valor = valor;
        this.data = data;
    }

    public double getValor(){
        return valor;
    }

    public String getData(){
        return data;
    }
}