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
  (let [exercise (get @state/exercises exercise-symbol)]
    (if (nil? exercise)
      [:span ""]
      [:img.img-fluid-v {:src (:svg exercise)}])))

(defn get-step-title
  [workout-step]
  (let [exercise (get @state/exercises (:state workout-step) )
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

; state to view dispatcher