(ns zpt.server.http-client
  (:import [java.io IOException])  
  (:import [org.apache.commons.httpclient HttpClient MultiThreadedHttpConnectionManager])
  (:import [org.apache.commons.httpclient.methods GetMethod]))


(let [client (HttpClient. (MultiThreadedHttpConnectionManager.))]
  
  (defn GET
    "Makes a HTTP GET request to url, and executes callback
     passing it the response input stream. Throws IOException
     if the server returns something else than 200"
    [url callback]
    (let [req (GetMethod. url)]
      (try 
        (.executeMethod client req)
        (if (= 200 (.getStatusCode req))
          (with-open [in (.getResponseBodyAsStream req)]
            (callback in))
          (throw (IOException. (str "Server returned " (.getStatusCode req) " for " url))))
        (finally (.releaseConnection req))))))
