package com.assino.controller;

import com.assino.dto.Dtos.AssinaturaRequest;
import com.assino.dto.Dtos.AssinaturaResponse;
import com.assino.model.CategoriaAssinatura;
import com.assino.model.ResumoGastos;
import com.assino.service.AssinaturaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * CRUD de assinaturas do usuário autenticado.
 */
@RestController
@RequestMapping("/api/assinaturas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AssinaturaController {

    private final AssinaturaService service;

    @GetMapping
    public List<AssinaturaResponse> listar(Principal p) {
        return service.listar(p.getName());
    }

    @GetMapping("/ativas")
    public List<AssinaturaResponse> ativas(Principal p) {
        return service.listarAtivas(p.getName());
    }

    @GetMapping("/categoria/{categoria}")
    public List<AssinaturaResponse> porCategoria(@PathVariable CategoriaAssinatura categoria, Principal p) {
        return service.listarPorCategoria(p.getName(), categoria);
    }

    @GetMapping("/resumo")
    public ResumoGastos resumo(Principal p) {
        return service.resumo(p.getName());
    }

    @GetMapping("/{id}")
    public AssinaturaResponse buscar(@PathVariable Long id, Principal p) {
        return service.buscar(id, p.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssinaturaResponse criar(@Valid @RequestBody AssinaturaRequest req, Principal p) {
        return service.criar(req, p.getName());
    }

    @PutMapping("/{id}")
    public AssinaturaResponse atualizar(@PathVariable Long id, @Valid @RequestBody AssinaturaRequest req, Principal p) {
        return service.atualizar(id, req, p.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id, Principal p) {
        service.deletar(id, p.getName());
    }
}
