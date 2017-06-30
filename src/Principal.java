import java.net.*;
import java.util.*;
import java.io.*;

public class Principal {

	public static Servidor servidor;
	public static Cliente cliente;
	private static Scanner entrada;

	public static void iniciarServidor() {
		try {
			servidor = new Servidor();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(servidor).start();
	}

	public static void iniciarCliente(String nomeArq) {
		try {
			cliente = new Cliente(nomeArq);
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
			System.out.println("\tDisseminação eficiente de arquivo\n" + "1. Abrir Arquivo metadados\n"
					+ "2. Visualizar status das transmissões\n" + "3. Gerar Arquivo Metadados\n" + "0. Sair"
					+ "\nOpcao:\n");
			opcao = entrada.nextInt();
			switch (opcao) {
			case 0:
				break;
			case 1:
				entrada = new Scanner(System.in);
				System.out.println("Digite o nome do arquivo Metadados:");
				String nomeArq = entrada.next();

				if(Servidor.verificaExistenciaArquivo(nomeArq)){
					iniciarCliente(nomeArq);
				}
				else {
					System.out.println("Arquivo inválido"); // Mudar isso depois
				}


				break;
			case 2:
				break;
			case 3:
				System.out.println("Digite o nome do arquivo");
				String nomeArqM = entrada.next();
				String ipLocal = getIP();
				// System.out.println(nomeArq);
				//System.out.println(">>>" + ipLocal);
				Metadados.criaArquivoMetadados(ipLocal, 1234, nomeArqM, nomeArqM);
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


		iniciarServidor();
		Cliente.retornaIpRastreador("foto1.jpg.torrent");
		menu();
		System.out.println("saiu");

	}
}
