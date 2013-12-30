package main;

import java.util.ArrayList;
import java.util.HashMap;

public class AFN {
	
	private String expressao;
	private ArrayList<String> simbolos;
	private HashMap<String, String[]> estados;
	private int contadorEstados;	
	
	private int estadoInicial;
	private int estadoInicialTemporario;
	private ArrayList<Integer> estadosDeAceitacao;
	
	private int posIniRecursao;
	private int posFimRecursao;
	
	public AFN(String expr){
		expressao = expr;
		simbolos = new ArrayList<String>();
		estados = new HashMap<String, String[]>();
		contadorEstados = 0;
		estadoInicial = -1;
		estadosDeAceitacao = new ArrayList<Integer>();
		posIniRecursao = -1;
		posFimRecursao = -1;
		
		definirSimbolos();
		definirEstados();
		imprimeTabela();
	}
	
	private void definirSimbolos(){		
		for (int i = 0 ; i < expressao.length() ; i++){
			if (ExpressaoRegular.isLetra(expressao.charAt(i)))
				if (!simbolos.contains(""+expressao.charAt(i)))
						simbolos.add(""+expressao.charAt(i));
		}
		simbolos.add(""+ExpressaoRegular.PALAVRA_VAZIA);
	}
	
	private void definirEstados(){
		int posIni = posIniRecursao == -1 ? 0 : posIniRecursao;
		int posFim = posFimRecursao == -1 ? expressao.length() : posFimRecursao;
		for (int i = posIni ; i < posFim ; i++){
			if (expressao.charAt(i) == '('){
				posIniRecursao = i + 1;
				posFimRecursao = expressao.indexOf(')', i);
				int numPosicoesAPular = posFimRecursao - posIniRecursao;
				int estadoInicialTemporarioParenteses = contadorEstados;
				definirEstados();
				estadoInicialTemporario = estadoInicialTemporarioParenteses;
				
				i += numPosicoesAPular;
			}
			else if (expressao.charAt(i) == ExpressaoRegular.VAZIO || 
					expressao.charAt(i) == ExpressaoRegular.PALAVRA_VAZIA){
				contadorEstados++;
				
				if (estadoInicial == -1)
					estadoInicial = contadorEstados;
				estadoInicialTemporario = contadorEstados;
				
				ArrayList<String> eEtr = inicializaEstados();
				estados.put("q"+contadorEstados, converterArrayListParaArray(eEtr));
				
				if (expressao.charAt(i) == ExpressaoRegular.PALAVRA_VAZIA){
					estadosDeAceitacao.clear();
					estadosDeAceitacao.add(contadorEstados);
				}
			}
			else if (expressao.charAt(i) == ExpressaoRegular.ESTRELA){
				contadorEstados++;
				ArrayList<String> eEtr = inicializaEstados();				
				eEtr.set(simbolos.indexOf(""+ExpressaoRegular.PALAVRA_VAZIA), "q"+estadoInicialTemporario);
				estados.put("q"+contadorEstados, converterArrayListParaArray(eEtr));
				
				for (Integer ea : estadosDeAceitacao){
					eEtr = converterArrayParaArrayList(estados.get("q"+ea));
					eEtr.set(simbolos.indexOf(""+ExpressaoRegular.PALAVRA_VAZIA), "q"+contadorEstados);
					estados.put("q"+ea, converterArrayListParaArray(eEtr));
				}				
				
				estadoInicial = contadorEstados;
				estadosDeAceitacao.clear();
				estadosDeAceitacao.add(contadorEstados);
			}
			else if (expressao.charAt(i) == ExpressaoRegular.CONCATENACAO){
				posIniRecursao = i+1;
				posFimRecursao = i+2;
				int numPosicoesAPular = posFimRecursao - posIniRecursao;
				contadorEstados++;
				definirEstados();
				estadosDeAceitacao.remove(estadosDeAceitacao.size()-1);
				
				for (Integer ea : estadosDeAceitacao){
					ArrayList<String> eEtr = converterArrayParaArrayList(estados.get("q"+ea));
					eEtr.set(simbolos.indexOf(""+ExpressaoRegular.PALAVRA_VAZIA), "q"+estadoInicialTemporario);
					estados.put("q"+ea, converterArrayListParaArray(eEtr));
				}
				
				estadosDeAceitacao.clear();
				estadosDeAceitacao.add(contadorEstados);
				
				i += numPosicoesAPular;
			}
			else if (expressao.charAt(i) == ExpressaoRegular.UNIAO){
				posIniRecursao = i+1;
				posFimRecursao = expressao.charAt(i+1) == '(' ? expressao.indexOf(')', i+1) : i+2;
				int numPosicoesAPular = posFimRecursao - posIniRecursao;
				int estadoA = estadoInicialTemporario;
				contadorEstados++;
				definirEstados();
				contadorEstados++;
				
				ArrayList<String> eEtr = inicializaEstados();				
				eEtr.set(simbolos.indexOf(""+ExpressaoRegular.PALAVRA_VAZIA), "q"+estadoA+"q"+estadoInicialTemporario);
				estados.put("q"+contadorEstados, converterArrayListParaArray(eEtr));
				
				estadoInicial = contadorEstados;				
				
				i += numPosicoesAPular;
			}
			else if (ExpressaoRegular.isLetra(expressao.charAt(i))){
				if (estadoInicial == -1)
					estadoInicial = contadorEstados;
				estadoInicialTemporario = contadorEstados;
				
				ArrayList<String> eEtr = inicializaEstados();				
				eEtr.set(simbolos.indexOf(""+expressao.charAt(i)), "q"+(contadorEstados+1));
				estados.put("q"+contadorEstados, converterArrayListParaArray(eEtr));				
				contadorEstados++;
				
				eEtr = inicializaEstados();
				estados.put("q"+contadorEstados, converterArrayListParaArray(eEtr));			
				
				estadosDeAceitacao.add(contadorEstados);				
			}
		}
		posIniRecursao = -1;
		posFimRecursao = -1;
	}
	
	private ArrayList<String> inicializaEstados(){
		ArrayList<String> array = new ArrayList<String>();		
		for (int i = 0 ; i < simbolos.size() ; i++)
			array.add(ExpressaoRegular.VAZIO+" ");
		
		return array;
	}
	
	private String[] converterArrayListParaArray(ArrayList<String> arraylist){
		String[] array = new String[simbolos.size()];
		int i = 0;
		
		for (String a : arraylist)
			array[i++] = a;
		
		return array;
	}
	
	private ArrayList<String> converterArrayParaArrayList(String[] array){
		ArrayList<String> arraylist = new ArrayList<String>();
		
		for (String a : array)
			arraylist.add(a);
		
		return arraylist;
	}
	
	private void imprimeTabela(){
		System.out.print("    ");
		for (String s : simbolos)
			System.out.print(s+"  ");
		System.out.println("");
		
		Object[] keysInv = estados.keySet().toArray();
		Object[] keys = new Object[keysInv.length];
		
		for (int i = 0 ; i < keysInv.length; i++)
			keys[i] = keysInv[keysInv.length - i - 1];
		
		for (Object o : keys){
			System.out.print(o+": ");
			for (String s : estados.get(o+""))
				System.out.print(s+" ");
			
			System.out.println("");
		}
		
		System.out.println("\nEstado inicial: q"+estadoInicial);
		System.out.print("Estado de aceitacao: ");
		
		for (Integer ea : estadosDeAceitacao)
			System.out.print("q"+ea+" ");
	}
	
}