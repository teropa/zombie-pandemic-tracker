(ns zpt.script.kml-parser
  (:import [java.io FileReader])
  (:import [org.xmlpull.v1 XmlPullParser XmlPullParserFactory]))

(defn- parse-polygon [s]
  (map
    (fn [point]
      (let [[lon lat] (.split point "," 2)]
        [(Double/valueOf lon) (Double/valueOf lat)]))
    (.split s " ")))

(defn pull-step [parser rdr]
  (letfn [(step [parser tag-stack style]
            (condp = (.next parser)
              XmlPullParser/START_TAG
                (recur parser (conj tag-stack (.getName parser)) style)
              XmlPullParser/END_TAG
                (recur parser (pop tag-stack) style)
              XmlPullParser/TEXT
                (condp = (peek tag-stack)
                  "coordinates"
                    (lazy-seq
                      (cons
                        {:poly (parse-polygon (.getText parser)), :style style}
                        (step parser tag-stack style)))
                  "styleUrl"
                    (recur parser tag-stack (.getText parser))
                  (recur parser tag-stack style))
              XmlPullParser/END_DOCUMENT
                (do (.close rdr) nil)
              (recur parser tag-stack style)))]
    (step parser [] nil)))

(defn parse [file]
  (let [fac (XmlPullParserFactory/newInstance)
        reader (FileReader. file)]
    (.setNamespaceAware fac true)
    (let [parser (.newPullParser fac)]
      (.setInput parser reader)
      (pull-step parser reader))))
  
