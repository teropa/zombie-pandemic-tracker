(ns zpt.script.tile-cutter
  (:import [java.io File])
  (:import [javax.imageio ImageIO])
  (:import [org.apache.commons.io FileUtils])
  (:import [org.apache.commons.io.filefilter FileFilterUtils TrueFileFilter]))

(defn- image-files []
  (FileUtils/listFiles
    (File. "public/tiles")
    (FileFilterUtils/suffixFileFilter "png")
    TrueFileFilter/INSTANCE))

(defn- zoom-level [img-file]
  (let [name (.getName img-file)]
    (Integer/valueOf (.substring name 0 (.indexOf name ".")))))

(defn- dirname [f]
  (.getParent f))

(defn- make-tile-file [parent z x y]
  (FileUtils/forceMkdir (File. (str parent "/" z "/" x)))
  (File. (str parent "/" z "/" x "/" y ".png")))

(defn- cut-tile [img-file img z-idx x-idx y-idx]
  (let [x (* 256 x-idx)
        y (* 256 y-idx)
        tile (.getSubimage img x y 256 256)]
    (ImageIO/write
      tile
      "png"
      (make-tile-file (dirname img-file) z-idx x-idx y-idx))))

(defn cut-tiles []
  (doseq [img-file (image-files)]
    (let [z-idx (zoom-level img-file)
          num-tiles (Math/pow 2 z-idx)
          img (ImageIO/read img-file)]
    (println "Cutting " num-tiles "x" num-tiles "tiles from " img-file)
    (dorun
      (for [x-idx (range num-tiles), y-idx (range num-tiles)]
        (cut-tile img-file img z-idx x-idx y-idx))))))
