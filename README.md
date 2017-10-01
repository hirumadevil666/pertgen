# pertgen

PERT図作成DSL。Clojureの内部DSLをDOT言語に変換して出力します。

## Usage

### Install Clojure

`` $ brew install lein``

### Build jar file

```
 $ lein uberjar
 Compiling pertgen.core
 Compiling pertgen.example
 Created XXXXX/pertgen/target/pertgen-0.1.0-SNAPSHOT.jar
 Created XXXXX/pertgen/target/pertgen-0.1.0-SNAPSHOT-standalone.jar
```
### Run script
```
 lein run ./doc/examples/script_example.clj
```
or
```
 java -jar lib/pertgen-0.1.0-SNAPSHOT-standalone.jar -h
```

### Intaractive dot file generation

In clojure repl
```
 $ lein repl

 ; open sesame
 pertgen.core=> (use 'pertgen.core)
 pertgen.core=> (set-output-file "out.dot")
 pertgen.core=> (set-interactive-mode)
 ; タスク定義
 pertgen.core=> (task xxx xxx xxxx)
 pertgen.core=> (task yyy xxx xxxx)
 pertgen.core=> (task zzz xxx xxxx)
 ; グラフを定義する
 pertgen.core=> (graph [xxx yyy] [zzz])
 ; 終了
 pertgen.core=> quit
```
repl で入力しつつ out.dot を VSCode のGraphviz plugin のプレビューなどで開いておくと対話的に編集している気分になれます。

# example

```
(use 'pertgen.core)
(set-output-file "script_example.dot") ; 出力ファイルの指定。無指定の場合は標準出力に出力する。
(task 開発環境整備 1 all xxx)
(task 設計 3 you)
(task インフラ手配 4 you)
(task 実装 5 me)
(task 単体テスト 6 me you can write comments here)
(task 結合テスト 7 me)
(task 本番デプロイ 8 me)
(task 受入テスト 9 customer)
(graph
 [設計 実装]
 [開発環境整備 実装 単体テスト 結合テスト 本番デプロイ]
 [インフラ手配 本番デプロイ 受入テスト])
(draw-graph)
```

![グラフ](https://github.com/hirumadevil666/pertgen/raw/images/doc/examples/script_example.png)
