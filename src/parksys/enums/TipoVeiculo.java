package parksys.enums;

public enum TipoVeiculo {
    MOTO("Motocicleta", 5.0, 1),
    CARRO("Automóvel", 10.0, 1),
    SUV("Caminhonete / SUV", 18.0, 2),
    CAMINHAO("Caminhão", 30.0, 3);

    private final String nomeLegivel;
    private final double tarifaHora;
    private final int vagasOcupadas;

    private TipoVeiculo(String nomeLegivel, double tarifaHora, int vagasOcupadas) {
        this.nomeLegivel = nomeLegivel;
        this.tarifaHora = tarifaHora;
        this.vagasOcupadas = vagasOcupadas;
    }

    public String getNomeLegivel() {
        return nomeLegivel;
    }

    public double getTarifaHora() {
        return tarifaHora;
    }

    public int getVagasOcupadas() {
        return vagasOcupadas;
    }

    @Override
    public String toString() {
        return nomeLegivel;
    }
}