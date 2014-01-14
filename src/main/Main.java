package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	
	public static void main(String args[]) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String expressao = in.readLine();
		expressao = ExpressaoRegular.formatarPorPrecedencia(expressao);
		
		AFN afn = new AFN(expressao);
		//afn.imprimeTabela();
		
		Object[] variaveisDoAFNProAFD = {afn.getSimbolos(), afn.getEstados(), afn.getEstadoInicial(), afn.getEstadosDeAceitacao()};
		AFD afd = new AFD(variaveisDoAFNProAFD);
		//afd.imprimeTabela();
		
		String palavra = in.readLine();
		while (!palavra.trim().isEmpty()){
			System.out.println(afd.checaValidadePalavra(palavra));
			palavra = in.readLine();
		}
		
		System.out.println("Fim!");
	}

}
