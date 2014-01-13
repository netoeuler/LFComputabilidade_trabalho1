package main;

import java.util.ArrayList;
import java.util.HashMap;

public class AutomatoFinito {
	
	//Expressão regular do automato finito
	protected String expressao;	

	//Símbolos pertencentes à linguagem do automato finito
	protected ArrayList<String> simbolos;	

	//HashMap com os estados do automato finito, onde o índice é o estado e seu conteúdo são as suas transições
	protected HashMap<String, String[]> estados;
	
	//Contador usado para nomear os estados. É incrementado cada vez que um novo estado é adicionado
	protected int contadorEstados;	
	
	//Estado inicial do automato finito
	protected String estadoInicial;
	
	//Estado inicial da sub-expressão que está sendo analisado. 
	//É usada para não perder o valor do estado inicial do automato finito
	protected String estadoInicialTemporario;
	
	//Estados de aceitação do automato finito
	protected ArrayList<Integer> estadosDeAceitacao;
	
	//Variável para guardar a posição inicial quando for iniciar uma recursão 
	protected int posIniRecursao;
	
	//Variável para guardar a posição final quando for iniciar uma recursão
	protected int posFimRecursao;
	
	/**
	 * Inicializa um estado com todas as transições vazias para a tabela de transições
	 * @return
	 */
	protected ArrayList<String> inicializaEstados(){
		ArrayList<String> array = new ArrayList<String>();		
		for (int i = 0 ; i < simbolos.size() ; i++)
			array.add(ExpressaoRegular.VAZIO+"");
		
		return array;
	}
	
	protected String[] converterArrayListParaArray(ArrayList<String> arraylist){
		String[] array = new String[simbolos.size()];
		int i = 0;
		
		for (String a : arraylist)
			array[i++] = a.trim();
		
		return array;
	}
	
	protected ArrayList<String> converterArrayParaArrayList(String[] array){
		ArrayList<String> arraylist = new ArrayList<String>();
		
		for (String a : array)
			arraylist.add(a);
		
		return arraylist;
	}
	
	public void imprimeTabela(){
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
	
	/**
	 * Ordena os conjuntos de estados para que não haja distinção.
	 * Evita, por exemplo, que q0q1 seja diferente de q1q0. 
	 * Então faz a ordenação de q1q0 ficando q0q1
	 * @param estados
	 * @return
	 */
	protected String ordenarConjuntoDeEstados(String estados){		
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
	 * GETTERS AND SETTERS
	 */
	public ArrayList<String> getSimbolos() {
		return simbolos;
	}

	public HashMap<String, String[]> getEstados() {
		return estados;
	}
	
	public String getEstadoInicial() {
		return estadoInicial;
	}

	public ArrayList<Integer> getEstadosDeAceitacao() {
		return estadosDeAceitacao;
	}

}
