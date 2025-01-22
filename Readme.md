# APIWK

## OBJETIVO DA APLICAÇÃO
Atender ao teste de conhecimento da empresa WK Tecnologia.

## O QUE FAZ A API
A API é responsável por processar um arquivo JSON enviado pelo front-end e retornar informações de acordo com as seguintes regras de negócio:

1. **Quantos candidatos existem na lista em cada estado do Brasil?**
2. **IMC médio em cada faixa de idade de 10 em 10 anos:**
    - Faixas: 0 a 10, 11 a 20, 21 a 30, etc.
    - Fórmula: IMC = peso / altura².
3. **Percentual de obesos entre homens e mulheres:**
    - Considera-se obeso quem possui IMC > 30.
4. **Média de idade para cada tipo sanguíneo.**
5. **Quantidade de possíveis doadores para cada tipo sanguíneo receptor.**

---

## FRAMEWORK E BIBLIOTECAS PRINCIPAIS

### **SPRING BOOT**
- **Versão:** 3.4.1
- Utilizado como base do projeto, gerenciado pelo `spring-boot-starter-parent`.

### **SPRING BOOT STARTERS**
- **spring-boot-starter-data-jpa:** Suporte ao JPA (Java Persistence API).
- **spring-boot-starter-data-jdbc:** Suporte para operações JDBC.
- **spring-boot-starter-web:** Para criar APIs RESTful e aplicativos web.
- **spring-boot-starter-tomcat:** Contêiner de servlet embutido.
- **spring-boot-devtools:** Ferramenta para facilitar o desenvolvimento.
- **spring-boot-starter-test:** Suporte para testes (escopo de teste).

---

## DEPENDÊNCIAS ESPECÍFICAS
- **Jakarta Persistence API (JPA):**
    - **Versão:** 3.1.0
    - Biblioteca para mapeamento objeto-relacional (ORM).

- **Jakarta Servlet API:**
    - **Versão:** 5.0.0
    - Fornece suporte ao desenvolvimento de servlets Java.

- **MySQL Connector:**
    - Sem versão especificada (controlado pela versão do Spring Boot).
    - Utilizado para conexão com bancos de dados MySQL.

---

## AMBIENTE DE BUILD

- **Maven Compiler Plugin:**
    - **Versão:** 3.11.0
    - Configurado para usar Java 17 como `source` e `target`.

- **Spring Boot Maven Plugin:**
    - Versão gerenciada pelo Spring Boot **3.4.1**.
    - Utilizado para empacotar a aplicação e facilitar a execução.

---

## LINGUAGEM E PLATAFORMA
- **Java:**
    - **Versão:** 17
    - Configurado como a linguagem utilizada no projeto.

---

## EMPACOTAMENTO
- **WAR (Web Application Archive):**
    - O projeto é empacotado como um arquivo WAR para ser implantado em servidores de aplicação.

---

## SCRIPT PARA CRIAÇÃO DA TABELA `DOADORES` NO MYSQL
```sql
CREATE TABLE `doadores` (
  `id` int NOT NULL AUTO_INCREMENT,
  `rg` varchar(255) DEFAULT NULL,
  `cpf` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `data_nasc` date DEFAULT NULL,
  `sexo` varchar(255) DEFAULT NULL,
  `mae` varchar(255) DEFAULT NULL,
  `pai` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `cep` varchar(255) DEFAULT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `numero` int DEFAULT NULL,
  `bairro` varchar(255) DEFAULT NULL,
  `cidade` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `telefone_fixo` varchar(255) DEFAULT NULL,
  `celular` varchar(255) DEFAULT NULL,
  `altura` float DEFAULT NULL,
  `peso` float DEFAULT NULL,
  `tipo_sanguineo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
