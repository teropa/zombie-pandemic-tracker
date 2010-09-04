(ns zpt.util)

(defn mapcat-indexed
  "Returns the result of applying concat to the result
   of applying map-indexed to f and colls"
  [f & colls]
  (apply concat (apply map-indexed f colls)))

(defn parse-double [#^String s]
  (Double/valueOf s))



