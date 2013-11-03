(ns cljs-uploader.client
  (:require [domina :as dom]
            [domina.events :as ev]
            [goog.net.IframeIo :as gnet]))

(defn upload []
  (.log js/console "uploading")
  (.log js/console  (goog.net.IframeIo.)))
   
   ; (gnet/sendFromForm (dom/by-id "form")
                      ; "/userfile")

(defn ^:export init [] 
  (ev/listen! (dom/by-id "upload-button")  :click upload))

