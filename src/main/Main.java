package main;

public class Main {
	
	//* . +
	public static void main(String args[]){
		//String expressao = ExpressaoRegular.formatarPorPrecedencia("a.(a.b.(b.c)*)*");
		String expressao = ExpressaoRegular.formatarPorPrecedencia("(a.b)+(a.b)");
		//System.out.println(expressao);		
		
		///*
		AFN afn = new AFN(expressao);
		//afn.imprimeTabela();
		
		Object[] variaveisDoAFNProAFD = {afn.getSimbolos(), afn.getEstados(), afn.getEstadoInicial(), afn.getEstadosDeAceitacao()};
		AFD afd = new AFD(variaveisDoAFNProAFD);
		afd.imprimeTabela();
		
		String palavra = "abab";
		//System.out.println("\n\n"+afd.checaValidadePalavra(palavra));
		//*/
	}

}
