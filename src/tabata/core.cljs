(ns tabata.core
  (:require [reagent.core :as r]
            [tabata.components.organisms :as organisms]))


(defn app []
  [organisms/mainscreen])

(defn ^:export main
  []
  (println "entrypoint")
  (r/render
    [app]
    (.getElementById js/document "app")))
