(ns zpt.server.osm-tile-cache-writer
  (:use zpt.util))

(def base (or (System/getenv "OSM_CACHE_DIR")
              "/home/node/osm_tile_cache/tiles/osm"))

(defn- write [_ img z x y]
  (let [f (str base "/" z "/" x "/" y ".png")]
    (write-img img f)))

(let [writer-agent (agent nil)]
  (defn write-later [img z x y]
    (send-off writer-agent write img z x y)))
  
