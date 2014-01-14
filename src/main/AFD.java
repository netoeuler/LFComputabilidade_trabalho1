package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class AFD extends AutomatoFinito{	
	
	private HashMap<String, String[]> estadosDoAFN;
	
	private ArrayList<String> estadoTemporario;	
	
	private HashMap<String, String> estadoCorrespondente;	
	
	public AFD(Object[] variaveis){		
		estadosDoAFN = (HashMap<String, String[]>) variaveis[1];
		estadoCorrespondente = new HashMap<String, String>();
		
		simbolos = (ArrayList<String>) variaveis[0];
		simbolos.remove(this.simbolos.size()-1);		
		estadoInicial = (String) variaveis[2];
		estadosDeAceitacao = (ArrayList<Integer>) variaveis[3];
		posIniRecursao = -1;
		posFimRecursao = -1;
		contadorEstados = estadosDoAFN.size();
		estadoTemporario = new ArrayList<String>();
		
		estados = new HashMap<String, String[]>();
		for (int i = 0 ; i < estadosDoAFN.size() ; i++){
			String[] est = estadosDoAFN.get("q"+i);
			estados.put("q"+i, est);
		}
		estados.put(""+ExpressaoRegular.VAZIO, converterArrayListParaArray(inicializaEstados()));
		
		eliminarTransicoesVazias();
		adicionarEstadosCompostos();
		removeTransicoesDeSimboloVazio();		
	}
	
	/**
	 * Adiciona os estados compostos que são formados ao converter o AFN em AFD
	 */
	private void adicionarEstadosCompostos(){
		for (int i = 0 ; i < estadosDoAFN.size() ; i++){
			String[] estadoAtual = estadosDoAFN.get("q"+i);
			
			for (int j = 0 ; j < estadoAtual.length; j++){
				String[] conjEstados = estadoAtual[j].substring(1).split("q");
				if (conjEstados.length > 1)
					geraConjuntoDeEstados(estadoAtual[j]);
			}
		}
	}
	
	/**
	 * Elimina as transições vazias uma vez que estas não fazem parte do AFD
	 * Para remover uma transição vazia de um estado é feita uma chamada recursiva "caminhando"
	 * sobre suas transições vazias até que encontre uma que não seja.
	 * Exemplificando: (qo -E-> q1 q1 -a->q2) vira (q0 -a-> q2)
	 */
	private void eliminarTransicoesVazias(){
		int posIni = posIniRecursao == -1 ? 0 : posIniRecursao;
		int posFim = posFimRecursao == -1 ? estadosDoAFN.size() : posFimRecursao;
		
		for (int i = posIni ; i < posFim ; i++){
			String[] estadoAtual = estadosDoAFN.get("q"+i);
			int indV = simbolos.size();			
			
			if (!estadoAtual[indV].equals(""+ExpressaoRegular.VAZIO)){
				String[] transicoesVazias = estadoAtual[estadoAtual.length - 1].substring(1).split("q");
				for (String ind : transicoesVazias){					
					int indice = new Integer(ind);
					posIniRecursao = indice;
					posFimRecursao = indice+1;
					
					//Como as transições vazias serão removidas, se um estado A possui
					//transição vazia com um estado B que é estado de aceitação, então
					//A passa a ser estado de aceitação
					if (estadosDeAceitacao.contains(indice) && !estadosDeAceitacao.contains(i))
						estadosDeAceitacao.add(i);
					
					eliminarTransicoesVazias();
					
					String[] estTemp;
					if (estadoTemporario.size() > 0)
						estTemp = estados.get(estadoTemporario.get(estadoTemporario.size() - 1));
					else
						estTemp = estados.get("q"+ind);
					
					for (int j = 0 ; j < estadoAtual.length - 1; j++){						
						if (!estTemp[j].equals(""+ExpressaoRegular.VAZIO))
							if (estadoAtual[j].equals(""+ExpressaoRegular.VAZIO))
								estadoAtual[j] = estTemp[j];
							else{
								estadoAtual[j] = estTemp[j]+estadoAtual[j];								
								estadoAtual[j] = ordenarConjuntoDeEstados(estadoAtual[j]);
								geraConjuntoDeEstados(estadoAtual[j]);
							}
					}
					estadoAtual[estadoAtual.length - 1] = ""+ExpressaoRegular.VAZIO;
					
					estados.put("q"+i, estadoAtual);
					if (estadoTemporario.size() > 0)
						estadoTemporario.remove(estadoTemporario.size() - 1);
				}
			}
			else if (posIniRecursao != -1)			
				estadoTemporario.add("q"+i);
		}
		
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
	 * Gera o conjunto de estados passado como parâmetro combinando as transições
	 * dos estados que fazem parte do conjunto
	 * @param est
	 */
	private void geraConjuntoDeEstados(String est){
		Set<String> keys = estadoCorrespondente.keySet();
		for (String k : keys)
			if (estadoCorrespondente.get(k).equals(est))
				return;
		
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
				
				if (!estadoAtual[i].equals(""+ExpressaoRegular.VAZIO))
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
	public int checaValidadePalavra(String palavra){
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
			return 0; //false
		else{
			for (String ea : estadoAtualArray){
				if (estadosDeAceitacao.contains(new Integer(ea)))
					return 1; //true
			}
			
			return 0; //false
		}
	}

}