package compiler.parser;

public class First {
    static public final RecoverySet main = new RecoverySet();
    static public final RecoverySet body = new RecoverySet();
    static public final RecoverySet enumDeclarations = new RecoverySet();
    static public final RecoverySet list_of_commands = new RecoverySet();
    static public final RecoverySet end_of_file = new RecoverySet();
    static public final RecoverySet selection_command = new RecoverySet();

    static {
        main.add(LanguageParser.DECLARATION);
        main.add(LanguageParser.BODY);

        body.add(LanguageParser.BODY);

        list_of_commands.add(LanguageParser.READ);
        list_of_commands.add(LanguageParser.WRITE);
        list_of_commands.add(LanguageParser.DESIGNATE);
        list_of_commands.add(LanguageParser.AVALIATE);
        list_of_commands.add(LanguageParser.REPEAT);

        end_of_file.add(LanguageParser.EOF);

        selection_command.add(LanguageParser.TRUE);
        selection_command.add(LanguageParser.UNTRUE);

        enumDeclarations.add(LanguageParser.DECLARATION);
    }
}
