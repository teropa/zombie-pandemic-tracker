(ns zpt.script.drawer
  (:import [java.awt Graphics2D])
  (:import [java.awt.image BufferedImage])
  (:import [java.io File])
  (:import [javax.imageio ImageIO])
  (:import [org.apache.commons.io FileUtils]))
  
(def out-dir "war/tiles")

(defn- open-img [idx level]
  (let [f (File. (str out-dir "/" idx "/" level ".png"))]
    (if (.exists f)
      (ImageIO/read f) 
      (do
        (FileUtils/forceMkdir (File. (str out-dir "/" idx)))
        (BufferedImage. (:size level) (:size level) BufferedImage/TYPE_INT_ARGB)))))

(defn init-dirs []
  (FileUtils/deleteDirectory (File. out-dir))
  (FileUtils/forceMkdir (File. out-dir)))

(defn draw [polys]
  (doseq [[{level :level, idx :idx} items] (group-by #(select-keys % [:level :idx]) polys)]
    (println "Drawing" (count items) "to" idx "/" level) 
    (let [img (open-img idx level)
          #^Graphics2D g (.getGraphics img)]
      (doseq [{poly :poly, color :color} items]
        (let [xs (int-array (map first poly))
              ys (int-array (map second poly))]
          (.setPaint g color)
          (.fillPolygon g xs ys (count poly))))
      (.dispose g)
      (ImageIO/write img "png" (File. (str out-dir "/" idx "/" (:z level) ".png"))))))
        
	
	  	
		