package compiler.parser;

public class TokenHandler {
    private static boolean closed = true;
    private static String token;
    private static int column;
    private static int line;
    private static int id;

    public static void writeInvalidToOutput(String token, int line, int column, int id){
        setClosed(false);
        setToken(token);
        setColumn(column);
        setLine(line);
        setId(id);
    }

    public static Token createToken(){
        Token token = new Token(getId(), getToken());
        token.beginColumn = getColumn();
        token.beginLine = getLine();
        token.kind = getId();

        return token;
    }

    public static boolean isClosed() {
        return closed;
    }

    public static void setClosed(boolean closed) {
        TokenHandler.closed = closed;
    }

    public static String getToken() {
        return token;
    }


    public static int getLine() {
        return line;
    }

    public static void setLine(int line) {
        TokenHandler.line = line;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        TokenHandler.id = id;
    }

    public static void setToken(String token) {
        TokenHandler.token = token;
    }

    public static int getColumn() {
        return column;
    }

    public static void setColumn(int column) {
        TokenHandler.column = column;
    }
}
