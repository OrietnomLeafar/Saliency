/**
 * Classe ExemploImagem, junto com a classe RendererImagem, mostra um exemplo de 
 * como trabalhar com imagens em OpenGL utilizando a API JOGL.
 * 
 * @author Marcelo Cohen, Isabel H. Manssour
 * @version 1.0
 */

import javax.swing.*;
import java.awt.*; 
import java.awt.event.*; 
import javax.media.opengl.*;

public class ExemploImagem
{
	private RendererImagem renderer;

	/**
	 * Construtor da classe ExemploImagem que não recebe par�metros. Cria uma janela e insere  
	 * um componente canvas OpenGL.
	 */
	public ExemploImagem()
	{
		// Cria janela
		JFrame janela = new JFrame("Imagens em OpenGL");   
		janela.setBounds(50,100,1300,600); 
		janela.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		BorderLayout layout = new BorderLayout(); 
		Container caixa=janela.getContentPane();
		caixa.setLayout(layout); 

		// Cria um objeto GLCapabilities para especificar o número de bits 
		// por pixel para RGBA
		GLCapabilities c = new GLCapabilities();
		c.setRedBits(8);
		c.setBlueBits(8);
		c.setGreenBits(8);
		c.setAlphaBits(8); 

		Imagem im[] = new Imagem[75];
		// Cria os objetos Imagem a partir de arquivos JPEG
		for (int i = 0; i < 75; i++) {
			im[i] = new Imagem("imagens/img"+(i+1)+".bmp");
			
		}


		// Cria o objeto que irá gerenciar os eventos
		renderer = new RendererImagem(im);

		// Cria um canvas, adiciona na janela, e especifica o objeto "ouvinte" 
		// para os eventos Gl, de mouse e teclado
		GLCanvas canvas = new GLCanvas(c);
		janela.add(canvas,BorderLayout.CENTER);
		canvas.addGLEventListener(renderer);        
		canvas.addMouseListener(renderer);
		canvas.addKeyListener(renderer);
		janela.setVisible(true);
		canvas.requestFocus();
	}

	/**
	 * Método main que apenas cria um objeto ExemploImagem.
	 */
	public static void main(String args[])
	{
		ExemploImagem ei = new ExemploImagem();
	}
}

