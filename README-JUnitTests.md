# 🧪 Testes Unitários - JUnit 5 + Mockito

JUnit, uma estrutura de teste de código aberto, tem sido a base para testes de software em Java há mais de duas décadas. Ele fornece uma maneira estruturada de escrever e executar testes repetíveis, permitindo que desenvolvedores e testadores detectem e corrijam bugs no início do ciclo de desenvolvimento. Ao isolar e testar unidades individuais de código, o JUnit ajuda a garantir que cada parte do aplicativo se comporte corretamente, promovendo maior confiança nas alterações de código e nos esforços de refatoração.
Mockito é um framework de mocking para Java. Ele é usado para simular as dependências da classe que você está testando.

## 📊 Diferenças: Testes Unitários vs Testes de Integração

| Característica | Testes Unitários | [Testes de Integração](README-IntegrationTests.md)   |
|----------------|------------------|------------------------------|
| **Escopo** | Testa componente isolado | Testa componentes integrados |
| **Dependências** | Usa mocks | Usa componentes reais        |
| **Banco de Dados** | Mockado | H2 em memória (real)         |
| **Velocidade** | Ultra rápido (ms) | Mais lento (segundos)        |
| **Complexidade** | Baixa | Média/Alta                   |
| **Quantidade** | Muitos (70-80%) | Menos (20-30%)               |
| **Objetivo** | Lógica de negócio | Integração entre camadas     |

## 📊 Estrutura de Diretórios

```
src/
├── main/
│   └── java/
│       └── com/desafio/userapi/
│           ├── controller/
│           ├── service/
│           ├── repository/
│           └── model/
└── test/
    ├── java/
    │   └── com/desafio/userapi/
    │       ├── integration/              # Testes de Integração
    │       │   ├── BaseIntegrationTest.java
    │       │   └── UsuarioIntegrationTest.java
    │       ├── service/                  # Testes Unitários
    │       │   └── UsuarioServiceImplTest.java
    │       └── controller/               # Testes Unitários
    │           └── UsuarioControllerTest.java
    └── resources/
        └── application-test.properties   # Config de testes
```


## 📊 Cobertura dos Testes

### ✅ Testes do Service (22 testes)
- **Listar**: 2 testes
- **Buscar por ID**: 2 testes
- **Criar**: 6 testes
- **Atualizar**: 6 testes
- **Deletar**: 2 testes

### ✅ Testes do Controller (14 testes)
- **GET /api/usuarios**: 3 testes
- **GET /api/usuarios/{id}**: 2 testes
- **POST /api/usuarios**: 4 testes
- **PUT /api/usuarios/{id}**: 2 testes
- **DELETE /api/usuarios/{id}**: 2 testes

**Total: 36 testes unitários** 🎯

## 🚀 Como Executar os Testes

### 1. Via Maven (Terminal)

#### Executar todos os testes
```bash
mvn test
```

#### Executar testes com relatório detalhado
```bash
mvn test -Dtest=UsuarioServiceImplTest
mvn test -Dtest=UsuarioControllerTest
```

#### Executar todos os testes e gerar relatório
```bash
mvn clean test
```

#### Executar teste específico
```bash
mvn test -Dtest=UsuarioServiceImplTest#deveCriarUsuarioComSucesso
```

## 📈 Relatório de Cobertura

### JaCoCo (Recomendado)

Adicione no `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

**Gerar relatório:**
```bash
mvn clean test jacoco:report
```

**Visualizar relatório:**
Abra: `target/site/jacoco/index.html`

UsuarioControllerTest: [UsuarioControllerTest](TestResults_UsuarioControllerTest.html)
UsuarioServiceImplTest: [UsuarioServiceImplTest](TestResults_UsuarioServiceImplTest.html)


### 8. **Testes Isolados**
- Cada teste é independente
- Uso de mocks para dependências
- Sem dependência de banco real

## 📋 Checklist de Testes

### ✅ Service Layer
- [x] Listar todos os usuários
- [x] Listar com página vazia
- [x] Buscar por ID existente
- [x] Buscar por ID inexistente
- [x] Criar usuário válido
- [x] Criar com email duplicado
- [x] Criar sem nome
- [x] Criar sem email
- [x] Criar com nome vazio
- [x] Criar com email vazio
- [x] Atualizar usuário válido
- [x] Atualizar mantendo email
- [x] Atualizar com email duplicado
- [x] Atualizar usuário inexistente
- [x] Atualizar sem nome
- [x] Atualizar sem email
- [x] Deletar usuário existente
- [x] Deletar usuário inexistente

### ✅ Controller Layer
- [x] GET lista de usuários (200)
- [x] GET lista vazia (200)
- [x] GET com paginação
- [x] GET por ID (200)
- [x] GET por ID inexistente (404)
- [x] POST criar usuário (201)
- [x] POST com email duplicado (400)
- [x] POST sem nome (400)
- [x] POST com email inválido (400)
- [x] PUT atualizar usuário (200)
- [x] PUT usuário inexistente (404)
- [x] DELETE usuário (204)
- [x] DELETE usuário inexistente (404)

## 🔍 Comandos Úteis

### Executar testes específicos
```bash
# Por classe
mvn test -Dtest=UsuarioServiceImplTest

# Por método
mvn test -Dtest=UsuarioServiceImplTest#deveCriarUsuarioComSucesso

# Por padrão
mvn test -Dtest=*ServiceImplTest
mvn test -Dtest=*ControllerTest
```

### Executar com verbosidade
```bash
mvn test -X
```

### Pular testes (build rápido)
```bash
mvn clean install -DskipTests
```

### Executar apenas testes falhados
```bash
mvn test -Dsurefire.rerunFailingTestsCount=2
```
## 🐛 Debugging de Testes

### 1. Habilitar logs
```properties
# application-test.properties
logging.level.com.desafio.userapi=DEBUG
```

## 📚 Recursos Adicionais

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Spring Test Documentation](https://docs.spring.io/spring-framework/reference/testing.html)

## ✅ Resultado Esperado

Ao executar `mvn test`, você deve ver:

```
✅ 36 testes passando
✅ 0 falhas
✅ Cobertura > 90%
✅ Tempo de execução < 5 segundos
```

---
** A API REST agora tem testes unitários seguindo as melhores práticas da indústria!**