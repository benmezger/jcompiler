package compiler.parser;

public class SymbolTable {

    private String identificador;
    private Integer categoria;
    //    0 para identificador de programa
    //    1, 2, 3 ou 4 para variáveis inteiras, reais, literais ou lógicas, respectivamente
    //    5, 6 ou 7 para constantes inteiras, reais ou literais, respectivamente
    private String atributo1;// Deslocamento na pilha de dados (pilha de execução)
    private String atributo2;// Para variáveis indexadas será o tamanho (comprimento) da variável e para as demais variáveis ou constantes será “-“ (hífen)

    public SymbolTable() {
    }

    public SymbolTable(String identificador, Integer categoria, String atributo1, String atributo2) {
        this.identificador = identificador;
        this.categoria = categoria;
        this.atributo1 = atributo1;
        this.atributo2 = atributo2;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Integer getCategoria() {
        return categoria;
    }

    public void setCategoria(Integer categoria) {
        this.categoria = categoria;
    }

    public String getAtributo1() {
        return atributo1;
    }

    public void setAtributo1(String atributo1) {
        this.atributo1 = atributo1;
    }

    public String getAtributo2() {
        return atributo2;
    }

    public void setAtributo2(String atributo2) {
        this.atributo2 = atributo2;
    }
}
