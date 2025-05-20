# 🚀 Point System Server (ps-server)
Backend do projeto Sistema de Ponto - "pontual.", desenvolvido durante o 3º semestre de Banco de Dados na Fatec São José dos Campos.

## 📋 Pré-requisitos

- Java JDK 20+;

## 🛠️ Configuração

### 1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/nome-do-projeto.git
   ```
### 2. Acesse o diretório
   ```bash
   cd point-system
   ```
## 🏗️ Build e Execução

   ```bash
 mvn clean install
 mvn spring-boot:run
   ```

## 🌐 Endpoints

### Acesse a docuentação da API para mais informações:
♦ [Documentação da API (Swagger)](/documentation/spring-docs/swagger-acess.md)  <br />

## 🚨 Troubleshooting

### Erro de porta em uso:
  ```bash
lsof -i :8080
kill -9 <PID>
   ```

### Problemas com dependências:
  ```bash
mvn clean install -U
   ```
