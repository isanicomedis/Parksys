package parksys.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import parksys.entities.*;
import parksys.enums.*;
import parksys.exceptions.*;
import parksys.observer.EstacionamentoObserver;

public class GerenciadorEstacionamento {
    private static GerenciadorEstacionamento instance;

    private Map<String, Vaga> vagas;
    private List<Registro> registros; 
    private List<Mensalista> mensalistas; 

    private final List<EstacionamentoObserver> observadores = new ArrayList<>();

    private GerenciadorEstacionamento() {
        vagas = new HashMap<>();
        registros = new ArrayList<>();
        mensalistas = new LinkedList<>();
        inicializarVagas();
    }

    public static synchronized GerenciadorEstacionamento getInstance() {
        if (instance == null) {
            instance = new GerenciadorEstacionamento();
        }
        return instance;
    }

    private void inicializarVagas() {
        String[] fileiras = {"A", "B"};
        for (String f : fileiras) {
            for (int i = 1; i <= 15; i++) {
                String id = String.format("%s%02d", f, i);
                vagas.put(id, new Vaga(id));
            }
        }
    }

    public synchronized Map<String, Vaga> getVagas() { return vagas; }
    public synchronized List<Registro> getRegistros() { return registros; }
    public synchronized List<Mensalista> getMensalistas() { return mensalistas; }

    public synchronized void setDadoscarregados(Map<String, Vaga> vagas, List<Registro> registros, List<Mensalista> mensalistas) {
        if(vagas != null) this.vagas = vagas;
        if(registros != null) this.registros = registros;
        if(mensalistas != null) this.mensalistas = mensalistas;
        notificarTodos();
    }

    public synchronized void addObserver(EstacionamentoObserver obs) { observadores.add(obs); }
    public synchronized void removeObserver(EstacionamentoObserver obs) { observadores.remove(obs); }

    private void notificarObservadores(String idVaga, StatusVaga status) {
        for (EstacionamentoObserver obs : observadores) {
            obs.onVagaAlterada(idVaga, status);
        }
    }

    private void notificarTodos() {
        for (Map.Entry<String, Vaga> entry : vagas.entrySet()) {
            notificarObservadores(entry.getKey(), entry.getValue().getStatus());
        }
    }

    public synchronized void cadastrarMensalista(Mensalista m) {
        mensalistas.add(m);
    }

    public synchronized void alocarVagaReservada(String idVaga) throws VagaOcupadaException {
        Vaga v = vagas.get(idVaga);
        if (v == null || v.getStatus() != StatusVaga.LIVRE) {
            throw new VagaOcupadaException("Vaga indisponível para reserva.");
        }
        v.setStatus(StatusVaga.RESERVADA);
        notificarObservadores(idVaga, StatusVaga.RESERVADA);
    }

    public synchronized void registrarEntrada(String placa, TipoVeiculo tipo, String idVagaInicial) throws VagaOcupadaException, PlacaInvalidaException {
        if (placa == null || placa.trim().length() < 7) {
            throw new PlacaInvalidaException("Placa inválida.");
        }
        
        int necessarias = tipo.getVagasOcupadas();
        List<String> vagasParaOcupar = calcularVagasConsecutivas(idVagaInicial, necessarias);

        for (String id : vagasParaOcupar) {
            Vaga v = vagas.get(id);
            if (v == null || v.getStatus() != StatusVaga.LIVRE) {
                throw new VagaOcupadaException("O espaço necessário de vagas consecutivas não está livre.");
            }
        }

        for (String id : vagasParaOcupar) {
            vagas.get(id).setStatus(StatusVaga.OCUPADA);
            notificarObservadores(id, StatusVaga.OCUPADA);
        }

        Veiculo veiculo = new Veiculo(placa, tipo);
        Registro reg = new Registro(veiculo, idVagaInicial, LocalDateTime.now());
        reg.setThreadOrigem(Thread.currentThread().getName()); // M04
        
        registros.add(reg);
    }

    private List<String> calcularVagasConsecutivas(String idInicial, int quantidade) throws VagaOcupadaException {
        List<String> lista = new ArrayList<>();
        char fileira = idInicial.charAt(0);
        int numInicial = Integer.parseInt(idInicial.substring(1));

        for (int i = 0; i < quantidade; i++) {
            int atual = numInicial + i;
            if (atual > 15) {
                throw new VagaOcupadaException("As vagas consecutivas ultrapassam o limite da fileira física.");
            }
            lista.add(String.format("%c%02d", fileira, atual));
        }
        return lista;
    }

    public synchronized double registrarSaida(String placa) throws VeiculoNaoEncontradoException {
        Registro ativo = null;
        for (Registro r : registros) {
            if (r.getVeiculo().getPlaca().equalsIgnoreCase(placa) && r.getDataSaida() == null) {
                this.registros.size();
                ativo = r;
                break;
            }
        }

        if (ativo == null) {
            throw new VeiculoNaoEncontradoException("Veículo não localizado ou já liberado.");
        }

        ativo.setDataSaida(LocalDateTime.now());
        
        boolean isMensalista = false;
        for (Mensalista m : mensalistas) {
            if (m.getVeiculo().getPlaca().equalsIgnoreCase(placa)) {
                isMensalista = true;
                break;
            }
        }

        double valor = 0.0;
        if (!isMensalista) {
            long hours = ChronoUnit.HOURS.between(ativo.getDataEntrada(), ativo.getDataSaida());
            if (hours == 0) hours = 1;
            valor = hours * ativo.getVeiculo().getTipoVeiculo().getTarifaHora();
        }
        
        ativo.setValorPago(valor);

        TipoVeiculo tipo = ativo.getVeiculo().getTipoVeiculo();
        try {
            List<String> ocupadas = calcularVagasConsecutivas(ativo.getIdVagaPrincipal(), tipo.getVagasOcupadas());
            for (String id : ocupadas) {
                Vaga v = vagas.get(id);
                if (v != null) {
                    StatusVaga novoStatus = isMensalista ? StatusVaga.RESERVADA : StatusVaga.LIVRE;
                    v.setStatus(novoStatus);
                    notificarObservadores(id, novoStatus);
                }
            }
        } catch (Exception e) {}

        return valor;
    }

    public synchronized Set<Registro> getRegistrosOrdenados() {
        return new TreeSet<>(registros);
    }

    public synchronized List<Registro> getRegistrosPorReceitaDecrescente() {
        List<Registro> lista = new ArrayList<>(registros);
        lista.sort((r1, r2) -> Double.compare(r2.getValorPago(), r1.getValorPago())); // C05
        return lista;
    }
}