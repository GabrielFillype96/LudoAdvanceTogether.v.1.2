package cards;

import java.util.ArrayList;
import java.util.List;

public class CardManager {

    /**
     * Cria e retorna as cartas de Pergunta da categoria FÁCIL.
     * Configurado para o Construtor 2 (Sim/Não) e Construtor 3 (Múltipla Escolha).
     */
    public static List<CustomCards> criarCartasFaceis() {
        List<CustomCards> cartasFaceis = new ArrayList<>();

        // CARTA 1: Pergunta Sim/Não (Usa Construtor 2)
        cartasFaceis.add(new CustomCards(
            1,                                       // ID da Carta
            "PERGUNTA",                              // Tipo Geral
            "A reciclagem de pilhas comuns e baterias pode ser feita no mesmo lixo de plásticos?", // Enunciado
            "AVANÇAR",                               // Efeito caso acerte
            "2/6",                                   // Valor do efeito (fração visual)
            "peao_azul.png",                         // Ícone do peão no canto superior direito
            "FÁCIL",                                 // Dificuldade
            "Não"                                    // Resposta correta
        ));

        // CARTA 2: Pergunta de Múltipla Escolha (Usa Construtor 3)
        String[] opcoesCarta2 = {"A) Descarte em rios", "B) Coleta Seletiva", "C) Lixo Comum"};
        cartasFaceis.add(new CustomCards(
            2, 
            "PERGUNTA", 
            "Qual é o destino mais adequado para lâmpadas fluorescentes queimadas visando a preservação?", 
            "AVANÇAR", 
            "1/6", 
            "peao_azul.png", 
            "FÁCIL", 
            opcoesCarta2, 
            "B"
        ));

        return cartasFaceis;
    }

    /**
     * Cria e retorna as cartas de Pergunta da categoria MÉDIO.
     */
    public static List<CustomCards> criarCartasMedias() {
        List<CustomCards> cartasMedias = new ArrayList<>();

        // CARTA 3: Pergunta de Múltipla Escolha Médio
        String[] opcoesCarta3 = {"A) Monóxido de Carbono", "B) Oxigénio", "C) Dióxido de Carbono"};
        cartasMedias.add(new CustomCards(
            3, 
            "PERGUNTA", 
            "Qual destes gases é o principal responsável pelo agravamento do efeito estufa gerado por indústrias?", 
            "AVANÇAR", 
            "3", 
            "peao_verde.png", 
            "MÉDIO", 
            opcoesCarta3, 
            "C"
        ));

        return cartasMedias;
    }

    /**
     * Cria e retorna as cartas de Pergunta da categoria DIFÍCIL.
     */
    public static List<CustomCards> criarCartasDificeis() {
        List<CustomCards> cartasDificeis = new ArrayList<>();

        // CARTA 4: Pergunta Sim/Não Difícil
        cartasFechadasDificeis(cartasDificeis);

        return cartasDificeis;
    }

    private static void cartasFechadasDificeis(List<CustomCards> lista) {
        lista.add(new CustomCards(
            4, 
            "PERGUNTA", 
            "O Protocolo de Kyoto foi o primeiro tratado internacional com metas obrigatórias de redução de gases?", 
            "AVANÇAR", 
            "1/2", 
            "peao_vermelho.png", 
            "DIFÍCIL", 
            "Sim"
        ));
    }

    /**
     * Cria e retorna as cartas especiais de SORTE.
     * Usa o Construtor 1 (Sem parâmetros de perguntas ou respostas).
     */
    public static List<CustomCards> criarCartasSorte() {
        List<CustomCards> cartasSorte = new ArrayList<>();

        // CARTA 5: Carta de Sorte pura (Usa Construtor 1)
        cartasSorte.add(new CustomCards(
            5, 
            "SORTE", 
            "Ótimas práticas ecológicas! A sua empresa adotou energia solar em todos os setores.", 
            "AVANÇAR", 
            "4", 
            "peao_amarelo.png"
        ));

        return cartasSorte;
    }

    /**
     * Cria e retorna as cartas especiais de AZAR.
     * Usa o Construtor 1 (Efeito negativo de retroceder).
     */
    public static List<CustomCards> criarCartasAzar() {
        List<CustomCards> cartasAzar = new ArrayList<>();

        // CARTA 6: Carta de Azar pura (Usa Construtor 1)
        cartasAzar.add(new CustomCards(
            6, 
            "AZAR", 
            "Desastre! Foi detetado um vazamento de resíduos químicos não tratados no rio local.", 
            "RETROCEDER", 
            "3", 
            "peao_vermelho.png"
        ));

        return cartasAzar;
    }

    /**
     * Cria e retorna as cartas especiais de SACANEAR.
     */
    public static List<CustomCards> criarCartasSacanear() {
        List<CustomCards> cartasSacanear = new ArrayList<>();

        // CARTA 7: Carta de Sacanear (Usa Construtor 1)
        cartasSacanear.add(new CustomCards(
            7, 
            "SACANEAR", 
            "Sabotagem industrial! Troque de lugar com o jogador que estiver mais à sua frente.", 
            "RETROCEDER", 
            "2", 
            "peao_verde.png"
        ));

        return cartasSacanear;
    }
}