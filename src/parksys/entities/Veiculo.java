package parksys.entities;

import java.io.Serializable;
import parksys.enums.TipoVeiculo;

public class Veiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String placa;
    private TipoVeiculo tipoVeiculo;

    public Veiculo(String placa, TipoVeiculo tipoVeiculo) {
        this.placa = placa;
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public TipoVeiculo getTipoVeiculo() { return tipoVeiculo; }
    public void setTipoVeiculo(TipoVeiculo tipoVeiculo) { this.tipoVeiculo = tipoVeiculo; }
}