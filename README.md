# PROJ_LP2_AED2_STREAM

Implementacao orientada a objetos em Java (Maven) para um sistema de streaming com:

- Modelo de dominio (`streaming.model`) com entidades como `User`, `Content`, `Artist`.
- Base de dados em memoria (`streaming.db.StreamingDB`) suportada por estruturas da biblioteca `algs4`.
- Representacao opcional por grafo (`streaming.db.StreamingGraph` / `StreamingGraphAPI`) para relacoes e interacoes.

O codigo inclui documentacao JavaDoc (para geracao automatica de HTML) e testes unitarios (JUnit 5).

## Como Compilar E Testar

Executar testes unitarios:

```bash
./mvnw test
```

## Gerar Documentacao (JavaDoc HTML)

Gerar as paginas HTML do JavaDoc:

```bash
./mvnw javadoc:javadoc
```

Output tipico:

- `target/site/apidocs/index.html`

## Algoritmos E Estruturas De Dados

### StreamingDB (R2/R3)

Estruturas base:

- `SeparateChainingHashST` (hash table com chaining) para armazenamento principal por `id`:
  - `users`, `contents`, `artists`, `genres`, `archivedusers`.
- `RedBlackBST` (arvore rubro-negra) como indice ordenado para pesquisas por chaves:
  - exemplos: `usersByRegion`, `contentsByGenre`, `contentsByTitle`, `artistsByBirthDate`, etc.

Indices e atualizacoes:

- Para suportar updates in-place sem varrer arvores inteiras, a DB guarda "snapshots" das chaves de indexacao
  por entidade (por exemplo `contentIndexKeys`). Assim, ao atualizar uma entidade existente, o codigo remove
  primeiro a entrada antiga dos indices e depois re-indexa com as chaves novas.

Complexidade (ordens de grandeza):

- Operacoes por `id` (hash table): O(1) em media, O(n) no pior caso (colisoes patologicas).
- Operacoes em indices ordenados (RedBlackBST): O(log n) para inserir/procurar/remover a chave.
- `validateConsistency()` (quando usado): tipicamente O(U + C + A) para varrer entidades e verificar referencias,
  onde U = users, C = contents, A = artists.

### StreamingGraph (R7/R8)

Estrutura:

- `EdgeWeightedDigraph` da `algs4` usa vertices identificados por inteiros (0..V-1).
- O projeto usa mapas de traducao:
  - `nodeIndex`: `String id -> int index`
  - `indexToEntity`: `int index -> Entity`

Rebuild (capacidade fixa):

- Como `EdgeWeightedDigraph` requer capacidade no construtor, o grafo e criado com capacidade inicial e,
  quando necessario, e reconstruido com o dobro do tamanho.
- O rebuild reatribui indices contiguos e re-adiciona as arestas a partir das interacoes guardadas.

Complexidade (ordens de grandeza):

- `addVertex`: O(1) amortizado (com rebuild ocasional).
- `addEdge` (via API): O(1) para inserir aresta depois de traduzir ids.
- Rebuild: O(V + E) para reconstruir vertices e re-adicionar arestas.

## Testes Implementados

Testes unitarios (JUnit 5) em `src/test/java` cobrem comportamentos base do modelo:

- `Entity.equals/hashCode` por `id`.
- `User.follow` (nao duplica follows).
- `Episode.setRating` valida limites.
- `Genre.equals/hashCode` por `id`.

Para executar todos os testes: `./mvnw test`.

