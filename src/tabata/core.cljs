(ns tabata.core
  (:require [reagent.core :as r]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.spec :as rss]
            [tabata.components.organisms :as organisms]))

(defonce match (r/atom nil))

(def routes
  [["/"
    {:name ::mainscreen
     :view organisms/mainscreen}]
   ["/about"
    {:name ::about
     :view organisms/about-page}]])

(defn app []
  [:div
   (if @match
     (let [view (:view (:data @match))]
       [view @match])
     [:div
      [:p "View not found!"]
      [:pre (print-str @match)]])
   [:footer.pt-5.my-5.text-muted.border-top
    [:span "Created by ersin"]
    [:a.mx-1 {:href "https://github.com/ebuckley/tabata"} "Code"]
    [:a.mx-1 {:href (rfe/href ::about)} "About"]]])

(defn ^:export main
  []
  (println "entrypoint")
  (rfe/start!
    (rf/router routes {:data {:coercion rss/coercion}})
    (fn [m] (reset! match m))
    {:use-fragment false})
  (r/render
    [app]
    (.getElementById js/document "app")))
