# ParkSys — Sistema de Gestão de Estacionamento

Sistema desenvolvido como projeto integrador da disciplina ARQDEOO, unindo os principais conceitos estudados ao longo do semestre em uma aplicação desktop completa com interface gráfica Swing.

O ParkSys permite registrar a entrada e saída de veículos, gerenciar 30 vagas distribuídas em duas fileiras (A e B), cadastrar mensalistas com vaga reservada, calcular tarifas automaticamente por tipo de veículo e emitir relatórios financeiros exportáveis em `.txt`.

---

## Tecnologias

- **Java 21**
- **IDE:** Eclipse
- **Interface gráfica:** Java Swing
- **Persistência:** Serialização Java (`.ser`)

---

## Estrutura de Pacotes

| Pacote | Responsabilidade |
|---|---|
| `parksys.main` | Ponto de entrada da aplicação (`Principal.java`) |
| `parksys.entities` | Entidades de domínio: `Vaga`, `Veiculo`, `Registro`, `Mensalista` |
| `parksys.enums` | Enumerações com dados de negócio: `TipoVeiculo`, `StatusVaga` |
| `parksys.exceptions` | Exceções personalizadas do domínio |
| `parksys.services` | Lógica de negócio, persistência e tarefas concorrentes |
| `parksys.observer` | Interface e implementação do padrão Observer |
| `parksys.ui` | Telas Swing — sem lógica de negócio |

---

## Como Executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/parksys.git
   ```

2. Abra o Eclipse e importe o projeto:
   - `File > Import > Existing Projects into Workspace`
   - Selecione a pasta `Parksys`

3. Certifique-se de que o projeto está configurado com **Java 21**:
   - Clique com o botão direito no projeto > `Properties > Java Build Path`

4. Execute a classe `parksys.main.Principal`:
   - Clique com o botão direito em `Principal.java > Run As > Java Application`

> Os arquivos de persistência (`estacionamento.ser`) são gerados automaticamente na raiz do projeto ao fechar a aplicação.

---

## Conceitos Aplicados

### Enums com dados de negócio
`TipoVeiculo` armazena tarifa por hora e número de vagas ocupadas em cada constante. Exemplo: `TipoVeiculo.CAMINHAO.getTarifaHora()` retorna `30.0` e `getVagasOcupadas()` retorna `3`. Isso elimina qualquer valor fixo no código.

### Collections
- `HashMap<String, Vaga>` — acesso O(1) por ID de vaga (ex: `"A01"`)
- `ArrayList<Registro>` — mantém a ordem de chegada dos veículos
- `LinkedList<Mensalista>` — eficiente para inserção e remoção frequente nas pontas
- `TreeSet<Registro>` — retorna registros em ordem cronológica crescente automaticamente, pois `Registro` implementa `Comparable`

### Serialização
Todas as entidades implementam `Serializable` com `serialVersionUID = 1L`. O campo `threadOrigem` em `Registro` é marcado como `transient` e não é persistido. O `GerenciadorArquivo` utiliza `ObjectOutputStream` e `ObjectInputStream` com blocos `try-catch-finally` para garantir o fechamento dos streams.

### Multithreading
`EntradaRunnable` implementa `Runnable` e simula o processamento de entrada com `Thread.sleep(200)`. Os métodos do `GerenciadorEstacionamento` que acessam as coleções são `synchronized` para evitar race conditions. O `MonitorRunnable` é uma thread daemon que imprime o status das vagas a cada 1 segundo e é encerrada com `interrupt()`.

### Padrões de Projeto
- **Singleton:** `GerenciadorEstacionamento.getInstance()` garante que todas as telas compartilham a mesma instância do gerenciador.
- **Observer:** `EstacionamentoObserver` desacopla o gerenciador das telas. O `PainelMonitor` implementa a interface e atualiza o painel visual de vagas em tempo real sempre que uma vaga muda de status.
- **MVC:** As classes do pacote `ui` não contêm lógica de negócio; toda operação passa pelos métodos do `GerenciadorEstacionamento`.

---

## Branches

| Branch | O que foi implementado |
|---|---|
| `main` | Setup inicial, `.gitignore` e estrutura de pacotes vazia |
| `feature/enums` | `TipoVeiculo` e `StatusVaga` com dados de negócio (T01–T05) |
| `feature/entities` | `Vaga`, `Veiculo`, `Registro`, `Mensalista` com `Serializable` (S01) |
| `feature/services` | `GerenciadorEstacionamento` com Collections (C01–C06) |
| `feature/serializacao` | `GerenciadorArquivo` com try-catch-finally (S02–S06) |
| `feature/threads` | `EntradaRunnable`, `MonitorRunnable`, `synchronized` (M01–M07) |
| `feature/patterns` | Singleton, Observer, MVC e `PainelMonitor` (P01–P06) |
| `feature/ui` | Todas as telas Swing e integração final |

---

## Autor

**Isabelle Nicomedis**
Sistemas para Internet — 3º Semestre
Instituto Federal de São Paulo — Campus Araraquara
Disciplina: ARQDEOO — Prof. Junior Fernandes Marques
