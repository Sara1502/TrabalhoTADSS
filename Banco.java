import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Banco {

    static class Cliente extends Thread {
        private final int tempoChega;
        private final int tempoAtt;

        public Cliente(int tempoChega, int tempoAtt) {
            this.tempoChega = tempoChega;
            this.tempoAtt = tempoAtt;
        }

        public int getTempoChega() {
            return tempoChega;
        }

        public int getTempoAtt() {
            return tempoAtt;
        }

        @Override
        public void run() {
            try {
                // Simula o tempo de atendimento
                Thread.sleep(tempoAtt * 1000); // Converte o tempoAtt de segundos para milissegundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final int horaComeco = 11 * 60 * 60;
        final int horaFim = 13 * 60 * 60;
        final int tempoMaxEsp = 120;
        final int tempoMaxServ = 120;
        final int tempoMinServ = 30;
        final int intervaloMax = 50;
        final int intervaloMain = 5;

        Random random = new Random();
        List<Cliente> clientes = new ArrayList<>();

        // Simula a chegada de clientes
        int currentTime = horaComeco;
        while (currentTime <= horaFim) {
            if (random.nextDouble() < 0.5) {
                int tempoChega = currentTime;
                int tempoAtt = tempoMinServ + random.nextInt(tempoMaxServ - tempoMinServ + 1);

                // Adiciona cliente de forma sincronizada
                synchronized (clientes) {
                    Cliente cliente = new Cliente(tempoChega, tempoAtt);
                    clientes.add(cliente);
                }
            }

            currentTime += intervaloMain + random.nextInt(intervaloMax - intervaloMain + 1);
        }

        // Inicia o atendimento dos clientes
        for (Cliente cliente : clientes) {
            cliente.start(); // Inicia a thread para cada cliente
        }

        // Espera que todos os clientes sejam atendidos
        for (Cliente cliente : clientes) {
            try {
                cliente.join(); // Aguarda a finalização do atendimento de cada cliente
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Estatísticas
        int totalClientes;
        int tempoEspMax = 0;
        int maxTempServ = 0;
        int tempoTotalEsper = 0;
        int tempoTotalServ = 0;

        // Leitura da lista de clientes de forma sincronizada
        synchronized (clientes) {
            totalClientes = clientes.size();

            for (Cliente cliente : clientes) {
                int tempoEsp = cliente.getTempoChega() - horaComeco;
                int tempoAtt = cliente.getTempoAtt();
                tempoEspMax = Math.max(tempoEspMax, tempoEsp);
                maxTempServ = Math.max(maxTempServ, tempoAtt);
                tempoTotalEsper += tempoEsp;
                tempoTotalServ += tempoAtt;
            }
        }

        double tempoMedioNoBanco = (double) (tempoTotalEsper + tempoTotalServ) / totalClientes;
        double tempoEspMedio = (double) tempoTotalEsper / totalClientes;

        // Imprime as estatísticas
        System.out.println("Número de clientes atendidos: " + totalClientes);
        System.out.println("Tempo máximo de espera: " + tempoEspMax + " segundos");
        System.out.println("Tempo máximo de atendimento: " + maxTempServ + " segundos");
        System.out.println("Média de tempo dentro do banco: " + tempoMedioNoBanco + " segundos");
        System.out.println("Média de tempo de espera: " + tempoEspMedio + " segundos");

        boolean objetivo = tempoEspMax <= tempoMaxEsp;
        System.out.println("O objetivo foi atingido? " + (objetivo ? "Sim" : "Não"));
    }
}
