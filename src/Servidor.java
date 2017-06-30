import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Servidor implements Runnable{
	
	public final int porto = 1234;
	public boolean servidorterminou = false;
	DatagramSocket soquete;

	public Servidor() throws SocketException {
		// registra o servidor no Sistema Operacional sob o porto 8484
		soquete = new DatagramSocket(this.porto);
	}
	
	public static boolean verificaExistenciaArquivo(String nomeArq) {//quem usa � servidor
		String caminho = new File("").getAbsolutePath(); // Cada usuario escolhe a pasta que ira conter os dados p serem compartilhados
		File file = new File(caminho +"\\"+ nomeArq);
		System.out.println(file);


		if (file.exists()) {
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		while(!servidorterminou){
			proverPecas();
		}
	}

	private void proverPecas() {
		
		try {
			DatagramPacket pacoteRequisicao = new DatagramPacket(new byte[256], 256); //OLHAR TAMANHO INDENTIFICADOR DA PE�A
			soquete.receive(pacoteRequisicao);//recebeu o nome da pe�a que ter� que mandar

			
			//TODO ACHAR A PE�A CERTA
			byte[] enviar = new byte[25600]; 
			DatagramPacket resposta = new DatagramPacket(enviar, enviar.length, pacoteRequisicao.getAddress(), pacoteRequisicao.getPort());
			soquete.send(resposta);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}

}
