(ns zpt.server.solanum-tile-servlet
  (:gen-class :extends "javax.servlet.http.HttpServlet")
  (:import [java.io File])
  (:import [org.apache.commons.codec.binary Base64])
  (:import [org.apache.commons.io FileUtils]))

(def base "/home/node/osm_tile_cache/tiles/solanum")

(defn- requested-tiles [req]
  (map
    #(.split % ",")
    (.getParameterValues req "t")))

(defn- encode [file]
  (String.
    (.encode (Base64.) 
      (FileUtils/readFileToByteArray file))))

(defn -doGet [_ req res]
  (.setContentType res "multipart/mixed; boundary=\"|||\"")
  (.setHeader res "MIME-Version" "1.0")
  (with-open [out (.getWriter res)]
    (doseq [[t z x y] (requested-tiles req)]
      (let [f (File. (str base "/" t "/" z "/" x "/" y ".png"))]
        (when (.exists f)
          (.println out "--|||")
          (.println out "Content-Type: image/png")
          (.println out (encode f))
          (.flush out))))
    (.println out)
    (.println out "\n--|||--\n")
    (.flush out)))

          
            
      
      
  


