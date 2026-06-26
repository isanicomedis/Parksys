package parksys.services;

import java.util.Map;
import parksys.entities.Vaga;
import parksys.enums.StatusVaga;

public class MonitorRunnable implements Runnable {
    private final GerenciadorEstacionamento gerenciador;

    public MonitorRunnable(GerenciadorEstacionamento gerenciador) {
        this.gerenciador = gerenciador;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int livres = 0, ocupadas = 0, reservadas = 0;
                
                synchronized (gerenciador) {
                    Map<String, Vaga> map = gerenciador.getVagas();
                    for (Vaga v : map.values()) {
                        if (v.getStatus() == StatusVaga.LIVRE) livres++;
                        else if (v.getStatus() == StatusVaga.OCUPADA) ocupadas++;
                        else if (v.getStatus() == StatusVaga.RESERVADA) reservadas++;
                    }
                }
                System.out.printf("[MONITOR SYSTEM] Livres: %d | Ocupadas: %d | Reservadas: %d%n", livres, ocupadas, reservadas);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // M06 Errata
        }
    }
}