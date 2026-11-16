# Assinô - Gerenciador de Assinaturas

Projeto Integrador (Sistemas de Informação, UNIFACISA). O Assinô ajuda o usuário a organizar suas assinaturas
(streaming, música, jogos, software...) e a supervisionar os gastos mensais.

## Funcionalidades

- Cadastro e login com senha criptografada (BCrypt) e autenticação JWT
- CRUD de assinaturas: nome, descrição, valor, dia de cobrança, categoria e status (ativa/inativa)
- Painel com gasto mensal, projeção anual, gráfico de gastos por categoria e próximas cobranças
- Filtros por categoria e status, busca por nome
- Cada usuário só acessa as próprias assinaturas
- Documentação da API no Swagger

## Tecnologias

| Camada    | Tecnologia                                         |
|-----------|----------------------------------------------------|
| Back-end  | Java 17, Spring Boot 3, Spring Security, JPA        |
| Banco     | PostgreSQL 16                                      |
| Front-end | React 18 + TypeScript (Vite), Recharts             |
| Testes    | JUnit 5, Mockito, MockMvc (banco H2 em memória)     |

## Estrutura

```
assino_spring/   API REST (controller -> service -> repository)
frontend/        Interface web
```

## Como rodar

Pré-requisitos: Java 17, Node 18+ e PostgreSQL (ou Docker). Não precisa instalar Maven: use o `mvnw`.

1. Banco de dados (com Docker):

   ```bash
   cd assino_spring
   docker compose up -d
   ```

   Sem Docker, crie um banco `assino_db` no PostgreSQL local (usuário/senha `postgres`/`postgres`, ou ajuste
   as variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD`).

2. API (porta 8080):

   ```bash
   cd assino_spring
   ./mvnw spring-boot:run
   ```

   No Windows (cmd/PowerShell) use `mvnw.cmd spring-boot:run`.
   Swagger: http://localhost:8080/swagger-ui.html

3. Front-end (porta 5173):

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

   Acesse http://localhost:5173 e crie uma conta.

## Testes

```bash
cd assino_spring
./mvnw test
```

Cobrem o cálculo do resumo de gastos, cadastro/login, validação de token, validação dos campos,
o CRUD completo pela API e o isolamento entre usuários.

## Endpoints principais

| Método | Rota                                  | Descrição                         |
|--------|---------------------------------------|-----------------------------------|
| POST   | `/api/auth/registrar`                 | Cria conta e devolve o token      |
| POST   | `/api/auth/login`                     | Login, devolve o token            |
| GET    | `/api/usuarios/perfil`                | Dados do usuário logado           |
| GET    | `/api/assinaturas`                    | Lista as assinaturas              |
| GET    | `/api/assinaturas/ativas`             | Lista só as ativas                |
| GET    | `/api/assinaturas/categoria/{cat}`    | Filtra por categoria              |
| GET    | `/api/assinaturas/resumo`             | Totais mensal, anual e por categoria |
| GET    | `/api/assinaturas/{id}`               | Busca uma assinatura              |
| POST   | `/api/assinaturas`                    | Cria                              |
| PUT    | `/api/assinaturas/{id}`               | Atualiza                          |
| DELETE | `/api/assinaturas/{id}`               | Remove                            |

Rotas de `/api/assinaturas` e `/api/usuarios` exigem o header `Authorization: Bearer <token>`.

## Equipe

Thiago Henriques Nunes, Kauan Selyson Dias Gonzaga, Luiz Felipe Azevedo de Brito, Icaro Vinicius Araujo de Almeida,
Miquéias Souza Regis, Douglas Rodrigues de Almeida. Orientador: Prof. Matheus Batista Silva.
