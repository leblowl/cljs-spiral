(ns cljsspiral.core
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.dom :as dom]))

(def pi Math/PI)
(def two-pi (* 2 pi))

(defn inc-interval [angle s-angle e-angle]
  "return next number in an interval between s-angle and e-angle"
  (if (= angle e-angle)
    s-angle
    (+ angle (/ pi 216))))

(defn get-radial-pos [[x y] r angle]
  "get position based on radius and angle from given x y coordinates"
  (let [xD (Math/round (* r (Math/cos angle)))
        yD (Math/round (* r (Math/sin angle)))]
    [(+ x xD) (+ y yD)]))

(defn plot-outer-circles [s-angle, r, circle-r]
  "get positions of 6 circles with radii of circle-r around a circumference"
  (mapv #(hash-map :pos (get-radial-pos [250 250] r %) :radius circle-r)
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
  (let [interval      (atom 0)
        ctx           (context)]
    (fn []
      (reset! interval (inc-interval @interval 0 two-pi))

      (.clearRect ctx 0 0 500 500)
      (doseq [circle (plot-outer-circles @interval (* 230 (Math/abs (Math/sin @interval))) 20)]
        (draw-circle ctx (:pos circle) (:radius circle) "#005A31"))
      (doseq [circle (plot-outer-circles (+ @interval (/ pi 6)) (* 190 (Math/abs (Math/sin @interval))) 15)]
        (draw-circle ctx (:pos circle) (:radius circle) "#A8CD1B"))
      (doseq [circle (plot-outer-circles @interval (* 160 (Math/abs (Math/sin @interval))) 10)]
        (draw-circle ctx (:pos circle) (:radius circle) "#CBE32D"))
      (doseq [circle (plot-outer-circles (+ @interval (/ pi 6)) (* 140 (Math/abs (Math/sin @interval))) 5)]
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
