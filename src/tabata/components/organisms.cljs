(ns tabata.components.organisms
  (:require [tabata.components.atoms :as atoms]
            [tabata.components.molecules :as molecules]
            [tabata.state :as state]))


(defn mainscreen
  []
  "Component which displays the current workout"
  [:main.main-app.container-sm
   [:div.btn-toolbar {:role "toolbar"}
    [:div.btn-group.me-4
     [atoms/button [:span [atoms/icon "skip-backward-fill"]] #(state/set-step (dec (:current-step @state/current-workout)))]
     [atoms/button [:span [atoms/icon "play-fill"]] #(state/restart-action)]
     [atoms/button [atoms/icon "pause-fill"] #((state/stop-ticker))]
     [atoms/button [:span [atoms/icon "skip-forward-fill"]] #(state/set-step (inc (:current-step @state/current-workout)))]]
    [:div.btn-group.me-4
     [atoms/button [atoms/icon "stop-fill"] #(state/start-action)]]
    [:div.btn-group.float-right
     [atoms/button [atoms/icon "pencil-square"] #(state/edit-action)]]]
   [molecules/current-state]
   [molecules/preview-current]
   #_[molecules/pick-exercise #(println "picked" %)]])
