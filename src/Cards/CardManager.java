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

    private static final String CAMINHO_JSON = "/assets/data/cardsContent.json";

    // Cache estático em memória para evitar releitura do disco em cada filtro
    private static JsonArray cachedJsonArray = null;

    /**
     * Carrega todas as cartas do arquivo JSON e filtra pela categoria ou tipo solicitado.
     * Utiliza cache em memória RAM após a primeira leitura.
     * 
     * @param filtro Categoria de filtragem (Ex: "FÁCIL", "MÉDIO", "DIFICIL", "SORTE", "AZAR")
     * @return Lista das cartas filtradas
     */
    public List<CustomCards> loadCard(String filtro) {
        List<CustomCards> cartasFiltradas = new ArrayList<>();
        
        if (filtro == null) {
            System.err.println("[CardManager] Aviso: O filtro informado é nulo.");
            return cartasFiltradas;
        }

        String filtroUpper = filtro.toUpperCase();

        // 1. Garante que o JSON está carregado em memória (Lazy Load com Cache)
        if (cachedJsonArray == null) {
            carregarJsonEmMemoria();
        }

        if (cachedJsonArray == null) {
            return cartasFiltradas; 
        }

        // 2. Filtra as cartas a partir do cache já parseado
        try {
            for (JsonElement elemento : cachedJsonArray) {
                JsonObject obj = elemento.getAsJsonObject();

                // Extrai apenas os campos universais
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
                            novaCarta = new CustomCards(id, tipoGeral, enunciado, efeito, valorEfeito, iconePeao, dificuldade, respostaCorreta);
                        } else {
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
                        novaCarta = new CustomCards(id, tipoGeral, enunciado, efeito, valorEfeito, iconePeao);
                    }

                    cartasFiltradas.add(novaCarta);
                }
            }
        } catch (Exception e) {
            System.err.println("[CardManager] Erro ao filtrar cartas do cache: " + e.getMessage());
            e.printStackTrace();
        }

        return cartasFiltradas;
    }

    /**
     * Carrega o JSON do disco apenas uma vez. Thread-safe.
     */
    private static synchronized void carregarJsonEmMemoria() {
        if (cachedJsonArray != null) return;

        var inputStream = CardManager.class.getResourceAsStream(CAMINHO_JSON);
        if (inputStream == null) {
            System.err.println("[CardManager] Erro: Arquivo JSON não encontrado em: " + CAMINHO_JSON);
            return; 
        }

        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            cachedJsonArray = gson.fromJson(reader, JsonArray.class);
            System.out.println("[CardManager] Arquivo de cartas carregado no cache com sucesso!");
        } catch (Exception e) {
            System.err.println("[CardManager] Erro crítico ao carregar JSON para a memória: " + e.getMessage());
            e.printStackTrace();
        }
    }
}