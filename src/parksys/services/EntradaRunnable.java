package parksys.services;

import parksys.enums.TipoVeiculo;

public class EntradaRunnable implements Runnable {
    private final String placa;
    private final TipoVeiculo tipo;
    private final String idVaga;
    private final GerenciadorEstacionamento gerenciador;

    public EntradaRunnable(String placa, TipoVeiculo tipo, String idVaga, GerenciadorEstacionamento gerenciador) {
        this.placa = placa;
        this.tipo = tipo;
        this.idVaga = idVaga;
        this.gerenciador = gerenciador;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(200); 
            gerenciador.registrarEntrada(placa, tipo, idVaga);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Falha na Thread concorrente: " + e.getMessage());
        }
    }
}