(ns zpt.script.draw-tiles
  (:use [clojure.contrib.duck-streams :only [read-lines]])
  (:import [java.awt Color])
  (:import [java.io File])
  (:import [javax.imageio ImageIO]))

(defn to-int-arr [strs]
  (into-array
    Integer/TYPE
    (map #(Integer/valueOf %) strs)))
  
(defn polys-grouped-by-tile [polys]
  (reduce
    (fn [res [tile & rst :as line]]
      (update-in res [tile] conj line))
    {}
    polys))
  
(defn draw [polys]
  (println polys))

(comment
	(doseq [[tile polys-in-tile] (polys-grouped-by-tile polys)]
	  (try
		  (let [img-file (File. tile)
		  		img (ImageIO/read img-file)
		  		g2d (.getGraphics img)]
		    (doseq [[_ r g b a poly] polys-in-tile]
		      (try 
			      (let [coords (.split poly ",")
			  		    xpoints (to-int-arr (take-nth 2 coords))
			  		    ypoints (to-int-arr (take-nth 2 (rest coords)))]
			  	    (.setPaint g2d (Color. (Integer/valueOf r) (Integer/valueOf g) (Integer/valueOf b) (Integer/valueOf a)))
			  	    (.fillPolygon g2d xpoints ypoints (/ (count coords) 2)))
		        (catch Exception e (.printStackTrace e))))
		  	(.dispose g2d)
		  	(ImageIO/write img "png" img-file))
	    (catch Exception e (.printStackTrace e)))))
	  	
	  	
		