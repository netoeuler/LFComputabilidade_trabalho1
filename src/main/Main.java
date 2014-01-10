package main;

public class Main {
	
	public static void main(String args[]){
		String expressao = "(a+b).b"; 
		
		AFN afn = new AFN(expressao);
		//afn.imprimeTabela();
		
		Object[] variaveisDoAFNProAFD = {afn.getSimbolos(), afn.getEstados(), afn.getEstadoInicial(), afn.getEstadosDeAceitacao()};
		AFD afd = new AFD(variaveisDoAFNProAFD);
		afd.imprimeTabela();
		
		String palavra = "ab";
		System.out.println("\n\n"+afd.checaValidadePalavra(palavra));		
	}

}
