(ns cljsspiral.core
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.dom :as dom]))

(def pi Math/PI)
(def two-pi (* 2 pi))

(defn inc-interval [angle from to]
  (if (== angle to)
    from
    (+ angle (/ pi 216))))

(defn get-radial-pos [[x y] r angle]
  "get position from center of circle based on radius and angle"
  (let [xD (Math/round (* r (Math/cos angle)))
        yD (Math/round (* r (Math/sin angle)))]
    [(+ x xD) (+ y yD)]))

(defn plot-outer-circles [s-angle, r, radius] ;could add r parameter here to change radius of all 6
  (mapv #(hash-map :pos (get-radial-pos [250 250] r %) :radius radius)
        (take 6 (iterate #(+ % (/ two-pi 6)) s-angle))))

(defn draw-circle [ctx [x y] r color]
  (.beginPath ctx)
  (.arc ctx x y r 0 two-pi false)
  (set! (.-fillStyle ctx) color)
  (.fill ctx)
  (.closePath ctx)
  ctx)

(defn context []
  (let [canvas (dom/getElement "canvas")]
    (set! (.-width canvas) 500)
    (set! (.-height canvas) 500)
    (.getContext canvas "2d")))

(defn render []
  (let [s-angle       (atom (/ pi 2))
        e-angle       (atom (/ pi 2))
        interval      (atom 0)
        outer-circles (atom (plot-outer-circles (/ pi 2) (* 250 (Math/abs (Math/sin @interval))) 20))
        ctx           (context)]
    (fn []
      (reset! e-angle (+ @e-angle (/ pi 120)))
      (reset! interval (inc-interval @interval 0 two-pi))
      (.clearRect ctx 0 0 500 500)
      ;(draw-circle ctx [250 250] 40 "#1b4376")
      (doseq [circle (plot-outer-circles @e-angle (* 230 (Math/abs (Math/sin @interval))) 20)]
        (draw-circle ctx (:pos circle) (:radius circle) "#005A31"))
      (doseq [circle (plot-outer-circles @e-angle (* 190 (Math/abs (Math/sin @interval))) 15)]
        (draw-circle ctx (:pos circle) (:radius circle) "#A8CD1B"))
      (doseq [circle (plot-outer-circles @e-angle (* 160 (Math/abs (Math/sin @interval))) 10)]
        (draw-circle ctx (:pos circle) (:radius circle) "#CBE32D"))
      (doseq [circle (plot-outer-circles @e-angle (* 140 (Math/abs (Math/sin @interval))) 5)]
        (draw-circle ctx (:pos circle) (:radius circle) "#F3FAB6"))

      (draw-circle ctx [250 250] 40 "#1b4376")
      (draw-circle ctx [245 245] 30 "black")
      (draw-circle ctx [230 225] 5 "white")
      (draw-circle ctx [223 245] 10 "white"))))

(defn animate []
  (js/setInterval (render) (/ 1000 60)))

(defn drawing-board []
  [:div {:id "content"}
   [:canvas {:id "canvas"}]])

(def attach-renderer
  (with-meta drawing-board
    {:component-did-mount animate}))

(defn app []
  [drawing-board]
  [attach-renderer])

(defn ^:export run []
  (reagent/render-component [app]
                            (.-body js/document)))
