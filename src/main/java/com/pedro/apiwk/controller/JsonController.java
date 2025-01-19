package com.pedro.apiwk.controller;

import com.pedro.apiwk.model.Doadores;
import com.pedro.apiwk.repository.DoadoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class JsonController {

    @Autowired
    private DoadoresRepository doadoresRepository;

    @PostMapping("/upload-json")
    public ResponseEntity<String> uploadJson(@RequestBody List<Map<String, Object>> jsonList) {
        try {
            for (Map<String, Object> json : jsonList) {
                Doadores doadores = new Doadores();
                doadores.setNome((String) json.get("nome"));
                doadores.setCpf((String) json.get("cpf"));
                doadores.setRg((String) json.get("rg"));

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

                doadoresRepository.save(doadores);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("Todos os doadores foram salvos com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o JSON.");
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
    public ResponseEntity<String> calcularImcPorFaixaEtaria() {
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

            StringBuilder resultado = new StringBuilder();
            imcPorFaixa.forEach((faixa, imc) ->
                    resultado.append("IMC ").append(faixa).append(" = ").append(String.format("%.2f", imc)).append("\n")
            );

            return ResponseEntity.ok(resultado.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao calcular o IMC por faixa etária.");
        }
    }

    @GetMapping("/percentual-obesos")
    public ResponseEntity<String> calcularPercentualObesos() {
        try {
            List<Doadores> doadores = doadoresRepository.findAll();

            long totalHomens = doadores.stream().filter(d -> "M".equalsIgnoreCase(d.getSexo())).count();
            long homensObesos = doadores.stream()
                    .filter(d -> "M".equalsIgnoreCase(d.getSexo()) && (d.getPeso() / Math.pow(d.getAltura(), 2)) > 30)
                    .count();

            long totalMulheres = doadores.stream().filter(d -> "F".equalsIgnoreCase(d.getSexo())).count();
            long mulheresObesas = doadores.stream()
                    .filter(d -> "F".equalsIgnoreCase(d.getSexo()) && (d.getPeso() / Math.pow(d.getAltura(), 2)) > 30)
                    .count();

            double percentualHomensObesos = totalHomens > 0 ? (homensObesos * 100.0 / totalHomens) : 0;
            double percentualMulheresObesas = totalMulheres > 0 ? (mulheresObesas * 100.0 / totalMulheres) : 0;

            String resultado = String.format(
                    "Quantidade de homens obesos: %d (%.2f%%)\nQuantidade de mulheres obesas: %d (%.2f%%)",
                    homensObesos, percentualHomensObesos,
                    mulheresObesas, percentualMulheresObesas
            );

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao calcular o percentual de obesos.");
        }
    }


    @GetMapping("/media-idade-por-tipo-sanguineo")
    public ResponseEntity<String> calcularMediaIdadePorTipoSanguineo() {
        try {
            List<Doadores> doadores = doadoresRepository.findAll();

            Map<String, Double> mediaIdadePorTipo = doadores.stream()
                    .collect(Collectors.groupingBy(
                            Doadores::getTipoSanguineo,
                            Collectors.averagingDouble(doador ->
                                    Period.between(doador.getDataNasc(), LocalDate.now()).getYears())
                    ));

            StringBuilder resultado = new StringBuilder();
            mediaIdadePorTipo.forEach((tipo, mediaIdade) ->
                    resultado.append("Tipo Sanguíneo: ").append(tipo)
                            .append(" - Média de Idade: ").append(String.format("%.2f", mediaIdade)).append("\n")
            );

            return ResponseEntity.ok(resultado.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao calcular a média de idade por tipo sanguíneo.");
        }
    }

    @GetMapping("/quantidade-doadores-por-tipo")
    public ResponseEntity<String> calcularQuantidadeDoadoresPorTipo() {
        try {
            List<Doadores> doadores = doadoresRepository.findAll();

            // Mapeamento de compatibilidade de doação baseado no print
            Map<String, List<String>> compatibilidade = Map.of(
                    "A+", List.of("A+", "AB+"),
                    "A-", List.of("A+", "A-", "AB+", "AB-"),
                    "B+", List.of("B+", "AB+"),
                    "B-", List.of("B+", "B-", "AB+", "AB-"),
                    "AB+", List.of("AB+"),
                    "AB-", List.of("AB+", "AB-"),
                    "O+", List.of("A+", "B+", "O+", "AB+"),
                    "O-", List.of("A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-")
            );

            Map<String, Long> quantidadeDoadoresPorTipo = compatibilidade.keySet().stream()
                    .collect(Collectors.toMap(
                            tipoReceptor -> tipoReceptor,
                            tipoReceptor -> doadores.stream()
                                    .filter(doador -> {
                                        // Verifica compatibilidade
                                        boolean ehCompativel = compatibilidade.get(tipoReceptor).contains(doador.getTipoSanguineo());

                                        // Calcula idade do doador
                                        int idade = Period.between(doador.getDataNasc(), LocalDate.now()).getYears();

                                        // Verifica se o doador está apto
                                        return ehCompativel && idade >= 16 && idade <= 69 && doador.getPeso() > 50;
                                    })
                                    .count()
                    ));

            StringBuilder resultado = new StringBuilder();
            quantidadeDoadoresPorTipo.forEach((tipo, quantidade) ->
                    resultado.append("Tipo Sanguíneo: ").append(tipo)
                            .append(" - Quantidade de Doadores: ").append(quantidade).append("\n")
            );

            return ResponseEntity.ok(resultado.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao calcular a quantidade de doadores por tipo sanguíneo.");
        }
    }

}
