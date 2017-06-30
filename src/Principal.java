import java.net.*;
import java.util.*;
import javax.swing.*;

public class Principal {

    public static Servidor servidor;
    public static Cliente cliente;
    private static Scanner entrada;
    public static String nomeArq;

    public static void iniciarServidor() {
        try {
            servidor = new Servidor();
        } catch (SocketException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new Thread(servidor).start();
    }

    public static void iniciarCliente(String nomeArquivo) {
        try {
            cliente = new Cliente(nomeArquivo);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new Thread(cliente).start();
    }

    public static void menu() throws Exception {// cliente
        int opcao;
        entrada = new Scanner(System.in);
        do {
            String a = JOptionPane.showInputDialog(null, "1. Abrir Arquivo metadados"
                    + "\n2. Visualizar status das transmissões\n3. Gerar Arquivo Metadados\n0. Sair"
                    + "\nOpcao:\n","Disseminação eficiente de arquivo",3);
            opcao = Integer.parseInt(a);
            switch (opcao) {
                case 1:
                    entrada = new Scanner(System.in);
                    System.out.println("Digite o nome do arquivo que deseja receber");
                    String nomeArq = entrada.next();

                    if(Servidor.verificaExistenciaArquivo(nomeArq)){
                        iniciarCliente(nomeArq);
                    }else{
                        JOptionPane.showMessageDialog(null, "Este arquivo não existe", "ERRO",2);
                    }
                    break;
                case 2:
                    break;
                case 3:
                    nomeArq = JOptionPane.showInputDialog("Digite o nome do arquivo");
                    if (Servidor.verificaExistenciaArquivo(nomeArq)) {
                        String ipLocal = getIP();
                        System.out.println(">>>" + ipLocal);
                        Metadados.criaArquivoMetadados(ipLocal, 1234, nomeArq, nomeArq);
                    }else{
                        JOptionPane.showMessageDialog(null, "Este arquivo não existe", "ERRO",2);
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private static String getIP() throws UnknownHostException, SocketException {
        // System.out.println("Host addr: " +
        // InetAddress.getLocalHost().getHostAddress()); // often returns
        // "127.0.0.1"
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements();) {
            NetworkInterface e = n.nextElement();
            // System.out.println("Interface: " + e.getName());
            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();) {
                InetAddress addr = a.nextElement();
                // System.out.println(" " + addr.getHostAddress());
                if (!addr.getHostAddress().contains("127.0.0.1") && !addr.getHostAddress().contains(":")) {
                    return addr.getHostAddress();
                }
            }
        }
        return InetAddress.getLocalHost().getHostAddress();// 127.0.0.1
    }

    public static void main(String[] args) throws Exception {
        // System.out.println(new File("Transmissoes").getAbsolutePath());
        // caminho absoluto da pasta Transmissoes
        iniciarServidor();
        menu();
        //System.out.println("saiu");
    }
}