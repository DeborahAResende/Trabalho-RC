import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Servidor implements Runnable {

	// public static List<Pares> ListaPares = new ArrayList<>();
	public final int porto = 1234;
	public boolean servidorterminou = false;
	DatagramSocket soquete;

	public Servidor() throws SocketException {
		// registra o servidor no Sistema Operacional sob o porto 8484
		soquete = new DatagramSocket(this.porto);
	}

	public static boolean verificaExistenciaArquivo(String nomeArq) {// quem usa servidor
		String caminho = new File("").getAbsolutePath();
		File file = new File(caminho + "/" + nomeArq);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		while (!servidorterminou) {
			proverPecas();
		}
	}

	private void proverPecas() {

		try {

			DatagramPacket pacoteRequisicao = new DatagramPacket(new byte[256], 256);
			soquete.receive(pacoteRequisicao);// recebeu o nome do arquivo
			String pacoteRecebido = new String(pacoteRequisicao.getData()).trim();
			//System.out.println("Pacote Recebido: " + pacoteRecebido);
			if (pacoteRecebido.contains("#")) {
				pacoteRecebido = pacoteRecebido.substring(1, pacoteRecebido.length());
				//System.out.println("Pacote Recebido#: " + pacoteRecebido);
				if (verificaExistenciaArquivo(pacoteRecebido)) {
					Pares p = new Pares();
					p = retornaParesdoArq(pacoteRecebido);
					if (p != null) {
						String dadosResposta = "";
						for (String s : p.ip) {
							dadosResposta = dadosResposta + s + "-";
						}
						DatagramPacket resposta = new DatagramPacket(dadosResposta.getBytes(),
								dadosResposta.getBytes().length, pacoteRequisicao.getAddress(),
								pacoteRequisicao.getPort());
						soquete.send(resposta);
					}
				} else {
					String dadosResposta = "O arquivo desejado não pode ser localizado";
					DatagramPacket resposta = new DatagramPacket(dadosResposta.getBytes(),
							dadosResposta.getBytes().length, pacoteRequisicao.getAddress(), pacoteRequisicao.getPort());
					soquete.send(resposta);
				}

			}
			else if (pacoteRecebido.contains("$")) {
				pacoteRecebido = pacoteRecebido.substring(1, pacoteRecebido.length());
				String nomeArq = pacoteRecebido.substring(0, pacoteRecebido.indexOf("/"));

				//System.out.println("Nome: " + nomeArq);
				int intervaloInicial=0;
				int intervaloFinal=0;
				intervaloInicial = Integer.parseInt(pacoteRecebido.substring(nomeArq.length()+1, pacoteRecebido.indexOf("-")));
				intervaloFinal =Integer.parseInt(pacoteRecebido.substring(pacoteRecebido.indexOf("-")+1, pacoteRecebido.length()));
				System.out.println("Valor i:" + intervaloInicial);
				System.out.println("Valor f:" + intervaloFinal);

				Path path = Paths.get(nomeArq);
				File arquivoDados = path.toFile();
				RandomAccessFile arq = new RandomAccessFile(arquivoDados, "r");

				for (int i=intervaloInicial;i<intervaloFinal;i++) {
					byte[] x =readPeca(i,arq);
					//System.out.println("aqui");
					System.out.println(x.toString());
					DatagramPacket resposta = new DatagramPacket(x, x.length, pacoteRequisicao.getAddress(), pacoteRequisicao.getPort());
					soquete.send(resposta);
				}


			}

			// System.out.println(pacoteRecebido);
			// addListaPares(pacoteRequisicao, pacoteRecebido);

			// readPeca();

			// TODO ACHAR A PE�A CERTA
			// byte[] enviar = new byte[25600];

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addListaPares(DatagramPacket pacoteRequisicao, String pacoteRecebido) {
		String ip = pacoteRequisicao.getSocketAddress().toString();
		ip = ip.substring(1, ip.indexOf(':'));
		// System.out.println(ip);
		// ListaPares.get(pacoteRecebido.i)
	}


	public static byte[] readPeca(int pecaID, RandomAccessFile inputArquidoDados) throws IOException {
		int offsetBytes = pecaID * Metadados.TAMANHO_PECA;
			/*
			 * input.skip( offsetBytes ); byte[] peca = new byte[tamanhoP];
			 * input.read(peca); return peca;
			 */
		byte[] pecaBytes = new byte[Metadados.TAMANHO_PECA];
		// vetor para adicionar o tamanho da peça do arquivo principal
		// o começo da peça

		inputArquidoDados.seek(offsetBytes);
		int bytesLidos = inputArquidoDados.read(pecaBytes, 0, Metadados.TAMANHO_PECA);
		// System.out.println("bytes lidos:" + bytesLidos);

		if(bytesLidos != Metadados.TAMANHO_PECA){ //ultima peça
			byte[] ultimaPecaBytes = Arrays.copyOf(pecaBytes, bytesLidos);
			return ultimaPecaBytes;
		}
		else
			return pecaBytes;
	}




	/*
	 * byte[] pecaBytes = new byte[Metadados.TAMANHO_PECA]; // vetor para adicionar
	 * o tamanho da peça do arquivo principal // o começo da peça
	 *
	 * inputArquidoDados.seek(offsetBytes); bytesLidos =
	 * inputArquidoDados.read(pecaBytes, 0, Metadados.TAMANHO_PECA); //
	 * System.out.println("bytes lidos:" + bytesLidos);
	 *
	 * if(bytesLidos != Metadados.TAMANHO_PECA){ //ultima peça byte[]
	 * ultimaPecaBytes = Arrays.copyOf(pecaBytes, bytesLidos); return
	 * ultimaPecaBytes; } else return pecaBytes; }
	 */

	public static boolean salvarParesRastrear(Pares p) {
		FileWriter arquivo;
		String texto = "";
		try {
			arquivo = new FileWriter(new File("rastreador.txt"), true);
			texto = p.nomeAquivo + "-";
			for (String x : p.ip) {
				texto = texto + x + "-";
			}
			// So \n basta no linux
			arquivo.write(texto + "\r\n");
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public Pares retornaParesdoArq(String nomeArq) {
		String caminho = new File("").getAbsolutePath();
		String[] conteudoLinhaSeparado;
		List<Pares> ListaPares = new ArrayList<>();
		try {
			FileReader arq = new FileReader(caminho + "\\" + "rastreador.txt");
			BufferedReader lerArq = new BufferedReader(arq);
			String linha = lerArq.readLine();
			// System.out.println("linha: "+linha);
			conteudoLinhaSeparado = linha.split("-");
			// System.out.println("qtd: "+conteudoLinhaSeparado[0]);
			Pares p = new Pares();
			p.nomeAquivo = conteudoLinhaSeparado[0];
			for (int i = 0; i < conteudoLinhaSeparado.length - 1; i++) {
				p.ip.add(conteudoLinhaSeparado[i + 1]);
			}
			ListaPares.add(p);

			while (linha != null) {
				linha = lerArq.readLine();
				if (linha != null) {
					conteudoLinhaSeparado = linha.split(linha, '-');

					Pares p2 = new Pares();
					p2.nomeAquivo = conteudoLinhaSeparado[0];
					for (int i = 0; i < conteudoLinhaSeparado.length - 1; i++) {
						p2.ip.add(conteudoLinhaSeparado[i + 1]);
					}
					ListaPares.add(p2);
				}
			}
			arq.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Pares l : ListaPares) {
			if (l.nomeAquivo.equals(nomeArq)) {
				return l;
			}
		}
		return null;
	}

}