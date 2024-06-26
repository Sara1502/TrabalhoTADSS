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
                Thread.sleep(tempoAtt * 1000);
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

        int currentTime = horaComeco;
        while (currentTime <= horaFim) {
            if (random.nextDouble() < 0.5) {
                int tempoChega = currentTime;
                int tempoAtt = tempoMinServ + random.nextInt(tempoMaxServ - tempoMinServ + 1);

                synchronized (clientes) {
                    Cliente cliente = new Cliente(tempoChega, tempoAtt);
                    clientes.add(cliente);
                }
            }

            currentTime += intervaloMain + random.nextInt(intervaloMax - intervaloMain + 1);
        }

        for (Cliente cliente : clientes) {
            cliente.start();
        }

        for (Cliente cliente : clientes) {
            try {
                cliente.join(); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int totalClientes;
        int tempoEspMax = 0;
        int maxTempServ = 0;
        int tempoTotalEsper = 0;
        int tempoTotalServ = 0;

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

        System.out.println("Número de clientes atendidos: " + totalClientes);
        System.out.println("Tempo máximo de espera: " + tempoEspMax + " segundos");
        System.out.println("Tempo máximo de atendimento: " + maxTempServ + " segundos");
        System.out.println("Média de tempo dentro do banco: " + tempoMedioNoBanco + " segundos");
        System.out.println("Média de tempo de espera: " + tempoEspMedio + " segundos");

        boolean objetivo = tempoEspMax <= tempoMaxEsp;
        System.out.println("O objetivo foi atingido? " + (objetivo ? "Sim" : "Não"));
    }
}
