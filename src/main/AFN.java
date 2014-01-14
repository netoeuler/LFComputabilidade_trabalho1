package main;

import java.util.ArrayList;
import java.util.HashMap;

public class AFN extends AutomatoFinito{
	
	private ArrayList<String> ultimosEstadosDeAceitacaoAdicionados;
	
	public AFN(String expr){
		expressao = expr;
		simbolos = new ArrayList<String>();
		estados = new HashMap<String, String[]>();
		contadorEstados = 0;
		estadoInicial = "-1";
		estadosDeAceitacao = new ArrayList<Integer>();
		posIniRecursao = -1;
		posFimRecursao = -1;
		ultimosEstadosDeAceitacaoAdicionados = new ArrayList<String>();
		
		definirSimbolos();
		definirEstados();
	}
	
	/**
	 * Preenche a lista de símbolos com todos os símbolos reconhecidos
	 * na expressão regular
	 */
	private void definirSimbolos(){		
		for (int i = 0 ; i < expressao.length() ; i++){
			if (ExpressaoRegular.isLetra(expressao.charAt(i)))
				if (!simbolos.contains(""+expressao.charAt(i)))
						simbolos.add(""+expressao.charAt(i));
		}
		simbolos.add(""+ExpressaoRegular.PALAVRA_VAZIA);
	}
	
	/**
	 * Preenche a lista de estados da expressão regular.
	 * Cada estado é adicionado na variável ArrayList<String> eEtr (estados E transições)
	 * que possui todas as transições correspondentes ao estado (é inicializado com todas 
	 * as transições vazias) e em seguida adicionado na lista de estados da expressão regular.
	 * 
	 */
	private void definirEstados(){
		int posIni = posIniRecursao == -1 ? 0 : posIniRecursao;
		int posFim = posFimRecursao == -1 ? expressao.length() : posFimRecursao;
		
		for (int i = posIni ; i < posFim ; i++){
			if (expressao.charAt(i) == '('){
				posIniRecursao = i + 1;
				calculaPosFimRecursao(i);
				
				int numPosicoesAPular = posFimRecursao - posIniRecursao;				
				definirEstados();
				
				i += numPosicoesAPular;
			}
			else if (expressao.charAt(i) == ExpressaoRegular.VAZIO || expressao.charAt(i) == ExpressaoRegular.PALAVRA_VAZIA){
				contadorEstados++;
				
				if (estadoInicial.equals("-1"))
					estadoInicial = ""+contadorEstados;
				estadoInicialTemporario = ""+contadorEstados;
				
				ArrayList<String> eEtr = inicializaEstados();
				estados.put("q"+contadorEstados, converterArrayListParaArray(eEtr));
				
				if (expressao.charAt(i) == ExpressaoRegular.PALAVRA_VAZIA){
					estadosDeAceitacao.remove(estadosDeAceitacao.size()-1);
					estadosDeAceitacao.add(contadorEstados);
				}
			}
			else if (expressao.charAt(i) == ExpressaoRegular.ESTRELA){
				contadorEstados++;
				ArrayList<String> eEtr = inicializaEstados();				
				eEtr.set(simbolos.indexOf(""+ExpressaoRegular.PALAVRA_VAZIA), "q"+estadoInicialTemporario);
				estados.put("q"+contadorEstados, converterArrayListParaArray(eEtr));
				
				ArrayList<String> estadoB = new ArrayList<String>();
				for (String e : ultimosEstadosDeAceitacaoAdicionados)
					estadoB.add(e);
				
				ultimosEstadosDeAceitacaoAdicionados.clear();
				
				//for (Integer ea : estadosDeAceitacao){
				for (String ea : estadoB){
					int indV = simbolos.indexOf(""+ExpressaoRegular.PALAVRA_VAZIA);					
					eEtr = converterArrayParaArrayList(estados.get("q"+ea));
					if (estados.get("q"+ea)[indV].equals(""+ExpressaoRegular.VAZIO))
						eEtr.set(indV, "q"+estadoInicialTemporario);
					else{
						String estadoASerAdicionado = ordenarConjuntoDeEstados(estados.get("q"+ea)[indV]+"q"+estadoInicialTemporario);
						eEtr.set(indV, estadoASerAdicionado);
					}
					estados.put("q"+ea, converterArrayListParaArray(eEtr));
					ultimosEstadosDeAceitacaoAdicionados.add(""+ea);
				}
				
				estadoInicialTemporario = ""+(contadorEstados);
				
				String[] estadoIniEstrela = estados.get("q"+contadorEstados);
				if (estadoIniEstrela[estadoIniEstrela.length-1].contains("q"+estadoInicial))
					estadoInicial = ""+contadorEstados;
								
				estadosDeAceitacao.add(contadorEstados);
				ultimosEstadosDeAceitacaoAdicionados.add(""+contadorEstados);
			}
			else if (expressao.charAt(i) == ExpressaoRegular.CONCATENACAO){
				posIniRecursao = i+1;
				calculaPosFimRecursao(i);
				//posFimRecursao = expressao.charAt(i+1) == '(' ? expressao.indexOf(')', i+1) : i+2;
					
				int numPosicoesAPular = posFimRecursao - posIniRecursao;
				String estadoA = estadoInicialTemporario;				
				ArrayList<String> estadoB = new ArrayList<String>();
				for (String e : ultimosEstadosDeAceitacaoAdicionados)
					estadoB.add(e);
				
				//Se estadoB for vazio, é porque não tinha sido adicionado nenhum estado
				//antes de ser chamada a concatenação. Com isso, é removido o último estado
				//de aceitação registrado já que é o estado que deixará de ser de aceitação
				//pois vai haver uma transição dele pra a concatenação
				if (estadoB.size() == 0)
					estadosDeAceitacao.remove( estadosDeAceitacao.size()-1 );
					
				contadorEstados++;
				definirEstados();
				
				//Os estados que eram de aceitação antes da concatenação deixam de ser...
				for (String ea : estadoB)
					if (estadosDeAceitacao.contains(new Integer(ea)))
						estadosDeAceitacao.remove( estadosDeAceitacao.indexOf(new Integer(ea)) );
				
				//...e é adicionada uma transição vazia dele(s) para o estado inicial da concatenação
				for (String ea : estadoB){
					ArrayList<String> eEtr = converterArrayParaArrayList(estados.get("q"+ea));
					int indV = simbolos.indexOf(""+ExpressaoRegular.PALAVRA_VAZIA);
					if (estados.get("q"+ea)[indV].equals(""+ExpressaoRegular.VAZIO))
						eEtr.set(indV, "q"+estadoInicialTemporario);
					else{
						String estadoASerAdicionado = ordenarConjuntoDeEstados(estados.get("q"+ea)[indV]+"q"+estadoInicialTemporario);
						eEtr.set(indV, estadoASerAdicionado);
					}
					estados.put("q"+ea, converterArrayListParaArray(eEtr));
				}				
				
				ultimosEstadosDeAceitacaoAdicionados.clear();				
				estadoInicialTemporario = estadoA;
				
				i += numPosicoesAPular;
			}
			else if (expressao.charAt(i) == ExpressaoRegular.UNIAO){
				posIniRecursao = i+1;
				calculaPosFimRecursao(i);
				//posFimRecursao = expressao.charAt(i+1) == '(' ? expressao.indexOf(')', i+1) : i+2;				
				
				int numPosicoesAPular = posFimRecursao - posIniRecursao;
				String estadoA = estadoInicialTemporario;
				ArrayList<String> estadoB = new ArrayList<String>();
				for (String e : ultimosEstadosDeAceitacaoAdicionados)
					estadoB.add(e);
				
				contadorEstados++;
				definirEstados();
				contadorEstados++;
				
				for (String e : estadoB)
					ultimosEstadosDeAceitacaoAdicionados.add(e);
				
				ArrayList<String> eEtr = inicializaEstados();				
				eEtr.set(simbolos.indexOf(""+ExpressaoRegular.PALAVRA_VAZIA), "q"+estadoA+"q"+estadoInicialTemporario);
				estados.put("q"+contadorEstados, converterArrayListParaArray(eEtr));								
				
				String[] estadoIniUniao = estados.get("q"+contadorEstados);
				if (estadoIniUniao[estadoIniUniao.length-1].contains("q"+estadoInicial))
					estadoInicial = ""+contadorEstados;
				
				//estadoInicial = ""+contadorEstados;
				estadoInicialTemporario = ""+contadorEstados;
				
				i += numPosicoesAPular;
			}
			else if (ExpressaoRegular.isLetra(expressao.charAt(i))){
				if (estadoInicial.equals("-1"))
					estadoInicial = ""+contadorEstados;
				estadoInicialTemporario = ""+contadorEstados;
				
				ArrayList<String> eEtr = inicializaEstados();				
				eEtr.set(simbolos.indexOf(""+expressao.charAt(i)), "q"+(contadorEstados+1));
				estados.put("q"+contadorEstados, converterArrayListParaArray(eEtr));				
				contadorEstados++;
				
				eEtr = inicializaEstados();
				estados.put("q"+contadorEstados, converterArrayListParaArray(eEtr));			
				
				estadosDeAceitacao.add(contadorEstados);
				ultimosEstadosDeAceitacaoAdicionados.clear();
				ultimosEstadosDeAceitacaoAdicionados.add(""+contadorEstados);
			}
		}
		
		posIniRecursao = -1;
		posFimRecursao = -1;
	}
	
	private void calculaPosFimRecursao(int i){
		if (expressao.charAt(i+1) != '(')
			posFimRecursao = i+2;
		else{
			int contadorParenteses = 0;
			posFimRecursao = i+2;
			while (true){
				if (expressao.charAt(posFimRecursao) == ')' && contadorParenteses == 0)
					break;
					
				if (expressao.charAt(posFimRecursao) == '(')
					contadorParenteses++;
				else if (expressao.charAt(posFimRecursao) == ')')
					contadorParenteses--;
				
				posFimRecursao++;
			}
		}
	}
	
}