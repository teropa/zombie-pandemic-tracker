(defproject zombie-pandemic-tracker "1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [xpp3/xpp3 "1.1.4c"]
                 [commons-io/commons-io "1.4"]]
  :dev-dependencies [[com.google.gwt/gwt-user "2.0.4"]
                     [com.google.gwt/gwt-dev "2.0.4"]
                     [gwt-dnd "3.0.1"]
                     [teropa/globetrotter "0.0.1-SNAPSHOT"]
                     [teropa/mxhr "0.0.1-SNAPSHOT"]
                     [lein-gwt "0.1.0"]]
  
  :gwt-modules ["zpt.Client"]
  :gwt-options {:XdisableCastChecking ""
                :XdisableClassMetadata ""})


