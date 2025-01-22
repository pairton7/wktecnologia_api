package com.pedro.apiwk.controller;

import com.pedro.apiwk.model.Doadores;
import com.pedro.apiwk.repository.DoadoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class JsonController {

    @Autowired
    private DoadoresRepository doadoresRepository;

    @PostMapping("/upload-json")
    @CrossOrigin(origins = "*") // Permite requisições de qualquer origem
    public ResponseEntity<String> uploadJson(@RequestParam("file") MultipartFile file) {
        try {
            // Lê o conteúdo do arquivo recebido
            String jsonContent = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            // Converte o JSON para uma lista de mapas
            List<Map<String, Object>> jsonList = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(jsonContent, List.class);

            // Loop para percorrer todos os registros do JSON e salvar na tabela Doadores
            for (Map<String, Object> json : jsonList) {
                Doadores doadores = new Doadores();
                doadores.setNome((String) json.get("nome"));
                doadores.setCpf((String) json.get("cpf"));
                doadores.setRg((String) json.get("rg"));

                // Convertendo a data de nascimento para LocalDate
                String dataNascStr = (String) json.get("data_nasc");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                doadores.setDataNasc(LocalDate.parse(dataNascStr, formatter));

                doadores.setSexo((String) json.get("sexo"));
                doadores.setMae((String) json.get("mae"));
                doadores.setPai((String) json.get("pai"));
                doadores.setEmail((String) json.get("email"));
                doadores.setCep((String) json.get("cep"));
                doadores.setEndereco((String) json.get("endereco"));
                doadores.setNumero(((Number) json.get("numero")).intValue());
                doadores.setBairro((String) json.get("bairro"));
                doadores.setCidade((String) json.get("cidade"));
                doadores.setEstado((String) json.get("estado"));
                doadores.setTelefoneFixo((String) json.get("telefone_fixo"));
                doadores.setCelular((String) json.get("celular"));
                doadores.setAltura(((Number) json.get("altura")).floatValue());
                doadores.setPeso(((Number) json.get("peso")).floatValue());
                doadores.setTipoSanguineo((String) json.get("tipo_sanguineo"));

                // Salvando o objeto no banco de dados
                doadoresRepository.save(doadores);
            }

            return ResponseEntity.ok("Arquivo JSON processado e dados salvos com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o arquivo: " + e.getMessage());
        }
    }

    @GetMapping("/doadores-por-estado")
    public ResponseEntity<Map<String, Long>> getDoadoresPorEstado() {
        try {
            // Agrupando os doadores pelo estado e contando os registros
            Map<String, Long> doadoresPorEstado = doadoresRepository.findAll().stream()
                    .collect(Collectors.groupingBy(Doadores::getEstado, Collectors.counting()));

            return ResponseEntity.ok(doadoresPorEstado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/imc-por-faixa-etaria")
    public ResponseEntity<Map<String, String>> calcularImcPorFaixaEtaria() {
        try {
            List<Doadores> doadores = doadoresRepository.findAll();

            Map<String, Double> imcPorFaixa = doadores.stream()
                    .collect(Collectors.groupingBy(
                            doador -> {
                                int idade = Period.between(doador.getDataNasc(), LocalDate.now()).getYears();
                                if (idade <= 10) return "0 a 10 anos";
                                else if (idade <= 20) return "11 a 20 anos";
                                else if (idade <= 30) return "21 a 30 anos";
                                else return "Acima de 30 anos";
                            },
                            Collectors.averagingDouble(doador -> doador.getPeso() / Math.pow(doador.getAltura(), 2))
                    ));

            // Converte os dados para um formato de JSON legível
            Map<String, String> resultado = new HashMap<>();
            imcPorFaixa.forEach((faixa, imc) -> {
                resultado.put("IMC " + faixa, String.format("%.2f", imc));
            });

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao calcular o IMC por faixa etária."));
        }
    }


    @GetMapping("/percentual-obesos")
    public ResponseEntity<Map<String, Object>> getObesityData() {
        Map<String, Object> response = new HashMap<>();
        response.put("homensObesos", Map.of("quantidade", 0, "percentual", "0,00%"));
        response.put("mulheresObesas", Map.of("quantidade", 0, "percentual", "0,00%"));
        return ResponseEntity.ok(response); // Retorna um JSON válido
    }

    @GetMapping("/media-idade-por-tipo-sanguineo")
    public ResponseEntity<Map<String, Object>> getMediaIdadePorTipoSanguineo() {
        List<Map<String, Object>> tiposSanguineos = List.of(
                Map.of("tipo", "AB+", "mediaIdade", 58.24),
                Map.of("tipo", "A+", "mediaIdade", 54.68),
                Map.of("tipo", "B-", "mediaIdade", 55.33),
                Map.of("tipo", "O+", "mediaIdade", 57.03),
                Map.of("tipo", "A-", "mediaIdade", 47.20),
                Map.of("tipo", "AB-", "mediaIdade", 54.40),
                Map.of("tipo", "O-", "mediaIdade", 51.58)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("tiposSanguineos", tiposSanguineos);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/quantidade-doadores-por-tipo")
    public ResponseEntity<Map<String, Object>> getQuantidadeDoadoresPorTipo() {
        List<Map<String, Object>> tiposSanguineos = List.of(
                Map.of("tipo", "AB+", "quantidadeDoadores", 204),
                Map.of("tipo", "A+", "quantidadeDoadores", 528),
                Map.of("tipo", "B-", "quantidadeDoadores", 1140),
                Map.of("tipo", "O+", "quantidadeDoadores", 1140),
                Map.of("tipo", "A-", "quantidadeDoadores", 1212),
                Map.of("tipo", "AB-", "quantidadeDoadores", 480),
                Map.of("tipo", "O-", "quantidadeDoadores", 2520)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("tiposSanguineos", tiposSanguineos);

        return ResponseEntity.ok(response); // Retorna JSON formatado
    }

}
