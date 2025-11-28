# 🚀 API REST - Gerenciamento de Usuários

API REST desenvolvida em Java com Spring Boot para gerenciamento completo de usuários (CRUD), com validações, paginação e banco de dados H2.

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Exemplos de Uso](#exemplos-de-uso)
- [Validações](#validações)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Testes](#testes)
- [Console H2](#console-h2)
- [Autor](#autor)

## Sobre o Projeto

Este projeto implementa uma API RESTful para gerenciamento de usuários, desenvolvida como solução para o Desafio 1 - Construção de API. A aplicação permite criar, listar, atualizar e deletar usuários, seguindo as melhores práticas REST e incluindo validações robustas.
<br>[Índice](#índice)  </br>

## Funcionalidades 

- ✅ **CRUD Completo de Usuários**
    - Criar novo usuário
    - Listar todos os usuários (com paginação e ordenação)
    - Buscar usuário por ID
    - Atualizar informações do usuário
    - Deletar usuário

- ✅ **Validações**
    - Nome obrigatório
    - Email obrigatório e único
    - Formato de email válido
    - Tratamento de erros personalizado

- ✅ **Recursos Adicionais**
    - Paginação e ordenação de resultados
    - Data de criação automática
    - Banco de dados em memória (H2)
    - Console H2 para visualização dos dados
      <br>[Índice](#índice)  </br>
  
## Tecnologias

- **Java 17**
- **Spring Boot 3.2.0**
    - Spring Web
    - Spring Data JPA
    - Spring Validation
- **H2 Database** (em memória)
- **Lombok** (redução de código boilerplate)
- **Maven** (gerenciamento de dependências)
  <br>[Índice](#índice)  </br>

## Pré-requisitos

Antes de começar, você precisa ter instalado:

- [Java JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [Maven 3.6+](https://maven.apache.org/download.cgi)
- Uma IDE (IntelliJ IDEA, Eclipse, VS Code)
- [Postman](https://www.postman.com/downloads/) ou [Insomnia](https://insomnia.rest/download) (opcional, para testes)

<br>[Índice](#índice)  </br>

## Instalação

1. **Clone o repositório**
```bash
git clone https://github.com/andrezatcascais/api-rest-java-spring-boot-interview.git
cd api-rest-java-spring-boot-interview
```

2. **Compile o projeto**
```bash
mvn clean install
```

## Como Executar

### Opção 1: Via Maven
```bash
mvn spring-boot:run
```

### Opção 2: Via JAR
```bash
mvn clean package
java -jar target/user-api-1.0.0.jar
```

### Opção 3: Via IDE
- Abra o projeto na sua IDE
- Execute a classe `UserApiApplication.java`

A aplicação estará disponível em: **http://localhost:8080**

## Endpoints da API

### Base URL
```
http://localhost:8080/api/usuarios
```

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/usuarios` | Lista todos os usuários (com paginação) |
| GET | `/api/usuarios/{id}` | Busca usuário por ID |
| POST | `/api/usuarios` | Cria novo usuário |
| PUT | `/api/usuarios/{id}` | Atualiza usuário existente |
| DELETE | `/api/usuarios/{id}` | Deleta usuário |

### Parâmetros de Paginação

| Parâmetro | Tipo | Padrão | Descrição |
|-----------|------|--------|-----------|
| page | int | 0 | Número da página |
| size | int | 10 | Itens por página |
| sort | string | id,asc | Campo e direção da ordenação |

**Exemplo:**
```
GET /api/usuarios?page=0&size=5&sort=nome,desc
```
<br>[Índice](#índice)  </br>

## Exemplos de Uso

### 1️⃣ Criar Usuário

**Request:**
```bash
POST /api/usuarios
Content-Type: application/json

{
  "nome": "Maria Silva",
  "email": "maria@email.com"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "dataCriacao": "2025-11-28T10:30:00"
}
```

### 2️⃣ Listar Usuários

**Request:**
```bash
GET /api/usuarios?page=0&size=10
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "nome": "Maria Silva",
      "email": "maria@email.com",
      "dataCriacao": "2025-11-28T10:30:00"
    },
    {
      "id": 2,
      "nome": "João Santos",
      "email": "joao@email.com",
      "dataCriacao": "2025-11-28T11:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 2,
  "totalPages": 1
}
```

### 3️⃣ Buscar por ID

**Request:**
```bash
GET /api/usuarios/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "dataCriacao": "2025-11-28T10:30:00"
}
```

### 4️⃣ Atualizar Usuário

**Request:**
```bash
PUT /api/usuarios/1
Content-Type: application/json

{
  "nome": "Maria Santos",
  "email": "maria.santos@email.com"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nome": "Maria Santos",
  "email": "maria.santos@email.com",
  "dataCriacao": "2025-11-28T10:30:00"
}
```

### 5️⃣ Deletar Usuário

**Request:**
```bash
DELETE /api/usuarios/1
```

**Response (204 No Content):**
```
(sem conteúdo no corpo da resposta)
```

## Validações

A API implementa as seguintes validações:

### Regras de Negócio

| Campo | Validação |
|-------|-----------|
| nome | Obrigatório, não pode ser vazio |
| email | Obrigatório, formato válido, único no sistema |
| dataCriacao | Preenchido automaticamente pelo sistema |

### Exemplos de Erros

**❌ Nome não informado (400 Bad Request):**
```json
{
  "nome": "Nome é obrigatório"
}
```

**❌ Email duplicado (400 Bad Request):**
```json
{
  "timestamp": "2025-11-28T10:30:00",
  "status": 400,
  "message": "Email já cadastrado: maria@email.com"
}
```

**❌ Usuário não encontrado (404 Not Found):**
```json
{
  "timestamp": "2025-11-28T10:30:00",
  "status": 404,
  "message": "Usuário não encontrado com ID: 999"
}
```

## Estrutura do Projeto

```
src/main/java/dev/andie/userapi/
├── controller/
│   └── UsuarioController.java       # Endpoints REST
├── dto/
│   └── UsuarioDTO.java              # Data Transfer Object
├── exception/
│   ├── GlobalExceptionHandler.java  # Tratamento global de erros
│   ├── ResourceNotFoundException.java
│   └── ValidationException.java
├── model/
│   └── Usuario.java                 # Entidade JPA
├── repository/
│   └── UsuarioRepository.java       # Interface de acesso ao BD
├── service/
│   └── UsuarioService.java          # Lógica de negócio
└── UserApiApplication.java          # Classe principal

src/main/resources/
└── application.properties           # Configurações da aplicação
```
<br>[Índice](#índice)  </br>

## Testes

### Testar com cURL

```bash
# Criar usuário
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome": "João Silva", "email": "joao@email.com"}'

# Listar todos
curl -X GET http://localhost:8080/api/usuarios

# Buscar por ID
curl -X GET http://localhost:8080/api/usuarios/1

# Atualizar
curl -X PUT http://localhost:8080/api/usuarios/1 \
  -H "Content-Type: application/json" \
  -d '{"nome": "João Santos", "email": "joao.santos@email.com"}'

# Deletar
curl -X DELETE http://localhost:8080/api/usuarios/1
```

### Testar com Postman

1. Importe a collection disponível em `/docs/postman_collection.json`
2. Configure a variável de ambiente `base_url` para `http://localhost:8080`
3. Execute os testes na ordem sugerida

### Script de Teste Completo

Execute este script para testar todo o CRUD:

```bash
# 1. Criar 3 usuários
curl -X POST http://localhost:8080/api/usuarios -H "Content-Type: application/json" -d '{"nome": "Ana Costa", "email": "ana@email.com"}'
curl -X POST http://localhost:8080/api/usuarios -H "Content-Type: application/json" -d '{"nome": "Bruno Lima", "email": "bruno@email.com"}'
curl -X POST http://localhost:8080/api/usuarios -H "Content-Type: application/json" -d '{"nome": "Carla Dias", "email": "carla@email.com"}'

# 2. Listar todos
curl -X GET http://localhost:8080/api/usuarios

# 3. Buscar específico
curl -X GET http://localhost:8080/api/usuarios/2

# 4. Atualizar
curl -X PUT http://localhost:8080/api/usuarios/2 -H "Content-Type: application/json" -d '{"nome": "Bruno Silva", "email": "bruno.silva@email.com"}'

# 5. Deletar
curl -X DELETE http://localhost:8080/api/usuarios/3

# 6. Listar novamente
curl -X GET http://localhost:8080/api/usuarios
```
<br>[Índice](#índice)  </br>

## Console H2

Acesse o console do banco H2 em memória:

**URL:** http://localhost:8080/h2-console

**Credenciais:**
- JDBC URL: `jdbc:h2:mem:userdb`
- User Name: `sa`
- Password: _(deixe vazio)_

**Queries úteis:**
```sql
-- Ver todos os usuários
SELECT * FROM usuarios;

-- Contar usuários
SELECT COUNT(*) FROM usuarios;

-- Buscar por email
SELECT * FROM usuarios WHERE email LIKE '%@email.com%';

-- Ordenar por data de criação
SELECT * FROM usuarios ORDER BY data_criacao DESC;
```
<br>[Índice](#índice)  </br>

## Boas Práticas Implementadas

- ✅ Arquitetura em camadas (Controller, Service, Repository)
- ✅ Separação de responsabilidades
- ✅ DTOs para transferência de dados
- ✅ Validações com Bean Validation
- ✅ Tratamento global de exceções
- ✅ Códigos HTTP apropriados
- ✅ Nomenclatura RESTful
- ✅ Paginação e ordenação
- ✅ Uso de Lombok para código limpo

## Próximos Passos

Melhorias que podem ser implementadas:

- [ ] Adicionar testes unitários (JUnit + Mockito)
- [ ] Implementar testes de integração
- [ ] Adicionar documentação com Swagger/OpenAPI
- [ ] Implementar autenticação e autorização (Spring Security)
- [ ] Migrar para banco de dados persistente (PostgreSQL/MySQL)
- [ ] Adicionar cache com Redis
- [ ] Implementar logs estruturados
- [ ] Adicionar Docker e Docker Compose
- [ ] CI/CD com GitHub Actions
<br>[Índice](#índice)  </br>

##  Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

##  Autor

**Seu Nome**

- GitHub: [@andrezatcascais](https://github.com/andrezatcascais/api-rest-java-spring-boot-interview)
- LinkedIn: [andrezatellescascais](https://www.linkedin.com/in/andrezatellescascais/)
- Email: infocastell@gmail.com

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para:

1. Fazer um fork do projeto
2. Criar uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abrir um Pull Request

## 💬 Suporte

Se você tiver alguma dúvida ou problema, por favor abra uma [issue](https://github.com/andrezatcascais/api-rest-java-spring-boot-interview/blob/main/issue).

---
