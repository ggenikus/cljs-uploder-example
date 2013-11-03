(ns cljs-uploader.client
  (:import goog.net.IframeIo)
  (:require [domina :as dom]
            [goog.events :as gev]
            [domina.events :as ev] ))

(defn upload [] 
  (let [io (IframeIo.)] 
    (gev/listen io (aget goog.net.EventType "SUCCESS") #(js/alert "SUCCESS!")) 
    (gev/listen io (aget goog.net.EventType "ERROR") #(js/alert "ERROR!")) 
    (gev/listen io (aget goog.net.EventType "COMPLETE") #(js/alert "COMPLETE!")) 
    (.setErrorChecker io #(not= "ok" (.getResponseText io)))
    (.sendFromForm io (dom/by-id "form") "/upload")))

(defn ^:export init [] 
  (ev/listen! (dom/by-id "upload-button")  :click upload))

