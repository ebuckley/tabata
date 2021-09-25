(ns tabata.components.molecules
  (:require [tabata.state :as state]
            [tabata.components.atoms :as atoms]))



(defn count-downer
  []
  (let [counter @state/counter]
    (do
      (if (< (:current counter) 3)
        (atoms/beep))                                       ; TODO add button to turn off sound somehow.
      [:div.d-flex.justify-content-center.p-3.flex-column.align-items-center
       [:h1.display-1 (:current counter)]])))

(defn next-exercise
  []
  (let [curr @state/current-workout
        next-step (get (:steps curr) (+ 1 (:current-step curr)))]
    (if (nil? next-step)
      [:div "You're finished nice!"]
      (let [cur-exercise ((:state next-step) @state/exercises)]
        [:div
         [:span.fw-bold
          (:name cur-exercise)
          [:span.fw-light (str " for " (:interval next-step) " seconds")]]]))))


(defn get-exercise-image
  [exercise-symbol]
  (let [exercise (exercise-symbol @state/exercises)]
    (if (nil? exercise)
      [:span ""]
      [:img.img-fluid-v {:src (:svg exercise)}])))

(defn get-step-title
  [workout-step]
  (let [exercise ((:state workout-step) @state/exercises)
        name (or (:name exercise)
                 (str (:state workout-step)))]
    [:h3
     name
     [:span.fw-light (str " for " (:interval workout-step) " seconds")]]))

(defn show-progress
  []
  (let [curr @state/current-workout
        progress (:current-step curr)
        total-length (count (:steps curr))]
    [atoms/static-bar total-length progress]))

(defn show-exercise
  []
  (let [curr @state/current-workout
        step (get (:steps curr) (:current-step curr))]
    [:div
     [atoms/static-bar (:total @state/counter) (:current @state/counter)]
     [get-step-title step]
     [:div.d-flex.justify-content-center.p-3 {:style {:height "25vh"}}
      [get-exercise-image (:state step)]]
     [count-downer]
     [next-exercise]
     [show-progress]]))

; TODO are the below candidates for lifiting to the organism level?

(defn edit-workout
  []
  [:div.pt-3
   [:h3 "Edit Workout"]
   (for [cur (state/just-current-exercises)]
     [:div.row.exercise__row {:key (:state cur)}
      [:div.col-3.d-flex.justify-content-center {:style {:height "125px"}} [get-exercise-image (:state cur)]]
      [:div.col-9.d-flex.align-items-center
       [get-step-title cur]]])
   [atoms/button "Add Exercise" #(println "Just add one more exercise or something")]])


(defn pick-exercise [picked-fn]
  [:div
   [:h2 "Pick Workout"]
   [:div.d-grid.grid-cols-3

    (for [[key exer] @state/exercises]
      [:div.col-3.d-flex.justify-content-center {:key   (str key)
                                                 :style {:height "125px"}}
       [get-exercise-image key]
       [:div.col-9.d-flex.align-items-center.p-3
        [atoms/button [atoms/icon "plus"] #(picked-fn key)]]])]])



(defn preview-current
  []
  #_[:div.pt-3
     [:h3 "This Workout"]
     [:div.d-grid.grid-cols-3
      (for [cur (state/just-current-exercises)]
        (do
          [:div {:key (:state cur) :style {:height "250px"}}
           [:span (str (:state cur))]
           [get-exercise-image (:state cur)]]))]])

(defn current-state
  []
  (case (state/workout-state @state/current-workout)
    :editing [edit-workout]
    :pre-start [:h3 "Just click start to get going!"]
    :finished [:h3 "Nice work!"]
    :started [show-exercise]))