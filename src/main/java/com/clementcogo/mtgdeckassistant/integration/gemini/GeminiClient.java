package com.clementcogo.mtgdeckassistant.integration.gemini;

import com.clementcogo.mtgdeckassistant.entities.DeckSlot;
import com.clementcogo.mtgdeckassistant.exception.GeminiException;
import com.clementcogo.mtgdeckassistant.exception.NotFoundException;
import com.clementcogo.mtgdeckassistant.integration.gemini.model.GeminiRawResponse;
import com.clementcogo.mtgdeckassistant.integration.gemini.model.ScryfallQuerySuggestions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Client d’intégration Gemini (appel API “bas niveau”).
 *
 * Rôle :
 * - Construire le prompt (instructions + contexte du commandant).
 * - Appeler le modèle Gemini via la lib google-genai.
 * - Retourner la sortie brute (texte) ou une structure parsée ensuite.
 */

@Service
public class GeminiClient {

    private final String model;

    private final ObjectMapper objectMapper;

    private final String promptTemplate = "Tu es un générateur de requêtes Scryfall pour EDH.\n" +
            "\n" +
            "Règles STRICTES :\n" +
            "- Réponds UNIQUEMENT en JSON valide (pas de backticks, pas de texte).\n" +
            "La réponse doit commencer par { et se terminer par } \n" +
            "Aucun texte avant/après \n" +
            "- La réponse DOIT respecter exactement ce schéma :\n" +
            "{\n" +
            "  \"queries\": [\n" +
            "    { \"title\": string, \"reason\": string, \"rawQuery\": string, \"order\": \"edhrec\" },\n" +
            "    ... (exactement 5 objets)\n" +
            "  ]\n" +
            "}\n" +
            "- \"rawQuery\" doit être une requête Scryfall brute (pas une URL), compatible avec /cards/search?q=...\n" +
            "- Toutes les requêtes doivent être pertinentes pour Commander.\n" +
            "\n" +
            "Commandant :\n" +
            "Nom: %s \n" +
            "Oracle Texte: %s\n" +
            "Type: %s\n" +
            "CMC: %s\n" +
            "Identité couleur: %s\n" +
            "\n" +
            "Génère 5 requêtes Scryfall différentes et complémentaires.";

    // The client gets the API key from the environment variable `GEMINI_API_KEY`.
    private final Client client;

    public GeminiClient(ObjectMapper objectMapper,@Value("${gemini.model}") String model,@Value("${gemini.apiKey}") String apiKey) {
        this.objectMapper = objectMapper;
        this.client = Client.builder().apiKey(apiKey).build();
        this.model = model;
    }

    public ScryfallQuerySuggestions getSuggestions(String commander,String typeLine,String cmc,String colorIdentity,String oracle) {

        String prompt = String.format(promptTemplate,commander,oracle,typeLine,cmc,colorIdentity);

        GenerateContentResponse response =
                client.models.generateContent(
                        model,
                        prompt,
                        null);

        System.out.println("Réponse brute :" + response.text());

        String cleanResponse = response.text();

        if(cleanResponse != null ) {
            cleanResponse = cleanResponse.trim();

            if(cleanResponse.isBlank()) {
                throw new GeminiException("Gemini returned an empty response");
            }

            // Retirer les fences
            if(cleanResponse.startsWith("```")) {
                int firstLine = cleanResponse.indexOf("\n");
                if (firstLine != -1){
                    cleanResponse = cleanResponse.substring(firstLine +1);
                }
                int lastFence = cleanResponse.lastIndexOf("```");
                if (lastFence != -1){
                    cleanResponse = cleanResponse.substring(0, lastFence);
                }
            }

            //retirer le texte avant ou après le JSON
            int start = cleanResponse.indexOf("{");
            int end = cleanResponse.lastIndexOf("}");
            if (start != -1 && end != -1 && end > start) {
                cleanResponse = cleanResponse.substring(start, end + 1).trim();
            }
            else {
                throw new GeminiException("Gemini response does not contain a JSON object");
            }
        }else {
            throw new GeminiException("Gemini returned an empty response");
        }

        ScryfallQuerySuggestions result;

        try {
            result = objectMapper.readValue(cleanResponse,ScryfallQuerySuggestions.class); //response.text();
        } catch (JsonProcessingException e) {
            throw new GeminiException("Gemini returned invalid JSON :" + e.getMessage());
        }

        System.out.println("Résultat parsed :" + result.toString());

        return result;
    }
}
