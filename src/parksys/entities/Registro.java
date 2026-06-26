package parksys.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Registro implements Serializable, Comparable<Registro> {
    private static final long serialVersionUID = 1L;

    private Veiculo veiculo;
    private String idVagaPrincipal;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private double valorPago;
    
  
    private transient String threadOrigem;

    public Registro(Veiculo veiculo, String idVagaPrincipal, LocalDateTime dataEntrada) {
        this.veiculo = veiculo;
        this.idVagaPrincipal = idVagaPrincipal;
        this.dataEntrada = dataEntrada;
    }

    public Veiculo getVeiculo() { return veiculo; }
    public String getIdVagaPrincipal() { return idVagaPrincipal; }
    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }
    public double getValorPago() { return valorPago; }
    public void setValorPago(double valorPago) { this.valorPago = valorPago; }
    public String getThreadOrigem() { return threadOrigem; }
    public void setThreadOrigem(String threadOrigem) { this.threadOrigem = threadOrigem; }

    @Override
    public int compareTo(Registro outro) {
        return this.dataEntrada.compareTo(outro.dataEntrada);
    }
}