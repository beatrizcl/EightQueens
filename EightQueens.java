package algoritmoGenetico;
import java.util.ArrayList;
import java.util.Scanner;

public class EightQueens {
	
	static Cromossomo resposta = new Cromossomo();
	static Cromossomo[] cromossomosPop, cromossomosFilho;
	static int tPopIni, pCorte, geracoes;
	static boolean encontrou;
	
	public static void main(String[] args) {
		
		
		tPopIni = 70;	//População inicial default
		pCorte = 4;		//Ponto de corte default/
		
		Scanner reader = new Scanner(System.in);
		
		//Permite informar um tamanho diferente para a população
		System.out.println("Informe o tamanho da população inicial: ");
		tPopIni = reader.nextInt();
		
		//Permite informar um ponto de corte diferente
		System.out.println("Informe o ponto de corte: ");
		pCorte = reader.nextInt();
		reader.close();
		
		//Inicializa os dados, gerando os indivíduos iniciais da população
		inicializaPopulacao(tPopIni);
		
		while (!encontrou) {	//Caso um resposta possível tenha sido encontrada, a iterações deixam de ser executadas, e o sistema parte para a apresentação do resultado
						
			//Método apenas para apresentação das gerações, ao mesmo tempo que elas são geradas. Não interfere na busca por uma resposta
			apresentaDadosGeracao(cromossomosPop);
			
			//Usados nos crusametos dos cromossomos
			Cromossomo[] cromossomosPai = new Cromossomo[tPopIni / 2];
			Cromossomo[] cromossomosMae = new Cromossomo[tPopIni / 2];
			
			//Seleciona os pais aleatóriamente, aumentando a possibilidade de varição genética
			selecaoPais(cromossomosPai, cromossomosMae);
			
			//Recombinação dos pais
			recombinacao(cromossomosPai, cromossomosMae);
			
			//Avaliação dos indivíduos
			avaliaPopulacao();
			
			//Seleção dos indivíduos mais adaptados
			selecionaProxGeracao();
			
			//Analisa os pesos dos indivíduos, para identificar se uma resposta possível já foi encontrada
			verificaIndividuosElegiveis();
			
		}
		
		System.out.println("\nEncontrado uma resposta depois de "+geracoes+" gerações ("+resposta+")");
		
	}
	
	public static void inicializaPopulacao(int tamPop) {
		cromossomosPop = new Cromossomo[tamPop];
		cromossomosFilho = new Cromossomo[tamPop];
		for (int i = 0; i < tamPop; i++) {
			cromossomosPop[i] = new Cromossomo();
		}
	}
	
	public static void verificaIndividuosElegiveis() {
		for (int i = 0; i < cromossomosPop.length; i++) {
			cromossomosPop[i].avaliaCromossomo();
			if (cromossomosPop[i].getPeso() == 0) {
				resposta = cromossomosPop[i];
				encontrou = true;
			}
		}
	}
	
	public static void selecaoPais(Cromossomo[] cromossomosPai, Cromossomo[] cromossomosMae) {
		int pai = 0;
		int mae = 0;
		for (int i = 0; i < tPopIni; i++) {
			if (((int) (Math.random() * 2)) % 2 == 0) {
				if (pai < tPopIni / 2) {
					cromossomosPai[pai++] = cromossomosPop[i];
				} else {
					cromossomosMae[mae++] = cromossomosPop[i];
				}
			} else {
				if (mae < tPopIni / 2) {
					cromossomosMae[mae++] = cromossomosPop[i];
				} else {
					cromossomosPai[pai++] = cromossomosPop[i];
				}
			}
		}
	}
	
	public static void recombinacao(Cromossomo[] cromossomosPai, Cromossomo[] cromossomosMae) {
		int posicao = -1;
		for (int i = 0; i < tPopIni / 2; i++) {
			cromossomosFilho[++posicao] = new Cromossomo(cromossomosPai[i].getGenes().clone());
			cromossomosFilho[++posicao] = new Cromossomo(cromossomosMae[i].getGenes().clone());
			for (int j = pCorte; j < cromossomosPai[i].nGenes; j++) {
				cromossomosFilho[posicao-1].getGenes()[j] = cromossomosMae[i].getGenes()[j];
				cromossomosFilho[posicao].getGenes()[j] = cromossomosPai[i].getGenes()[j];
			}
		}
	}
	
	public static void avaliaPopulacao() {
		for (int i = 0; i < tPopIni; i++) {
			cromossomosPop[i].avaliaCromossomo();
			cromossomosFilho[i].avaliaCromossomo();
		}
	}
	
	public static void selecionaProxGeracao() {
		ArrayList<Cromossomo> alCromossomos = new ArrayList<Cromossomo>();
		for (int i = 0; i < tPopIni; i++) {
			alCromossomos.add(cromossomosPop[i]);
			alCromossomos.add(cromossomosFilho[i]);
		}
		
		//Aplica mutação em um indivídou aleatório
		alCromossomos.get((int) (Math.random() * alCromossomos.size())).sofreMutacao();
		
		//Ordena os indivíduos pelo peso
		for (int i = 0; i < alCromossomos.size(); i++) {
			int pesoAtual = alCromossomos.get(i).getPeso();
			int posPesoMaior = i;
			for (int j = i + 1; j < alCromossomos.size(); j++) {
				if (alCromossomos.get(j).getPeso() > pesoAtual) {
					posPesoMaior = j;
					pesoAtual = alCromossomos.get(j).getPeso(); 
				}
			}
			if (posPesoMaior != i) {
				Cromossomo cromossomo = alCromossomos.get(posPesoMaior);
				alCromossomos.remove(posPesoMaior);
				alCromossomos.add(i, cromossomo);
			}
		}
		cromossomosPop = new Cromossomo[tPopIni];
		cromossomosFilho = new Cromossomo[tPopIni];
		
		//Mantém como população os melhores indivíos (método elitista)
		for (int i = 0; i < tPopIni; i++) {
			cromossomosPop[i] = alCromossomos.get(i);
		}
	}
	
	public static void apresentaDadosGeracao(Cromossomo[] populacao) {
		System.out.println("  *****  Geração " + geracoes++ + "  *****  ");
		for (int i = 0; i < populacao.length; i++) {
			System.out.println("Cromossomo: " + populacao[i].getGenotipo() + " - Peso: " + populacao[i].getPeso());
		}
		System.out.println();
	}
	
}

class Cromossomo {
	
	int nGenes;
	int peso;
	int[] genes;
	
	Cromossomo() {
		this.nGenes = 8;
		this.genes = new int[nGenes];
		for (int i = 0; i < nGenes; i++) {
			genes[i] = (int) (Math.random() * nGenes);
		}
		avaliaCromossomo();
	}
	
	Cromossomo(int[] genes) {
		this.nGenes = genes.length;
		this.genes = genes;
		avaliaCromossomo();
	}

	public int getnGenes() {
		return nGenes;
	}

	public void setnGenes(int nGenes) {
		this.nGenes = nGenes;
	}

	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}

	public int[] getGenes() {
		return genes;
	}

	public void setGenes(int[] genes) {
		this.genes = genes;
	}
	
	public void sofreMutacao() {
		int gene1 = (int) (Math.random() * nGenes);
		int gene2 = (int) (Math.random() * nGenes);
		while (gene1 == gene2) {
			gene2 = (int) (Math.random() * nGenes);
		}
		int gene = genes[gene1];
		genes[gene1] = genes[gene2];
		genes[gene2] = gene;
		
		avaliaCromossomo();
	}
	
	public void avaliaCromossomo() {
		peso = 0;
		for (int i = 0; i < nGenes; i++) {
			for (int j = i+1, k = 1; j < genes.length; j++, k++) {
				//Verifica rainhas na mesma linha ou coluna
				if (genes[i] == genes[j]) {
					peso -=10;
				}
				
				//Verifica rainhas na mesma diagonal
				if (genes[i]+k == genes[j] || genes[i]-k == genes[j]) {
					peso -=10;
				}
			}
		}
	}
	
	public String getFenotipo() {
		String retorno = " A" + (genes[0] + 1) 
				+ " B" + (genes[1] + 1) 
				+ " C" + (genes[2] + 1) 
				+ " D" + (genes[3] + 1)
				+ " E" + (genes[4] + 1)
				+ " F" + (genes[5] + 1)
				+ " G" + (genes[6] + 1)
				+ " H" + (genes[7] + 1);
		return retorno;
	}
	
	public String getGenotipo() {
		return genes[0] + "-" + genes[1] + "-" + genes[2] + "-" + genes[3] + "-" + genes[4] + "-" + genes[5] + "-" + genes[6] + "-" + genes[7];
	}
	
	@Override
	public String toString() {
		String retorno = "Fenótipo: " + getFenotipo() + " - Genótipo: " + getGenotipo();
		return retorno;
	}
	
}