(ns zpt.server.header-filter
  (:gen-class :implements [javax.servlet.Filter]))

(defn- set-cache-headers [req res]
  (let [url (str (.getRequestURL req))]
    (.setHeader res
      "Cache-Control"
      (cond
        (or (.contains url ".cache.") (.contains url "/tiles/"))
          "public max-age=31536000" ; one year
        (.contains url ".nocache.")
          "no-cache, no-store" ; no cache
        :else
          "public max-age=3600")))) ; one hour
  
(defn -doFilter [_ req res chain]
  (set-cache-headers req res)
  (.doFilter chain req res))

(defn -init [_ _])
(defn -destroy [_])


