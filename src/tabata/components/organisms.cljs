(ns tabata.components.organisms
  (:require [tabata.components.atoms :as atoms]
            [tabata.components.molecules :as molecules]
            [tabata.state :as state]
            [reagent.core :as r]))


(defn show-exercise
  []
  (let [curr @state/current-workout
        step (get (:steps curr) (:current-step curr))]
    [:div
     [atoms/static-bar (:total @state/counter) (:current @state/counter)]
     [molecules/get-step-title step]
     [:div.d-flex.justify-content-center.p-3 {:style {:height "25vh"}}
      [molecules/get-exercise-image (:state step)]]
     [molecules/count-downer]
     [molecules/next-exercise]
     [molecules/show-progress]]))


(defn pick-exercise [exer interval picked-fn deleted-fn]
  (let [has-exer (nil? (get state/exercises exer))
        choosing-exercise (r/atom false)
        interval-atom (r/atom interval)
        exer-atom (r/atom exer)]
    (fn []
      [:div.container.border.rounded.py-3
       [:div.row.g-3.align-items-center
        [:div.col-auto
         (if has-exer
           [atoms/button
            [:div.d-flex.align-items-center
             [:div {:style {:width "75px"}}
              [molecules/get-exercise-image @exer-atom]]]
            #(reset! choosing-exercise (not @choosing-exercise))]
           [:div "Choose one now!"])]
        [:div.col-auto
         [:span.lead (print-str @exer-atom) " for"]]
        [:div.col-auto
         [:input.form-control.form-control-lg
          {:type        :number
           :placeholder "40"
           :value       @interval-atom
           :on-change   #(reset! interval-atom (.-value (.-target %)))}]]
        [:div.col-auto [:span.form-text "seconds"]]
        [:div.col-auto
         [:div.btn-group
          [atoms/button [atoms/icon "save"] #(picked-fn @exer-atom @interval-atom)] ; SAVE the exercise
          [atoms/button [atoms/icon "trash"] #(deleted-fn)]] ; DELETE the exercise
         ]]
       (if @choosing-exercise
         [:div
          [:span.lead "Choose an exercise"]
          [:div.d-flex.flex-wrap.gap-3
           (for [[key exer] @state/exercises]
             [:div {:key (str key)}
              [atoms/button
               [:div.d-flex.align-items-center.justify-content-center.p-3
                {:key (str key)}
                [:div {:style {:width "125px" :height "125px"}}
                 [molecules/get-exercise-image key]]]
               #(do
                  (reset! choosing-exercise false)
                  (reset! exer-atom key))]]

             )]]
         [:div])])))

(defn pick-or-view-row
  [id cur]
  (let [is-edit (r/atom false)]
    (fn []
      (if @is-edit
        [:div
         [pick-exercise (:state cur) (:interval cur)
          #(do
             (state/upgrade-workout-step id %1 %2)
             (reset! is-edit false))
          #(do
             (state/delete-workout-step id)
             (reset! is-edit false))]]
        [:div.row.exercise__row {:on-click #(reset! is-edit true)}
         [:div.col-3.d-flex.justify-content-center {:style {:height "125px"}} [molecules/get-exercise-image (:state cur)]]
         [:div.col-9.d-flex.align-items-center
          [molecules/get-step-title cur]]]))))

(defn edit-workout
  []
  (let [current-steps (:steps @state/current-workout)
        idx-steps (map-indexed (fn [id val] [id val]) current-steps)]
    [:div.pt-3
     [:h3 "Edit Workout"]
     (for [[idx cur] idx-steps]
       [:div {:key (str idx "-" (print-str cur))}
        [pick-or-view-row idx cur]])
     #_(for [cur (state/just-current-exercises)]
         [pick-or-view-row cur])
     [atoms/button "Add Exercise" #(println "Just add one more exercise or something")]]))

(defn current-state
  []
  (case (state/workout-state @state/current-workout)
    :editing [edit-workout]
    :pre-start [:h3 "Just click start to get going!"]
    :finished [:h3 "Nice work!"]
    :started [show-exercise]))

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
   [current-state]
   [molecules/preview-current]
   [pick-exercise :squat 40 #(println "picked" %) #(println "selected delete!")]])
