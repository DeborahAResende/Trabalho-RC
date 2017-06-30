import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
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
            int indicePeca = sorteiaIndicePeca();
			DatagramPacket pacoteRequisicao = new DatagramPacket(nomeArquivo.getBytes(), indicePeca,
            nomeArquivo.length(), enderecoServidor, porto);
			soqueteCliente.send(pacoteRequisicao);
			DatagramPacket pacoteResposta = new DatagramPacket(new byte[25600], 25600);
			soqueteCliente.receive(pacoteResposta);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int sorteiaIndicePeca(){
	    if(this.pecasPendentes.size()>1) {
            return new Random().nextInt(this.pecasPendentes.size());
        }
	    return 0;
    }

	public void comparaPeca(byte[] recebi){
        try {
            String hashRecebida = Metadados.geraChave(recebi);
            boolean tem = codigosHash.contains(hashRecebida);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

	private String getEnderecoParVizinho() {
        // TODO Auto-generated method stub
		return "192.168.7.106";
	}

    public void lerArquivo (int comecaLerLinha){
        int contLinha= 0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(nomeArq+".torrent"));
            while(br.ready()){
                if(contLinha >= comecaLerLinha) {
                    String linha = br.readLine();
                    codigosHash.add(linha);
                }
                contLinha++;
            }
            br.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

}
