import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Servidor implements Runnable{

	public List<Pares> ListaPares = new ArrayList<>();
	public final int porto = 1234;
	public boolean servidorterminou = false;
	DatagramSocket soquete;

	public Servidor() throws SocketException {
		// registra o servidor no Sistema Operacional sob o porto 8484
		soquete = new DatagramSocket(this.porto);
	}

	public static boolean verificaExistenciaArquivo(String nomeArq) {//quem usa � servidor
		String caminho = new File("").getAbsolutePath();
		File file = new File(caminho +"/"+ nomeArq);
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

            DatagramPacket pacoteRequisicao = new DatagramPacket(new byte[256], 256);
			soquete.receive(pacoteRequisicao);//recebeu o nome da pe�a que ter� que mandar
            System.out.println("offset "+pacoteRequisicao.getOffset());
            String pacoteRecebido=new String(pacoteRequisicao.getData()).trim();
            System.out.println(pacoteRecebido);
            //readPeca(pacoteRequisicao.getOffset(), RandomAccessFile );

			System.out.println(pacoteRecebido);
			//addListaPares(pacoteRequisicao, pacoteRecebido);

			System.out.println("proveu");
			//readPeca();

			//TODO ACHAR A PE�A CERTA
			byte[] enviar = new byte[25600];
			DatagramPacket resposta = new DatagramPacket(enviar, enviar.length, pacoteRequisicao.getAddress(), pacoteRequisicao.getPort());
			soquete.send(resposta);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addListaPares(DatagramPacket pacoteRequisicao, String pacoteRecebido) {
		String ip = pacoteRequisicao.getSocketAddress().toString();
		ip = ip.substring(1, ip.indexOf(':'));
		//System.out.println(ip);
		//ListaPares.get(pacoteRecebido.i)
	}

	/*public static byte[] readPeca(int pecaID, RandomAccessFile inputArquidoDados) throws IOException {

		int offsetBytes = pecaID * Metadados.TAMANHO_PECA;
		/*
		 * input.skip( offsetBytes ); byte[] peca = new byte[tamanhoP];
		 * input.read(peca); return peca;
		 */
		/*byte[] pecaBytes = new byte[Metadados.TAMANHO_PECA];
		// vetor para adicionar o tamanho da peça do arquivo principal
		// o começo da peça

		inputArquidoDados.seek(offsetBytes);
		bytesLidos = inputArquidoDados.read(pecaBytes, 0, Metadados.TAMANHO_PECA);
		// System.out.println("bytes lidos:" + bytesLidos);

		if(bytesLidos != Metadados.TAMANHO_PECA){ //ultima peça
			byte[] ultimaPecaBytes = Arrays.copyOf(pecaBytes, bytesLidos);
			return ultimaPecaBytes;
		}
		else
			return pecaBytes;
	}*/

}