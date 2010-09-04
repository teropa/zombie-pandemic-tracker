(ns zpt.server.osm-tile-servlet
  (:gen-class :extends "javax.servlet.http.HttpServlet")
  (:import [javax.imageio ImageIO])
  (:import [org.apache.commons.httpclient HttpClient MultiThreadedHttpConnectionManager])
  (:import [org.apache.commons.httpclient.methods GetMethod]))

(let [client (HttpClient. (MultiThreadedHttpConnectionManager.))]
  (defn- read-img [z x y]
    (let [req (GetMethod. (str "http://tile.openstreetmap.org/" z "/" x "/" y ".png"))]
      (try 
	      (.executeMethod client req)
	      (with-open [in (.getResponseBodyAsStream req)]
	        (ImageIO/read in))
       (finally (.releaseConnection req))))))
  
(defn -doGet [_ req res]
  (let [[_ z x y] (re-find #"\/(\d+)\/(\d+)\/(\d+).png" (.getPathInfo req))
        img (read-img z x y)]
    (.setContentType res "image/png")
    (with-open [out (.getOutputStream res)]
      (ImageIO/write img "png" out)
      (.flush out))))
    