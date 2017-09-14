(ns pertgen.core)


(def main[])
(defn eval-pert [exp]
  "xxxxx")

;(def *node-table* {})
;(def *nodename-table* {})
;(def *edge-table* {})

(defn has-edge? [start end]
  true)

(defmacro task 
  "generate task object"
  [name period assignee]
    `(list (str '~name) (str '~period) (str '~assignee)))

(task 概要設計 1 ume)
(task 詳細設計 1 ume)
(task インフラ手配 6 ume)
(task 実装 10 ume)
(task 単体テスト 10 ume)
(task 結合テスト 3 ume)
(task 本番デプロイ 1 ume)

; section graph
;(def graph '(
;  (概要設計　詳細設計 実装)
;  (インフラ手配 実装)
;  (実装 単体テスト 結合テスト 本番デプロイ)
;  (デプロイ準備 本番デプロイ)
;))

;(eval-pert graph)
;
