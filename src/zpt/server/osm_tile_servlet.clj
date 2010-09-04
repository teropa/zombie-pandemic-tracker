(ns zpt.server.osm-tile-servlet
  (:gen-class :extends "javax.servlet.http.HttpServlet")
  (:use zpt.util)
  (:require [zpt.server.osm-tile-cache-writer :as cache])
  (:import [java.io IOException])
  (:import [java.awt.color ColorSpace])
  (:import [java.awt.image ColorConvertOp])
  (:import [javax.imageio ImageIO])
  (:import [org.apache.commons.httpclient HttpClient MultiThreadedHttpConnectionManager])
  (:import [org.apache.commons.httpclient.methods GetMethod]))

(let [client (HttpClient. (MultiThreadedHttpConnectionManager.))]
  (defn- read-img [z x y]
    (let [url (str "http://tile.openstreetmap.org/" z "/" x "/" y ".png")
          req (GetMethod. url)]
      (try 
	      (.executeMethod client req)
        (if (= 200 (.getStatusCode req))
          (with-open [in (.getResponseBodyAsStream req)]
            (ImageIO/read in))
          (throw (IOException. (str "Upstream returned " (.getStatusCode req) " for " url))))
       (finally (.releaseConnection req))))))

(defn- grayscale [img]
  (let [convert (ColorConvertOp. (ColorSpace/getInstance ColorSpace/CS_GRAY) nil)]
    (.filter convert img nil)))
  
(defn -doGet [_ req res]
  (let [[_ z x y] (re-find #"\/(\d+)\/(\d+)\/(\d+).png" (.getPathInfo req))
        img (grayscale (read-img z x y))]
    (.setContentType res "image/png")
    (with-open [out (.getOutputStream res)]
      (stream-img img out)
      (.flush out))
    (cache/write-later img z x y)))

