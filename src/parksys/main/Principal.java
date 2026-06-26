package parksys.main;

import javax.swing.SwingUtilities;
import parksys.entities.Registro;
import parksys.enums.TipoVeiculo;
import parksys.services.*;
import parksys.ui.TelaInicial;

public class Principal {
    public static void main(String[] args) {
        System.out.println("=== INICIALIZANDO PARKSYS SYSTEM ===");

        GerenciadorEstacionamento gerenciador = GerenciadorEstacionamento.getInstance();

        DadosParkSys dadosSalvos = GerenciadorArquivo.desserializar("estacionamento.ser"); 
        if (dadosSalvos != null) {
            gerenciador.setDadoscarregados(dadosSalvos.getVagas(), dadosSalvos.getRegistros(), dadosSalvos.getMensalistas());
            
            
            System.out.println("\n--- Registros Recuperados e Campo Transient (M07) ---");
            for (Registro r : gerenciador.getRegistros()) {
                System.out.println("Placa: " + r.getVeiculo().getPlaca() + " | Thread de Entrada Original: " + r.getThreadOrigem());
            }
        }

        
        Thread threadMonitor = new Thread(new MonitorRunnable(gerenciador));
        threadMonitor.setDaemon(true);
        threadMonitor.start();

      
        System.out.println("\n[SISTEMA] Disparando 4 threads de entrada concorrentes...");
        Thread t1 = new Thread(new EntradaRunnable("ABC1234", TipoVeiculo.CARRO, "A01", gerenciador), "Entrada-1");
        Thread t2 = new Thread(new EntradaRunnable("XYZ5678", TipoVeiculo.MOTO, "A02", gerenciador), "Entrada-2");
        Thread t3 = new Thread(new EntradaRunnable("KJL9988", TipoVeiculo.SUV, "B01", gerenciador), "Entrada-3");
        Thread t4 = new Thread(new EntradaRunnable("MNO4455", TipoVeiculo.CAMINHAO, "B10", gerenciador), "Entrada-4");

        t1.start(); t2.start(); t3.start(); t4.start();

        try {
            t1.join(); t2.join(); t3.join(); t4.join(); 
            System.out.println("[SISTEMA] Threads sincronizadas e finalizadas com sucesso.\n");
        } catch (InterruptedException e) {
            System.err.println("Interrupção nas cargas.");
        }

      
        SwingUtilities.invokeLater(() -> {
            new TelaInicial().setVisible(true);
        });
    }
}