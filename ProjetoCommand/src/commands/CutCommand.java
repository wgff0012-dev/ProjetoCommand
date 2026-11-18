package commands;

import editor.Editor;

public class CutCommand extends Command {

    // construtor do ctrl+x
    public CutCommand(Editor editor) {
        super(editor);
    }

    // recorta o texto, tirando ele da tela e salvando no histórico
    @Override
    public boolean execute() {
        // se não tiver selecionado algo, nada acontece
        if (editor.textField.getSelectedText().isEmpty()) return false;

        backup();
        String source = editor.textField.getText();
        editor.clipboard = editor.textField.getSelectedText();
        editor.textField.setText(cutString(source));
        return true;
    }

    // remove o texto selecionado
    private String cutString(String source) {
        String start = source.substring(0, editor.textField.getSelectionStart());
        String end = source.substring(editor.textField.getSelectionEnd());
        return start + end;
    }
}
