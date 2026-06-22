package cards;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import actions.CardAnswerValidation;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CardManager {

    private static final String CAMINHO_JSON = "/assets/data/cardsContent.json";

    /**
     * Carrega todas as cartas do arquivo JSON e filtra pela categoria ou tipo solicitado.
     * @param filtro Categoria de filtragem (Ex: "FÁCIL", "MÉDIO", "DIFÍCIL", "SORTE", "AZAR")
     * @return Lista de objetos CustomCards prontos para uso.
     */
    public static List<CustomCards> loadCard(String filtro, CardAnswerValidation cardAnswerValidation) {
        List<CustomCards> cartasFiltradas = new ArrayList<>();
        Gson gson = new Gson();

        try {
            // Abre o arquivo JSON armazenado dentro da pasta de recursos (assets)
            var inputStream = CardManager.class.getResourceAsStream(CAMINHO_JSON);
            if (inputStream == null) {
                System.err.println("[CardManager] Erro: Arquivo JSON não encontrado em: " + CAMINHO_JSON);
                return cartasFiltradas; 
            }

            // Lê o arquivo garantindo suporte a acentos (UTF-8)
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            
            // Converte o texto JSON em um Array de objetos interativos
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);

            // Varre cada objeto dentro do array do JSON
            for (JsonElement elemento : jsonArray) {
                JsonObject obj = elemento.getAsJsonObject();

                // Extrai os dados básicos de texto e controle
                int id = obj.get("id").getAsInt();
                String tipoGeral = obj.get("tipoGeral").getAsString().toUpperCase();
                String enunciado = obj.get("enunciado").getAsString();
                String efeito = obj.get("efeito").getAsString();
                String valorEfeito = obj.get("valorEfeito").getAsString();
                String iconePeao = obj.get("iconePeao").getAsString();
                String dificuldade = obj.get("dificuldade").getAsString().toUpperCase();
                String tipoPergunta = obj.get("tipoPergunta").getAsString().toUpperCase();
                String respostaCorreta = obj.get("respostaCorreta").getAsString();

                // Verifica se a carta atual corresponde ao filtro que estamos buscando
                boolean correspondeAoFiltro = tipoGeral.equals(filtro.toUpperCase()) || dificuldade.equals(filtro.toUpperCase());
                
                if (correspondeAoFiltro) {
                    CustomCards novaCarta;

                    // Decide qual Construtor da carta chamar com base no tipo
                    if (tipoGeral.equals("PERGUNTA")) {
                        if (tipoPergunta.equals("SIM_NAO")) {
                            // Construtor 2: Pergunta Sim / Não (Adicionado cardAnswerValidation no final)
                            novaCarta = new CustomCards(id, tipoGeral, enunciado, efeito, valorEfeito, iconePeao, dificuldade, respostaCorreta, cardAnswerValidation);
                        } else {
                            // Construtor 3: Múltipla Escolha
                            JsonArray arrayAlternativas = obj.getAsJsonArray("alternativas");
                            String[] alternativas = new String[arrayAlternativas.size()];
                            for (int i = 0; i < arrayAlternativas.size(); i++) {
                                alternativas[i] = arrayAlternativas.get(i).getAsString();
                            }
                            
                            // Construtor 3 (Adicionado cardAnswerValidation no final)
                            novaCarta = new CustomCards(id, tipoGeral, enunciado, efeito, valorEfeito, iconePeao, dificuldade, alternativas, respostaCorreta, cardAnswerValidation);
                        }
                    } else {
                        // Construtor 1: Cartas Especiais (Adicionado cardAnswerValidation no final)
                        novaCarta = new CustomCards(id, tipoGeral, enunciado, efeito, valorEfeito, iconePeao, cardAnswerValidation);
                    }

                    // Adiciona a carta gerada à nossa lista de retorno
                    cartasFiltradas.add(novaCarta);
                }
            }
            
            reader.close();
        } catch (Exception e) {
            System.err.println("[CardManager] Erro crítico ao processar o JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return cartasFiltradas;
    }
}