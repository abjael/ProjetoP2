public class ResultadoVenda {
    private  double valor;
    private String data;

    public ResultadoVenda (double valor, String data){
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