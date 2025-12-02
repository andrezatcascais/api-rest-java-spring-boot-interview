# 🧪 Guia de Testes de Integração 

Neste mundo dinâmico de desenvolvimento de software, garantir que os componentes individuais funcionem perfeitamente é apenas uma peça do quebra-cabeça. A verdadeira medida de uma aplicação robusta e confiável reside na forma como esses componentes se unem, interagem e funcionam como um todo coeso. É aqui que entra o teste de integração, desempenhando um papel fundamental na garantia da harmonia do seu sistema de software.

## 📊 Diferenças: Testes Unitários vs Testes de Integração

| Característica | [Testes Unitários](README-JUnitTests.md) | Testes de Integração |
|----------------|--------------------------|----------------------|
| **Escopo** | Testa componente isolado | Testa componentes integrados |
| **Dependências** | Usa mocks                | Usa componentes reais |
| **Banco de Dados** | Mockado                  | H2 em memória (real) |
| **Velocidade** | Ultra rápido (ms)        | Mais lento (segundos) |
| **Complexidade** | Baixa                    | Média/Alta |
| **Quantidade** | Muitos (70-80%)          | Menos (20-30%) |
| **Objetivo** | Lógica de negócio        | Integração entre camadas |

### **Configuração Separada para Testes**

```properties
# src/test/resources/application-test.properties

# Banco em memória (limpo a cada execução)
spring.datasource.url=jdbc:h2:mem:testdb

# DDL automático (cria/dropa tabelas)
spring.jpa.hibernate.ddl-auto=create-drop

# Logs detalhados para debugging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Desabilita banner (console mais limpo)
spring.main.banner-mode=off
```
## 🚀 Como Executar

### Via Maven
```bash
# Executar todos os testes de integração
mvn test -Dtest=*IntegrationTest

# Executar teste específico
mvn test -Dtest=UsuarioIntegrationTest

# Executar com relatório de cobertura
mvn clean test jacoco:report
```

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


## 📚 Recursos Adicionais

- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [TestContainers](https://www.testcontainers.org/) - Para testes com Docker
---

## ✅ Resumo

Testes de integração bem escritos:
- ✅ Testam integração real entre componentes
- ✅ Usam banco de dados real (H2 em memória)
- ✅ São isolados e independentes
- ✅ Têm nomenclatura clara e descritiva
- ✅ Verificam múltiplas camadas
- ✅ São organizados logicamente
- ✅ Têm boa cobertura de cenários
- ✅ São fáceis de manter e entender

**🎉 O teste de integração é um elemento crucial do processo de desenvolvimento de software. 
Ele garante que os componentes individuais de um sistema funcionem perfeitamente juntos, identificando e corrigindo problemas antes que cheguem à produção. 
Através da implementação dos testes de Integração nas camadas dessa aplicação, exploramos a importância para o fornecimento de software robusto, confiável e de alta qualidade.