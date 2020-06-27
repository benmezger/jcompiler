package compiler.parser;

import compiler.parser.Instruction;
import compiler.parser.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LanguageRules {

    private String contexto;
    private String verificarAtribuicao;
    private Integer VT; //Número de constantes e de variável
    private Integer VP; //Número de constantes ou variáveis de um determinado tipo;
    private Integer VIT; //Tamanho das variáveis indexadas de um determinado tipo;
    private Integer tipo; //Iindica um determinado tipo de constante ou variável. (variável 1-NATURAL 2-REAL 3-CHAR 4-BOOLEAN) (constante 5-NATURAL 6-REAL 7-CHAR)
    private Integer ponteiro; //Indicador da posição onde será gerada a próxima instrução na área de instruções
    private Boolean variavel_indexada;//Valor lógico que indica ou não a ocorrência de uma variável indexada;
    private List<String> pilha_de_desvios; //Pilha de endereços para resolução de desvios com operandos inicialmente desconhecidos, quando da análise dos comandos de seleção e de repetição;
    private List<SymbolTable> pilha_de_simbolos;
    private List<Instruction> pilha_de_instrucoes;
    private List<String> error;
    private String identificadorAcao11;
    private String identificadorAcao18;
    private Integer constante_inteiraAcao13;
    private List<SymbolTable> list_atributos;

    public LanguageRules() {
        pilha_de_simbolos = new ArrayList<>();
        pilha_de_instrucoes = new ArrayList<>();
        pilha_de_desvios = new ArrayList<>();
        error = new ArrayList<>();
        list_atributos = new ArrayList<>();
        identificadorAcao11 = "";
        identificadorAcao18 = "";
        constante_inteiraAcao13 = 0;
        ponteiro = 1;
        VT = 0;
        VP = 0;
        VIT = 0;
        tipo = 0;
        variavel_indexada = false;
        contexto = "";
    }

    public List<String> getError() {
        return error;
    }

    public List<SymbolTable> getPilha_de_simbolos() {
        return pilha_de_simbolos;
    }

    public List<Instruction> getPilha_de_instrucoes() {
        return pilha_de_instrucoes;
    }

    private boolean VerificarIdentificadorExistenteTabelaSimbolo(String identificador){
        boolean retorno = false;
        for (SymbolTable tabelaSimbolos : pilha_de_simbolos) {
            if(tabelaSimbolos.getIdentificador().toUpperCase().equals(identificador.toUpperCase())){
                retorno = true;
                break;
            }
        }
        return retorno;
    }


    private SymbolTable RecuperarSinbolo(String identificador){
        for (SymbolTable tabelaSimbolos : pilha_de_simbolos) {
            if(tabelaSimbolos.getIdentificador().toUpperCase().equals(identificador.toUpperCase())){
                return tabelaSimbolos;
            }
        }
        return null;
    }

    public void regra1(String identificador){
        pilha_de_simbolos.add(new SymbolTable(identificador, 0, "-", "-"));
        pilha_de_instrucoes.add(new Instruction(ponteiro, "STP", "0"));
    }

    public void regra2(){
        contexto = "CONSTANTE";
        VIT = 0;
    }

    public void regra3(){
        VP = VP + VIT;
        switch(tipo.toString()){
            case "1":
                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALI", VP.toString()));
                ponteiro = ponteiro + 1;
                break;
            case "2":
                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALR", VP.toString()));
                ponteiro = ponteiro + 1;
                break;
            case "3":
                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALS", VP.toString()));
                ponteiro = ponteiro + 1;
                break;
            case "4":
                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALB", VP.toString()));
                ponteiro = ponteiro + 1;
                break;
            case "5":
                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALI", VP.toString()));
                ponteiro = ponteiro + 1;
                break;
            case "6":
                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALR", VP.toString()));
                ponteiro = ponteiro + 1;
                break;
            case "7":
                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALS", VP.toString()));
                ponteiro = ponteiro + 1;
                break;
            default :
                break;
        }

        if (tipo.toString().equals("1") || tipo.toString().equals("2") || tipo.toString().equals("3") || tipo.toString().equals("4")){
            VP = 0;
            VIT = 0;
        }
    }

    public void regra4(String valor){
        switch(tipo.toString()){
            case "5":
                Pattern roma = Pattern.compile("[0-9]{1,3}");
                if(!roma.matcher(valor).matches()){
                    error.add("O valor (" + valor + ") é um inteiro inválido");
                }
                pilha_de_instrucoes.add(new Instruction(ponteiro, "LDI", valor));
                ponteiro = ponteiro + 1;
                break;
            case "6":
                Pattern romafloat = Pattern.compile("[0-9]{1,5}.[0-9]{1,2}");
                if(!romafloat.matcher(valor).matches()){
                    error.add("O valor (" + valor + ") é um float inválido");
                }
                pilha_de_instrucoes.add(new Instruction(ponteiro, "LDR", valor));
                ponteiro = ponteiro + 1;
                break;
            case "7":
                pilha_de_instrucoes.add(new Instruction(ponteiro, "LDS", valor));
                ponteiro = ponteiro + 1;
            default :
                break;
        }

        pilha_de_instrucoes.add(new Instruction(ponteiro, "STC", VP.toString()));
        ponteiro = ponteiro + 1;
        VP = 0;
    }

    public void regra5(){
        contexto = "VARIAVEL";
    }

    public void regra6(){
        if(contexto.toUpperCase().equals("VARIAVEL")){
            tipo = 1;
        }else{
            tipo = 5;
        }
    }

    public void regra7(){
        if(contexto.toUpperCase().equals("VARIAVEL")){
            tipo = 2;
        }else{
            tipo = 6;
        }
    }

    public void regra8(){
        if(contexto.toUpperCase().equals("VARIAVEL")){
            tipo = 3;
        }else{
            tipo = 7;
        }
    }

    public void regra9(){
        if(contexto.toUpperCase().equals("VARIAVEL")){
            tipo = 4;
        }else{
            error.add("Tipo inválido para constante. Não é possível criar constante booleana");
        }
    }

    public void regra10(String identificador){
        if(VerificarIdentificadorExistenteTabelaSimbolo(identificador)){
            error.add("Identificador (" + identificador + ") já declarado");
        }else{
            VT = VT + 1;
            VP = VP + 1;
            pilha_de_simbolos.add(new SymbolTable(identificador, tipo, VT.toString(), "-"));
        }
    }

    public void regra11(String identificador){
        if(contexto.toUpperCase().equals("VARIAVEL")){
            if(VerificarIdentificadorExistenteTabelaSimbolo(identificador)){
                error.add("Identificador (" + identificador + ") já declarado");
            }else{
                variavel_indexada = false;
                identificadorAcao11 = identificador;
            }
        }else{
            variavel_indexada = false;
            identificadorAcao11 = identificador;
        }
    }

    public void regra12(){
        Integer auxVT;
        Integer auxAtributo;
        switch(contexto){
            case "VARIAVEL":
                if(!variavel_indexada){
                    VT = VT + 1;
                    VP = VP + 1;
                    pilha_de_simbolos.add(new SymbolTable(identificadorAcao11, tipo, VT.toString(), "-"));
                }else{
                    VIT = VIT + constante_inteiraAcao13;
                    auxVT = VT +1;
                    pilha_de_simbolos.add(new SymbolTable(identificadorAcao11, tipo, auxVT.toString(), constante_inteiraAcao13.toString()));
                    VT = VT + constante_inteiraAcao13;
                }
                break;
            case "ATRIBUICAO":
                SymbolTable tabelaSimbolo;
                tabelaSimbolo = RecuperarSinbolo(identificadorAcao11);
                if(VerificarIdentificadorExistenteTabelaSimbolo(identificadorAcao11) && (tabelaSimbolo.getCategoria() == 1
                        || tabelaSimbolo.getCategoria() == 2 || tabelaSimbolo.getCategoria() == 3 || tabelaSimbolo.getCategoria() == 4)){
                    if(tabelaSimbolo.getAtributo2().equals("-")){
                        if(!variavel_indexada){
                            list_atributos.add(tabelaSimbolo);
                        }else{
                            error.add("Identificador (" + identificadorAcao11 + ") de variável não indexada");
                        }
                    }else{
                        if(variavel_indexada){
                            SymbolTable newSimbolo = tabelaSimbolo;
                            auxAtributo = Integer.parseInt(tabelaSimbolo.getAtributo1())+constante_inteiraAcao13-1;
                            newSimbolo.setAtributo1(auxAtributo.toString());
                            list_atributos.add(newSimbolo);
                        }else{
                            error.add("Identificador (" + identificadorAcao11 + ") de variável indexada exige índice");
                        }
                    }
                }else{
                    error.add("Identificador (" + identificadorAcao11 + ") não declarado ou de constante");
                }
                break;
            case "ENTRADA DADOS":
                tabelaSimbolo = RecuperarSinbolo(identificadorAcao11);
                if(VerificarIdentificadorExistenteTabelaSimbolo(identificadorAcao11) && (tabelaSimbolo.getCategoria() == 1
                        || tabelaSimbolo.getCategoria() == 2 || tabelaSimbolo.getCategoria() == 3 || tabelaSimbolo.getCategoria() == 4)){
                    if(tabelaSimbolo.getAtributo2().equals("-")){
                        if(!variavel_indexada){
                            pilha_de_instrucoes.add(new Instruction(ponteiro, "REA", tabelaSimbolo.getCategoria().toString()));
                            ponteiro = ponteiro + 1;
                            pilha_de_instrucoes.add(new Instruction(ponteiro, "STR", tabelaSimbolo.getAtributo1()));
                            ponteiro = ponteiro + 1;
                        }else{
                            error.add("Identificador (" + identificadorAcao11 + ") de variável não indexada");
                        }
                    }else{
                        if(variavel_indexada){
                            pilha_de_instrucoes.add(new Instruction(ponteiro, "REA", tabelaSimbolo.getCategoria().toString()));
                            ponteiro = ponteiro + 1;
                            auxAtributo = Integer.parseInt(tabelaSimbolo.getAtributo1()) + constante_inteiraAcao13-1;
                            pilha_de_instrucoes.add(new Instruction(ponteiro, "STR", auxAtributo.toString()));
                            ponteiro = ponteiro + 1;
                        }else{
                            error.add("Identificador (" + identificadorAcao11 + ") de variável indexada exige índice");
                        }
                    }
                }else{
                    error.add("Identificador (" + identificadorAcao11 + ") não declarado ou de constante");
                }
                break;
            default :
                break;
        }
    }

    public void regra13(Integer constante_inteira){
        constante_inteiraAcao13 = constante_inteira;
        variavel_indexada = true;
    }

    public void regra14(){
        contexto = "ATRIBUICAO";
    }

    private boolean regexInt(String teste){
        Pattern roma = Pattern.compile("[0-9]{1,3}");
        if(!roma.matcher(teste).matches()){
            return true;
        }
        return false;
    }

    private boolean regexFloat(String teste){
        Pattern roma = Pattern.compile("[0-9]{1,5}.[0-9]{1,2}");
        if(!roma.matcher(teste).matches()){
            return true;
        }
        return false;
    }

    public void regra15(){
        for (SymbolTable simbolo : list_atributos) {
//            if((verificarAtribuicao.contains(".") && simbolo.getCategoria() == 1) || (simbolo.getCategoria() == 1 && regexInt(verificarAtribuicao))){
//                error.add("O valor (" + verificarAtribuicao + ") é diferente do tipo da variável inteira");
//            } else if((!verificarAtribuicao.contains(".") && simbolo.getCategoria() == 2) || (simbolo.getCategoria() == 2 && regexFloat(verificarAtribuicao))){
//                error.add("O valor (" + verificarAtribuicao + ") é diferente do tipo da variável float");
//            } else if(simbolo.getCategoria() == 4 && !((verificarAtribuicao.equals("TRUE") || verificarAtribuicao.equals("FALSE")))){
//                error.add("O valor (" + verificarAtribuicao + ") é diferente do tipo da variável boolean");
//            }
            if(simbolo.getCategoria() == 4){
                pilha_de_instrucoes.add(new Instruction(ponteiro, "STR", verificarAtribuicao));
            } else {
                pilha_de_instrucoes.add(new Instruction(ponteiro, "STR", simbolo.getAtributo1()));
            }

            ponteiro = ponteiro + 1;
        }
        list_atributos = new ArrayList<>();
    }

    public void regra16(){
        contexto = "ENTRADA DADOS";
    }

    public void regra17(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "WRT", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra18(String identificador){
        SymbolTable tabelaSimbolo;
        tabelaSimbolo = RecuperarSinbolo(identificador);
        if(VerificarIdentificadorExistenteTabelaSimbolo(identificador) && (tabelaSimbolo.getCategoria() != 0)){
            variavel_indexada = false;
            identificadorAcao18 = identificador;
        }else{
            error.add("Identificador (" + identificador + ") não declarado");
        }
    }

    public void regra19(){
        SymbolTable tabelaSimbolo;
        tabelaSimbolo = RecuperarSinbolo(identificadorAcao18);
        Integer auxAtributo;
        if(!variavel_indexada){
            if(tabelaSimbolo.getAtributo2().equals("-")){
                pilha_de_instrucoes.add(new Instruction(ponteiro, "LDV", tabelaSimbolo.getAtributo1()));
                ponteiro = ponteiro + 1;
            }else{
                error.add("Identificador (" + identificadorAcao18 + ") de variável indexada exige índice");
            }
        }else{
            if(!tabelaSimbolo.getAtributo2().equals("-")){
                auxAtributo = Integer.parseInt(tabelaSimbolo.getAtributo1()) + constante_inteiraAcao13-1;
                pilha_de_instrucoes.add(new Instruction(ponteiro, "LDV", auxAtributo.toString()));
                ponteiro = ponteiro + 1;
            }else{
                error.add("Identificador (" + identificadorAcao18 + ") de constante ou de variável não indexada");
            }
        }
    }

    public void regra20(Integer constante_inteira){
        verificarAtribuicao = constante_inteira.toString();
        pilha_de_instrucoes.add(new Instruction(ponteiro, "LDI", constante_inteira.toString()));
        ponteiro = ponteiro + 1;
    }

    public void regra21(Float constante_real){
        verificarAtribuicao = constante_real.toString();
        pilha_de_instrucoes.add(new Instruction(ponteiro, "LDR", constante_real.toString()));
        ponteiro = ponteiro + 1;
    }

    public void regra22(String constante_literal){
        verificarAtribuicao = constante_literal;
        pilha_de_instrucoes.add(new Instruction(ponteiro, "LDS", constante_literal));
        ponteiro = ponteiro + 1;
    }

    public void regra23(){
        String topoPilhaDesvio = pilha_de_desvios.get(pilha_de_desvios.size()-1);
        pilha_de_desvios.remove(pilha_de_desvios.size()-1);
        for(int i = 0; i < pilha_de_instrucoes.size(); i ++){
            if(pilha_de_instrucoes.get(i).getPonteiro() == Integer.parseInt(topoPilhaDesvio)){
                pilha_de_instrucoes.get(i).setEndereco(ponteiro.toString());
                break;
            }
        }
    }

    public void regra24(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMF", "?"));
        pilha_de_desvios.add(ponteiro.toString());
        ponteiro = ponteiro + 1;
    }

    public void regra25(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMT", "?"));
        pilha_de_desvios.add(ponteiro.toString());
        ponteiro = ponteiro + 1;
    }

    public void regra26(){
        //to_do
        Integer auxPonteiro;
        String topoPilhaDesvio = pilha_de_desvios.get(pilha_de_desvios.size()-1);
        pilha_de_desvios.remove(pilha_de_desvios.size()-1);
        auxPonteiro = ponteiro + 1;
        for(int i = 0; i < pilha_de_instrucoes.size(); i ++){
            if(pilha_de_instrucoes.get(i).getPonteiro() == Integer.parseInt(topoPilhaDesvio)){
                pilha_de_instrucoes.get(i).setEndereco(auxPonteiro.toString());
                break;
            }
        }
        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMP", "?"));
        pilha_de_desvios.add(ponteiro.toString());
        ponteiro = ponteiro + 1;
    }

    public void regra27(){
        pilha_de_desvios.add(ponteiro.toString());
    }

    public void regra28(){
        String topoPilhaDesvio = pilha_de_desvios.get(pilha_de_desvios.size()-1);
        pilha_de_desvios.remove(pilha_de_desvios.size()-1);
        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMT", topoPilhaDesvio));
        ponteiro = ponteiro + 1;
    }

    public void regra29(){
        pilha_de_desvios.add(ponteiro.toString());
    }

    public void regra30(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMF", "?"));
        pilha_de_desvios.add(ponteiro.toString());
        ponteiro = ponteiro + 1;
    }

    public void regra31(){
        Integer auxPonteiro;
        String topoPilhaDesvio = pilha_de_desvios.get(pilha_de_desvios.size()-1);
        pilha_de_desvios.remove(pilha_de_desvios.size()-1);
        auxPonteiro = ponteiro + 1;
        for(int i = 0; i < pilha_de_instrucoes.size(); i ++){
            if(pilha_de_instrucoes.get(i).getPonteiro() == Integer.parseInt(topoPilhaDesvio)){
                pilha_de_instrucoes.get(i).setEndereco(auxPonteiro.toString());
                break;
            }
        }
        topoPilhaDesvio = pilha_de_desvios.get(pilha_de_desvios.size()-1);
        pilha_de_desvios.remove(pilha_de_desvios.size()-1);
        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMP", topoPilhaDesvio.toString()));
        ponteiro = ponteiro + 1;
    }

    public void regra32(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "EQL", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra33(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "DIF", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra34(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "SMR", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra35(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "BGR", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra36(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "SME", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra37(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "BGE", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra38(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "ADD", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra39(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "SUB", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra40(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "OR", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra41(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "MUL", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra42(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "DIV", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra43(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "DVI", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra44(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "MOD", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra45(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "AND", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra46(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "POT", "0"));
        ponteiro = ponteiro + 1;
    }

    public void regra47(){
        verificarAtribuicao = "TRUE";
        pilha_de_instrucoes.add(new Instruction(ponteiro, "LDB", "TRUE"));
        ponteiro = ponteiro + 1;
    }

    public void regra48(){
        verificarAtribuicao = "FALSE";
        pilha_de_instrucoes.add(new Instruction(ponteiro, "LDB", "FALSE"));
        ponteiro = ponteiro + 1;
    }

    public void regra49(){
        pilha_de_instrucoes.add(new Instruction(ponteiro, "NOT", "0"));
        ponteiro = ponteiro + 1;
    }

}

