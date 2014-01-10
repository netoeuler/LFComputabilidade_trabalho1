package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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
		contadorEstados = estadosDoAFN.size();
		
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
			boolean ehConjuntoDeEstados = false;
			
			String[] estadoAtual = estadosDoAFN.get("q"+i);
			if (estadoAtual == null){
				estadoAtual = estadosDoAFN.get( estadoCorrespondente.get("q"+i) );
				if (estadoAtual == null)
					estadoAtual = estadosDoAFN.get(""+ExpressaoRegular.VAZIO);
				else
					ehConjuntoDeEstados = true;
			}
			
			for (int k = 0 ; k < estadoAtual.length ; k++){
				if (!estadoAtual[k].equals(""+ExpressaoRegular.VAZIO))
					estadoAtual[k] = ordenarConjuntoDeEstados(estadoAtual[k]);
			}			
			
			boolean apenasTransicaoVazia = true;			
			//Verifica todas as transições do estado
			for (int j = 0 ; j < estadoAtual.length && apenasTransicaoVazia; j++){
				estadoAtual[j] = estadoAtual[j].trim();
				
				if (j != (estadoAtual.length - 1) && !estadoAtual[j].equals(""+ExpressaoRegular.VAZIO))
					apenasTransicaoVazia = false;
			}
			
			if (ehConjuntoDeEstados ||
					(apenasTransicaoVazia && !estadoAtual[estadoAtual.length - 1].equals(""+ExpressaoRegular.VAZIO))){
				String[] transicoesVazias = estadoAtual[estadoAtual.length - 1].split("q");
				for (String ind : transicoesVazias){
					if (ind.isEmpty())
						continue;
					
					if (!ehConjuntoDeEstados){
						int indice = new Integer(ind);
						posIniRecursao = indice;
						posFimRecursao = indice+1;					
						definirEstados();
					}										
					
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
					//estadoTemporario = null;					
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
	
	private void geraConjuntoDeEstados(String est){
		ArrayList<String> array = inicializaEstados();
		
		String[] listaEstados = est.substring(1).split("q");
		String[] estadoTemporario = null;
		
		for (String e : listaEstados){
			if (e.trim().isEmpty())
				continue;
			
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