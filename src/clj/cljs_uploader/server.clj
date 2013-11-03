(ns cljs-uploader.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :as resources]
            [ring.middleware.multipart-params :as mp]
            [ring.util.response :as response]
            [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route])
  (:gen-class))

(defn- not-nil [x] ((complement nil?) x))

(defn save-file [username {tmpf :tempfile fname :filename}] 
  {:pre [((complement clojure.string/blank?) username) 
         (not-nil tmpf) (not-nil tmpf)]}

  (println "Received " fname " from " username)
  (.mkdir (io/file "uploaded"))
  (io/copy tmpf (io/file "uploaded" fname))
  "ok")

(defroutes app-routes 
  (GET "/" [] (response/resource-response "index.html" {:root "public"}))
  (mp/wrap-multipart-params
    (POST "/upload" [username userfile] (println userfile) (save-file username userfile)))
  (route/resources "/"))

(defn -main [& args]
  (jetty/run-jetty app-routes {:port 3000}))

