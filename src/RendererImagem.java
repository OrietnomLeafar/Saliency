/**
 * Classe RendererImagem, junto com a classe ExemploImagem, mostra um exemplo de 
 * como trabalhar com imagens em OpenGL utilizando a API JOGL.
 * 
 * @author Marcelo Cohen, Isabel H. Manssour 
 * @version 1.0
 */


import java.awt.Window;
import java.awt.event.*; 

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import javax.swing.JInternalFrame;

import com.sun.opengl.util.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class RendererImagem extends MouseAdapter implements GLEventListener, KeyListener
{
	// Atributos
	private GL gl;
	private GLU glu;
	private GLUT glut;
	private GLAutoDrawable glDrawable;
	private double fAspect;
	private Imagem imgs[],imGt[], nova,salient;
	private int sel;

	private int img[][], gray[][];
	private LinkedList<Aresta> edges;
	private int [][]GV;
	private Arquivo arq, arqC;
	private boolean temAresta[][];
	private ArrayList<Double> WPSI;
	private int choice;
	private Janela frame;

	
	/**
	 * Construtor da classe RendererImagem que recebe um array com as imagens
	 */
	public RendererImagem(Imagem imgs[])
	{
		choice  = 0;
		// Inicializa o valor para correï¿½Ã£o de aspecto   
		fAspect = 1;

		// Imagem carregada do arquivo
		this.imgs = imgs;
		
		nova = null;
		sel = 0;	// selecionada = primeira imagem
	}

	/**
	 * Mï¿½todo definido na interface GLEventListener e chamado pelo objeto no qual serï¿½ feito o desenho
	 * logo apï¿½s a inicializaï¿½ï¿½o do contexto OpenGL. 
	 */    
	public void init(GLAutoDrawable drawable)
	{
		glDrawable = drawable;
		gl = drawable.getGL();
		// glu = drawable.getGLU();       
		glu = new GLU();
		glut = new GLUT();

		drawable.setGL(new DebugGL(gl));        

		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		// Define a janela de visualizaï¿½Ã£o 2D
		gl.glMatrixMode(GL.GL_PROJECTION);
		glu.gluOrtho2D(0,1,0,1);
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}

	/**
	 * Método definido na interface GLEventListener e chamado pelo objeto no qual serï¿½ feito o desenho
	 * para comeï¿½ar a fazer o desenho OpenGL pelo cliente.
	 */  
	public void display(GLAutoDrawable drawable)
	{
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		gl.glLoadIdentity();    

		gl.glColor3f(0.0f, 0.0f, 1.0f);

		// Desenha a imagem original
		gl.glRasterPos2f(0,0);
		gl.glDrawPixels(imgs[sel].getWidth(), imgs[sel].getHeight(),
				GL.GL_BGR, GL.GL_UNSIGNED_BYTE, imgs[sel].getData());

		// Desenha a imagem resultante
		if(choice>0){
			if(choice ==1){
				if(nova!=null) {
					gl.glRasterPos2f(0.5f,0);
					gl.glDrawPixels(nova.getWidth(), nova.getHeight(),
							GL.GL_BGR, GL.GL_UNSIGNED_BYTE, nova.getData());
				}
			}else{
				if(salient!=null) {
					gl.glRasterPos2f(0.5f,0);
					gl.glDrawPixels(salient.getWidth(), salient.getHeight(),
							GL.GL_BGR, GL.GL_UNSIGNED_BYTE, salient.getData());
				}

			}
		}
	}

	/**
	 * Método definido na interface GLEventListener e chamado pelo objeto no qual serÃ¡ feito o desenho
	 * depois que a janela foi redimensionada.
	 */  
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		gl.glViewport(0, 0, width, height);
		fAspect = (float)width/(float)height;      
	}

	/**
	 * Método definido na interface GLEventListener e chamado pelo objeto no qual serÃ¡ feito o desenho
	 * quando o modo de exibiï¿½Ã£o ou o dispositivo de exibiï¿½Ã£o associado foi alterado.
	 */  
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { }

	

	/**
	 * Método definido na interface KeyListener que estÃ¡ sendo implementado para, 
	 * de acordo com as teclas pressionadas, permitir mover a posiï¿½Ã£o do observador
	 * virtual.
	 */        
	public void keyPressed(KeyEvent e)
	{
		// F1 para prÃ³xima imagem
		if(e.getKeyCode()==KeyEvent.VK_F1)
		{
			if(++sel>imgs.length-1) sel=imgs.length-1;
		}
		// F2 para imagem anterior
		else if(e.getKeyCode()==KeyEvent.VK_F2)
		{
			if(--sel<0) sel = 0;
		}

		// Cria a imagem resultante
		nova = (Imagem) imgs[sel].clone();

		switch (e.getKeyCode())
		{
		case KeyEvent.VK_1:		// Para exibir a imagem "original": nÃ£o faz nada
			System.out.println("Negative");
			negative();
			break;
		case KeyEvent.VK_2:		// Para converter a imagem para tons de cinza
			System.out.println("Grayscale");
			convertToGrayScale();
			break;     
		
		case KeyEvent.VK_3:		//PSI com imagens borradas
			choice = 1;
			arq = new Arquivo("ax.in", "psiBl5.out");
			for (int i = 0; i < imgs.length; i++) {				
				nova = (Imagem) imgs[i].clone();
				convertToGrayScale();
				blur(5);
				PSI();
			}
			
			break;
			
		case KeyEvent.VK_4:		//PSI com imagens normais
			choice = 1;
			arq = new Arquivo("ax.in", "psi.out");
			for (int i = 0; i < imgs.length; i++) {
				nova = (Imagem) imgs[i].clone();
				Coord p1 = new Coord(0,0);
				Coord p2 = new Coord(nova.getWidth()-1,nova.getHeight()-1);
				this.frame = new Janela(p1,p2);
				convertToGrayScale();
				PSI();
			}
			
			break;
			
		case KeyEvent.VK_5:
			choice = 2;
			arq = new Arquivo("ax.in", "psiAndSal.out");

			
			for (int i = 0; i < imgs.length; i++) {
				nova = (Imagem) imgs[i].clone();
				salient = (Imagem) nova.clone();

				simpleSaliency();
							
				choice = 1;
				convertToGrayScale();
				PSI();
			}

			
			break;
			
		case KeyEvent.VK_6:			
			choice = 2;
			salient = (Imagem) nova.clone();
			
			histogramSaliency();
			
			break;
		
		case KeyEvent.VK_ESCAPE:	System.exit(0);
		break;
		}  
		glDrawable.display();
	}

	/**
	 * Método definido na interface KeyListener.
	 */      
	public void keyTyped(KeyEvent e) { }

	/**
	 * Método definido na interface KeyListener.
	 */       
	public void keyReleased(KeyEvent e) { }
     
	public void negative(){
		int actR = 0,actG = 0,actB = 0;
		int newR, newG, newB;

		int wid = nova.getWidth();
		int hei = nova.getHeight();

		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				actR = nova.getR(i, j);
				actG = nova.getG(i, j);
				actB = nova.getB(i, j);

				newR = 255 - actR;
				newG = 255 - actG;
				newB = 255 - actB;

				nova.setPixel(i, j, newR, newG, newB);
			}
		}
	}
	
	public void convertToGrayScale() 
	{ 
		// Tarefa 1:
		//		Gerar uma imagem em tons de cinza 
		//		Use os métodos 
		//			getPixel/getR/getG/getB e setPixel da classe Imagem
		// 		Altere apenas o atributo nova.
		//     Experimente executar e testar nas imagens disponibilizadas.

		int R=0, G=0, B=0;
		int cinza = 0;
		int xInit = frame.esqSup.x;
		int yInit = frame.esqSup.y;
		
		gray = new int [frame.width][frame.height];

		for (int i = xInit; i < xInit+frame.width; i++) {
			for (int j = yInit; j < yInit+frame.height; j++) {
				R = nova.getR(i, j);
				G = nova.getG(i, j);
				B = nova.getB(i, j);

				cinza = (R+G+B)/3;
				gray[i-xInit][j-yInit] = cinza;
				nova.setPixel(i, j, cinza, cinza, cinza);
			}
		}

		
	}    
	
	public void binarize(int width,int height){
		int mean = 0;
		int max = 0;
		int min = 1000;
		int tam = width*height;
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				
				if(salient.getB(i, j)>0){
					mean += salient.getB(i, j);
					
				}else{
					tam--;
				}
	
				if(salient.getB(i, j)> max){
					max = salient.getB(i, j);
				}
				if(salient.getB(i, j)<min){
					min = salient.getB(i, j);
				}
			}
		}
		System.out.println("sum: "+mean);
		mean = mean/(tam);
		System.out.println(mean+ " max: "+ max+"/ min: "+min);
		float norma = (float)max/255;
		norma +=1;
		System.out.println(norma);
		//mean *= norma;
		
		System.out.println("nova media: "+mean);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if(salient.getB(i, j)>mean){
					salient.setPixel(i, j, 255, 255, 255);
				}else{
					salient.setPixel(i, j, 0, 0, 0);
				}
				
			}
		}
		
	}
	
	public void otsuBinarization(int wid, int hei){
		int[] histogram =new int [255];
		float[] prob = new float [255];
		int n = wid*hei;
		int L = 0, T = 0, Tvarmax = 0, px = 0;
		float P1 = 0, P2 = 0, m1 = 0, m2 = 0, mg = 0;
		float varMax = -1, var = 0, varG = 0, varC = 0;
		
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				px = salient.getB(i, j);
				
				histogram[px] +=1;
				if(px > L){
					L = px;
				}
			}
		}
		
		for (int i = 0; i < L; i++) {
			prob[i] = ((float)histogram[i]/(float)n);
		}
		
		do{
			//probabilidades
			for (int i = 0; i <= T; i++) {
				P1 += prob[i];
			}
			P2 = 1 - P1;
			//por aqui
			//intensidades medias
			for (int i = 0; i <= T; i++) {
				m1 += i*prob[i];
				mg += i*prob[i];
			}
			m1 /= P1;
			
			for (int i = T+1; i < L; i++) {
				m2 += i*prob[i];
				mg += i*prob[i];
			}
			m2 /= P2;
			
			//variancias
			for (int i = 0; i < L; i++) {
				varG += Math.pow((i-mg), 2)*prob[i];
			}
			
			varC =(float) (P1*Math.pow((m1 - mg), 2) + P2*Math.pow((m2 - mg), 2));
			
			var = varC/varG;
			
			if(var > varMax){
				varMax = var;
				Tvarmax = T;
			}
			
			T++;
			
			//resetando variaveis
			P1 = 0; m1 = 0; m2 = 0; mg = 0; varG = 0;
			
		}while(T < L);
		
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				px = salient.getB(i, j);
				
				if(px > Tvarmax){
					salient.setPixel(i, j, 255, 255, 255);
				}else{
					salient.setPixel(i, j, 0, 0, 0);
				}
			}
		}
		
	}
	
	public void opening(int wid, int hei){
		LinkedList<Coord> toChange = new LinkedList<Coord>();
		int px = 0;
		//erosao
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				px = salient.getB(i, j);
				
				if(px == 255){
					if(i == 0 || j == 0 || i == wid-1 || j == hei-1){
						salient.setPixel(i, j, 0, 0, 0);
					}else{
						if(salient.getB(i-1, j-1) == 0 || salient.getB(i-1, j) == 0 || salient.getB(i-1, j+1) == 0 || salient.getB(i, j+1) == 0 || salient.getB(i+1, j+1) == 0 || salient.getB(i+1, j) == 0 || salient.getB(i+1, j-1) == 0 || salient.getB(i, j-1) == 0 ){
							toChange.add(new Coord(i, j));
						}
					}
				}
			}
		}
		
		for (Coord c : toChange) {
			salient.setPixel(c.x, c.y, 0, 0, 0);
		}
		toChange.clear();
		
		//dilatacao
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				px = salient.getB(i, j);
				
				if(px == 255){
				
					if(salient.getB(i-1, j-1) == 0){
						toChange.add(new Coord(i-1, j-1));
					}
					if(salient.getB(i-1, j) == 0 ){
						toChange.add(new Coord(i-1, j));
					}
					if(salient.getB(i-1, j+1) == 0 ){
						toChange.add(new Coord(i-1, j+1));
					}
					if(salient.getB(i, j+1) == 0 ){
						toChange.add(new Coord(i, j+1));
					}
					if(salient.getB(i+1, j+1) == 0 ){
						toChange.add(new Coord(i+1, j+1));
					}
					if(salient.getB(i+1, j) == 0 ){
						toChange.add(new Coord(i+1, j));
					}
					if(salient.getB(i+1, j-1) == 0 ){
						toChange.add(new Coord(i+1, j-1));
					}
					if(salient.getB(i, j-1) == 0 ){
						toChange.add(new Coord(i, j-1));
					}
				}
			}
		}
		
		for (Coord c : toChange) {
			salient.setPixel(c.x, c.y, 255, 255, 255);
		}	
		
	}

	public void setWindow(int wid, int hei){
		int Xes = -1, Xdi = 0;
		int Yes = 1000, Ydi = -1;
		
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				if(salient.getB(i, j) == 255){
					if(Xes < 0){
						Xes = i;
					}
					if(i > Xdi){
						Xdi = i;
					}
					if(j < Yes){
						Yes = j;
					}
					if(j > Ydi){
						Ydi = j;
					}
					
				}
			}
		}
		
		if(Xes-8 >=0){
			Xes -=8;
		}
		if(Yes-8 >=0){
			Yes -=8;
		}
		if(Xdi+8 < salient.getWidth()){
			Xdi +=8;
		}
		if(Ydi+8 < salient.getHeight()){
			Ydi +=8;
		}
		
		Coord p1= null;
		Coord p2 = null;

		if((Xdi-Xes >= (float) (salient.getWidth()*0.85))&& (Ydi-Yes >= (float)(salient.getHeight()*0.85))){
			p1 = new Coord(0, 0);
			p2 = new Coord(salient.getWidth()-1, salient.getHeight()-1);
			System.out.println("NORMAL");
		}else{
			p1 = new Coord(Xes, Yes);
			p2 = new Coord(Xdi, Ydi);
			System.out.println("JANELA");
		}
		
		this.frame = new Janela(p1, p2);

		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				if((i == Xes && j >= Yes && j <= Ydi) || (i == Xdi && j >= Yes && j <= Ydi) || (j == Yes && i >= Xes && i <= Xdi) || (j == Ydi && i >= Xes && i <= Xdi)){
					salient.setPixel(i, j, 255, 0, 255);
					
				}
			}
		}
	}
	
	public void histogramSaliency(){
		int wid = salient.getWidth();
		int hei = salient.getHeight();

		ArrayList<Cor> cores = new ArrayList<Cor>(); 
		ArrayList<Float> prob = new ArrayList<Float>();
		CIELab c = new CIELab();
		Cor[][] LabColors = new Cor[wid][hei];
		float[] atualRGB = new float[3];
		float[] atualLab = new float[3];
		
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				atualRGB[0] = salient.getR(i, j);
				atualRGB[1] = salient.getG(i, j);
				atualRGB[2] = salient.getB(i, j);
				
				atualLab = c.fromRGB(atualRGB);
				Cor Lab = new Cor(atualLab);
				int index = existeCor(cores, Lab);
				
				if(index > 0){
					prob.set(index, prob.get(index)+1);
				}else{
					cores.add(Lab);
					prob.add((float) 1.0);
				
				}
				
				LabColors[i][j] = Lab;			
			}
		}			
		
		for (int i = 0; i < cores.size(); i++) {
			float L1 = cores.get(i).L;
			float a1 = cores.get(i).a;
			float b1 = cores.get(i).b;
			float sal = 0;
			
			for (int j = 0; j < cores.size(); j++) {
				float L2 = cores.get(j).L;
				float a2 = cores.get(j).a;
				float b2 = cores.get(j).b;
				
				if(i != j){
					float dist = (float) (Math.pow(L2-L1, 2) + Math.pow(a2-a1, 2) +Math.pow(b1-b2, 2));
					
					dist = (float) Math.pow(dist, 1/2);
					
					sal += dist;
				}
			}
			
			cores.get(i).sal = sal;
		}
		
	}
	
	public int existeCor(ArrayList<Cor> cores, Cor atual){
		int pos = -1;
		
		for (int i = 0; i < cores.size() && pos < 0; i++) {
			if(cores.get(i).L == atual.L && cores.get(i).a == atual.a && cores.get(i).b == atual.b){
				pos = i;
			}
		}
		
		return pos;
	}
	
	public void simpleSaliency(){
		int width = salient.getWidth();
		int height = salient.getHeight();
		int Rc[][] = new int[width][height];
		int Gc[][] = new int[width][height];
		int Bc[][] = new int[width][height];
		int Yc[][] = new int[width][height];
		int R = 0, G = 0, B = 0;
		float Ir = 0, Ig = 0, Ib = 0, Iy = 0;
		float varR = 0, varG = 0, varB = 0, varY = 0;
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				R = salient.getR(i, j);
				G = salient.getG(i, j);
				B = salient.getB(i, j);
				
				Rc[i][j] = modulo(R - (G+B)/2);
				Gc[i][j] = modulo(G - (R+B)/2);
				Bc[i][j] = modulo(B - (R+G)/2);
				Yc[i][j] = modulo((R+G)/2 - modulo(R-G)/2 - B);
				
				Ir += Rc[i][j];
				Ig += Gc[i][j];
				Ib += Bc[i][j];
				Iy += Yc[i][j];
			}
		}
		
		Ir /= width*height;
		Ig /= width*height;
		Ib /= width*height;
		Iy /= width*height;
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				varR +=(float) Math.pow(Rc[i][j] - Ir, 2);
				varG +=(float) Math.pow(Gc[i][j] - Ig, 2);
				varB +=(float) Math.pow(Bc[i][j] - Ib, 2);
				varY +=(float) Math.pow(Yc[i][j] - Iy, 2);
			}
		}
		
		varR /= width*height;
		varG /= width*height;
		varB /= width*height;
		varY /= width*height;
		

		if(varR > varG && varR > varB &&varR > varY){
			System.out.println("Red");
			saliencyMap(Rc, Ir, width, height);
		}else if(varG > varR && varG > varB && varG > varY){
			System.out.println("Green");
			saliencyMap(Gc, Ig, width, height);
		}else if(varB > varR && varB > varG && varB > varY){
			System.out.println("Blue");
			saliencyMap(Bc, Ib, width, height);
		}else{
			System.out.println("Yellow");
			saliencyMap(Yc, Iy, width, height);
		}
		otsuBinarization(width, height);
		opening(width, height);
		setWindow(width, height);
	}
	
	
	public void saliencyMap(int [][] ch, float Ic,int wid, int hei){
		int[][] s = new int[wid][hei];
		float[][] f = new float[wid][hei];
		int sMin = 1000, sMax = 0;
		int[] center = new int [2];
		center[0] = wid/2;
		center[1] = hei/2;
		float L =(float) Math.sqrt(Math.pow(wid, 2)+ Math.pow(hei, 2))/2;
		
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				s[i][j] = modulo((int)(ch[i][j] - Ic));
				
				if(s[i][j] < sMin){
					sMin = s[i][j];
				}
				if(s[i][j] > sMax){
					sMax = s[i][j];
				}
				
				f[i][j] = 1/(1+ (float)Math.sqrt(Math.pow(i-center[0], 2)+ Math.pow(j-center[1], 2))/L);
			}
		}
		
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				s[i][j] = 255 * (s[i][j]-sMin)/(sMax - sMin);
				
				s[i][j] *= f[i][j];
				
				salient.setPixel(i, j, s[i][j], s[i][j], s[i][j]);
			}
		}
		
		
	}
	
	public int modulo(int valor){
		int m = valor;
		
		if(m < 0){
			m *= -1;
		}
		
		return m;
	}
	
	public void blur(int dim){
		int wid = nova.getWidth();
		int hei = nova.getHeight();
		
		for (int x = 0; x < wid-dim-1; x++) {
			for (int y = 0; y < hei-dim-1; y++) {
				int soma = 0;
				
				for (int i = 0; i < dim; i++) {
					for (int j = 0; j < dim; j++) {
						int p = nova.getR(x+i,y+j);
						soma += p;
					}
				}
				int media = soma/(dim*dim);
				
				nova.setPixel(x+(dim/2), y+(dim/2), media, media, media);
			}
		}
	}
	
	public void applyKernel(float [][]vert, float[][] hor){

		int wid = frame.width;
		int hei = frame.height;
		int xInit = frame.esqSup.x;
		int yInit = frame.esqSup.y;
		this.GV = new int[wid][hei];

		double Gb = 0;
		double alfa = 4.7;
		double T = 0;


		for(int x=xInit; x<xInit+wid-2; x++){
			for(int y=yInit; y<yInit+hei-2; y++){

				int somav = 0;

				for(int i=0; i<3; i++){
					for(int j=0; j<3; j++){
						int p = nova.getR(x+i,y+j);

						somav += p * vert[i][j];					

					}
				}

				this.GV[x-xInit+1][y-yInit+1] =(int) somav;

				double r = (somav * somav);
				r = Math.sqrt(r);
				
				Gb += r;
			}
		}

		Gb = Gb/(wid*hei);

		T = alfa*Gb;

		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < hei; j++) {
				if(GV[i][j] >= T){
					nova.setPixel(i+xInit, j+yInit, 255, 255, 255);
				}else{
					nova.setPixel(i+xInit, j+yInit, 0, 0, 0);
				}

			}
		}
	}

	public void thinning(int xInit, int yInit){
		
		LinkedList<Coord> toChange = new LinkedList<Coord>();
		boolean mudou;
		int T = 0;
		int N = 0;

		do{
			mudou = false;

			for (int i = 1; i < img.length-1; i++) {
				for (int j = 1; j < img[i].length-1; j++) {
					if(img[i][j] == 1){
						N = getN(i,j);
						T = getT(i,j);

						if(T == 1 && N >= 2 && N <= 6 && (img[i-1][j]*img[i][j+1]*img[i+1][j])==0 && (img[i][j+1]*img[i+1][j]*img[i][j-1])==0){
							toChange.add(new Coord(i,j));
							
							mudou = true;
						}
					}
				}
			}
			
			for(Coord c: toChange){
				img[c.x][c.y] = 0;
			}
			toChange.clear();
			
			for (int i = 1; i < img.length-1; i++) {
				for (int j = 1; j < img[i].length-1; j++) {
					if(img[i][j] == 1){
						N = getN(i,j);
						T = getT(i,j);

						if(T == 1 && N >= 2 && N <= 6 && (img[i-1][j]*img[i][j+1]*img[i][j-1])==0 && (img[i-1][j]*img[i+1][j]*img[i][j-1])==0){
							toChange.add(new Coord(i,j));
							
							mudou = true;
						}
					}
				}
			}
			for(Coord c: toChange){
				img[c.x][c.y] = 0;
			}
			toChange.clear();

		}while(mudou);

		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[i].length; j++) {
				if(img[i][j] == 1){
					nova.setPixel(i+xInit, j+yInit, 255,255,255);
				}else{
					nova.setPixel(i+xInit, j+yInit, 0, 0, 0);
				}
			}
		}
	}

	int getN(int i, int j){
		return img[i-1][j-1]+img[i-1][j]+img[i-1][j+1]+img[i][j+1]+img[i+1][j+1]+img[i+1][j]+img[i+1][j-1]+img[i][j-1];
	}

	int getT(int i, int j){
		int count = 0;

		if(img[i-1][j] == 0 && img[i-1][j+1] == 1){
			count++;
		}

		if(img[i-1][j+1] == 0 && img[i][j+1] == 1){
			count++;
		}

		if(img[i][j+1] == 0 && img[i+1][j+1] == 1){
			count++;
		}

		if(img[i+1][j+1] == 0 && img[i+1][j] == 1){
			count++;
		}

		if(img[i+1][j] == 0 && img[i+1][j-1] == 1){
			count++;
		}

		if(img[i+1][j-1] == 0 && img[i][j-1] == 1){
			count++;
		}

		if(img[i][j-1] == 0 && img[i-1][j-1] == 1){
			count++;
		}

		if(img[i-1][j-1] == 0 && img[i-1][j] == 1){
			count++;
		}

		return count;
	}

	void atualizarImgPB(int xInit, int yInit){
		
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[i].length; j++) {
				if(nova.getB(i+xInit, j+yInit) == 255){
					img[i][j] = 1;
				}else{
					img[i][j] = 0;
				}
			}
		}
	}
	
	public void identifyEdges(int wid, int hei,int xInit, int yInit){
		
		temAresta = new boolean[wid][hei];
		this.edges = new LinkedList<Aresta>();
		
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[i].length; j++) {
				if(img[i][j] == 1 && !temAresta[i][j]){
					Aresta e = new Aresta(new Coord(i+xInit,j+yInit));
					temAresta[i][j] = true;
					this.edges.add(e);
					verificarViz(wid, hei, i, j, img, xInit, yInit);
				}
			}
		}
		
		int count = 0;
		ArrayList<Integer> removidos = new ArrayList<Integer>();
		
		for (Aresta e : edges) {
			
			if(e.getTamanho()== 1){
				removidos.add(count);
				
			}
			count++;
		}
		System.out.println("\narestas: "+edges.size()+"");
		count = 0;
		/*for (Integer i : removidos) {
			edges.remove(i.intValue()-count);
			count ++;
		}
		
		System.out.println("\narestas: "+edges.size()+"");*/
	}
	
	public void verificarViz(int wid, int hei,int x, int y, int[][]imag,int xInit, int yInit){
		
		if(x > 0 && !temAresta[x-1][y] && imag[x-1][y] == 1){
			edges.getLast().adicionar(new Coord(x+xInit-1,y+yInit));
			temAresta[x-1][y] = true;
			verificarViz(wid, hei, x-1, y,imag, yInit, yInit);
		}
		
		if(x < wid-1 && !temAresta[x+1][y] && imag[x+1][y] == 1){
			edges.getLast().adicionar(new Coord(x+xInit+1,y+yInit));
			temAresta[x+1][y] = true;
			verificarViz(wid, hei, x+1, y,imag, yInit, yInit);
		}
		
		if(y > 0 && !temAresta[x][y-1] && imag[x][y-1] == 1){
			edges.getLast().adicionar(new Coord(x+xInit,y+yInit-1));
			temAresta[x][y-1] = true;
			verificarViz(wid, hei, x, y-1,imag, yInit, yInit);
		}
		
		if(y < hei-1 && !temAresta[x][y+1] && imag[x][y+1] == 1){
			edges.getLast().adicionar(new Coord(x+xInit,y+yInit+1));
			temAresta[x][y+1] = true;
			verificarViz(wid, hei, x, y+1,imag, yInit, yInit);
		}
		
		if(x > 0 && y > 0 && !temAresta[x-1][y-1] && imag[x-1][y-1] == 1){
			edges.getLast().adicionar(new Coord(x+xInit-1,y+yInit-1));
			temAresta[x-1][y-1] = true;
			verificarViz(wid, hei, x-1, y-1,imag, yInit, yInit);
		}
		
		if(x < wid-1 && y > 0 && !temAresta[x+1][y-1] && imag[x+1][y-1] == 1){
			edges.getLast().adicionar(new Coord(x+xInit+1,y+yInit-1));
			temAresta[x+1][y-1] = true;
			verificarViz(wid, hei, x+1, y-1,imag, yInit, yInit);
		}
		
		if(x < wid-1 && y < hei-1 && !temAresta[x+1][y+1] && imag[x+1][y+1] == 1){
			edges.getLast().adicionar(new Coord(x+xInit+1,y+yInit+1));
			temAresta[x+1][y+1] = true;
			verificarViz(wid, hei, x+1, y+1,imag, yInit, yInit);
		}
		
		if(x > 0 && y < hei-1 && !temAresta[x-1][y+1] && imag[x-1][y+1] == 1){
			edges.getLast().adicionar(new Coord(x+xInit-1,y+yInit+1));
			temAresta[x-1][y+1] = true;
			verificarViz(wid, hei, x-1, y+1,imag, yInit, yInit);
		}
	}
	
	public void edgeWidth(int wid,int xInit, int yInit){

		ArrayList<Double> allWx = new ArrayList<Double>();
		ArrayList<Double> allMx = new ArrayList<Double>();
		WPSI = new ArrayList<Double>();
			
		for (Aresta e : edges) {
			double Waux = 0;
			double Wx = 0;
			int Wup = 0;
			int Wdown = 0;
			double Mx = 0;
			
			for (Coord c : e.pontos) {
				double Imax = 0;
				double Imin = 1;
				int esq = c.x -8;
				int dir = c.x +8;
				int y = c.y;
				
				if(esq < xInit){
					esq = xInit;
				}
				if(dir >= wid+xInit){
					dir = wid+xInit-1;
				}
				
				for (int i = esq; i <= dir; i++) {
					if(i != c.x){
						double Iaux = (double)gray[i-xInit][y-yInit]/255;
						if(Iaux > Imax){
							Imax = Iaux;
							Wup = i;
						}
						
						if(Iaux < Imin){
							Imin = Iaux;
							Wdown = i;
						}
					}
				}
				Waux = Wup - Wdown;
				if(Waux < 0 ){
					Waux *= -1;
				}
		
				double dif = Imax - Imin;
				if(dif < 0 ){
					dif *= -1;
				}
		
				Wx += Waux;
				
				Mx += dif/Wx;
			}
			
			Wx = Wx/e.getTamanho();
			Mx = Mx/e.getTamanho();
			allWx.add(Wx);
			allMx.add(Mx);
			

		}
		
		for (int i = 0; i< allMx.size(); i++) {
			double psi =0;
			if(allWx.get(i).doubleValue() >= 3.0){
				psi = allWx.get(i).doubleValue() - allMx.get(i).doubleValue();
			}else{
				psi = allWx.get(i).doubleValue();
			}
			WPSI.add(psi);
			
		}
		
	}
	
	public void sharpness(int wid, int hei, int xInit, int yInit){
		
		int bWid = 0;
		int bHei = 0;
		double localPSI[][];
		
		if(wid%32 != 0){
			bWid = (wid/32)+1;
		}else{
			bWid = (wid/32);
		}
		if(hei%32 != 0){
			bHei = (hei/32)+1;
		}else{
			bHei = (hei/32);
		}
		
		localPSI = new double[bWid][bHei];

		for (int i = xInit; i < xInit+wid; i +=32) {
			for (int j = yInit; j < yInit+hei; j +=32) {
				ArrayList<Integer> insiders = new ArrayList<Integer>();
				for (int k = 0;k < edges.size();k++) {
					if(edges.get(k).isInside(i, j)){
						insiders.add(k);
					}
				}
				double media =0;
				if(insiders.size()>2){
					for (Integer in : insiders) {
						media += WPSI.get(in.intValue());
					}
					media = media/insiders.size();
				}
				
				localPSI[(i-xInit)/32][(j-yInit)/32] = media;
				
			}
			
		}

		int per = (int) ((bWid*bHei)*0.18);
		double mediaGl = 0;	
		
		for (int p = 0; p < per; p++) {
			double maior = 0;
			int im = 0;
			int jm = 0;
			for (int i = 0; i < localPSI.length; i++) {
				for (int j = 0; j < localPSI[i].length; j++) {
					if(localPSI[i][j] > maior){
						maior = localPSI[i][j];
						im = i;
						jm = j;
					}
				}
			}
			mediaGl += maior;
			localPSI[im][jm] = 0;
			
		}
		
		mediaGl = mediaGl/per;
		arq.println(mediaGl);
		System.out.println("Global Sharpness: "+mediaGl+"\n");
	}
	
	public void PSI()
	{
		int xInit = frame.esqSup.x;
		int yInit = frame.esqSup.y;
		int wid = frame.width;
		int hei = frame.height;
		this.img = new int[wid][hei];
		this.choice = 1;

		float[][] vertical = {{1,2,1}, {0,0,0}, {-1,-2,-1}};
		float[][] horizontal = {{-1,0,1}, {-2,0,2}, {-1,0,1}};

		applyKernel(vertical, horizontal);
		atualizarImgPB(xInit,yInit);
		
		thinning(xInit, yInit);
		
		identifyEdges(wid,hei,xInit,yInit);
		
		edgeWidth(wid, xInit,yInit);
		sharpness(wid, hei, xInit, yInit);
		
		
	}

	
}

class Cor{
	float L;
	float a;
	float b;
	float sal;
	
	public Cor(float[] lab){
		this.L = lab[0];
		this.a = lab[1];
		this.b = lab[2];
	}
}

class Janela{
	
	public Coord esqSup;
	public Coord dirInf;
	public int width;
	public int height;
	
	public Janela(Coord es, Coord di){
		this.esqSup = es;
		this.dirInf = di;
		this.width = di.x - es.x +1;
		this.height = di.y - es.y +1;
	}
	
}

class Coord{
	public int x;
	public int y;
	
	public Coord(int x, int y){
		this.x = x;
		this.y = y;
	}
}

class Aresta{
	
	public LinkedList<Coord> pontos = new LinkedList<Coord>();
	private int tamanho;
	
	public Aresta(Coord init){
		this.pontos.add(init);
		this.setTamanho(1);
	}
	
	public void adicionar(Coord x){
		this.pontos.add(x);
		setTamanho(1);
	}
	
	void imprimirPts(){
		for (Coord p : pontos) {
			System.out.print("x: "+p.x+" y: "+p.y+" /");
		}
		
	}
	
	public boolean isInside(int i, int j){
		boolean inside = false;
		
		for (Coord c : this.pontos) {
			if(c.x >= i && c.x< i+32){
				if (c.y >=j && c.y < j+32) {
					inside = true;
				}
			}
		}
		
		return inside;
	}
	
	public int getTamanho() {
		return tamanho;
	}

	public void setTamanho(int tamanho) {
		this.tamanho += tamanho;
	}
	
	
}
