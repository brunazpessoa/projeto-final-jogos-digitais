
<div align="center">
  <h1>PIQUE-FOREST</h1>
</div>

<div align="center">
Bruna Zakaib Pessoa - 10417079 | Isabela Reali da Costa – 10439161 | João Vitor Macea - 10437139
</div>
<hr>
<br>


# Resumo  
Este trabalho apresenta o desenvolvimento de um jogo interativo criado com o framework libGDX, controlado exclusivamente por meio da coleta de dados de pedaladas de um dispositivo IoT conectado a uma bicicleta estática. O jogo tem como público-alvo pacientes em reabilitação motora de todas as idades, promovendo o aprimoramento da coordenação, velocidade e consistência dos movimentos. Ambientado em uma floresta, o personagem deve chegar à sua cesta de piquenique primeiro que o animal do qual ele foge. A movimentação e as ações no jogo são determinadas pela intensidade e regularidade das pedaladas, que também servem como métrica de desempenho, indicador de progresso e pontuação do jogador. À medida que o jogador vence os desafios, a dificuldade aumenta, promovendo um treino progressivo e dinâmico. Assim, o jogo propõe uma forma lúdica e motivacional de apoiar a evolução física de pacientes em tratamento.
Palavras-chave: IoT; reabilitação física; consistência; motivacional.</p>


# 1. Introdução 
A reabilitação motora representa um desafio complexo no contexto clínico e terapêutico, pois exige do paciente não apenas disciplina e esforço físico contínuo, mas também um elevado grau de engajamento cognitivo e motivacional, elementos frequentemente negligenciados em abordagens tradicionais, que podem tornar os exercícios repetitivos e monótonos. Essa falta de motivação compromete a adesão ao tratamento, reduzindo a eficácia das intervenções e prolongando o tempo necessário para alcançar melhorias significativas nas funções motoras. Nesse cenário, a aplicação de jogos digitais como instrumentos terapêuticos surge como uma alternativa eficaz e promissora, integrando o lúdico, a interatividade, a diversão e feedbacks imediatos, características que incentivam os pacientes a participarem ativamente, tornando a prática da fisioterapia mais envolvente e prazerosa. <br>
O presente trabalho descreve o desenvolvimento de um jogo interativo criado com o framework libGDX, cujo controle depende da coleta de dados de pedaladas de uma bicicleta estática equipada com dispositivo IoT, permitindo que o esforço físico real do paciente seja diretamente transformado em ações no ambiente virtual. <br>
Socialmente, o projeto contribui significativamente ao possibilitar que pacientes de todas as idades participem de exercícios terapêuticos de forma lúdica, segura e divertida. Jogos digitais voltados para integração de tecnologia e auxílio em atividades fisioterápicas são de suma importância, combinam entretenimento com exercícios físicos, incentivando o engajamento, mostrando evolução e pontos de melhoria de forma imediata e acelerando os resultados do tratamento.

# 2. Desenvolvimento / Metodologia

Para o desenvolvimento do jogo, utilizou-se o framework libGDX, com toda a programação realizada em Java por meio do ambiente de desenvolvimento IntelliJ IDEA. O controle de versionamento e colaboração foi feito através de um repositório no GitHub, disponível em: https://github.com/brunazpessoa/projeto-final-jogos-digitais. </br>
A primeira etapa do desenvolvimento foi a definição da dinâmica central do jogo, estruturado em três fases cuja dificuldade aumenta conforme a distância percorrida pelo jogador. A jogabilidade integra os dados enviados pelo dispositivo IoT da bicicleta estática, fazendo com que a intensidade das pedaladas controle diretamente o personagem: pedaladas moderadas resultam em caminhada, enquanto maior velocidade faz o personagem correr. Essa resposta imediata reforça a sensação de imersão e a relação direta entre ação física e retorno visual. </br>
As condições de vitória e derrota também foram definidas de forma simples e intuitiva. O jogador vence ao pedalar o suficiente para manter distância do animal adversário e alcançar a cesta de piquenique no fim da fase. A derrota ocorre caso o ritmo diminua e o animal consiga alcançá-lo. Esse conjunto de escolhas promove engajamento contínuo e incentiva o esforço físico de maneira lúdica e motivacional.

# 2.1 Desenvolvimento Personagem
Os personagens foram desenvolvidos em pixel art no Aseprite, utilizando uma paleta reduzida para garantir boa leitura visual e coerência com a estética geral do jogo. A escolha pelo estilo pixelado foi motivada por sua leveza, facilidade de animação e identidade retrô. </br>
<center><img width="448" height="133" alt="image" src="https://github.com/user-attachments/assets/4a3944b6-8700-44b4-b93d-f96f4aafa4e7" /> <img width="195" height="88" alt="image" src="https://github.com/user-attachments/assets/3ee70ede-2c3a-4476-8b74-6b3e94d9105e" /></center> <p><b> Figura 1. Sprite Personagens </b></p> <br>
Foi desenhado com uma silhueta simples e marcante, permitindo identificar o personagem mesmo em movimento. As animações de caminhada e corrida foram criadas quadro a quadro, de forma a refletir diretamente o ritmo das pedaladas, reforçando o feedback imediato ao jogador. <br>

# 2.2 Desenvolvimento Telas
O layout da tela principal do jogo foi projetado para ser limpo e funcional, mantendo o foco no personagem e no animal perseguidor, permitindo que o jogador acompanhe visualmente sua evolução conforme pedala na bicicleta conectada ao sistema. <br><br>
<img width="330" height="185" alt="image" src="https://github.com/user-attachments/assets/a88d523e-dea9-4055-8b27-21658e20fb1a" />
<p><b> Figura 2. Fundo Tela </b></p> <br> <br>
A tela de Menu, possibilita startar o jogo ou sair. Seguindo o layout e design do nosso jogo. <br>
<img width="341" height="192" alt="image" src="https://github.com/user-attachments/assets/cf4536d4-3178-465e-aa6d-68bc28af5d41" /><p><b> Figura 3. Tela Menu </b></p> <br>
Por fim, tem-se as telas de Game Over, inicializada quando o jogador perde e a tela de Vitória. Ambas as telas informam os dados obtidos após a jogada, Tempo, Distância, Quantidade de Pedaladas e Velocidade média. <br>
<img width="204" height="115" alt="image" src="https://github.com/user-attachments/assets/416f7403-738a-4994-8713-41d33eb9d4e5" /> <img width="200" height="112" alt="image" src="https://github.com/user-attachments/assets/eb05716e-494d-4333-9d4e-4477e01ff135" /> <p><b> Figura 4. Tela Game Over e Tela de Vitória, respectivamente </b></p> <br> <br>

# 3. Resultados e Discussão
O jogo apresentou desempenho estável nos testes, sem travamentos, e integrou corretamente os dados enviados pelo sensor da bicicleta. A comunicação entre o dispositivo IoT e o libGDX mostrou-se consistente, permitindo que o personagem reagisse em tempo real ao ritmo das pedaladas.<br>
Durante o desenvolvimento, surgiram desafios técnicos, especialmente na troca de fases e na interpretação correta do movimento físico dentro do jogo. Esses pontos exigiram várias iterações, ajustes e testes até alcançar uma integração confiável. <br>
Esse processo gerou aprendizados importantes sobre comunicação entre hardware e software, gerenciamento de estados no libGDX e organização do código, reforçando a importância de modularização e testes contínuos para garantir uma experiência fluida. <br>

# 4. Conclusão
O desenvolvimento do jogo demonstrou o potencial de integrar tecnologia, movimento físico e ludicidade para apoiar processos de reabilitação motora. A interação em tempo real entre o ato de pedalar e a resposta imediata do personagem contribui para aumentar o engajamento, reduzir a monotonia típica de exercícios repetitivos e oferecer ao paciente uma percepção clara de progresso, elementos essenciais para melhorar a adesão ao tratamento. <br>
O jogo atingiu seus objetivos principais: funcionar de forma estável, interpretar corretamente o esforço físico do usuário e proporcionar uma experiência motivacional e acessível para diferentes perfis de pacientes. Além disso, o aumento gradual de dificuldade e a estrutura baseada em quilometragem permitem que o usuário evolua no próprio ritmo, favorecendo a consistência do treino. <br>
Como trabalhos futuros, destacam-se melhorias na interface, inclusão de novos cenários e inimigos, sistema de pontuação mais detalhado, feedbacks visuais e sonoros mais ricos e possibilidade de registrar e acompanhar o desempenho do paciente ao longo do tempo. <br>






















