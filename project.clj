(defproject zombie-pandemic-tracker "1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [commons-io/commons-io "1.4"]
                 [commons-httpclient/commons-httpclient "3.1"]]
  :dev-dependencies [[xpp3/xpp3 "1.1.4c"]
                     [com.google.gwt/gwt-user "2.0.4"]
                     [com.google.gwt/gwt-dev "2.0.4"]
                     [com.google.gwt/gwt-servlet "2.0.4"]
                     [gwt-dnd "3.0.1"]
                     [teropa/globetrotter "0.0.1-SNAPSHOT"]
                     [teropa/mxhr "0.0.1-SNAPSHOT"]
                     [lein-gwt "0.1.0"]
                     [uk.org.alienscience/leiningen-war "0.0.7"]]
  
  :namespaces [zpt.server.osm-tile-servlet]
  
  :gwt-modules ["zpt.Client"]
  :gwt-options {:style "PRETTY"
                :XdisableCastChecking ""
                :XdisableClassMetadata ""}
  
  :web-content "war")
