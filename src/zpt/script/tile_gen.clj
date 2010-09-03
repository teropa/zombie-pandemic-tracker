(ns zpt.script.tile-gen
  (:gen-class)
  (:use zpt.script.kml-parser)
  (:use zpt.script.draw-tiles)
  (:import [java.awt Color]))

(def tile-size 256)
(def num-zoom-levels 1)

(def zoomlevels
  (map
    (fn [z]
      (let [tc (Math/pow 2 z)]
        {:z z, :tilecount tc, :size (* tile-size tc)}))
    (range 1 (inc num-zoom-levels))))

(def colors
  {"#Style1" (Color. 183 25 25 140) ; r g b a
   "#Style2" (Color. 183 25 25 160)
   "#Style3" (Color. 183 25 25 180)
   "#Style4" (Color. 183 25 25 200)
   "#Style5" (Color. 183 25 25 220)})

(defn- sign [n]
  (if (< n 0) -1 1))

(defn- lon-rad-to-deg [lon]
  (if (< (Math/abs lon) Math/PI)
    lon
    (- lon (* (sign lon) Math/PI 2))))

(defn- project-point [lon lat]
  (if (<= (Math/abs (- (Math/abs lat) (/ Math/PI 2)))
          1.0e-10)
    (throw (UnsupportedOperationException. "Transformation cannot be computed at the poles"))
    [(* 6378137 (lon-rad-to-deg lon))
     (* 6378137 (Math/log (Math/tan (+ (/ Math/PI 4) (* 0.5 lat)))))]))


(defn- project-polygon [poly]
  (map
    (fn [[lon lat]]
      (project-point (Math/toRadians lon) (Math/toRadians lat))) 
    poly))

(defn- wraps-date-line? [poly]
  false)

(defn- translate-to-pixels [poly z]
  poly)

(defn- drawables [item]
  (let [projected (project-polygon (:poly item))]
    (map
      #(merge item {:z %, :poly (translate-to-pixels projected %)})
      zoomlevels)))

(defn- parse-all []
  (parse ["data/control_00010_I.kml", "data/control_00011_I.kml"]))

(defn -main [& args]
  (doseq [part (partition-all 10000 (parse-all))]
    (draw
      (remove wraps-date-line?
        (mapcat drawables part)))))
