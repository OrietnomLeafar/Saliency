/**
 * Classe Imagem é capaz de carregar uma imagem qualquer nos formatos
 * suportados pela API Java (JPEG, PNG, etc), e retornar um array de bytes 
 * para uso em OpenGL.
 * 
 * @author Marcelo Cohen, Isabel H. Manssour
 * @version 1.0
 */

import java.io.*;
import java.awt.image.*;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.nio.ByteBuffer;
import com.sun.opengl.util.texture.TextureData;

public class Imagem 
{
	private int width, height;
	private BufferedImage im;
	private TextureData td;
	private ByteBuffer buf;
	private byte data[];
	private String name;

	public Imagem(String arquivo)
	{
		load(arquivo);
	}

	public void load(String arquivo) 
	{
		// Inicializa atributo
		name = arquivo;
	
		System.out.print("Carregando "+arquivo+"... ");	

		// Tenta carregar o arquivo		
		im = null;
		try {
			im = ImageIO.read(new File(arquivo));
		}
		catch (IOException e) {
			System.out.println("Erro na leitura de "+arquivo);
		}

		// Obtém largura e altura
		width  = im.getWidth();
		height = im.getHeight();
		System.out.println(width+" x "+height);
		// Gera uma nova TextureData...
		int[] pixels = im.getRGB(0, 0, width, height, null, 0, width);

//		td = new TextureData(0,0,false,im);
		// ...e obtém um ByteBuffer a partir dela
//		buf = (ByteBuffer) td.getBuffer();

		// Finalmente, obtém o array correspondente
//		data = buf.array();

		data = new byte[width*3*height];
		int pos = 0;
		for(int i=0; i<pixels.length; i++)
		{
			Color c = new Color(pixels[i]);
			data[pos++] = (byte) c.getBlue();
			data[pos++] = (byte) c.getGreen();
			data[pos++] = (byte) c.getRed();
		}
//		data = (byte []) pixels.clone();
		
		// Calcula tamanho de uma linha
		int width3 = width*3;

		// Copia a imagem
		byte aux[] = (byte []) data.clone();

		// Inverte as linhas da imagem original
		int iSrc, iDst;
		for (int lin=height-1; lin>=0; lin--)
		{
			iSrc = width3*lin;
			iDst = width3*(height-1-lin);
			System.arraycopy(aux,iSrc,data,iDst,width3);
		}

		// Libera o clone
		aux = null;
		pixels = null;
		buf = ByteBuffer.wrap(data);
		//System.out.println(width + " x " + height);
	}

	public String getFileName() { return name; }

	public Object clone() {
		Imagem nova = new Imagem(name);
		return nova;
	}

	public int getWidth() { return width; }
	
	public int getHeight() { return height; }

	public void setPixel(int x, int y, Pixel p) 
	{
		int addr = y*width*3 + x*3;
		data[addr++] = (byte) p.getB();
		data[addr++] = (byte) p.getG();
		data[addr] = (byte) p.getR();
	}

	public void setPixel(int x, int y, int r, int g, int b) 
	{
		int addr = y*width*3 + x*3;
		data[addr++] = (byte) b;
		data[addr++] = (byte) g;
		data[addr]   = (byte) r;
	}

	public Pixel getPixel(int x, int y) 
	{
		int addr = y*width*3 + x*3;
		int ar,ag,ab;
		ar = (int) (data[addr+2]);
		ag = (int) (data[addr+1]);
		ab = (int) (data[addr]);
		if(ar<0) ar+=256;
		if(ag<0) ag+=256;
		if(ab<0) ab+=256;
		return new Pixel(ar,ag,ab);
	}

	public int getR(int x, int y) {
		int addr = y*width*3 + x*3;
		int aux = (int) (data[addr+2]);
		if(aux<0) aux+=256;
		return aux;
	}

	public int getG(int x, int y) {
		int addr = y*width*3 + x*3;
		int aux = (int) (data[addr+1]);
		if(aux<0) aux+=256;
		return aux;
	}

	public int getB(int x, int y) {
		int addr = y*width*3 + x*3;
		int aux = (int) (data[addr]);
		if(aux<0) aux+=256;
		return aux;
	}

	public ByteBuffer getData() { return buf; }

}

