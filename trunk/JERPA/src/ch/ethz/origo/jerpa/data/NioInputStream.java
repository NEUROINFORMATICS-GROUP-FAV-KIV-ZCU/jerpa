package ch.ethz.origo.jerpa.data;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * T��da pro abstrakci �ten� dat ze souboru s vyu�it�m <code>java.nio</code>
 * knihoven.
 * 
 * @author Jiri Kucera
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 11/17/2009
 * @since 0.1.0
 */
public final class NioInputStream {

	private ByteBuffer buffer;

	private ByteOrder byteOrder;

	/**
	 * Vytvo�� <code>NioInputStream</code> nad souborem zadan�m objektem typu
	 * <code>File</code>.<br/>
	 * Po�ad� byt� je Big Endian.
	 * 
	 * @param file
	 *          Vstupn� soubor.
	 * @throws java.io.IOException
	 */
	public NioInputStream(String file) throws IOException {
		this(new File(file));
	}

	/**
	 * Vytvo�� <code>NioInputStream</code> nad souborem zadan�m n�zvem.
	 * 
	 * @param file
	 *          N�zev nebo cesta k souboru.
	 * @param byteOrder
	 *          Po�ad� byt�.
	 * @throws java.io.IOException
	 */
	public NioInputStream(String file, ByteOrder byteOrder) throws IOException {
		this(new File(file), byteOrder);
	}

	/**
	 * Vytvo�� <code>NioInputStream</code> nad souborem zadan�m n�zvem.<br/>
	 * Po�ad� byt� je Big Endian.
	 * 
	 * @param file
	 *          N�zev nebo cesta k souboru.
	 * @throws java.io.IOException
	 */
	public NioInputStream(File file) throws IOException {
		this(new FileInputStream(file.getAbsolutePath()).getChannel());
	}

	/**
	 * Vytvo�� <code>NioInputStream</code> nad souborem zadan�m objektem typu
	 * <code>File</code>.
	 * 
	 * @param file
	 *          Vstupn� soubor.
	 * @param byteOrder
	 *          Po�ad� byt�.
	 * @throws java.io.IOException
	 */
	public NioInputStream(File file, ByteOrder byteOrder) throws IOException {
		this(new FileInputStream(file.getAbsolutePath()).getChannel(), byteOrder);
	}

	/**
	 * Vytvo�� <code>NioInputStream</code> nad otev�en�m streamem typu
	 * <code>FileInputStream</code>.<br/>
	 * Po�ad� byt� je Big Endian.
	 * 
	 * @param fileInputStream
	 *          Vstupn� stream.
	 * @throws java.io.IOException
	 */
	public NioInputStream(FileInputStream fileInputStream) throws IOException {
		this(fileInputStream.getChannel());
	}

	/**
	 * Vytvo�� <code>NioInputStream</code> nad otev�en�m streamem typu
	 * <code>FileInputStream</code>.
	 * 
	 * @param fileInputStream
	 *          Vstupn� stream.
	 * @param byteOrder
	 *          Po�ad� byt�.
	 * @throws java.io.IOException
	 */
	public NioInputStream(FileInputStream fileInputStream, ByteOrder byteOrder)
			throws IOException {
		this(fileInputStream.getChannel(), byteOrder);
	}

	/**
	 * Vytvo�� <code>NioInputStream</code> nad kan�lem zadan�m objektem typu
	 * <code>FileChannel</code>.<br/>
	 * Po�ad� byt� je Big Endian.
	 * 
	 * @param fileChannel
	 *          Kan�l.
	 * @throws java.io.IOException
	 */
	public NioInputStream(FileChannel fileChannel) throws IOException {
		this(fileChannel, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Vytvo�� <code>NioInputStream</code> nad kan�lem zadan�m objektem typu
	 * <code>FileChannel</code>.
	 * 
	 * @param fileChannel
	 *          Kan�l.
	 * @param byteOrder
	 *          Po�ad� byt�.
	 * @throws java.io.IOException
	 */
	public NioInputStream(FileChannel fileChannel, ByteOrder byteOrder)
			throws IOException {
		buffer = ByteBuffer.allocateDirect((int) fileChannel.size());
		fileChannel.read(buffer);
		fileChannel.close();
		buffer.position(0);
		buffer.order(byteOrder);
		this.byteOrder = byteOrder;
	}

	/**
	 * Vytvo�� <code>NioInputStream</code> nad polem bajt�.
	 * 
	 * @param input
	 *          Pole bajt�, nad kter�m se otev�e instance t��dy.
	 */
	public NioInputStream(byte[] input) {
		buffer = ByteBuffer.wrap(input);
		buffer.position(0);
		buffer.order(ByteOrder.nativeOrder());
	}

	/**
	 * P�e�te �ty�i bajty od aktu�ln� pozice ve streamu, vr�t� hodnotu float
	 * p�epo�tenou podle nastaven�ho po�ad� byt� a nastav� pozici o �ty�i bajty
	 * vp�ed.
	 * 
	 * @return Hodnota float na aktu�ln� pozici ve streamu.
	 * @throws java.io.IOException
	 */
	public float readFloat() throws java.io.IOException {
		return buffer.getFloat();
	}

	/**
	 * Nastav� pozici ve streamu na zadanou hodnotu.
	 * 
	 * @param pos
	 *          Pozice ve streamu.
	 * @throws java.io.IOException
	 */
	public void seek(int pos) throws IOException {
		buffer.position(pos);
	}

	/**
	 * Vr�t� po�et zb�vaj�c�ch bajt� ve streamu.
	 * 
	 * @return Po�et zb�vaj�c�h bajt�.
	 */
	public int getRemaining() {
		return buffer.remaining();
	}

	/**
	 * P�e�te nejv�e <code>len</code> znak� ze streamu do pole bajt�. Vrac� po�et
	 * p�e�ten�ch znak�.
	 * 
	 * @param buf
	 *          Pole, do kter�ho jsou data na�tena.
	 * @param off
	 *          Po��ate�n� pozice v poli <code>buff</code>, od kter� se na�tou
	 *          data.
	 * @param len
	 *          Maxim�ln� po�et p�e�ten�ch bajt�.
	 * @return Po�et na�ten�ch bajt�, pop�. -1 v p��pad�, �e nejsou k dispozici
	 *         ��dn� data z d�vodu dosa�en� konce souboru.
	 * @throws EOFException
	 */
	public int read(byte[] buf, int off, int len) throws EOFException {
		int remaining = buffer.remaining();

		if (len <= remaining) {
			buffer.get(buf, off, len);
			return len;
		} else if (remaining > 0) {
			buffer.get(buf, off, len);
			return remaining;
		} else {
			return -1;
		}
	}
}
