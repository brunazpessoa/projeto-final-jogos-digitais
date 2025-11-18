
<div align="center">
  <h1>PIQUE-FOREST</h1>
</div>

<div align="center">
Bruna Zakaib Pessoa - 10417079 | Isabela Reali da Costa – 10439161 | João Vitor Macea - 10437139
</div>
<hr>
<br>


# Resumo  
Este trabalho apresenta o desenvolvimento de um jogo interativo criado com o framework libGDX, controlado exclusivamente por meio da coleta de dados de pedaladas de um dispositivo IoT conectado a uma bicicleta estática. O jogo tem como público-alvo pacientes em reabilitação motora de todas as idades, promovendo o aprimoramento da coordenação, velocidade e consistência dos movimentos. Ambientado em uma floresta, o personagem deve proteger sua cesta de piquenique de animais curiosos que tentam roubá-la. A movimentação e as ações no jogo são determinadas pela intensidade e regularidade das pedaladas, que também servem como métrica de desempenho, indicador de progresso e pontuação do jogador. À medida que o jogador vence os desafios, a dificuldade aumenta, promovendo um treino progressivo e dinâmico. Assim, o jogo propõe uma forma lúdica e motivacional de apoiar a evolução física de pacientes em tratamento.
<p>Palavras-chave: IoT; reabilitação física; consistência; motivacional.</p>


# 1. Introdução 
A reabilitação motora representa um desafio complexo no contexto clínico e terapêutico, pois exige do paciente não apenas disciplina e esforço físico contínuo, mas também um elevado grau de engajamento cognitivo e motivacional, elementos frequentemente negligenciados em abordagens tradicionais, que podem tornar os exercícios repetitivos e monótonos. Essa falta de motivação compromete a adesão ao tratamento, reduzindo a eficácia das intervenções e prolongando o tempo necessário para alcançar melhorias significativas nas funções motoras. Nesse cenário, a aplicação de jogos digitais como instrumentos terapêuticos surge como uma alternativa eficaz e promissora, integrando o lúdico, a interatividade, a diversão e feedbacks imediatos, características que incentivam os pacientes a participarem ativamente, tornando a prática da fisioterapia mais envolvente e prazerosa. <br>
O presente trabalho descreve o desenvolvimento de um jogo interativo criado com o framework libGDX, cujo controle depende da coleta de dados de pedaladas de uma bicicleta estática equipada com dispositivo IoT, permitindo que o esforço físico real do paciente seja diretamente transformado em ações no ambiente virtual. A mecânica do jogo está estruturada para monitorar indicadores de desempenho motor, como velocidade, consistência e regularidade das pedaladas, utilizados tanto para determinar a movimentação e as ações do personagem quanto como métricas de pontuação e progresso ao longo das fases. <br>
O jogo se passa em uma floresta, apresentando desafios crescentes representados pelos animais que tentam roubar a cesta de piquenique do personagem principal. São três fases, com dificuldade gradativamente aumentada, promovendo evolução progressiva das habilidades motoras do jogador e estimulando foco e treino. Essa abordagem possibilita que o paciente assuma um papel ativo em seu próprio desenvolvimento funcional, recebendo feedback imediato de seu desempenho e percebendo de forma tangível o impacto de suas ações.
Socialmente, o projeto contribui significativamente ao possibilitar que pacientes de todas as idades participem de exercícios terapêuticos de forma lúdica, segura e divertida. Jogos digitais voltados para integração de tecnologia e auxílio em atividades fisioterápicas são de suma importância, combinam entretenimento com exercícios físicos, incentivando o engajamento, mostrando evolução e pontos de melhoria de forma imediata e acelerando os resultados do tratamento.

# 2. Desenvolvimento / Metodologia

Para o desenvolvimento do jogo, utilizou-se o framework libGDX, com toda a programação realizada em Java por meio do ambiente de desenvolvimento IntelliJ IDEA. O controle de versionamento e colaboração foi feito através de um repositório no GitHub, disponível em: https://github.com/brunazpessoa/projeto-final-jogos-digitais.</br>
A primeira etapa consistiu na definição da dinâmica e mecânica central do jogo. O projeto foi estruturado em três fases, cada uma apresentando aumento progressivo de dificuldade. A jogabilidade baseia-se na integração entre o jogo e os dados coletados pela bicicleta estática equipada com um dispositivo IoT. Dessa forma, a intensidade e a regularidade das pedaladas determinam diretamente o comportamento do personagem na tela. </br>
Quando o jogador pedala em ritmo moderado, o personagem se move caminhando; ao aumentar a velocidade das pedaladas, o personagem passa a correr, obedecendo de forma imediata aos comandos transmitidos pelo dispositivo. Essa resposta em tempo real cria uma interação direta e imersiva, permitindo ao jogador perceber claramente a relação entre suas ações físicas e os efeitos no ambiente virtual. </br>
Foram estabelecidas também as condições de vitória e derrota. O jogador vence a fase ao pedalar o suficiente para manter distância do animal adversário e alcançar a cesta de piquenique localizada no final do percurso, acionada por colisão. A derrota ocorre quando o ritmo das pedaladas diminui a ponto de permitir que o animal alcance o personagem. Assim, o game promove engajamento contínuo e incentiva o esforço físico de forma lúdica e motivadora.


