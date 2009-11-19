package ch.ethz.origo.jerpa.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * T��da pro abstrakci z�pisu dat do souboru s vyu�it�m <code>java.nio</code>
 * knihoven.
 * 
 * @author Jiri Kucera
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 11/17/2009 
 * @since 0.1.0
 */
public final class NioOutputStream {

	private final int BYTE_BUFFER_SIZE = 2 * 1024 * 1024;
	private FileChannel channel;
	private ByteBuffer buffer;
	private boolean opened;

	/**
	 * Vytvo�� <code>NioOutputStream</code> nad souborem zadan�m n�zvem. Po�ad�
	 * byt� je Big Endian.
	 * 
	 * @param file
	 *          N�zev nebo cesta k souboru.
	 * @throws FileNotFoundException
	 */
	public NioOutputStream(String file) throws FileNotFoundException {
		this(new File(file));
	}

	/**
	 * Vytvo�� <code>NioOutputStream</code> nad souborem zadan�m n�zvem.
	 * 
	 * @param file
	 *          N�zev nebo cesta k souboru.
	 * @param byteOrder
	 *          Po�ad� byt�.
	 * @throws java.io.FileNotFoundException
	 */
	public NioOutputStream(String file, ByteOrder byteOrder)
			throws FileNotFoundException {
		this(new File(file), byteOrder);
	}

	/**
	 * Vytvo�� <code>NioOutputStream</code> nad souborem zadan�m jako objekt
	 * <code>File</code>. Po�ad� byt� je Big Endian.
	 * 
	 * @param file
	 *          Vstupn� soubor.
	 * @throws FileNotFoundException
	 */
	public NioOutputStream(File file) throws FileNotFoundException {
		this(new FileOutputStream(file));
	}

	/**
	 * Vytvo�� <code>NioOutputStream</code> nad souborem zadan�m jako objekt
	 * <code>File</code>.
	 * 
	 * @param file
	 *          Vstupn� soubor.
	 * @param byteOrder
	 *          Po�ad� byt�.
	 * @throws java.io.FileNotFoundException
	 */
	public NioOutputStream(File file, ByteOrder byteOrder)
			throws FileNotFoundException {
		this(new FileOutputStream(file), byteOrder);
	}

	/**
	 * Vytvo�� <code>NioOutputStream</code> nad otev�en�m streamem typu
	 * <code>FileOutputStream</code>. Po�ad� byt� je Big Endian.
	 * 
	 * @param stream
	 *          Vstupn� stream.
	 */
	public NioOutputStream(FileOutputStream stream) {
		this(stream.getChannel());
	}

	/**
	 * Vytvo�� <code>NioOutputStream</code> nad otev�en�m streamem typu
	 * <code>FileOutputStream</code>.
	 * 
	 * @param stream
	 *          Vstupn� stream.
	 * @param byteOrder
	 *          Po�ad� byt�.
	 */
	public NioOutputStream(FileOutputStream stream, ByteOrder byteOrder) {
		this(stream.getChannel(), byteOrder);
	}

	/**
	 * Vytvo�� <code>NioOutputStream</code> nad kan�lem zadan�m objektem typu
	 * <code>FileChannel</code>. Po�ad� byt� je Big Endian.
	 * 
	 * @param channel
	 *          Kan�l.
	 */
	public NioOutputStream(FileChannel channel) {
		this(channel, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Vytvo�� <code>NioOutputStream</code> nad kan�lem zadan�m objektem typu
	 * <code>FileChannel</code>.
	 * 
	 * @param channel
	 *          Kan�l.
	 * @param byteOrder
	 *          Po�ad� byt�.
	 */
	public NioOutputStream(FileChannel channel, ByteOrder byteOrder) {
		this.channel = channel;
		buffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
		buffer.order(byteOrder);
		opened = true;
	}

	/**
	 * Zap�e data zb�vaj�c� v bufferu a uzav�e v�stupn� soubor.
	 * 
	 * @throws java.io.IOException
	 */
	public void close() throws IOException {
		if (!opened) {
			return;
		} else {
			opened = false;
		}
		if (channel != null && channel.isOpen()) {
			write();
			channel.force(true);
			channel.close();
		} else {
			while (buffer.position() < buffer.capacity()) {
				buffer.put((byte) 0);
			}
		}
	}

	/**
	 * Zap�e hodnotu typu <code>float</code> do souboru.
	 * 
	 * @param f
	 *          Hodnota k z�pisu.
	 * @throws java.io.IOException
	 */
	public void writeFloat(float f) throws java.io.IOException {
		checkAndFlush(4);
		buffer.putFloat(f);
	}

	/**
	 * Zkontroluje, zda je v bufferu dostatek m�sta pro <code>bytes</code> byt�.
	 * Pokud ne, vypr�zdn� buffer do v�stupn�ho souboru a vypr�zdn� buffer.
	 * 
	 * @param bytes
	 *          Po�adovan� po�et byt�.
	 * @throws java.io.IOException
	 */
	private void checkAndFlush(int bytes) throws IOException {
		if (buffer.remaining() < bytes) {
			write();
			buffer.clear();
		}
	}

	/**
	 * Zap�e obsah bufferu do v�stupn�ho souboru.
	 * 
	 * @throws java.io.IOException
	 */
	private void write() throws IOException {
		buffer.limit(buffer.position());
		buffer.position(0);
		if (channel != null && channel.isOpen()) {
			channel.write(buffer);
		}
	}
}
