(ns zpt.script.tile-gen
  (:gen-class)
  (:use zpt.script.kml-parser)
  (:use zpt.script.drawer)
  (:use zpt.script.tile-cutter)
  (:import [java.awt Color])
  (:import [java.io File])
  (:import [org.apache.commons.io FileUtils]))

(def tile-size 256)
(def num-zoom-levels 7)
(def world-bounds [-2.0037508342789244E7
                   -2.0037508342789244E7
                   2.0037508342789244E7
                   2.0037508342789244E7])
(def world-width (- (world-bounds 2) (world-bounds 0)))
(def world-height (- (world-bounds 3) (world-bounds 1)))

(def zoomlevels
  (map
    (fn [z]
      (let [tc (Math/pow 2 z)]
        {:z z, :tilecount tc, :size (* tile-size tc)}))
    (range 0 (inc num-zoom-levels))))

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
  (let [width-ratio (/ (:size z) world-width)
        height-ratio (/ (:size z) world-height)]
    (map
      (fn [[lon lat]]
        (let [from-left (- lon (world-bounds 0))
              from-top (- (world-bounds 3) lat)]
          [(* from-left width-ratio)
           (* from-top height-ratio)]))
      poly)))

(defn- assoc-color [poly]
  (assoc poly :color (colors (:style poly))))

(defn- drawables [item]
  (let [projected (project-polygon (:poly item))]
    (map
      #(merge item {:level %, :poly (translate-to-pixels projected %)})
      zoomlevels)))

(defn- parse-all []
  (let [extensions (make-array String 1)]
    (aset extensions 0 "kml")
    (parse (sort (FileUtils/listFiles (File. "data") extensions false)))))

(defn- draw-images []
  (doseq [part (partition-all 10000 (parse-all))]
    (println "drawing")
    (draw
      (->> (mapcat drawables part)
           (remove wraps-date-line?)
           (map assoc-color)))))
    
(defn gen-tiles []
  (init-dirs)
  (draw-images)
  (cut-tiles))

