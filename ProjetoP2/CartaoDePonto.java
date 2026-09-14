public class CartaoDePonto {
    private  double hora;
    private String data;

    public CartaoDePonto (double hora, String data){
        this.hora = hora;
        this.data = data;
    }

    public double getHoras(){
        return hora;
    }

    public String getData(){
        return data;
    }
}