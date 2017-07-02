import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

public class Cliente implements Runnable {

    String endCliente;
    int portoCliente;

    public List<String> codigosHash = new ArrayList<>();
    public List<Integer> pecasPendentes = new ArrayList<>();
    public String nomeArq;
    public DatagramSocket soqueteCliente;
    public String ipServidor; //ip_do_vizinho
    public boolean clienteTerminou = false;
    public int porto = 1234;

    public Cliente(String nomeArq) throws Exception {
        this.nomeArq = nomeArq;
        soqueteCliente = new DatagramSocket();
        endCliente = getIP();
    }

    private static String getIP() throws UnknownHostException, SocketException {
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements(); ) {
            NetworkInterface e = n.nextElement();
            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements(); ) {
                InetAddress addr = a.nextElement();
                if (!addr.getHostAddress().contains("127.0.0.1") && !addr.getHostAddress().contains(":")) {
                    return addr.getHostAddress();
                }
            }
        }
        return InetAddress.getLocalHost().getHostAddress();// 127.0.0.1
    }

    @Override
    public void run() {
        while (!clienteTerminou) {
            try {
                solicitaPecas(nomeArq);
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void solicitaPecas(String nomeArquivoTorrent) throws UnknownHostException { // ip e porto como paramentros?

        try {

            // Passo 1 - Envia uma mensagem p ip do ArqTorrent com #+nomeArqTorrent
            ipServidor = getEnderecoRastreador(nomeArquivoTorrent);
            InetAddress enderecoServidor = InetAddress.getByName(ipServidor);
            String nomeArqOriginal = "#" + retornaNomeArq(nomeArquivoTorrent);
            DatagramPacket pacoteRequisicao = new DatagramPacket((nomeArqOriginal).getBytes(), nomeArqOriginal.length(), enderecoServidor, porto);
            soqueteCliente.send(pacoteRequisicao);

            //Recebe como resposta os pares que possuem alguma peça daquele arquivo
            DatagramPacket pacoteResposta = new DatagramPacket(new byte[256], 256);
            soqueteCliente.receive(pacoteResposta);
            String dadosResposta = new String(pacoteResposta.getData()).trim();
            System.out.println("Dados resposta: " + dadosResposta);
            String[] ipPares = dadosResposta.split("-");
            //System.out.println("Quantidade:" +ipPares.length);


            //ARRUMAR ISSO DEPOIS
            String nomeArqePecas = "$" + retornaNomeArq(nomeArquivoTorrent)+"/0-30";
            DatagramPacket pacoteRequisicaoPecas = new DatagramPacket((nomeArqePecas).getBytes(), nomeArqePecas.length(), enderecoServidor, porto);
            soqueteCliente.send(pacoteRequisicaoPecas);

            System.out.println("nome: "+nomeArqOriginal);
            Path path = Paths.get(nomeArqOriginal);

            File arquivoDados = path.toFile();
            RandomAccessFile arq = new RandomAccessFile(arquivoDados,"rw");
            int i=0;
            while(i<30){
                DatagramPacket peca = new DatagramPacket(new byte[1000], 1000);
                soqueteCliente.receive(peca);

                System.out.println(peca);
                String g = Metadados.geraChave(peca.getData());
                System.out.println(i + "> " + g);
               // writePeca(i,peca.getData(),arq);
                i++;
            }



            //DatagramPacket pacoteResposta = new DatagramPacket(new byte[25600], 25600);
            //soqueteCliente.receive(pacoteResposta);
            clienteTerminou = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
        }

    }

    public int sorteiaIndicePeca() {
        if (this.pecasPendentes.size() > 1) {
            return new Random().nextInt(this.pecasPendentes.size());
        }
        return 0;
    }

    public void comparaPeca(byte[] recebi) {
        try {
            String hashRecebida = Metadados.geraChave(recebi);
            boolean tem = codigosHash.contains(hashRecebida);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String getEnderecoRastreador(String nomeArqTorrent) {
        String caminho = new File("").getAbsolutePath();
        String declaracao;
        String[] IpPorto = new String[2];
        String nome = "";
        try {
            FileReader arq = new FileReader(caminho + "\\" + nomeArqTorrent);
            BufferedReader lerArq = new BufferedReader(arq);

            String linha = lerArq.readLine();
            declaracao = linha.substring(11, linha.length());
            IpPorto = declaracao.split("-", 2);
            linha = lerArq.readLine(); // lê da segunda até a última linha
            nome = linha.substring(5, linha.length());

            arq.close();
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
        return IpPorto[0];

    }

    public void lerArquivo(int comecaLerLinha) {
        int contLinha = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(nomeArq + ".torrent"));
            while (br.ready()) {
                if (contLinha >= comecaLerLinha) {
                    String linha = br.readLine();
                    codigosHash.add(linha);
                }
                contLinha++;
            }
            br.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public static String retornaNomeArq(String nomeArqTorrent) {
        String caminho = new File("").getAbsolutePath();
        String declaracao;
        String nome = "";
        try {
            FileReader arq = new FileReader(caminho + "\\" + nomeArqTorrent);
            BufferedReader lerArq = new BufferedReader(arq);

            String linha = lerArq.readLine();
            declaracao = linha.substring(12, linha.length());
            linha = lerArq.readLine(); // lê da segunda até a última linha
            nome = linha.substring(5, linha.length());

            arq.close();
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
        return nome;
    }

    public static void writePeca(int pecaID, byte[] pecaBytes, RandomAccessFile output) throws IOException {
        int offsetBytes = pecaID * Metadados.TAMANHO_PECA;
        output.seek(offsetBytes);
        output.write(pecaBytes, offsetBytes, Metadados.TAMANHO_PECA);
        //output.close();
    }
}
