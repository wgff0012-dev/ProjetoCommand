package commands;

import java.util.Stack;

public class CommandHistory {

    // pilha armazenando os comandos do desfazer
    private Stack<Command> history = new Stack<>();

    // adiciona um novo commando na pilha
    public void push(Command c) {
        history.push(c);
    }

    // remove o último comando executado
    public Command pop() {
        return history.pop();
    }

    // verificando se o histórico está vazio
    public boolean isEmpty() { return history.isEmpty(); }
}
