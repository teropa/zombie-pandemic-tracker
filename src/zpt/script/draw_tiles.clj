(ns zpt.script.draw-tiles
  (:import [java.awt Graphics2D])
  (:import [java.awt.image BufferedImage])
  (:import [java.io File])
  (:import [javax.imageio ImageIO]))
  
(defn draw [polys]
  (doseq [[level items] (group-by :level polys)]
    (let [img (BufferedImage. (:size level) (:size level) BufferedImage/TYPE_INT_ARGB)
          #^Graphics2D g (.getGraphics img)]
      (doseq [poly (map :poly items)]
        (let [xs (int-array (map first poly))
              ys (int-array (map second poly))]
          (.setPaint g java.awt.Color/BLACK)
          (.fillPolygon g xs ys (count poly))))
      (.dispose g)
      (ImageIO/write img "png" (File. "test.png")))))
        
	
	  	
		