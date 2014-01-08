package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class AFD extends AutomatoFinito{	
	
	private HashMap<String, String[]> estadosDoAFN;
	
	private String indiceEstadoTemporario;
	
	private String[] estadoTemporario;
	
	private String[] estadoTemporario2;
	
	private int numeroRecursoes;
	
	private HashMap<String, String> estadoCorrespondente;
	
	private String produtoCartesianoDoEstadoAtual;
	
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
		produtoCartesianoDoEstadoAtual = "";
		numeroRecursoes = 0;
		
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
			for (int j = 0 ; j < estadoAtual.length && apenasTransicaoVazia; j++){
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
					numeroRecursoes++;
					produtoCartesianoDoEstadoAtual += "q"+indice;
					definirEstados();
					numeroRecursoes--;
					
					if (estadoTemporario == null){
						estadoTemporario = estadoTemporario2;
						estadoTemporario2 = null;
					}
					
					for (int j = 0 ; j < estadoAtual.length ; j++){
						if (!estadoTemporario[j].equals(""+ExpressaoRegular.VAZIO))
							if (estadoAtual[j].equals(""+ExpressaoRegular.VAZIO))
								estadoAtual[j] = estadoTemporario[j];
							else
								estadoAtual[j] = estadoTemporario[j]+estadoAtual[j]; 
					}
					
					
					if (numeroRecursoes == 0){
						if (produtoCartesianoDoEstadoAtual.isEmpty())
							estados.put("q"+i, estadoAtual);
						else{
							String pc = produtoCartesianoOrdenado("q"+i+produtoCartesianoDoEstadoAtual);
							estados.put(pc, estadoAtual);
							
							substituiTransicoes("q"+i, pc);
							
							if (pc.contains(estadoInicial))
								estadoInicial = pc.substring(1);
						}						
						
						produtoCartesianoDoEstadoAtual = "";
					}
					//else
						//produtoCartesianoDoEstadoAtual += "q"+i;  
					
					
					//estados.put(produtoCartesianoDoEstadoAtual, estadoAtual);
					//produtoCartesianoDoEstadoAtual = "";
					
					//estados.put("q"+i, estadoAtual);
					estadoTemporario = null;					
				}
			}
			else if (!apenasTransicaoVazia && posIniRecursao != -1){
				estadoTemporario = estadoAtual;
				estadoTemporario2 = estadoAtual;
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
	
	private void substituiTransicoes(String old, String nu){
		Set<String> chaves = estados.keySet();		
		
		for (String s : chaves){
			String[] estadoAtual = estados.get(s);
			
			for (int i = 0 ; i < estadoAtual.length ; i++)
				if (estadoAtual[i].equals(old))
					estadoAtual[i] = nu;
			
			estados.put(s, estadoAtual);
		}
	}
	
	private String produtoCartesianoOrdenado(String estados){
		String[] pc = estados.split("q");
		for (int i = 1 ; i < pc.length ; i++)
			for (int j = 1 ; j < pc.length ; j++){
				if (Integer.parseInt(pc[i]) <  Integer.parseInt(pc[j])){
					String aux = pc[i];
					pc[i] = pc[j];
					pc[j] = aux;
				}
			}
		
		String pcOrdenado = "";
		
		for (int i = 1 ; i < pc.length ; i++)
			pcOrdenado += "q"+pc[i];
		
		return pcOrdenado;
	}
	
	public boolean checaValidadePalavra(String palavra){
		String estadoAtual = "q"+estadoInicial;
		
		for (int i = 0 ; i < palavra.length() ; i++){			
			String[] transicoes = estados.get(estadoAtual);
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
