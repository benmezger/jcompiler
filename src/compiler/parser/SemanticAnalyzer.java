package compiler.parser;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class union {
    public Integer categoria;
    public Integer atr1;
    public Integer atr2;

    public union(Integer categoria, Integer atr1, Integer atr2) {
        this.categoria = categoria;
        this.atr1 = atr1;
        this.atr2 = atr2;
    }

    @Override
    public String toString() {
        return " {" +
                "categoria=" + categoria +
                ", atr1=" + atr1 +
                ", atr2='" + atr2 + '\'' +
                '}';
    }
}

public class SemanticAnalyzer {

    private final String ERRO1 = "Erro Semantico 1: identificador ja declarado -> ";
    private final String ERRO2 = "Erro Semantico 2: identificador de variavel indexada -> ";
    private final String ERRO3 = "Erro Semantico 3: identificador de variavel - faltou indice -> ";
    private final String ERRO4 = "Erro Semantico 4: identificador não declarado ou identificador usado como identificador do programa ou identificador é uma constante/tipo enumerado -> ";
    private final String ERRO5 = "Erro Semantico 5: tipo invalido para constante de tipo logic";
    private final String ERRO6 = "Erro Semantico 6: tipo invalido para constante de tipo enumerado";
    private final String ERRO7 = "Erro Semantico 7: identificador não declarado ou identificador usado como identificador do programa ou identificador é um tipo enumerado -> -> ";
    private final String ERRO8 = "Erro Semantico 8: identificador de constante ou variavel nao indexada -> ";
    public List<String> erros = new ArrayList<>();
    public Boolean execute = false;
    private CONTEXTO contexto = CONTEXTO.NENHUM;
    private Integer vt = 0;
    private Integer vp = 0;
    private Integer vi = 0;
    private Integer tvi = 0;
    private Integer ponteiro = 1;
    private Boolean variavelIndexada = null;
    private Stack<Integer> pilhaDeDesvios = new Stack<>();
    private List<Instruction> areaDeInstrucoes = new ArrayList<>();
    private Map<String, union> tabelaDeSimbolos = new LinkedHashMap<>();
    private Map<String, union> tupla = new HashMap();
    private Integer tipo = null;
    private SAIDA saida = SAIDA.NENHUM;
    private Map<String, List<String>> tabelaDeTiposEnumerados = new LinkedHashMap<>();
    private List<Integer> listaDeAtributos = new ArrayList<>();
    private Integer constIntArmazenadaAcao12 = null;
    private String idArmazenadoAcao10 = "";
    private String idArmazenadoAcao24 = "";

    public List<Instruction> getInstructions() {
        return this.areaDeInstrucoes;
    }

    /*
     * #1:
     * reconhecimento do identificador de programa
     */
    public void action1(String token) {
        if (!execute) return;
        System.out.println("TOKNE! ->");
        System.out.println(token);
        System.out.println(tabelaDeSimbolos);
        tabelaDeSimbolos.put(token, new union(0, null, null));
    }

    /*
     * #2:
     * reconhecimento de fim de programa
     */
    public void action2() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "STP", new Item(0)));
    }

    /*
     * #3:
     * reconhecimento de identificador de tipo enumerado
     */
    public void action3(String token) {
        if (!execute) return;
        if (tabelaDeSimbolos.containsKey(token) || tabelaDeTiposEnumerados.containsKey(token)) {
            erros.add(ERRO1 + token);
            return;
        }
        tabelaDeTiposEnumerados.put(token, new ArrayList<>());
    }

    /*
     * #4:
     * reconhecimento de identificador de constante de tipo enumerado
     */
    public void action4(String token) {
        if (!execute) return;
        if (identificadorJaDeclarado(token)) {
            erros.add(ERRO1 + token);
            return;
        }

        List<String> list = null;
        Iterator<Map.Entry<String, List<String>>> it = tabelaDeTiposEnumerados.entrySet().iterator();

        while (it.hasNext())
            list = it.next().getValue();

        if (list != null)
            list.add(token);
    }

    /*
     * #5:
     * reconhecimento das palavras reservadas as constant
     */
    public void action5() {
        if (!execute) return;
        contexto = CONTEXTO.AS_CONSTANT;
        vi = 0;
        tvi = 0;
    }

    /*
     * #6:
     * reconhecimento do teÌrmino da declaracÌ§aÌƒo de constantes ou variaÌveis de um determinado tipo
     */
    public void action6() {
        if (!execute) return;
        List<String> keyList = new ArrayList<>(tabelaDeSimbolos.keySet());
        int last_index = keyList.size() - 1;
        for (int i = last_index; i > last_index - (vp + vi); --i)
            tabelaDeSimbolos.get(keyList.get(i)).categoria = tipo;

        vp += tvi;

        switch (tipo) {
            case 1:
            case 5:
                areaDeInstrucoes.add(new Instruction(ponteiro, "ALI", new Item(vp)));
                ponteiro++;
                break;
            case 2:
            case 6:
                areaDeInstrucoes.add(new Instruction(ponteiro, "ALR", new Item(vp)));
                ponteiro++;
                break;
            case 3:
            case 7:
                areaDeInstrucoes.add(new Instruction(ponteiro, "ALS", new Item(vp)));
                ponteiro++;
                break;
            case 4:
                areaDeInstrucoes.add(new Instruction(ponteiro, "ALB", new Item(vp)));
                ponteiro++;
                break;
        }

        if (tipo == 1 || tipo == 2 || tipo == 3 || tipo == 4) {
            vp = 0;
            vi = 0;
            tvi = 0;
        }
    }

    /*
     * #7:
     * reconhecimento de valor na declaracÌ§aÌƒo de constante
     */
    public void action7(Token token) {
        if (!execute) return;
        switch (tipo) {
            case 5:
                areaDeInstrucoes.add(new Instruction(ponteiro, "LDI", new Item(Integer.parseInt(token.image))));
                ponteiro++;
                break;
            case 6:
                areaDeInstrucoes.add(new Instruction(ponteiro, "LDR", new Item(Float.parseFloat(token.image))));
                ponteiro++;
                break;
            case 7:
                areaDeInstrucoes.add(new Instruction(ponteiro, "LDS", new Item(token.image)));
                ponteiro++;
                break;
        }
        areaDeInstrucoes.add(new Instruction(ponteiro, "STC", new Item(vp)));
        ponteiro++;
        vp = 0;
    }

    /*
     * #8:
     * reconhecimento das palavras reservadas as variable
     */
    public void action8() {
        if (!execute) return;
        contexto = CONTEXTO.AS_VARIABLE;
    }

    /*
     * #9:
     * reconhecimento de identificador de constante
     */
    public void action9(String token) {
        if (!execute) return;
        if (identificadorJaDeclarado(token)) {
            erros.add(ERRO1 + token);
            return;
        }
        vt++;
        vp++;
        tabelaDeSimbolos.put(token, new union(null, vt, null));
    }

    /*
     * #10:
     * reconhecimento de identificador de variaÌvel
     */
    public void action10(String token) {
        if (!execute) return;
        if (contexto == CONTEXTO.AS_VARIABLE) {
            if (identificadorJaDeclarado(token)) {
                erros.add(ERRO1 + token);
                return;
            }
            variavelIndexada = false;
            idArmazenadoAcao10 = token;
        } else if (contexto == CONTEXTO.ATRIBUICAO || contexto == CONTEXTO.ENTRADA_DADOS) {
            variavelIndexada = false;
            idArmazenadoAcao10 = token;
        }
    }

    /*
     * #11:
     * reconhecimento de identificador de variaÌvel e tamanho da variaÌvel indexada
     */
    public void action11() {
        if (!execute) return;

        if (contexto == CONTEXTO.AS_VARIABLE) {

            if (!variavelIndexada) {
                vt++;
                vp++;
                tabelaDeSimbolos.put(idArmazenadoAcao10, new union(null, vt, null));
            } else {
                vi++;
                tvi += constIntArmazenadaAcao12;
                tabelaDeSimbolos.put(idArmazenadoAcao10,
                        new union(null, vt + 1, constIntArmazenadaAcao12));
                vt += constIntArmazenadaAcao12;
            }

        } else if (contexto == CONTEXTO.ATRIBUICAO) {

            if (tabelaDeSimbolos.containsKey(idArmazenadoAcao10) && isIdentificadorDeVariavel(idArmazenadoAcao10)) {
                Integer atr1 = tabelaDeSimbolos.get(idArmazenadoAcao10).atr1;
                Integer atr2 = tabelaDeSimbolos.get(idArmazenadoAcao10).atr2;
                if (atr2 == null) {
                    if (!variavelIndexada) {
                        listaDeAtributos.add(atr1);
                    } else {
                        erros.add(ERRO2 + idArmazenadoAcao10);
                        return;
                    }
                } else {
                    if (variavelIndexada) {
                        listaDeAtributos.add(atr1 + constIntArmazenadaAcao12 - 1);
                    } else {
                        erros.add(ERRO3 + idArmazenadoAcao10);
                        return;
                    }
                }
            } else {
                erros.add(ERRO4 + idArmazenadoAcao10);
                return;
            }

        } else if (contexto == CONTEXTO.ENTRADA_DADOS) {

            if (tabelaDeSimbolos.containsKey(idArmazenadoAcao10) && isIdentificadorDeVariavel(idArmazenadoAcao10)) {
                Integer atr1 = tabelaDeSimbolos.get(idArmazenadoAcao10).atr1;
                Integer atr2 = tabelaDeSimbolos.get(idArmazenadoAcao10).atr2;
                Integer cat = tabelaDeSimbolos.get(idArmazenadoAcao10).categoria;
                if (atr2 == null) {
                    if (!variavelIndexada) {
                        areaDeInstrucoes.add(new Instruction(ponteiro, "REA", new Item(cat)));
                        ponteiro++;
                        areaDeInstrucoes.add(new Instruction(ponteiro, "STR", new Item(atr1)));
                        ponteiro++;
                    } else {
                        erros.add(ERRO2 + idArmazenadoAcao10);
                        return;
                    }
                } else {
                    if (variavelIndexada) {
                        areaDeInstrucoes.add(new Instruction(ponteiro, "REA", new Item(cat)));
                        ponteiro++;
                        areaDeInstrucoes.add(new Instruction(ponteiro, "STR",
                                new Item(atr1 + constIntArmazenadaAcao12 - 1)));
                        ponteiro++;
                    } else {
                        erros.add(ERRO3 + idArmazenadoAcao10);
                        return;
                    }
                }
            } else {
                erros.add(ERRO4 + idArmazenadoAcao10);
                return;
            }
        }
    }

    /*
     * #12:
     * reconhecimento de constante inteira como tamanho da variaÌvel indexada
     */
    public void action12(String token) {
        if (!execute) return;
        constIntArmazenadaAcao12 = Integer.valueOf(token);
        variavelIndexada = true;
    }

    /*
     * #13:
     * reconhecimento da palavra reservada integer
     */
    public void action13() {
        if (!execute) return;
        tipo = (contexto == CONTEXTO.AS_VARIABLE) ? 1 : 5;
    }

    /*
     * #14:
     * reconhecimento da palavra reservada float
     */
    public void action14() {
        if (!execute) return;
        tipo = (contexto == CONTEXTO.AS_VARIABLE) ? 2 : 6;
    }

    /*
     * #15:
     * reconhecimento da palavra reservada string
     */
    public void action15() {
        if (!execute) return;
        tipo = (contexto == CONTEXTO.AS_VARIABLE) ? 3 : 7;
    }

    /*
     * #16:
     * reconhecimento da palavra reservada boolean
     */
    public void action16() {
        if (!execute) return;
        if (contexto == CONTEXTO.AS_VARIABLE)
            tipo = 4;
        else
            erros.add(ERRO5);
    }

    /*
     * #17:
     * reconhecimento de identificador de tipo enumerado
     */
    public void action17() {
        if (!execute) return;
        if (contexto == CONTEXTO.AS_VARIABLE)
            tipo = 1;
        else
            erros.add(ERRO6);
    }

    /*
     * #18:
     * reconhecimento do iniÌcio do comando de atribuicÌ§aÌƒo
     */
    public void action18() {
        if (!execute) return;
        contexto = contexto.ATRIBUICAO;
    }

    /*
     * #19:
     * reconhecimento do fim do comando de atribuicÌ§aÌƒo
     */
    public void action19() {
        if (!execute) return;
        listaDeAtributos.stream().forEach(i -> {
            areaDeInstrucoes.add(new Instruction(ponteiro, "STR", new Item(i)));
            ponteiro++;
        });
        listaDeAtributos.clear();
    }

    // #20: reconhecimento do comando de entrada de dados
    public void action20() {
        if (!execute) return;
        contexto = CONTEXTO.ENTRADA_DADOS;
    }

    // #21: reconhecimento das palavras reservadas â€œwrite all thisâ€
    public void action21() {
        if (!execute) return;
        saida = SAIDA.WRITE_ALL_THIS;
    }

    //#22: reconhecimento das palavras reservadas â€œwrite thisâ€
    public void action22() {
        if (!execute) return;
        saida = SAIDA.WRITE_THIS;
    }

    // #23: reconhecimento de mensagem em comando de saiÌda de dados
    public void action23() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "WRT", new Item(0)));
        ponteiro++;
    }

    // #24: reconhecimento de identificador em comando de saiÌda ou em expressaÌƒo
    public void action24(String token) {
        if (!execute) return;
        if (tabelaDeSimbolos.containsKey(token) && isIdentificadorDeConstanteOuVariavel(token)) {
            variavelIndexada = false;
            idArmazenadoAcao24 = token;
        } else {
            erros.add(ERRO7 + token);
        }
    }

    // #25: reconhecimento de identificador de constante ou de
    // variaÌvel e tamanho de variaÌvel indexada em comando de saiÌda
    public void action25() {
        if (!execute) return;
        System.out.println("HEREEEEE");
        System.out.println(idArmazenadoAcao24);
        System.out.println(tabelaDeSimbolos);
        try {
            Integer atr1 = tabelaDeSimbolos.get(idArmazenadoAcao24).atr1;
            Integer atr2 = tabelaDeSimbolos.get(idArmazenadoAcao24).atr2;
            if (!variavelIndexada) {
                if (atr2 == null) {
                    if (saida == SAIDA.WRITE_ALL_THIS) {
                        areaDeInstrucoes.add(
                                new Instruction(ponteiro, "LDS", new Item(idArmazenadoAcao24 + " = ")));
                        ponteiro++;
                        areaDeInstrucoes.add(new Instruction(ponteiro, "WRT", new Item(0)));
                        ponteiro++;
                    }
                    areaDeInstrucoes.add(new Instruction(ponteiro, "LDV", new Item(atr1)));
                    ponteiro++;
                } else {
                    erros.add(ERRO3 + idArmazenadoAcao24);
                }

            } else {
                if (atr2 != null) {
                    if (saida == SAIDA.WRITE_ALL_THIS) {
                        areaDeInstrucoes.add(new Instruction(ponteiro, "LDS",
                                new Item(idArmazenadoAcao24 + "[" + constIntArmazenadaAcao12 + "] = ")));
                        ponteiro++;
                        areaDeInstrucoes.add(new Instruction(ponteiro, "WRT", new Item(0)));
                        ponteiro++;
                    }
                    areaDeInstrucoes.add(
                            new Instruction(ponteiro, "LDV", new Item(atr1 + constIntArmazenadaAcao12 - 1)));
                    ponteiro++;
                } else {
                    erros.add(ERRO8 + idArmazenadoAcao24);
                }
            }
        } catch (NullPointerException e){
            return;
        }

    }

    //#26: reconhecimento de constante inteira em comando de saiÌda ou em expressaÌƒo
    public void action26(String token) {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "LDI", new Item(Integer.parseInt(token))));
        ponteiro++;
    }

    //#27: reconhecimento de constante real em comando de saiÌda ou em expressaÌƒo
    public void action27(String token) {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "LDR", new Item(Float.parseFloat(token))));
        ponteiro++;
    }

    //#28: reconhecimento de constante literal em comando de saiÌda ou em expressaÌƒo
    public void action28(String token) {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "LDS", new Item(token)));
        ponteiro++;
    }

    //#29: reconhecimento de fim de comando de selecÌ§aÌƒo
    public void action29() {
        if (!execute) return;
        Integer endereçoInstruçãoDesvioAcao30_31_32 = pilhaDeDesvios.pop();
        areaDeInstrucoes
                .stream()
                .filter(i -> i.getPointer() == endereçoInstruçãoDesvioAcao30_31_32)
                .findFirst()
                .ifPresent(i -> i.setParameter(new Item(ponteiro)));
    }

    // #30: reconhecimento da palavra reservada true
    public void action30() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "JMF", new Item("?")));
        ponteiro++;
        pilhaDeDesvios.push(ponteiro - 1);
    }

    // #31: reconhecimento da palavra reservada untrue
    public void action31() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "JMT", new Item("?")));
        ponteiro++;
        pilhaDeDesvios.push(ponteiro - 1);
    }

    //#32: reconhecimento da palavra reservada untrue (ou true)
    public void action32() {
        if (!execute) return;
        //desempilhar da pilha de desvios o endereço da instrução de desvio empilhado na ação #30 ou #31
        Integer endereçoInstruçãoDesvioAcao30_31 = pilhaDeDesvios.pop();

        //atualizar a instrução de desvio com: endereço = ponteiro + 1
        areaDeInstrucoes
                .stream()
                .filter(i -> i.getPointer() == endereçoInstruçãoDesvioAcao30_31)
                .findFirst()
                .ifPresent(i -> i.setParameter(new Item(ponteiro + 1)));

        //gerar instrução:(ponteiro, JMP, ?),onde endereço = ?
        areaDeInstrucoes.add(new Instruction(ponteiro, "JMP", new Item("?")));
        ponteiro++;

        //empilhar(ponteiro - 1) em pilha de desvios, ou seja, o endereço da instrução JMP
        pilhaDeDesvios.push(ponteiro - 1);

    }

    // #33: reconhecimento do iniÌcio de expressaÌƒo em comando de repeticÌ§aÌƒo
    public void action33() {
        if (!execute) return;
        pilhaDeDesvios.push(ponteiro);
    }

    //#34: reconhecimento de expressaÌƒo em comando de repeticÌ§aÌƒo
    public void action34() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "JMF", new Item("?")));
        ponteiro++;
        pilhaDeDesvios.push(ponteiro - 1);
    }

    // #35: reconhecimento do fim do comando de repeticÌ§aÌƒo
    public void action35() {
        if (!execute) return;
        //desempilhar da pilha de desvios o endereço da instrução de desvio empilhado na ação #34
        Integer endereçoInstruçãoDesvioAcao34 = pilhaDeDesvios.pop();

        //atualizar a instrução de desvio com: endereço ¬ ponteiro + 1
        areaDeInstrucoes
                .stream()
                .filter(i -> i.getPointer() == endereçoInstruçãoDesvioAcao34)
                .findFirst()
                .ifPresent(i -> i.setParameter(new Item(ponteiro + 1)));

        //desempilhar da pilha de desvios o endereço da instrução empilhado na ação #33
        Integer endereçoInstruçãoDesvioAcao33 = pilhaDeDesvios.pop();

        //gerar instrução: (ponteiro, JMP, “endereço”), onde "endereço" é igual ao valor desempilhado
        areaDeInstrucoes.add(new Instruction(ponteiro, "JMP", new Item(endereçoInstruçãoDesvioAcao33)));
        ponteiro++;
    }

    //#36: reconhecimento de operacÌ§aÌƒo relacional igual
    public void action36() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "EQL", new Item(0)));
        ponteiro++;
    }

    //#37: reconhecimento de operacÌ§aÌƒo relacional igual
    public void action37() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "DIF", new Item(0)));
        ponteiro++;
    }

    // #38: reconhecimento de operacÌ§aÌƒo relacional menor que
    public void action38() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "SMR", new Item(0)));
        ponteiro++;
    }

    // #39: reconhecimento de operacÌ§aÌƒo relacional maior que
    public void action39() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "BGR", new Item(0)));
        ponteiro++;
    }

    //#40: reconhecimento de operacÌ§aÌƒo relacional menor ou igual
    public void action40() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "SME", new Item(0)));
        ponteiro++;
    }

    //acÌ§aÌƒo #41: reconhecimento de operacÌ§aÌƒo relacional maior ou igual
    public void action41() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "BGE", new Item(0)));
        ponteiro++;
    }

    // #42: reconhecimento de operacÌ§aÌƒo aritmeÌtica adicÌ§aÌƒo
    public void action42() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "ADD", new Item(0)));
        ponteiro++;
    }

    //#43: reconhecimento de operacÌ§aÌƒo aritmeÌtica subtraÃ§Ã£o
    public void action43() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "SUB", new Item(0)));
        ponteiro++;
    }

    //reconhecimento de operacÌ§aÌƒo loÌgica OU ( | )
    public void action44() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "OR", new Item(0)));
        ponteiro++;
    }

    // #45: reconhecimento de operacÌ§aÌƒo aritmeÌtica multiplicaÃ§Ã£o
    public void action45() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "MUL", new Item(0)));
        ponteiro++;
    }

    // #46: reconhecimento de operacÌ§aÌƒo aritmeÌtica divisÃ£o
    public void action46() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "DIV", new Item(0)));
        ponteiro++;
    }

    //#47: reconhecimento de operacÌ§aÌƒo aritmeÌtica divisÃ£o inteira
    public void action47() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "DIV", new Item(0)));
        ponteiro++;
    }

    // #48: reconhecimento de operacÌ§aÌƒo aritmeÌtica resto da divisÃ£o inteira
    public void action48() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "MOD", new Item(0)));
        ponteiro++;
    }

    // #49: reconhecimento de operacÌ§aÌƒo loÌgica E (&)
    public void action49() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "AND", new Item(0)));
        ponteiro++;
    }

    //#50: reconhecimento de operacÌ§aÌƒo aritmeÌtica potÃªncia
    public void action50() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "POW", new Item(0)));
        ponteiro++;
    }

    //#51: reconhecimento de identificador de constante ou de variaÌvel e tamanho de variaÌvel indexada em expressaÌƒo
    public void action51() {
        if (!execute) return;

        Integer atr1 = tabelaDeSimbolos.get(idArmazenadoAcao24).atr1;
        Integer atr2 = tabelaDeSimbolos.get(idArmazenadoAcao24).atr2;

        if (!variavelIndexada) {
            if (atr2 == null) {
                areaDeInstrucoes.add(new Instruction(ponteiro, "LDV", new Item(atr1)));
                ponteiro++;
            } else {
                erros.add(ERRO3 + idArmazenadoAcao24);
            }

        } else {
            if (atr2 != null) {
                areaDeInstrucoes.add(
                        new Instruction(ponteiro, "LDV", new Item(atr1 + constIntArmazenadaAcao12 - 1)));
                ponteiro++;
            } else {
                erros.add(ERRO8 + idArmazenadoAcao24);
            }
        }
    }

    //#52: reconhecimento de constante loÌgica true
    public void action52() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "LDB", new Item(true)));
        ponteiro++;
    }

    //#53: reconhecimento de constante loÌgica untrue
    public void action53() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "LDB", new Item(false)));
        ponteiro++;
    }

    //#54: reconhecimento de operacÌ§aÌƒo loÌgica NAÌƒO ( ! )
    public void action54() {
        if (!execute) return;
        areaDeInstrucoes.add(new Instruction(ponteiro, "NOT", new Item(0)));
        ponteiro++;
    }

    private Boolean identificadorJaDeclarado(String token) {
        if (tabelaDeSimbolos.containsKey(token) ||
                tabelaDeTiposEnumerados.containsKey(token) ||
                tabelaDeTiposEnumerados
                        .entrySet()
                        .stream()
                        .filter(t -> t.getValue().contains(token))
                        .findFirst()
                        .isPresent()) {
            return true;
        }
        return false;
    }

    private Boolean isIdentificadorDeVariavel(String token) {
        return Stream.of(1, 2, 3, 4)
                .filter(n -> n.compareTo(tabelaDeSimbolos.get(token).categoria) == 0)
                .findFirst()
                .isPresent();
    }

    private Boolean isIdentificadorDeConstante(String token) {
        return Stream.of(5, 6, 7)
                .filter(n -> n.compareTo(tabelaDeSimbolos.get(token).categoria) == 0)
                .findFirst()
                .isPresent();
    }

    private Boolean isIdentificadorDeConstanteOuVariavel(String token) {
        return isIdentificadorDeVariavel(token) || isIdentificadorDeConstante(token);
    }

    @Override
    public String toString() {
        return "Tabela {\n" +
                "    contexto = " + contexto + ",\n" +
                "    ponteiro = " + ponteiro + ",\n" +
                "    vt = " + vt + ",\n" +
                "    vp = " + vp + ",\n" +
                "    vi = " + vi + ",\n" +
                "    tvi = " + tvi + ",\n" +
                "    variavelIndexada = " + variavelIndexada + ",\n" +
                "    pilhaDeDesvios = " + pilhaDeDesvios + ",\n" +
                "    areaDeInstrucoes = " + getAreaInstrucoesAsString() + ",\n" +
                "    tabelaDeSimbolos = " + getContentTabelasAsString(tabelaDeSimbolos) + ",\n" +
                "    tupla=" + getContentTabelasAsString(tupla) + ",\n" +
                "    erros=" + getListaDeErrosAsString() + "\n" +
                '}';
    }

    private String getContentTabelasAsString(Map<String, union> tabela) {
        if (tabela.isEmpty()) return "{}";
        List<String> list = tabela.entrySet().stream()
                .map(t -> t.getKey() + " = " + t.getValue().toString())
                .collect(Collectors.toList());
        String r = "{\n";
        for (String s : list) {
            r += "        " + s + "\n";
        }
        return r + "    }";
    }

    private String getAreaInstrucoesAsString() {
        if (areaDeInstrucoes.isEmpty()) return "{}";
        String r = "{\n";
        for (Instruction ai : areaDeInstrucoes) {
            r += "        " + ai.toString() + "\n";
        }
        return r + "    }";
    }

    public String getListaDeErrosAsString() {
        if (erros.isEmpty()) return "{}";
        String r = "{\n";
        for (String e : erros) {
            r += "        " + e + "\n";
        }
        return r + "    }";
    }

    public int getNumeroDeErros() {
        return erros.size();
    }

    public List<Instruction> getAreaDeInstrucoes() {
        return areaDeInstrucoes;
    }

    public enum CONTEXTO {ENTRADA_DADOS, ATRIBUICAO, AS_CONSTANT, AS_VARIABLE, NENHUM}

    public enum SAIDA {WRITE_ALL_THIS, WRITE_THIS, NENHUM}
}
