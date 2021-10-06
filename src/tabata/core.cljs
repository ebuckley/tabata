(ns tabata.core
  (:require [reagent.core :as r]
            [tabata.components.organisms :as organisms]))


(defn app []
  [:div
   [organisms/mainscreen]
   [:footer.pt-5.my-5.text-muted.border-top
    [:span "Created by ersin"]
    [:a.my-1 {:href "https://github.com/ebuckley/tabata"} "Code"]]])

(defn ^:export main
  []
  (println "entrypoint")
  (r/render
    [app]
    (.getElementById js/document "app")))
