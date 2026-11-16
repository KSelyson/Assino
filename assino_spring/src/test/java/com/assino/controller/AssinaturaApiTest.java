package com.assino.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Fluxo completo pela API: cadastro, CRUD, resumo e isolamento entre usuários. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AssinaturaApiTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper json;

    private String registrar() throws Exception {
        String body = """
                {"nome":"Teste","email":"%s@email.com","senha":"segredo123"}""".formatted(UUID.randomUUID());
        String resp = mvc.perform(post("/api/auth/registrar").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(resp).get("token").asText();
    }

    @Test
    void semTokenRetorna401() throws Exception {
        mvc.perform(get("/api/assinaturas")).andExpect(status().isUnauthorized());
    }

    @Test
    void crudEResumo() throws Exception {
        String token = registrar();
        String netflix = """
                {"nome":"Netflix","valor":39.90,"dataCobranca":5,"categoria":"STREAMING"}""";

        String criada = mvc.perform(post("/api/assinaturas").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON).content(netflix))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ativa").value(true))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(criada).get("id").asLong();

        mvc.perform(post("/api/assinaturas").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Spotify","valor":21.90,"dataCobranca":12,"categoria":"MUSICA"}"""))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/assinaturas/resumo").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMensal").value(61.80))
                .andExpect(jsonPath("$.quantidadeAssinaturas").value(2));

        mvc.perform(put("/api/assinaturas/" + id).header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Netflix","valor":39.90,"dataCobranca":5,"categoria":"STREAMING","ativa":false}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa").value(false));

        mvc.perform(get("/api/assinaturas/ativas").header("Authorization", token))
                .andExpect(jsonPath("$.length()").value(1));

        mvc.perform(delete("/api/assinaturas/" + id).header("Authorization", token))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/assinaturas/" + id).header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void validacaoRetorna400ComCampos() throws Exception {
        String token = registrar();
        mvc.perform(post("/api/assinaturas").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"","valor":-1,"dataCobranca":40}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.valor").exists())
                .andExpect(jsonPath("$.campos.dataCobranca").exists())
                .andExpect(jsonPath("$.campos.categoria").exists());
    }

    @Test
    void usuarioNaoVeAssinaturaDeOutro() throws Exception {
        String ana = registrar();
        String bruno = registrar();
        String criada = mvc.perform(post("/api/assinaturas").header("Authorization", ana)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"nome":"Max","valor":29.90,"dataCobranca":1,"categoria":"STREAMING"}"""))
                .andReturn().getResponse().getContentAsString();
        JsonNode node = json.readTree(criada);

        mvc.perform(get("/api/assinaturas/" + node.get("id").asLong()).header("Authorization", bruno))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/assinaturas").header("Authorization", bruno))
                .andExpect(jsonPath("$.length()").value(0));
    }
}
