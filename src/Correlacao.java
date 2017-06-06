
public class Correlacao {

	public static void main(String[] args) {
		Spearman();

	}

	public static void Spearman() {
		int subj = 2;
		Arquivo corr = new Arquivo("ax.in", "corrSal.out");

		while(subj != 1){
			Arquivo subArq = new Arquivo("subjectscores1.txt", "temp.txt");
			Arquivo psiArq = new Arquivo("psiAndSal.out", "temp.txt");

			double[][] subjAndPsi = new double [75][2];
			int[] yi = new int [75];
			double di = 0;

			int count = 1;	

			if(subj >= 20){
				subj = 0;
			}

			String sAtual = "";
			double vAtual = 0;
			int index = 0;

			while(!subArq.isEndOfFile() && index<50){
				sAtual = subArq.readString();

				if(count%21 == subj){
					vAtual = Double.parseDouble(sAtual);
					subjAndPsi[index][0] = vAtual;				

					sAtual = psiArq.readString();
					vAtual = Double.parseDouble(sAtual);
					subjAndPsi[index][1] = vAtual;
					index++;
				}

				count++;
			}

			quickSort(subjAndPsi, 0, subjAndPsi.length-1);

			for (int i = 0; i < yi.length; i++) {
				double atual = subjAndPsi[i][1];
				int pos = 1;
				for (int j = 0; j < yi.length; j++) {

					if(i != j && atual > subjAndPsi[j][1]){
						pos++;
					}
				}
				yi[i] = pos;
			}

			for (int i = 0; i < yi.length; i++) {
				di += ((i+1)-yi[i])*((i+1)-yi[i]);
			}
			subArq.close();
			psiArq.close();
			corr.println("correlação "+(subj-1)+ ": "+(1.0 - ((6.0*di)/(Math.pow(116, 3) - 116.0))));
			subj++;

		}
	}

	public static void quickSort(double[][] vetor, int inicio, int fim) {
		if (inicio < fim) {
			int posicaoPivo = separar(vetor, inicio, fim);
			quickSort(vetor, inicio, posicaoPivo - 1);
			quickSort(vetor, posicaoPivo + 1, fim);
		}
	}

	private static int separar(double[][] vetor, int inicio, int fim) {
		double pivo1 = vetor[inicio][0];
		double pivo2 = vetor[inicio][1];
		int i = inicio + 1, f = fim;
		while (i <= f) {
			if (vetor[i][0] <= pivo1)
				i++;
			else if (pivo1 < vetor[f][0])
				f--;
			else {
				double troca1 = vetor[i][0];
				double troca2 = vetor[i][1];

				vetor[i][0] = vetor[f][0];
				vetor[i][1] = vetor[f][1];

				vetor[f][0] = troca1;
				vetor[f][1] = troca2;
				i++;
				f--;
			}
		}
		vetor[inicio][0] = vetor[f][0];
		vetor[inicio][1] = vetor[f][1];

		vetor[f][0] = pivo1;
		vetor[f][1] = pivo2;
		return f;
	}
}

