import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Metadados {
	/** Estrutura do Metadados **/
	private String ipTracker; // Ip do Tracker
	private int portTracker;// Porto do Tracker
	private String nomeArquivo;// NOme do arquivo,endereço dele
	private String arquivoSaida;// A saida

	private float tamanhoArquivo;
	public static final int TAMANHO_PECA = 1000; // tamanho da peça
	public static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
	// private byte[] novos;

	private Metadados() {
	}

	public String getIpTracker() {
		return ipTracker;
	}

	public void setIpTracker(String ipTracker) {
		this.ipTracker = ipTracker;
	}

	public int getPortTracker() {
		return portTracker;
	}

	public void setPortTracker(int portTracker) {
		this.portTracker = portTracker;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getArquivoSaida() {
		return arquivoSaida;
	}

	public void setArquivoSaida(String arquivoSaida) {
		this.arquivoSaida = arquivoSaida;
	}

	public static Metadados carregaArquivoMetadados(String nomeArquivoMetadados) {
		Metadados arquivoMeta = new Metadados();
		// ler na mesma ordem que foi escrito

		return arquivoMeta;
	}

	public static Metadados criaArquivoMetadados(String ipTracker, int portTracker, String nomeArquivo,
			String arquivoSaida) throws Exception {
		Metadados arquivoMeta = new Metadados();

		arquivoMeta.setIpTracker(ipTracker);
		arquivoMeta.setPortTracker(portTracker);
		arquivoMeta.setNomeArquivo(nomeArquivo);
		arquivoMeta.setArquivoSaida(arquivoSaida);
		// int chave=0;
		// Inicia com zero
		// int fim=TAMANHO_PECA;
		// Fim inicialmente é o tamanho da peça
		String hash = "";
		// Hash inicia com vazio

		Path path = Paths.get(nomeArquivo);
		// Pacote peg o endereço do arquivo
		File arquivoDados = path.toFile();


		// byte[] bytes=Files.readAllBytes(path);
		//System.out.println("Existe: " + arquivoDados.exists());
		//System.out.println(arquivoDados.length());
		arquivoMeta.tamanhoArquivo = arquivoDados.length();

		RandomAccessFile entradaDados = new RandomAccessFile(arquivoDados, "rw");
		// output = new BufferedOutputStream(new FileOutputStream(new
		// File(arquivoSaida)));

		BufferedWriter saidaMetaDados = new BufferedWriter(new FileWriter(arquivoSaida + ".torrent"));
		//System.out.println(arquivoDados.length());
		int quantidadePecas = (int) Math.ceil((double) arquivoMeta.tamanhoArquivo / TAMANHO_PECA);

		saidaMetaDados.append("declaracao:" + arquivoMeta.getIpTracker() + "-" + arquivoMeta.getPortTracker() + "\n");
		saidaMetaDados.append("nome:" + arquivoDados.getName() + "\n");
		saidaMetaDados.append("tamanho:" + arquivoDados.length() + "\n");
		saidaMetaDados.append("quantidade peça:" + quantidadePecas + "\n");
		saidaMetaDados.append("tamanho da peça:" + TAMANHO_PECA + "\n");

		// saidaMetaDados.append("hash das peças:\n");

		byte[] bufferPeca = new byte[TAMANHO_PECA];

		for (int idPeca = 0; idPeca < quantidadePecas; idPeca++) {
			//System.out.println("id da peça:" + idPeca);
			bufferPeca = readPeca(idPeca, entradaDados);

			//hash = geraChave(criaP(bufferPeca, idPeca, TAMANHO_PECA)) + "\n";
			hash = geraChave(bufferPeca) + "\n";
			saidaMetaDados.append(hash);
		} // for cada peca uma a uma

		saidaMetaDados.flush();
		saidaMetaDados.close();
		return arquivoMeta;
	}

	public static int bytesLidos;

	public static byte[] readPeca(int pecaID, RandomAccessFile inputArquidoDados) throws IOException {
		int offsetBytes = pecaID * TAMANHO_PECA;
		/*
		 * input.skip( offsetBytes ); byte[] peca = new byte[tamanhoP];
		 * input.read(peca); return peca;
		 */
		byte[] pecaBytes = new byte[TAMANHO_PECA];
		// vetor para adicionar o tamanho da peça do arquivo principal
		// o começo da peça
		
		inputArquidoDados.seek(offsetBytes);
		bytesLidos = inputArquidoDados.read(pecaBytes, 0, TAMANHO_PECA);
		// System.out.println("bytes lidos:" + bytesLidos);
		
		if(bytesLidos != TAMANHO_PECA){ //ultima peça
			byte[] ultimaPecaBytes = Arrays.copyOf(pecaBytes, bytesLidos);
			return ultimaPecaBytes;
		}
		else	
			return pecaBytes;
	}

	public static void writePeca(int pecaID, byte[] pecaBytes, RandomAccessFile output) throws IOException {
		int offsetBytes = pecaID * TAMANHO_PECA;
		output.write(pecaBytes, offsetBytes, TAMANHO_PECA);
	}

	public static byte[] criaP(byte[] bytes, int id, int fim) {
		//System.out.println("função da peça");
		byte[] novos = new byte[TAMANHO_PECA];
		int j = 0;
		for (int i = id; i < fim; i++) {
			novos[j] = bytes[i];
			j++;
		}
		return novos;
	}

	public static String geraChave(byte[] novo) throws NoSuchAlgorithmException {
		//System.out.println("entrou no geraHash");
		/*
		 * int id=novo.hashCode();
		 * 
		 * System.out.println(id);
		 */
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] buf = md.digest(novo);
		char[] chars = new char[2 * buf.length];
		for (int i = 0; i < buf.length; ++i) {
			chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}
		return new String(chars);
	}

}
