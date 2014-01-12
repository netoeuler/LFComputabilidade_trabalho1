package main;

import java.util.HashMap;

public class ExpressaoRegular {
	
	public static char UNIAO = '+';
	public static char CONCATENACAO = '.';
	public static char ESTRELA = '*';
	public static char PALAVRA_VAZIA = 'E';
	public static char VAZIO = 'V';
	
	public static boolean isLetra(char s){
		return (s != UNIAO) && (s != CONCATENACAO) && (s != ESTRELA) &&
				(s != PALAVRA_VAZIA) && (s != VAZIO) && (s != '(') && (s != ')');
	}
	
	/**
	 * Separa as expressões por parênteses para seguirem a ordem de precedência
	 * estrela - concatenação - união
	 * @param expressao
	 * @return
	 */
	public static String formatarPorPrecedencia(String expressao){
		//Separar por parênteses as expressões com estrela para que tenham prioridade na execução
		for (int i = 0 ; i < expressao.length() ; i++){
			if (expressao.charAt(i) == ESTRELA){				
				if (i > 1 && isLetra(expressao.charAt(i-1))){					
					expressao = expressao.substring(0, i-1) +
							"(" + expressao.substring(i-1,i+1) + ")" +
							expressao.substring(i+1,expressao.length());					 
					
					i++;
				}
				else if (i > 1 && expressao.charAt(i-1) == ')'){
					int posFim = i-1;
					int contadorParenteses = 0;
					while(true){
						posFim--;
						
						if (expressao.charAt(posFim) == ')')
							contadorParenteses++;
						else if (contadorParenteses > 0 && expressao.charAt(posFim) == '(')
							contadorParenteses--;
						else if (contadorParenteses == 0 && expressao.charAt(posFim) == '(')
							break;
					}
					
					expressao = expressao.substring(0, posFim) + 
							"(" + expressao.substring(posFim,i+1) + ")" +
							expressao.substring(i+1,expressao.length());					
					
					i++;
				}
			}
		}
		
		//Separar por parênteses as expressões com concatenação para que tenham prioridade na execução
		for (int i = 0 ; i < expressao.length() ; i++){
			if (expressao.charAt(i) == CONCATENACAO){				
				if (i > 1 && isLetra(expressao.charAt(i-1))){					
					expressao = expressao.substring(0, i-1) +
							"(" + expressao.substring(i-1,i+2) + ")" +
							expressao.substring(i+2,expressao.length());					 
					
					i++;
				}
			}
		}
		
		return expressao;
	}
}