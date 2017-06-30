import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Cliente implements Runnable {

	String endCliente;
	int portoCliente;

	public DatagramSocket soqueteCliente;
	public String ipServidor; // ip_do_vizinho
	public boolean clienteTerminou = false;
	public int porto = 1234;
	private static Scanner entrada;
	public String nomeArq;

	public Cliente(String nomeArq ) throws Exception {
		this.nomeArq=nomeArq;
		soqueteCliente = new DatagramSocket();
		endCliente = getIP();
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

	@Override
	public void run() {
		while (!clienteTerminou) {
			//String nomeArquivo = "oi paula";// nome dentro do metadados
			try {
				solicitaPecas(nomeArq);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void solicitaPecas(String nomeArquivo) throws UnknownHostException { // ip
																					// e
																					// porto
																					// como
																					// paramentros?
		try {
			ipServidor = getEnderecoParVizinho();
			InetAddress enderecoServidor = InetAddress.getByName(ipServidor);
			//System.out.println(enderecoServidor);

			DatagramPacket pacoteRequisicao = new DatagramPacket(nomeArquivo.getBytes(), nomeArquivo.length(),
					enderecoServidor, this.porto);
			soqueteCliente.send(pacoteRequisicao);
			//System.out.println("enviando req");
			DatagramPacket pacoteResposta = new DatagramPacket(new byte[25600], 25600);
			soqueteCliente.receive(pacoteResposta);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getEnderecoParVizinho() {
		// TODO Auto-generated method stub
		return "192.168.7.106";
	}

	public static void retornaIpRastreador(String nomeArqMetadados) {
		String caminho = new File("").getAbsolutePath(); // Cada usuario escolhe a pasta que ira conter os dados p serem compartilhados
		String declaracao;
		try {
			FileReader arq = new FileReader(caminho + "\\"+nomeArqMetadados);
			BufferedReader lerArq = new BufferedReader(arq);
			String linha = lerArq.readLine();
			declaracao = linha.substring(12, linha.length());
			//System.out.println("Declaração:" + declaracao);
			String ip = declaracao.substring(0,declaracao.indexOf('-'));
			System.out.println("ip:" + ip);
			arq.close();
		}

	 catch (IOException e) {
		System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
	}
	}

}
