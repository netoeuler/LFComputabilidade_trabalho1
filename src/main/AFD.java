package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class AFD extends AutomatoFinito{	
	
	private HashMap<String, String[]> estadosDoAFN;
	
	private String[] estadoTemporario;	
	
	private HashMap<String, String> estadoCorrespondente;	
	
	public AFD(Object[] variaveis){		
		estadosDoAFN = (HashMap<String, String[]>) variaveis[1];
		estadoCorrespondente = new HashMap<String, String>();
		
		simbolos = (ArrayList<String>) variaveis[0];
		simbolos.remove(this.simbolos.size()-1);		
		estados = (HashMap<String, String[]>) variaveis[1];
		estadoInicial = (String) variaveis[2];
		estadosDeAceitacao = (ArrayList<Integer>) variaveis[3];
		posIniRecursao = -1;
		posFimRecursao = -1;
		contadorEstados = estadosDoAFN.size();
		
		definirEstados();
		removeTransicoesDeSimboloVazio();		
	}	
	
	/**
	 * Verifica todos os estados definidos no AFN e faz suas devidas alterações
	 * para formar um AFD
	 */
	public void definirEstados(){
		ArrayList<String> eEtr;
		
		if (!estados.containsKey(""+ExpressaoRegular.VAZIO)){
			eEtr = inicializaEstados();		
			estados.put(""+ExpressaoRegular.VAZIO, converterArrayListParaArray(eEtr));
		}
		
		int posIni = posIniRecursao == -1 ? 0 : posIniRecursao;
		int posFim = posFimRecursao == -1 ? estadosDoAFN.size() : posFimRecursao;
		
		for (int i = posIni ; i < posFim ; i++){
			boolean ehConjuntoDeEstados = false;
			
			String[] estadoAtual = estadosDoAFN.get("q"+i);
			if (estadoAtual == null){
				estadoAtual = estadosDoAFN.get( estadoCorrespondente.get("q"+i) );
				if (estadoAtual == null)
					estadoAtual = estadosDoAFN.get(""+ExpressaoRegular.VAZIO);
				else
					ehConjuntoDeEstados = true;
			}
			
			//Checa se alguma transição é conjunto de estados e se for ordena
			for (int k = 0 ; k < estadoAtual.length ; k++){
				int numQs = estadoAtual[k].substring(1).split("q").length;
				if (!estadoAtual[k].equals(""+ExpressaoRegular.VAZIO) && numQs > 1)
					estadoAtual[k] = ordenarConjuntoDeEstados(estadoAtual[k]);
			}			
			
			boolean apenasTransicaoVazia = true;			
			//Verifica todas as transições do estado
			for (int j = 0 ; j < estadoAtual.length && apenasTransicaoVazia; j++){				
				if (j != (estadoAtual.length - 1) && !estadoAtual[j].equals(""+ExpressaoRegular.VAZIO))
					apenasTransicaoVazia = false;
			}
			
			//Faz o tratamento do estado se ele tiver transição vazia ou for um conjunto de estados
			if ((apenasTransicaoVazia && !estadoAtual[estadoAtual.length - 1].equals(""+ExpressaoRegular.VAZIO))
					|| ehConjuntoDeEstados){
				String[] transicoesVazias = estadoAtual[estadoAtual.length - 1].substring(1).split("q");
				for (String ind : transicoesVazias){					
					//Se for uma transição vazia, faz recursão até achar uma transição que não seja vazia
					//para associar o estado ao primeiro após a transição vazia
					//Ex: (q0 -E-> q1 -a-> q2) vira (q0 -a-> q2)					
					if (!ehConjuntoDeEstados){
						int indice = new Integer(ind);
						posIniRecursao = indice;
						posFimRecursao = indice+1;					
						definirEstados();
					}
					else
						estadoTemporario = converterArrayListParaArray(inicializaEstados());
					
					for (int j = 0 ; j < estadoAtual.length ; j++){
						if (!estadoTemporario[j].equals(""+ExpressaoRegular.VAZIO))
							if (estadoAtual[j].equals(""+ExpressaoRegular.VAZIO))
								estadoAtual[j] = estadoTemporario[j];
							else{
								estadoAtual[j] = estadoTemporario[j]+estadoAtual[j];								
								estadoAtual[j] = ordenarConjuntoDeEstados(estadoAtual[j]);
								geraConjuntoDeEstados(estadoAtual[j]);
							}
					}					
					
					if (ehConjuntoDeEstados)
						estados.put(estadoCorrespondente.get("q"+i), estadoAtual);
					else
						estados.put("q"+i, estadoAtual);
				}
			}
			else if (!apenasTransicaoVazia && posIniRecursao != -1){
				//Ao percorrer a transição vazia e achar uma que não seja, o estado que possui essa
				//transição é armazenado em estadoTemporario
				estadoTemporario = estadoAtual;
			}
		}
		
		posIniRecursao = -1;
		posFimRecursao = -1;		
	}
	
	/**
	 * Remove as transições de palavra vazia, pois só fazem parte do AFN
	 */
	private void removeTransicoesDeSimboloVazio(){
		for (int i = 0 ; i < estados.size() ; i++){
			String[] estadoAtual = estados.get("q"+i);
			
			if (estadoAtual != null)
				estadoAtual[estadoAtual.length - 1] = "";
		}
	}
	
	/**
	 * Ordena os conjuntos de estados para que não haja distinção.
	 * Evita, por exemplo, que q0q1 seja diferente de q1q0. 
	 * Então faz a ordenação de q1q0 ficando q0q1
	 * @param estados
	 * @return
	 */
	private String ordenarConjuntoDeEstados(String estados){		
		String[] pc = estados.substring(1).split("q");
		for (int i = 0 ; i < pc.length ; i++)
			for (int j = 0 ; j < pc.length ; j++){
				if (Integer.parseInt(pc[i]) <  Integer.parseInt(pc[j])){
					String aux = pc[i];
					pc[i] = pc[j];
					pc[j] = aux;
				}
			}
		
		estados = "";
		
		for (int i = 0 ; i < pc.length ; i++){
			if (!estados.contains("q"+pc[i]))
				estados += "q"+pc[i];
		}
		
		return estados;
	}
	
	/**
	 * Gera o conjunto de estados passado como parâmetro combinando as transições
	 * dos estados que fazem parte do conjunto
	 * @param est
	 */
	private void geraConjuntoDeEstados(String est){
		ArrayList<String> array = inicializaEstados();
		
		String[] listaEstados = est.substring(1).split("q");
		String[] estadoTemporario = null;
		
		for (String e : listaEstados){
			String[] estadoAtual = estadosDoAFN.get("q"+e);
			
			for (int i = 0 ; i < estadoAtual.length - 1; i++){
				if (estadoTemporario != null && !estadoAtual[i].equals(""+ExpressaoRegular.VAZIO))
					if (estadoAtual[i].equals(""+ExpressaoRegular.VAZIO))
						estadoAtual[i] = estadoTemporario[i];
					else{
						String[] estAux = estadoTemporario[i].substring(1).split("q");
						for (String eaux : estAux){
							if (!estadoAtual[i].contains("q"+eaux))
								estadoAtual[i] += ("q"+eaux);
						}
					}
				
				array.set(i, estadoAtual[i]);
			}			
			estadoTemporario = estadoAtual;
		}
		
		for (int i = 0 ; i < array.size() ; i++){
			if (!array.get(i).equals(""+ExpressaoRegular.VAZIO))
				array.set(i, ordenarConjuntoDeEstados(array.get(i)));
		}
		
		estados.put(est, converterArrayListParaArray(array));
		estadoCorrespondente.put("q"+(contadorEstados++), est);
	}
	
	/**
	 * Passa uma palavra como parâmetro e ela percorre os estados
	 * do AFD para saber se é válida
	 * @param palavra
	 * @return
	 */
	public boolean checaValidadePalavra(String palavra){
		String estadoAtual = "q"+estadoInicial;
		
		for (int i = 0 ; i < palavra.length() ; i++){			
			String[] transicoes = estados.get(estadoAtual);
			if (transicoes == null){
				transicoes = estados.get( estadoCorrespondente.get(estadoAtual) );
				if (transicoes == null)
					transicoes = estados.get(""+ExpressaoRegular.VAZIO);
			}
			
			int pos = simbolos.indexOf(""+palavra.charAt(i));
			if (!transicoes[pos].equals(""+ExpressaoRegular.VAZIO))
				transicoes[pos] = transicoes[pos].replace(""+ExpressaoRegular.VAZIO, "").trim();
			estadoAtual = transicoes[pos];
		}
		
		String[] estadoAtualArray = estadoAtual.replace("q", " ").substring(1).split(" ");
		
		if (estadoAtual.equals(""+ExpressaoRegular.VAZIO))
			return false;
		else{
			for (String ea : estadoAtualArray){
				if (estadosDeAceitacao.contains(new Integer(ea)))
					return true;
			}
			
			return false;
		}
	}

}