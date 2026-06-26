package parksys.services;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import parksys.entities.*;

public class GerenciadorArquivo {

    public static void serializar(Map<String, Vaga> vagas, List<Registro> registros, List<Mensalista> mensalistas, String path) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(path));
            DadosParkSys dados = new DadosParkSys(vagas, registros, mensalistas);
            oos.writeObject(dados);
            System.out.println("[SUCESSO] Operação de salvamento concluída.");
        } catch (IOException e) {
            System.err.println("[FALHA] Falha ao serializar objetos: " + e.getMessage());
        } finally {
            if (oos != null) {
                try { oos.close(); } catch (IOException e) {}
            }
        }
    }

    public static DadosParkSys desserializar(String path) {
        ObjectInputStream ois = null;
        try {
            File file = new File(path);
            if (!file.exists()) return null;
            ois = new ObjectInputStream(new FileInputStream(file));
            return (DadosParkSys) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FALHA] Falha na leitura persistente. Criando base em branco.");
            return null;
        } finally {
            if (ois != null) {
                try { ois.close(); } catch (IOException e) {}
            }
        }
    }

    public static void exportarRelatorioTxt(List<Registro> registros, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("==================================================\n");
            writer.write("          PARKSYS - EXPORTAÇÃO COMPLETA           \n");
            writer.write("==================================================\n");
            writer.write("Gerado: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n\n");
            
            double total = 0;
            for (Registro r : registros) {
                writer.write(String.format("Placa: %s | Vaga: %s | Pago: R$ %.2f\n", 
                        r.getVeiculo().getPlaca(), r.getIdVagaPrincipal(), r.getValorPago()));
                total += r.getValorPago();
            }
            writer.write("--------------------------------------------------\n");
            writer.write(String.format("TOTAL ACUMULADO COM COBRANÇAS: R$ %.2f\n", total));
            System.out.println("[SUCESSO] Relatório texto gerado.");
        } catch (IOException e) {
            System.err.println("[FALHA] Falha de gravação de TXT.");
        }
    }
}