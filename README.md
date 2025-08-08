# 🏥 Sistema de Gerenciamento de Pacientes e Exames (Java Swing + MySQL)

<p>
Este é um projeto de um sistema de desktop para gerenciar pacientes e seus exames médicos. A aplicação, desenvolvida em Java, utiliza a biblioteca Swing para criar uma interface gráfica e o MySQL para a persistência dos dados. O projeto foi concebido como um exercício acadêmico para aplicar e reforçar conceitos de programação orientada a objetos (POO), desenvolvimento de interfaces gráficas (GUI), integração com bancos de dados relacionais e o uso do padrão de projeto DAO (Data Access Object).
</div>

<br>

<center>
  <img width="785" height="583" alt="image" src="https://github.com/user-attachments/assets/c602fae4-4582-4718-a36d-fea52ae78b9c" />
</center>

# 🛞 Execução do projeto
<h2>
  É possivel realizar a execução do projeto clicando no clinic.jar ou pelo terminal utilizando o seguinte comando:
</h2>

  ```
  java -jar clinic.jar    # ou clinic-17.jar
  ```

# 🎯 Objetivo
O principal objetivo é fornecer um ambiente de aprendizado prático onde é possível visualizar a construção de uma aplicação CRUD (Create, Read, Update, Delete) completa, com uma interface visual e conexão a um banco de dados real, simulando um sistema de prontuários eletrônicos.

# ✨ Funcionalidades
- Gerenciamento de Pacientes: Cadastrar, consultar, atualizar e excluir registros de pacientes.

- Gerenciamento de Exames: Cadastrar, consultar, atualizar e excluir exames, associando-os a um paciente.

- Interface Gráfica: Todas as operações são realizadas através de janelas, formulários e botões construídos com Java Swing.

- Persistência de Dados: Todos os dados de pacientes e exames são salvos em um banco de dados MySQL.

- Arquitetura Limpa: O código é organizado utilizando o padrão DAO para separar a lógica de negócios das regras de acesso a dados.

# 💻 Tecnologias Utilizadas
- Java: Linguagem principal do projeto.

- Swing: Biblioteca para a construção da interface gráfica (GUI).

- JDBC: Para a conexão e comunicação com o banco de dados.

- MySQL: Sistema de Gerenciamento de Banco de Dados para armazenamento dos dados.

# 🌿 Versionamento e Branches
Este repositório está organizado em duas branches principais para garantir a compatibilidade com diferentes versões do Java. Escolha a branch que corresponde ao seu ambiente de desenvolvimento:

- main: Versão do projeto compatível com o Java 21.

- jdk-17: Versão do projeto compatível com o Java 17.

<b>Certifique-se de estar na branch correta antes de compilar e executar o projeto.</b>

# 📂 Estrutura do Projeto
```
prontuario/
    ├── app/         # Ponto de entrada da aplicação
    ├── dao/         # Data Access Objects (DAO)
    ├── database/    # Classes de conexão com o banco
    |── exception    # Excessões (Tratamento de erros)
    ├── model/       # Modelos de domínio (Paciente, Exame)
    |── services/    # Facade (Interface Simplificada)
    |── utils/       # Validações e Formatação
    |── view/        # Interfaces com implementação do Swing
```


# ⚙️ Requisitos

- Java 17 ou 21 (de acordo com a branch escolhida).

- MySQL instalado e em execução.

- IDE como Eclipse, IntelliJ ou VSCode (Opcional, mas recomendado).

# 🗄️ Banco de Dados (MySQL)

O banco de dados é gerado automaticamente, verifique o arquivo config.txt e configure-o de acordo com seu ambiente. 
```
DB_USERNAME=root     
DB_PASSWORD=root        
DB_ADDRESS=localhost    
DB_PORT=3306
DB_NAME=prontuario
```

# 🚀 Como Executar
Clone o repositório:
```
git clone <url-do-repositorio>
#Acesse o diretório e escolha a branch:

cd <nome-do-diretorio>
git checkout main-jdk17  # ou main-jdk21
```
- Configure o banco de dados: Crie o banco de dados e as tabelas usando o script SQL fornecido acima.

- Atualize a conexão: Abra o projeto em sua IDE e atualize os detalhes de conexão com o banco de dados no arquivo de configuração (geralmente em database/DatabaseConnection.java).

- Compile e execute: Encontre a classe principal que inicializa a interface Swing (ex: Application.java ou MainFrame.java) e execute-a.

# 🤝 Contribuições
Este projeto é destinado a fins educacionais. Contribuições, sugestões e melhorias são sempre bem-vindas!

Feito com ☕ e 💻 para fins de aprendizado.
