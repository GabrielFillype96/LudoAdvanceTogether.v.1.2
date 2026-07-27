package cards;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CardManager {

    private final String CAMINHO_JSON = "/assets/data/cardsContent.json";

    /**
     * Carrega todas as cartas do arquivo JSON e filtra pela categoria ou tipo solicitado.
     * @param filtro Categoria de filtragem (Ex: "FÁCIL", "MÉDIO", "DIFÍCIL", "SORTE", "AZAR")
     * @return Lista das cartas filtradas
     */
    public List<CustomCards> loadCard(String filtro) {
        List<CustomCards> cartasFiltradas = new ArrayList<>();
        
        if (filtro == null) {
            System.err.println("[CardManager] Aviso: O filtro informado é nulo.");
            return cartasFiltradas;
        }

        String filtroUpper = filtro.toUpperCase();
        Gson gson = new Gson();

        var inputStream = CardManager.class.getResourceAsStream(CAMINHO_JSON);
        if (inputStream == null) {
            System.err.println("[CardManager] Erro: Arquivo JSON não encontrado em: " + CAMINHO_JSON);
            return cartasFiltradas; 
        }

        // Try-with-resources garante o fechamento automático do reader mesmo em caso de exceção
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            if (jsonArray == null) return cartasFiltradas;

            for (JsonElement elemento : jsonArray) {
                JsonObject obj = elemento.getAsJsonObject();

                // Extrai apenas os campos universais (presentes em todas as cartas)
                int id = obj.has("id") ? obj.get("id").getAsInt() : -1;
                String tipoGeral = obj.has("tipoGeral") ? obj.get("tipoGeral").getAsString().toUpperCase() : "";
                String enunciado = obj.has("enunciado") ? obj.get("enunciado").getAsString() : "";
                String efeito = obj.has("efeito") ? obj.get("efeito").getAsString() : "";
                String valorEfeito = obj.has("valorEfeito") ? obj.get("valorEfeito").getAsString() : "";
                String iconePeao = obj.has("iconePeao") ? obj.get("iconePeao").getAsString() : "";
                String dificuldade = obj.has("dificuldade") ? obj.get("dificuldade").getAsString().toUpperCase() : "";

                // Verifica correspondência do filtro
                boolean correspondeAoFiltro = tipoGeral.equals(filtroUpper) || dificuldade.equals(filtroUpper);
                
                if (correspondeAoFiltro) {
                    CustomCards novaCarta;

                    if ("PERGUNTA".equals(tipoGeral)) {
                        String tipoPergunta = obj.has("tipoPergunta") ? obj.get("tipoPergunta").getAsString().toUpperCase() : "";
                        String respostaCorreta = obj.has("respostaCorreta") ? obj.get("respostaCorreta").getAsString() : "";

                        if ("SIM_NAO".equals(tipoPergunta)) {
                            // Construtor: Pergunta Sim / Não
                            novaCarta = new CustomCards(id, tipoGeral, enunciado, efeito, valorEfeito, iconePeao, dificuldade, respostaCorreta);
                        } else {
                            // Construtor: Múltipla Escolha
                            String[] alternativas = new String[0];
                            if (obj.has("alternativas") && obj.get("alternativas").isJsonArray()) {
                                JsonArray arrayAlt = obj.getAsJsonArray("alternativas");
                                alternativas = new String[arrayAlt.size()];
                                for (int i = 0; i < arrayAlt.size(); i++) {
                                    alternativas[i] = arrayAlt.get(i).getAsString();
                                }
                            }
                            
                            novaCarta = new CustomCards(id, tipoGeral, enunciado, efeito, valorEfeito, iconePeao, dificuldade, alternativas, respostaCorreta);
                        }
                    } else {
                        // Construtor: Cartas Especiais (Sorte/Azar/etc.)
                        novaCarta = new CustomCards(id, tipoGeral, enunciado, efeito, valorEfeito, iconePeao);
                    }

                    cartasFiltradas.add(novaCarta);
                }
            }
        } catch (Exception e) {
            System.err.println("[CardManager] Erro crítico ao processar o JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return cartasFiltradas;
    }
}