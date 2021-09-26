(ns tabata.state
  (:require [reagent.core :as r]))
(def current-workout (r/atom {
                              :current-step nil
                              :editing      false
                              :steps
                                            [{:interval 3 :state :countdown}
                                             {:interval 40 :state :squat}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :burpees}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :lunge}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :push-up-rotate}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :high-knees}
                                             ; one round
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :squat}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :burpees}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :lunge}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :push-up-rotate}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :high-knees}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :squat}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :burpees}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :lunge}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :push-up-rotate}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :high-knees}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :squat}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :burpees}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :lunge}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :push-up-rotate}
                                             {:interval 20 :state :rest}
                                             {:interval 40 :state :high-knees}
                                             ]}))
#_(def current-workout
    (r/atom {:steps        [{:interval 3
                             :state    :countdown}
                            {:interval 40
                             :state    :jumping-jacks}
                            {:interval 20
                             :state    :rest}
                            {:interval 40
                             :state    :mountain-climber}
                            {:interval 20
                             :state    :rest}
                            {:interval 40
                             :state    :push-up-rotate}
                            {:interval 20
                             :state    :rest}
                            {:interval 40
                             :state    :burpees}
                            {:interval 20
                             :state    :rest}
                            {:interval 40
                             :state    :squat}]
             :current-step nil
             :editing      false}))


(def counter (r/atom {:state   :stopped
                      :total   0
                      :current 0}))

; the ticker interval
(def one-second-interval (r/atom nil))

(def exercises
  (r/atom {:squat            {:svg  "/img/squat.svg"
                              :name "squat"}
           :push-up-rotate   {:svg  "/img/push-up-rotate.svg"
                              :name "push up & rotate"}
           :mountain-climber {:svg  "/img/mountain-climber.svg"
                              :name "mountain climbers"}
           :rest             {:svg  "/img/meditation.svg"
                              :name "rest up"}
           :lunge            {:svg  "/img/lunge.svg"
                              :name "jumping lunge"}
           :jumping-jacks    {:svg  "/img/jumping-jacks.svg"
                              :name "jumping jack"}
           :high-knees       {:svg  "/img/high-knees.svg"
                              :name "high knees"}
           :burpees          {:svg  "/img/burpees.svg"
                              :name "burpee"}}))


; DERIVED state

(defn just-current-exercises []
  (filter (fn [{s :state}]
            (and
              (not (= s :rest))
              (not (= s :countdown))))
          (:steps @current-workout)))


(defn workout-state
  "get the state of the workout :pre-start :finished :started"
  [curr]

  (if (:editing curr)
    :editing
    (if (nil? (:current-step curr))
      :pre-start
      (if (>= (:current-step curr) (count (:steps curr)))
        :finished
        :started))))



(defn stop-ticker []
  (let [val @one-second-interval]
    (if (nil? val)
      (println "stopped while already stopped..")
      (do
        (js/clearInterval val)
        (reset! one-second-interval nil)))))

; side effects on the current step
(defn set-step
  [id]
  (swap! current-workout assoc :current-step id)
  ; set counter-state to started
  (let [step (get (:steps @current-workout) id)
        new-counter {:state   :started
                     :total   (:interval step)
                     :current (:interval step)}]
    (if (nil? step)
      (do
        (stop-ticker))
      (swap! counter merge new-counter))))

(defn inc-workout-step
  []
  (swap! current-workout update :current-step inc))


; TICKER state handling
(defn tick
  []
  (let [curr-tick (:current @counter)
        next-tick (dec curr-tick)]
    (if (= curr-tick 0)
      (set-step (inc (:current-step @current-workout)))
      (swap! counter assoc :current next-tick))))

; start the clock yo
(defn ticker []
  (if (nil? @one-second-interval)
    (let [id (js/setInterval (fn [] (tick)) 1000)]
      (println "started ticker: " id)
      (reset! one-second-interval id))
    nil))                                                   ;; do nothing if the state is already started



(defn start-action
  []
  (ticker)
  (set-step 0))

(defn restart-action
  []
  (println "restart-acti4on")
  (case (workout-state @current-workout)
    :pre-start (start-action)
    :started (ticker)
    :finished (start-action)))

(defn edit-action []
  (let [curr @current-workout
        is-editing (:editing curr)]
    (stop-ticker)
    (swap!
      current-workout
      merge
      {:editing      (not is-editing)
       :current-step nil})))

(defn upgrade-workout-step [id workout interval]
  (println "upgrade workout step " id " workout:" workout " interval:" interval)
  (swap!
    current-workout
    update-in [:steps id]
    merge {:state workout :interval interval}))

(defn drop-at-index
  "takes a collection and returns a collection with the element at id dropped"
  [id col]
  (into [] (->> col
       (map-indexed (fn [curid val] [curid val]))
       (filter (fn [[curid val]]
                 (not (= curid id))))
       (map #(get % 1)))))

(defn delete-workout-step [id]

  (let [new (drop-at-index id (:steps @current-workout))]
    (println "drop at id: " id " new \n" new)
    (swap! current-workout assoc :steps new)))