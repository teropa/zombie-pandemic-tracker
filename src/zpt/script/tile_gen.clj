(ns zpt.script.tile-gen
  (:gen-class)
  (:use zpt.script.kml-parser))

(defn -main [& args]
  (parse "data/control_00010_I.kml"))
