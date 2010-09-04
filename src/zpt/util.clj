(ns zpt.util
  (:import [java.io File])
  (:import [javax.imageio ImageIO]))

(defn mapcat-indexed
  "Returns the result of applying concat to the result
   of applying map-indexed to f and colls"
  [f & colls]
  (apply concat (apply map-indexed f colls)))

(defn parse-double [#^String s]
  (Double/valueOf s))

(defn write-img [img path]
  (ImageIO/write img "png" (File. path)))

(defn stream-img [img out]
  (ImageIO/write img "png" #^java.io.OutputStream out))

