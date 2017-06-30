import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Cliente implements Runnable {

	String endCliente;
	int portoCliente;

	public String nomeArq;
	public DatagramSocket soqueteCliente;
	public String ipServidor; //ip_do_vizinho
	public boolean clienteTerminou = false;
	public int porto;

	public Cliente(String nomeArq) throws Exception {
		this.nomeArq = nomeArq;
		soqueteCliente = new DatagramSocket();
		endCliente = getIP();
	}

	private static String getIP() throws UnknownHostException, SocketException {
		Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
		for (; n.hasMoreElements();) {
			NetworkInterface e = n.nextElement();
			Enumeration<InetAddress> a = e.getInetAddresses();
			for (; a.hasMoreElements();) {
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

	private void solicitaPecas(String nomeArquivo) throws UnknownHostException { // ip e porto como paramentros?
		try {
			ipServidor = getEnderecoParVizinho();
			InetAddress enderecoServidor = InetAddress.getByName(ipServidor);

			DatagramPacket pacoteRequisicao = new DatagramPacket(nomeArquivo.getBytes(), nomeArquivo.length(),
					enderecoServidor, porto);
			soqueteCliente.send(pacoteRequisicao);
			DatagramPacket pacoteResposta = new DatagramPacket(new byte[25600], 25600);
			soqueteCliente.receive(pacoteResposta);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getEnderecoParVizinho() {
		// TODO Auto-generated method stub
		return null;
	}

}
