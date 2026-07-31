package control;

import java.awt.Color;

public enum GameStatusManager {

    // === INICIALIZAÇÃO E TURNOS ===
    TITULO_JOGO(
        "LUDO: ADVANCE TOGETHER", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/game.png"
    ),
    SORTEANDO_JOGADOR(
        "Sorteando o jogador inicial...", 
        new Color(241, 196, 15), 
        "/assets/img/gameBarStatus_emojis/machine.gif" // Caça Níquel
    ),
    INICIO_VENCEDOR(
        "Começa com %s!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/partyHat.gif" // Chapéu de festa
    ),
    VEZ_DE_JOGADOR(
        "Vez de %s...", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/playerTurn.gif" // Mão apontando para o jogador principal
    ),
    SUA_VEZ_COMPRA(
        "Sua vez! Clique no deck para revelar sua carta.", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/playerTurn.gif" // Mão apontando para o jogador principal
    ),
    TURNO_CPU_INICIO(
        "Turno de %s", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/robot-2.gif" // Robô
    ),
    CPU_PENSANDO(
        "%s está pensando...", 
        new Color(241, 196, 15), 
        "/assets/img/gameBarStatus_emojis/cpuThinking.gif" // Engrenagem
    ),

    // === CARTAS ===
    CARTA_PERGUNTA(
        "Desafio! Selecione a alternativa correta.", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/brain.gif" // Cérebro
    ),
    CARTA_SORTE(
        "Parece que alguém aqui tem muita sorte!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/fourLeafClover.gif" // Trevo de 4 folhas
    ),
    CARTA_AZAR(
        "Que azar! Parece que alguém vai voltar algumas casas!", 
        new Color(231, 76, 60), 
        "/assets/img/gameBarStatus_emojis/skull.gif" // Caveira
    ),
    CARTA_PEGADINHA(
        "Seu espertinho! Escolha um jogador para sacanear.", 
        new Color(230, 126, 34), 
        "/assets/img/gameBarStatus_emojis/purpleDevil.gif" // Cara diabinho roxo
    ),

    // === RESPOSTAS E PENALIDADES ===
    RESPOSTA_INCORRETA_SELF(
        "Resposta incorreta! Você perdeu a vez.", 
        new Color(231, 76, 60), 
        "/assets/img/gameBarStatus_emojis/thumbsDown.gif" // Dedo para baixo
    ),
    RESPOSTA_INCORRETA_OTHER(
        "%s errou a resposta e perdeu a vez!", 
        new Color(231, 76, 60), 
        "/assets/img/gameBarStatus_emojis/thumbsDown.gif" // Dedo para baixo
    ),
    PENALIDADE_SEIS_SELF(
        "PENALIDADE! Três 6s seguidos. Você perdeu a vez!", 
        new Color(231, 76, 60), 
        "/assets/img/gameBarStatus_emojis/knocked.gif" // Nocauteado
    ),
    PENALIDADE_SEIS_OTHER(
        "PENALIDADE! %s tirou três 6s seguidos e perdeu a vez!", 
        new Color(231, 76, 60), 
        "/assets/img/gameBarStatus_emojis/knocked.gif" // Nocauteado
    ),
    BASE_SAIDA_INVALIDA(
        "Movimento inválido! Esse peão precisa de 1 ou 6 para sair da base.", 
        new Color(231, 76, 60), 
        "/assets/img/gameBarStatus_emojis/crossMark.gif" // X cruzado
    ),

    // === AZAR / PROTEÇÃO ===
    AZAR_PROTEGIDO_SELF(
        "PROTEGIDO! Seus peões estão salvos na Base ou na Zona Segura!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/shield.png" // Escudo
    ),
    AZAR_PROTEGIDO_OTHER(
        "%s está protegido na Zona Segura ou na Base e não retrocede!", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/shield.png" // Escudo
    ),
    AZAR_MOVER_SELF(
        "O peão azarado %d vai retroceder %d casas!", 
        new Color(231, 76, 60), 
        "/assets/img/gameBarStatus_emojis/crying.gif" // Cara chorando
    ),
    AZAR_RETROCEDER(
        "%s deu azar e terá que retroceder %d casas!", 
        new Color(231, 76, 60), 
        "/assets/img/gameBarStatus_emojis/emojis/laugh.gif" // Cara rindo
    ),

    // === SORTE E MOVIMENTAÇÃO ===
    SORTE_SAIR_BASE(
        "%s está com sorte e vai colocar um peão em jogo!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/fourLeafClover.gif" // Trevo de 4 folhas + Robô
    ),
    SORTE_AVANCAR(
        "%s está com sorte e vai avançar %d casas!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/fourLeafClover.gif" // Trevo de 4 folhas + Robô
    ),
    PEGADINHA_USA_CPU(
        "%s vai usar uma Pegadinha contra outro jogador!", 
        new Color(230, 126, 34), 
        "/assets/img/gameBarStatus_emojis/purpleDevil.gif" // Cara diabinho roxo
    ),
    DESAFIO_SAIR_BASE(
        "%s acertou a resposta e vai colocar um peão em jogo!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/machineLeg.gif" // Perna mecânica
    ),
    DESAFIO_MOVER(
        "%s acertou a resposta e vai %s %d casas!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/machineLeg.gif" // Perna mecânica
    ),
    SEM_MOVIMENTOS_CPU(
        "%s não tem jogadas possíveis. Passando a vez...", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/robot.gif" // Robô
    ),
    SORTE_HUMANO(
        "Parece que alguém aqui tem muita sorte!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/fourLeafClover.gif" // Trevo de 4 folhas
    ),
    ACERTO_HUMANO(
        "Parabéns, você acertou a resposta!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/partyHat.gif" // Chapéu de festa + Mão Ok
    ),
    SEM_PEOES_DISPONIVEIS(
        "Não há jogadas possíveis! Passando a vez...", 
        new Color(241, 196, 15), 
        "/assets/img/gameBarStatus_emojis/frustrated.gif" // Cara frustrada
    ),
    AUTO_MOVE_SORTE(
        "Movendo o peão sortudo %d automaticamente...", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/pawnMoving.gif" // Peão movendo
    ),
    AUTO_MOVE_UNICO(
        "Apenas o peão %d está disponível. Movendo automaticamente...", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/pawnMoving.gif" // Peão movendo
    ),

    // === SELEÇÃO DE PEÕES ===
    ESCOLHA_PEOES(
        "Escolha entre %s.", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/pointingDown.gif" // Mão apontando para baixo + Peão
    ),
    PEAO_SELECIONADO(
        "Peão %d selecionado. Confirme sua jogada clicando nele!", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/pawnBalance.gif" // Peão equilibrando
    ),
    PEAO_FINALIZADO(
        "O peão %d já chegou ao final do percurso.", 
        new Color(230, 180, 80), 
        "/assets/img/gameBarStatus_emojis/pawn.gif" // Peão
    ),
    PEAO_NA_BASE(
        "O peão %d está na base. Você precisa de 1 ou 6 para tirá-lo.", 
        new Color(230, 180, 80), 
        "/assets/img/gameBarStatus_emojis/pawn.gif" // Peão
    ),
    PEAO_SEM_MOVIMENTO(
        "O peão %d não tem movimentos válidos nesta jogada.", 
        new Color(230, 180, 80), 
        "/assets/img/gameBarStatus_emojis/pawn.gif" // Peão
    ),
    PEAO_BLOQUEADO_TORRE(
        "O peão %d não pode ser movido pois há uma torre em seu caminho.", 
        new Color(231, 76, 60), 
        "/assets/img/gameBarStatus_emojis/crossMark.gif" // X cruzado
    ),

    // === FINALIZAÇÃO E ATAQUE ===
    CHEGOU_CENTRO(
        "INCRÍVEL! O peão %d de %s chegou ao Centro!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/trophy.png" // Troféu
    ),
    VITORIA_HUMANO(
        "PARABÉNS! Você levou todos os 4 peões ao Centro e VENCEU O JOGO!", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/crown.png" // Coroa + Medalha 1 lugar
    ),
    VITORIA_CPU(
        "FIM DE JOGO! A %s venceu a partida.", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/crown.png" // Coroa + Medalha 1 lugar
    ),
    BONUS_SEIS_HUMANO(
        "Turno Bônus! Você tirou 6 e poderá realizar uma nova jogada.", 
        new Color(46, 204, 113), 
        "/assets/img/gameBarStatus_emojis/dice.gif" // Dado
    ),
    BONUS_SEIS_CPU(
        "%s tirou um '6' e ganhou turno extra!", 
        new Color(255, 255, 255), 
        "/assets/img/gameBarStatus_emojis/dice.gif" // Dado
    ),
    ATAQUE_CAPTURAR(
        "ATAQUE! %s capturou o %s!", 
        new Color(230, 126, 34), 
        "/assets/img/gameBarStatus_emojis/crossedSwords.png" // Espadas Cruzadas
    );

    private final String template;
    private final Color color;
    private final String iconPath;

    GameStatusManager(String template, Color color, String iconPath) {
        this.template = template;
        this.color = color;
        this.iconPath = iconPath;
    }

    public String format(Object... args) {
        if (args == null || args.length == 0) return template;
        return String.format(template, args);
    }

    public Color getColor() { return color; }
    public String getIconPath() { return iconPath; }
}