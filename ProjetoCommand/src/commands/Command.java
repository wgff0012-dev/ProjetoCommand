package commands;

import editor.Editor;

public abstract class Command {
    public Editor editor;
    private String backup;

    // construtor que associa os comandos ao editor
    Command(Editor editor) {
        this.editor = editor;
    }

    // faz backup do texto, pra que mudanças possam ser desfeitas
    void backup() {
        backup = editor.textField.getText();
    }

    // restaura o que havia antes da alteração, usando o backup
    public void undo() {
        editor.textField.setText(backup);
    }

    // método que é implementado por cada comando específico, retorna true se o comando tiver modificado o estado
    public abstract boolean execute();
}
