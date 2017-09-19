(ns pertgen.core
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.java.io :as io] )
  (:require [clojure.pprint :refer (cl-format)])
  (:require [clojure.string :as string])
  (:require [cljstache.core :refer [render render-resource]])
  (:gen-class))

;;; FIXME
;;; まとめる
(def task-table (atom  {}))
(def node-sym-table (atom {}))
(def edge-set (atom  #{}))
(def output-file (atom nil))
(def paths (atom '()))
(def interactive-mode (atom false))
(def template-file (atom "template.dot.mustache"))
(declare get-templatefile write-graph)

(defn- has-edge? [e]
  (if (get @edge-set e)
    true
    false))

(defn set-interactive-mode []
  (reset! interactive-mode true))

(defn set-script-mode []
  (reset! interactive-mode false))

(defn clear-edge []
  (reset! edge-set #{}))

(defn- add-edge [edge]
  ""
  (swap! edge-set conj edge))

(defn- remove-dup-edge [p]
  "xxxxx"
  (let [one-path (partition 2 1 p)]
    (let [g (group-by has-edge? one-path)]
      (dorun
       (map add-edge (get g false [])))
      (get g false []))))

(defn- check-task [li]
  (let [r (filter #(nil? (get @task-table %)) li)]
    (if (nil? (first r))
      li
      (throw  (ex-info (format "task %s がみつかりません" (first r)) {:cause :task-check})))))

(defn format-task [node-sym-table t]
  (let [[key task] t
        node-name (get node-sym-table key)
        [task-name day assignee note] task]
    {:node-name (name  node-name), :task-name task-name, :assignee assignee, :day day :note note}))

(defn format-path [node-sym-table p]
  (let [path-str (->> p
                      (map node-sym-table)
                      (map name)
                      (string/join " -> " ))]
    {:path path-str}))

(defn- get-dotstring [template-path p]
  (let [ opts {:nodes (map #(format-task @node-sym-table  %) @task-table )
               :paths (map #(format-path @node-sym-table %) p)}]
    (render-resource template-path opts)))

(defn- write-graph [w p]
  (let [t (get-templatefile)
        s (get-dotstring t p)]
    (cond (= w *out*) (println s)
          :else
          (spit w s))))

;;; (split-path '([a b] [c d] [d e] [e f] [f g]))
;;; '([a b] [c d] [d e] [e f] [f g])
;; -> [(a b) (c d e f g) ]

(defn split-path [p]
  "xxxxx"
  (loop [li p  last_node nil work [] result []]
    (if (empty? li)
      (if (empty? work)
        result
        (conj result work))
      (let [[start end] (first li) ]
        (if (nil? last_node)
          (recur (rest li) end [start end] result )
          (if (= last_node start)
            (recur (rest li) end (conj work end) result)
            (recur (rest li) end [start end] (conj result work))))))))

(defn draw-graph []
  (do (clear-edge)
      (try
        (let [p(->> (map check-task @paths)
                    (map remove-dup-edge )
                    (map split-path)
                    (map #(mapcat conj %))
                    (filter #(not (empty? %))) ;remove empty list
                    )]
          ;;
          (if (nil? @output-file)
            (write-graph *out* p)
            (with-open [w (clojure.java.io/writer @output-file)]
              (write-graph w p))))
        (catch Exception e
          (cond (= :task-check (-> e ex-data :cause)) (do (println (.getMessage e)) nil)
                :else (throw e))))))

(defn call-draw-graph []
  (if (true? @interactive-mode)
    (draw-graph)))

(def task-sym
  "xxxxx"
  (let [suffix (atom 0)]
    (fn [] (do (swap! suffix inc)
               (->> @suffix
                    (format "node_%d" )
                    keyword)))))

(defn regist-task [task]
  "regist task object on node task"
  (let [[name period assignee note] task ]
    (if (find @task-table (keyword name))
      (prn (format "Task '%s' already defined.Overwrite it." name)))
    (do
      (swap! task-table assoc (keyword name ) task)
      (swap! node-sym-table assoc (keyword name) (task-sym))
      (call-draw-graph))))

(defmacro unregist-task [t]
  `(do
     (swap! task-table dissoc (keyword '~t))
     (swap! node-sym-table dissoc (keyword '~t))
     (call-draw-graph)))

(defmacro task-note
  ([] "")
  ([x] `(str '~x))
  ([x & rest]
   `(apply str (str '~x " ") (task-note ~@rest))))

(defmacro task
  "generate task object"
  [name period assignee & rest]
  `(let [task# (list (str '~name) (str '~period) (str '~assignee) (task-note ~@rest))]
     (regist-task task#)
     task# ))

(defn unregist-task-f [task-name]
  "task-name : task-name keyword"
  (do
    (swap! task-table dissoc task-name)
    (swap! node-sym-table dissoc task-name)
    (call-draw-graph)))

(defn initialize-pertgen []
  "initialize global variables"
  (reset! paths [])
  (reset! node-sym-table {})
  (reset! task-table {})
  (reset! edge-set  #{}))

(defn set-output-file [filename]
  (reset! output-file filename ))

;; (t (a b c))
;; (t (a b c) (d e f))
;; (t (a b c) (d e f) (f g h))

(defmacro symli2key
  [li]
  `(map keyword '~li))
;;
;; (make-path-keyword
;;  (a b c ) (d e f) (g h i j))
;;return  [(:a :b :c) (:d :e :f) (:g :h :i :i)]
(defmacro make-path-keyword
  ([] [])
  ([x] `[(symli2key ~x)])
  ([x & rest]
   `(apply conj [(symli2key ~x)] (make-path-keyword ~@rest))))

(defmacro graph
  ([& rest]
   `(regist-graph-by-list  (make-path-keyword ~@rest))))

(defn regist-graph-by-list [new-paths]
  ;; regist whole path
  (do
    (reset! paths new-paths)
    (call-draw-graph)))

(defn- add-path [p]
  ;; add single path
  (do
    (swap! paths conj  p)
    (call-draw-graph)))

(defn regist-graph [x & rest]
  (regist-graph-by-list  (apply conj [x] rest)))

(defn get-templatefile []
  @template-file)
(defn set-templatefile [s]
  (reset! template-file s))

(def option-spec
  [["-h" "--help" "Show help."]
   ["-t" "--template TEMPLATE" "mustache template file name(in classpath)"
    :default "template.dot.mustache"]
   ])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args option-spec)]
    (cond
      (:help options) (println (format " FILENAME     script filename.\n%s" summary ))
      (< (count arguments) 1) (do (println "Need script filnename") (System/exit 0))
      :else (do
              (set-templatefile (:template options))
              (load-file (first arguments))))))
