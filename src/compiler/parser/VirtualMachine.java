package compiler.parser;

/*
TODO
import br.univali.compiladores.sintatico.pront.Pront;
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class VirtualMachine {

    private List<SymbolTable> pilha_de_simbolos;
    private List<Instruction> pilha_de_instrucoes;
    private List<String> pilha;
    private List<String> error;
    private Integer ponteiro;
    private Integer topo;
    //private Pront pront;
    private List<String> result;
    private boolean loop;
    private String valorEntrada;

    private boolean regexFloat(String value){
        Pattern roma = Pattern.compile("[0-9]{1,5}.[0-9]{1,2}");
        return roma.matcher(value).matches();
    }

    private boolean regexInteger(String value){
        Pattern roma = Pattern.compile("[0-9]{1,3}");
        return roma.matcher(value).matches();
    }

    public VirtualMachine(List<SymbolTable> pilha_de_simbolos, List<Instruction> pilha_de_instrucoes) {
        this.pilha_de_simbolos = pilha_de_simbolos;
        this.pilha_de_instrucoes = pilha_de_instrucoes;
        pilha = new ArrayList<>();
        ponteiro = 0;
        topo = 0;
        this.result = new ArrayList<>();
        //pront = new Pront();
        //pront.setVisible(true);
        loop = true;
    }

    public List<SymbolTable> getPilha_de_simbolos() {
        return pilha_de_simbolos;
    }

    public void setPilha_de_simbolos(List<SymbolTable> pilha_de_simbolos) {
        this.pilha_de_simbolos = pilha_de_simbolos;
    }

    public List<Instruction> getPilha_de_instrucoes() {
        return pilha_de_instrucoes;
    }

    public void setPilha_de_instrucoes(List<Instruction> pilha_de_instrucoes) {
        this.pilha_de_instrucoes = pilha_de_instrucoes;
    }

    public List<String> getError() {
        return error;
    }

    public void executar(){
        error = new ArrayList<>();
        while(loop){
            chamarInstruction(pilha_de_instrucoes.get(ponteiro));
        }
        loop = true;
    }

    private void chamarInstruction(Instruction instrucao){
        switch(instrucao.getInstrucao()){
            case "ADD":
                instrucaoADD();
                break;
            case "ALB":
                instrucaoALB(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "ALI":
                instrucaoALI(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "ALR":
                instrucaoALR(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "ALS":
                instrucaoALS(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "AND":
                instrucaoAND();
                break;
            case "BGE":
                instrucaoBGE();
                break;
            case "BGR":
                instrucaoBGR();
                break;
            case "DIF":
                instrucaoDIF();
                break;
            case "DIV":
                instrucaoDIV();
                break;
            case "EQL":
                instrucaoEQL();
                break;
            case "JMF":
                instrucaoJMF(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "JMP":
                instrucaoJMP(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "JMT":
                instrucaoJMT(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "LDV":
                instrucaoLDV(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "LDB":
                instrucaoLDB(instrucao.getEndereco());
                break;
            case "LDI":
                instrucaoLDI(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "LDR":
                instrucaoLDR(Float.parseFloat(instrucao.getEndereco()));
                break;
            case "LDS":
                instrucaoLDS(instrucao.getEndereco());
                break;
            case "MUL":
                instrucaoMUL();
                break;
            case "NOT":
                instrucaoNOT();
                break;
            case "OR":
                instrucaoOR();
                break;
            case "REA":
                instrucaoREA(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "SME":
                instrucaoSME();
                break;
            case "SMR":
                instrucaoSMR();
                break;
            case "STR":
                instrucaoSTR(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "STP":
                instrucaoSTP();
                break;
            case "SUB":
                instrucaoSUB();
                break;
            case "WRT":
                instrucaoWRT();
                break;
            case "STC":
                instrucaoSTC(Integer.parseInt(instrucao.getEndereco()));
                break;
            case "DVI":
                instrucaoDVI();
                break;
            case "MOD":
                instrucaoMOD();
                break;
            case "POT":
                instrucaoPOT();
                break;
            default :
                break;
        }
    }

    private void instrucaoADD(){
        Float aux = (Float.parseFloat(pilha.get(topo - 2)) + Float.parseFloat(pilha.get(topo - 1)));
        if(pilha.get(topo - 2).contains(".") || pilha.get(topo - 1).contains(".")){
            if(!regexFloat(aux.toString())){
                error.add("Soma resultou em um valor de float inválido. Valor: " + aux.toString());
                loop = false;
            }
        } else {
            Integer aux2 = Math.round(aux);
            if(!regexInteger(aux2.toString())){
                error.add("Soma resultou em um valor de inteiro inválido. Valor: " + aux2.toString());
                loop = false;
            }
            int a = Math.round(aux);
            aux = (float) a;
        }
        pilha.set(topo - 2, aux.toString());
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoALB(Integer deslocamento){
        for(int i = topo + 1; i <= topo + deslocamento; i++){
            pilha.add("FALSE");
        }
        topo = topo + deslocamento;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoALI(Integer deslocamento){
        for(int i = topo + 1; i <= topo + deslocamento; i++){
            pilha.add("0");
        }
        topo = topo + deslocamento;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoALR(Integer deslocamento){
        for(int i = topo + 1; i <= topo + deslocamento; i++){
            pilha.add("0.0");
        }
        topo = topo + deslocamento;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoALS(Integer deslocamento){
        for(int i = topo + 1; i <= topo + deslocamento; i++){
            pilha.add(" ");
        }
        topo = topo + deslocamento;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoAND(){
        boolean aux = pilha.get(topo - 2).equals("TRUE") && pilha.get(topo - 1).equals("TRUE");
        if(aux){
            pilha.set(topo-2, "TRUE");
        }else{
            pilha.set(topo-2, "FALSE");
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoBGE(){
        boolean aux = (Float.parseFloat(pilha.get(topo - 2)) >= Float.parseFloat(pilha.get(topo - 1)));
        if(aux){
            pilha.set(topo-2, "TRUE");
        }else{
            pilha.set(topo-2, "FALSE");
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoBGR(){
        boolean aux = (Float.parseFloat(pilha.get(topo - 2)) > Float.parseFloat(pilha.get(topo - 1)));
        if(aux){
            pilha.set(topo-2, "TRUE");
        }else{
            pilha.set(topo-2, "FALSE");
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoDIF(){
        boolean aux = pilha.get(topo -2).equals(pilha.get(topo - 1));
        if(!aux){
            pilha.set(topo-2, "TRUE");
        }else{
            pilha.set(topo-2, "FALSE");
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoDIV(){
        if(pilha.get(topo - 1).equals("0") | pilha.get(topo - 1).equals("00") | pilha.get(topo - 1).equals("000") | pilha.get(topo - 1).equals("0000")
                | pilha.get(topo - 1).equals("00000") | pilha.get(topo - 1).equals("0.0") | pilha.get(topo - 1).equals("00.0")
                | pilha.get(topo - 1).equals("000.0") | pilha.get(topo - 1).equals("0000.0") | pilha.get(topo - 1).equals("00000.0")
                | pilha.get(topo - 1).equals("0.00") | pilha.get(topo - 1).equals("00.00")
                | pilha.get(topo - 1).equals("000.00") | pilha.get(topo - 1).equals("0000.00") | pilha.get(topo - 1).equals("00000.00")
        ){
            error.add("Não pode existir divisão por por 0");
            loop = false;
        }
        Float aux = Float.parseFloat(pilha.get(topo - 2)) / Float.parseFloat(pilha.get(topo - 1));
        if(pilha.get(topo - 2).contains(".") || pilha.get(topo - 1).contains(".")){
            if(!regexFloat(aux.toString())){
                error.add("Divisão resultou em um valor de float inválido. Valor: " + aux.toString());
                loop = false;
            }
        } else {
            Integer aux2 = Math.round(aux);
            if(!regexInteger(aux2.toString())){
                error.add("Divisão resultou em um valor de inteiro inválido. Valor: " + aux2.toString());
                loop = false;
            }
            int a = Math.round(aux);
            aux = (float) a;
        }
        pilha.set(topo - 2, aux.toString());
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoEQL(){
        boolean aux = pilha.get(topo - 2).equals(pilha.get(topo - 1));
        if(aux){
            pilha.set(topo-2, "TRUE");
        }else{
            pilha.set(topo-2, "FALSE");
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoJMF(Integer endereco){
        if(pilha.get(topo - 1).equals("FALSE")){
            ponteiro = endereco - 1;
        }else{
            ponteiro = ponteiro +1;
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
    }

    private void instrucaoJMP(Integer endereco){
        ponteiro = endereco - 1;
    }

    private void instrucaoJMT(Integer endereco){
        if(pilha.get(topo - 1).equals("TRUE")){
            ponteiro = endereco - 1;
        }else{
            ponteiro = ponteiro + 1;
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
    }

    private void instrucaoLDV(Integer endereco){
        topo = topo + 1;
        pilha.add(pilha.get(endereco - 1));
        ponteiro = ponteiro + 1;
    }

    private void instrucaoLDB(String constante){
        topo = topo + 1;
        pilha.add(constante.toUpperCase());
        ponteiro = ponteiro + 1;
    }

    private void instrucaoLDI(Integer constante){
        topo = topo + 1;
        pilha.add(constante.toString());
        ponteiro = ponteiro + 1;
    }

    private void instrucaoLDR(Float constante){
        topo = topo + 1;
        pilha.add(constante.toString());
        ponteiro = ponteiro + 1;
    }

    private void instrucaoLDS(String constante){
        topo = topo + 1;
        pilha.add(constante);
        valorEntrada = constante;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoMUL(){
        Float aux = Float.parseFloat(pilha.get(topo - 2)) * Float.parseFloat(pilha.get(topo - 1));
        if(pilha.get(topo - 2).contains(".") || pilha.get(topo - 1).contains(".")){
            if(!regexFloat(aux.toString())){
                error.add("Multiplicação resultou em um valor de float inválido. Valor: " + aux.toString());
                loop = false;
            }
        } else {
            if(!regexInteger(aux.toString())){
                error.add("Multiplicação resultou em um valor de inteiro inválido. Valor: " + aux.toString());
                loop = false;
            }
            int a = Math.round(aux);
            aux = (float) a;
        }
        pilha.set(topo - 2, aux.toString());
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoNOT(){
        if(pilha.get(topo - 1).equals("TRUE")){
            pilha.set(topo - 1, "FALSE");
        }else{
            pilha.set(topo - 1, "TRUE");
        }
        ponteiro = ponteiro + 1;
    }

    private void instrucaoOR(){
        if(pilha.get(topo - 2).equals("TRUE") || pilha.get(topo - 1).equals("TRUE")){
            pilha.set(topo - 2, "TRUE");
        }else{
            pilha.set(topo - 2, "FALSE");
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoREA(Integer tipo){
        topo = topo + 1;
        String value = JOptionPane.showInputDialog(valorEntrada);
//        String value = pront.getValor();
//        pilha.add(pront.getValor()); //veriticar como para entrada;
//implementar validação de inteiro float e boolean. char não precisa também deve ser implementado no set validação de entrada
        if(tipo == 1){
            Integer validador = Math.round(Float.parseFloat(value));
            if(!regexInteger(value)){
                error.add("O valor (" + value + ") é um inteiro inválido");
                loop = false;
            }
            pilha.add(value);
        } else if(tipo == 2){
            Float validador = Float.parseFloat(value);
            if(!regexFloat(value)){
                error.add("O valor (" + value + ") é um float inválido");
                loop = false;
            }
            pilha.add(value);
        } else if(tipo == 3){
            pilha.add(value);
        } else if(tipo == 4){
            if(value.toUpperCase().equals("TRUE") || value.toUpperCase().equals("1")){
                pilha.add("TRUE");
            } else if(value.toUpperCase().equals("FALSE") || value.toUpperCase().equals("0")){
                pilha.add("FALSE");
            } else {
                error.add("valor (" + value + ") é um boolean inválido");
                loop = false;
            }
        }
        ponteiro = ponteiro + 1;
    }

    private void instrucaoSME(){
        boolean aux = (Float.parseFloat(pilha.get(topo - 2)) <= Float.parseFloat(pilha.get(topo - 1)));
        if(aux){
            pilha.set(topo - 2, "TRUE");
        }else{
            pilha.set(topo - 2, "FALSE");
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoSMR(){
        boolean aux = (Float.parseFloat(pilha.get(topo - 2)) < Float.parseFloat(pilha.get(topo - 1)));
        if(aux){
            pilha.set(topo - 2, "TRUE");
        }else{
            pilha.set(topo - 2, "FALSE");
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoSTR(Integer endereco){
        pilha.set(endereco - 1, pilha.get(topo - 1));
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoSTP(){
        loop = false;
    }

    private void instrucaoSUB(){
        Float aux = Float.parseFloat(pilha.get(topo - 2)) - Float.parseFloat(pilha.get(topo - 1));
        if(pilha.get(topo - 2).contains(".") || pilha.get(topo - 1).contains(".")){
            if(!regexFloat(aux.toString())){
                error.add("Soma resultou em um valor de float inválido. Valor: " + aux.toString());
                loop = false;
            }
        } else {
            if(!regexInteger(aux.toString())){
                error.add("Soma resultou em um valor de inteiro inválido. Valor: " + aux.toString());
                loop = false;
            }
            int a = Math.round(aux);
            aux = (float) a;
        }

        pilha.set(topo-2, aux.toString());
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoWRT(){
        this.result.add(pilha.get(topo - 1));
        //pront.setValor(pilha.get(topo - 1));
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoSTC(Integer deslocamento){
        for(int i = topo - deslocamento -1; i <= topo - 2; i++){
            pilha.set(i, pilha.get(topo - 1));
        }
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoDVI() {
        if(pilha.get(topo - 1).equals("0") | pilha.get(topo - 1).equals("00") | pilha.get(topo - 1).equals("000") | pilha.get(topo - 1).equals("0000")
                | pilha.get(topo - 1).equals("00000") | pilha.get(topo - 1).equals("0.0") | pilha.get(topo - 1).equals("00.0")
                | pilha.get(topo - 1).equals("000.0") | pilha.get(topo - 1).equals("0000.0") | pilha.get(topo - 1).equals("00000.0")
                | pilha.get(topo - 1).equals("0.00") | pilha.get(topo - 1).equals("00.00")
                | pilha.get(topo - 1).equals("000.00") | pilha.get(topo - 1).equals("0000.00") | pilha.get(topo - 1).equals("00000.00")
        ){
            error.add("Não pode existir divisão por por 0");
            loop = false;
        }
        Integer aux = (Math.round(Float.parseFloat(pilha.get(topo - 2))) / Math.round(Float.parseFloat(pilha.get(topo - 1))));
        pilha.set(topo - 2, aux.toString());
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoMOD() {
        Float aux = (Float.parseFloat(pilha.get(topo - 2)) % Float.parseFloat(pilha.get(topo - 1)));
        pilha.set(topo - 2, aux.toString());
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    private void instrucaoPOT() {
        Float aux = (float) Math.pow(Float.parseFloat(pilha.get(topo - 2)), Float.parseFloat(pilha.get(topo - 1)));
        if(pilha.get(topo - 2).contains(".") || pilha.get(topo - 1).contains(".")){
            if(!regexFloat(aux.toString())){
                error.add("Potência resultou em um valor de float inválido. Valor: " + aux.toString());
                loop = false;
            }
        } else {
            Integer aux2 = Math.round(aux);
            if(!regexInteger(aux2.toString())){
                error.add("Potência resultou em um valor de inteiro inválido. Valor: " + aux2.toString());
                loop = false;
            }
            int a = Math.round(aux);
            aux = (float) a;
        }
        pilha.set(topo - 2, aux.toString());
        pilha.remove(topo - 1);
        topo = topo - 1;
        ponteiro = ponteiro + 1;
    }

    public void setProntVisible(boolean b) {
        //pront.setVisible(b);
    }

}

