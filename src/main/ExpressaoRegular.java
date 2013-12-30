package main;

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

}
