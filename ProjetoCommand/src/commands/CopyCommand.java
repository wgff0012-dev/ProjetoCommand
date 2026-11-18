package commands;

import editor.Editor;

public class CopyCommand extends Command {

    // construtor do ctrl+c
    public CopyCommand(Editor editor) {
        super(editor);
    }

    // copia o texto e retorna false pra não ir pro histórico
    @Override
    public boolean execute() {
        editor.clipboard = editor.textField.getSelectedText();
        return false;
    }
}
