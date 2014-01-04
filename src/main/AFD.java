package main;

import java.util.ArrayList;
import java.util.HashMap;

public class AFD extends AutomatoFinito{	
	
	private HashMap<String, String[]> estadosDoAFN;
	
	private String indiceEstadoTemporario;
	
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
		
		definirEstados();
		removeTransicoesDeSimboloVazio();
	}	
	
	public void definirEstados(){
		ArrayList<String> eEtr;
		
		if (!estados.containsKey(""+ExpressaoRegular.VAZIO)){
			eEtr = inicializaEstados();		
			estados.put(""+ExpressaoRegular.VAZIO, converterArrayListParaArray(eEtr));
		}
		
		int posIni = posIniRecursao == -1 ? 0 : posIniRecursao;
		int posFim = posFimRecursao == -1 ? estadosDoAFN.size() : posFimRecursao;
		
		for (int i = posIni ; i < posFim ; i++){
			String[] estadoAtual = estadosDoAFN.get("q"+i) == null ? estadosDoAFN.get(""+ExpressaoRegular.VAZIO) : estadosDoAFN.get("q"+i);	
			
			boolean apenasTransicaoVazia = true;
			//Verifica todas as transições do estado
			for (int j = 0 ; j < estadoAtual.length ; j++){
				estadoAtual[j] = estadoAtual[j].trim();
				
				if (j != (estadoAtual.length - 1) && !estadoAtual[j].equals(""+ExpressaoRegular.VAZIO))
					apenasTransicaoVazia = false;
					
				//Adiciona os estados que não fazem parte do AFN
				 /*if (!estados.containsKey(estadoAtual[j])){
					 eEtr = inicializaEstados();
					 estados.put(""+estadoAtual[j], converterArrayListParaArray(eEtr));
				 }*/
			}
			
			if (apenasTransicaoVazia && !estadoAtual[estadoAtual.length - 1].equals(""+ExpressaoRegular.VAZIO)){
				String[] transicoesVazias = estadoAtual[estadoAtual.length - 1].split("q");
				for (String ind : transicoesVazias){
					if (ind.isEmpty())
						continue;
					
					int indice = new Integer(ind);
					posIniRecursao = indice;
					posFimRecursao = indice+1;
					definirEstados();
					
					for (int j = 0 ; j < estadoAtual.length ; j++){
						if (!estadoTemporario[j].equals(""+ExpressaoRegular.VAZIO))
							if (estadoAtual[j].equals(""+ExpressaoRegular.VAZIO))
								estadoAtual[j] = estadoTemporario[j];
							else
								estadoAtual[j] = estadoTemporario[j]+estadoAtual[j]; 
					}
					
					estados.put("q"+i, estadoAtual);
					estadoTemporario = null;
				}
			}
			else if (!apenasTransicaoVazia && posIniRecursao != -1){
				estadoTemporario = estadoAtual;
				indiceEstadoTemporario = ""+i;
			}
		}
		
		posIniRecursao = -1;
		posFimRecursao = -1;		
	}
	
	private void removeTransicoesDeSimboloVazio(){
		for (int i = 0 ; i < estados.size() ; i++){
			String[] estadoAtual = estados.get("q"+i);
			
			if (estadoAtual != null)
				estadoAtual[estadoAtual.length - 1] = "";
		}
	}
	
	public boolean checaValidadePalavra(String palavra){
		String estadoAtual = "q"+estadoInicial;
		
		for (int i = 0 ; i < palavra.length() ; i++){			
			String[] transicoes = estados.get(estadoAtual);
			int pos = simbolos.indexOf(""+palavra.charAt(i));
			estadoAtual = transicoes[pos];
		}
		
		estadoAtual = estadoAtual.replace("q", "").trim();
		
		if (estadoAtual.equals(""+ExpressaoRegular.VAZIO))
			return false;
		else		
			return estadosDeAceitacao.contains(new Integer(estadoAtual));		
	}

}
