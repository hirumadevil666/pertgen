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
 [インフラ手配 本番デプロイ 受入テスト])q
(draw-graph)
