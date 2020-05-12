package compiler.parser;

public class ErrorStruct {
    private ParseException error = null;
    private String msg = null;
    public ErrorStruct(String msg, ParseException error){
        this.error = error;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public ParseException getError() {
        return error;
    }
}
