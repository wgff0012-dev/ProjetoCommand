# ProjetoCommand

Editor de texto simples, desenvolvido em Java com interface gráfica Swing, que aplica o padrão de projeto **Command** para implementar as operações de copiar, recortar, colar e desfazer.

Este projeto foi desenvolvido para fins acadêmicos, com o objetivo de demonstrar a aplicação prática de um padrão de projeto comportamental.

---

## Sumário

1. [Explicação do projeto](#1-explicação-do-projeto)
2. [Justificativa do padrão escolhido](#2-justificativa-do-padrão-escolhido)
3. [Estrutura do projeto](#3-estrutura-do-projeto)
4. [Como executar](#4-como-executar)
5. [Integrantes](#5-integrantes)

---

## 1. Explicação do projeto

### 1.1 Objetivo

O ProjetoCommand é um editor de texto minimalista composto por uma área de digitação e quatro botões: **Ctrl+C** (copiar), **Ctrl+X** (recortar), **Ctrl+V** (colar) e **Ctrl+Z** (desfazer). O objetivo central não é a sofisticação do editor, mas a demonstração de como o padrão Command permite desacoplar quem solicita uma operação (os botões da interface) de quem sabe executá-la (as classes de comando), além de viabilizar o recurso de desfazer.

### 1.2 Funcionamento

Cada ação do usuário na interface é representada por um objeto de comando:

| Botão  | Comando         | Responsabilidade                                                                 |
|--------|-----------------|-----------------------------------------------------------------------------------|
| Ctrl+C | `CopyCommand`   | Copiar o texto selecionado para a área de transferência (`clipboard`) do editor.  |
| Ctrl+X | `CutCommand`    | Copiar o texto selecionado para a área de transferência e removê-lo do texto.     |
| Ctrl+V | `PasteCommand`  | Inserir o conteúdo da área de transferência na posição do cursor.                 |
| Ctrl+Z | *(histórico)*   | Desfazer o último comando que alterou o texto.                                    |

Ao acionar um botão, o editor instancia o comando correspondente e o executa. Comandos que modificam o conteúdo do texto (recortar e colar) registram, antes da alteração, um *backup* do texto atual. Se a execução do comando indicar que houve alteração (retorno `true` do método `execute()`), ele é empilhado no histórico. Ao acionar o botão de desfazer, o editor remove o comando mais recente do histórico e invoca seu método `undo()`, que restaura o texto salvo no backup.

Comandos que não alteram o texto, como a cópia, não são empilhados no histórico (retornam `false`), pois não há o que desfazer. O mesmo vale para o recorte sem texto selecionado e para a colagem com a área de transferência vazia: nesses casos nenhuma alteração ocorre e o comando também não é registrado.

Vale observar que a área de transferência utilizada é um atributo interno do editor (`clipboard`), e não a área de transferência do sistema operacional.

### 1.3 Participantes do padrão no projeto

| Papel no padrão Command | Classe no projeto                                      | Descrição                                                                                                                          |
|-------------------------|--------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| **Command**             | `commands.Command`                                     | Classe abstrata que define a interface comum: `execute()` (abstrato), `undo()` e o método auxiliar `backup()`.                     |
| **ConcreteCommand**     | `CopyCommand`, `CutCommand`, `PasteCommand`            | Implementações específicas de cada operação.                                                                                       |
| **Invoker**             | `editor.Editor` (botões e método `executeCommand`)     | Cria e dispara os comandos em resposta às ações do usuário, sem conhecer os detalhes de cada operação. Também gerencia o histórico. |
| **Receiver**            | `editor.Editor` (`textField` e `clipboard`)            | Mantém o estado sobre o qual os comandos atuam: a área de texto e a área de transferência.                                         |
| **Histórico**           | `commands.CommandHistory`                              | Pilha (`java.util.Stack`) de comandos executados, utilizada para o recurso de desfazer, na ordem inversa à da execução (LIFO).      |
| **Client**              | `main.Main`                                            | Ponto de entrada que instancia o editor e inicializa a interface.                                                                  |

---

## 2. Justificativa do padrão escolhido

### 2.1 Por que o Command é o mais adequado

O padrão **Command** encapsula uma requisição como um objeto, permitindo parametrizar clientes com diferentes requisições, enfileirar ou registrar operações e oferecer suporte a operações reversíveis. Para um editor de texto com histórico de ações, essas características se aplicam diretamente:

- **Desacoplamento entre interface e lógica.** Os botões não conhecem a lógica de copiar, recortar ou colar. Eles apenas criam e executam um comando. Assim, a interface pode ser alterada (por exemplo, adicionando atalhos de teclado ou itens de menu) sem modificar a implementação das operações.
- **Suporte natural ao desfazer.** Como cada ação é um objeto, é possível armazená-lo em uma pilha e invocar seu `undo()` posteriormente. Sem o padrão, o histórico precisaria ser mantido de forma improvisada dentro da própria interface.
- **Reutilização.** O mesmo comando pode ser associado a vários elementos de interface (botão, menu, atalho) sem duplicação de código.
- **Extensibilidade (princípio aberto/fechado).** Novas operações, como "negrito", "localizar e substituir" ou "refazer", podem ser adicionadas criando-se novas subclasses de `Command`, sem alterar as existentes.
- **Responsabilidade única.** Cada classe de comando é responsável por uma única operação, o que facilita a leitura, a manutenção e os testes.

### 2.2 Padrões alternativos

Outros padrões poderiam ser empregados, total ou parcialmente, para resolver o mesmo problema:

| Padrão alternativo | Como poderia ser usado | Limitações em relação ao Command |
|--------------------|------------------------|----------------------------------|
| **Memento** | Salvar *snapshots* completos do estado do editor a cada alteração e restaurá-los ao desfazer. | Resolve bem o desfazer, mas não estrutura as operações em si. Isso significa que o desacoplamento entre interface e ações permaneceria um problema em aberto. Na prática, os dois padrões são frequentemente combinados, e o próprio projeto utiliza uma ideia semelhante ao Memento ao guardar o texto anterior no atributo `backup` de cada comando. |
| **Strategy** | Encapsular cada operação como uma estratégia intercambiável. | Estruturalmente parecido com o Command, mas com intenção diferente: o Strategy troca *algoritmos* de uma mesma tarefa, enquanto o Command representa *requisições* que podem ser armazenadas, enfileiradas e desfeitas. Não oferece, por si só, a noção de histórico. |
| **Template Method** | Definir em uma classe base o esqueleto do fluxo (fazer backup, executar, registrar) e deixar as subclasses preencherem os passos. | Poderia complementar o Command (a classe abstrata `Command` já se aproxima disso), mas isoladamente não desacopla o solicitante do executor. |
| **Chain of Responsibility** | Encadear manipuladores que decidem quem processa cada ação do usuário. | Útil quando várias partes podem tratar uma mesma requisição, o que não é o caso aqui, pois cada botão corresponde a exatamente uma operação. |
| **Sem padrão (lógica nos *listeners*)** | Implementar cada operação diretamente nos eventos dos botões. | Funciona para um exemplo muito simples, mas mistura interface e lógica, dificulta o desfazer e não escala com a adição de novas funcionalidades. |

Conclui-se que o Command é a escolha mais adequada por atender simultaneamente aos dois requisitos centrais do problema (desacoplar as ações da interface e permitir desfazê-las), sendo que o Memento seria a alternativa mais plausível, preferencialmente em combinação com o Command, e não em substituição a ele.

---

## 3. Estrutura do projeto

```
ProjetoCommand/
└── src/
    ├── commands/
    │   ├── Command.java          # Classe abstrata base dos comandos
    │   ├── CommandHistory.java   # Histórico (pilha) de comandos executados
    │   ├── CopyCommand.java      # Comando de copiar
    │   ├── CutCommand.java       # Comando de recortar
    │   └── PasteCommand.java     # Comando de colar
    ├── editor/
    │   └── Editor.java           # Interface gráfica, invocador e receptor
    └── main/
        └── Main.java             # Ponto de entrada da aplicação
```

---

## 4. Como executar

### Requisitos

- JDK compatível com a configuração do projeto (o arquivo `.classpath` aponta para o **Java SE 25**).
- IDE Eclipse (recomendada, pois o projeto foi criado com ela), ou outra IDE com suporte a Java.

### Passos

1. Clone o repositório ou extraia o arquivo `.zip` do projeto.
2. Importe o projeto na IDE (no Eclipse: *File > Import > Existing Projects into Workspace*).
3. Execute a classe `main.Main`.
4. Digite um texto na janela e utilize os botões para copiar, recortar, colar e desfazer.

---

## 5. Integrantes

- Nicolas Nery da Silva Feitosa
- William Gabriel Felix Farias
- Isacson Joabe Lima Cruz
