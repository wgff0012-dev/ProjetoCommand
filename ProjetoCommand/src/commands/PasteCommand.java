package commands;

import editor.Editor;

public class PasteCommand extends Command {

    // construtor do ctrl+v
    public PasteCommand(Editor editor) {
        super(editor);
    }

    // cola o texto e faz backup
    @Override
    public boolean execute() {
        if (editor.clipboard == null || editor.clipboard.isEmpty()) return false;

        backup();
        editor.textField.insert(editor.clipboard, editor.textField.getCaretPosition());
        return true;
    }
}
