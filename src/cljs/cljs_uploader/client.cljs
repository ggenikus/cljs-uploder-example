(ns cljs-uploader.client 
  (:import goog.net.IframeIo)
  (:require [domina :as dom]
            [goog.events :as gev]
            [domina.events :as ev]))

;; Использую API
;; Google closure
;; отправляем форму на сервер.
;; Для этого создаем объект ```IframeIo```
;; и вызываем у него метод sendFromForm,
;; передадим в параметрах объект формы и 
;; путь куда будет отправляться запрос.
;;
;; Для проверки успешности 
;; вызываем ```setErrorChecker```, 
;; передаем в параметрах 
;; функцию которая
;; должна вернуть false если
;; возник ошибка. Для проверки
;; успешности мы проверяем что
;; Сервер вернул строку "ok" 
;;
;; Для иллюстрации успешности 
;; установим листенеры на три 
;; типа событий: 
;;
;; ``SUCCESS`` - когда 
;; ```errorChecker``` вернет ```false```
;;
;; ```ERROR``` - ```errorChecker``` вернет ```true```
;;
;; ```COMPLETE``` - запрос уйдет на сервер,
;; сработает в не зависимости от errorChecker
;; 
;; Детальнее - ["api goog.net.IframeIo"](http://docs.closure-library.googlecode.com/git/class_goog_net_IframeIo.html)
;;
(defn upload [] 
  (let [io (IframeIo.)] 
    (gev/listen io 
                (aget goog.net.EventType "SUCCESS") 
                #(js/alert "SUCCESS!")) 
    (gev/listen io 
                (aget goog.net.EventType "ERROR") 
                #(js/alert "ERROR!")) 
    (gev/listen io 
                (aget goog.net.EventType "COMPLETE") 
                #(js/alert "COMPLETE!")) 
    (.setErrorChecker io #(not= "ok" (.getResponseText io)))
    (.sendFromForm io (dom/by-id "form") "/upload")))

;; На нажатие на upload-button будм вызывать upload 
(defn ^:export init [] 
  (ev/listen! (dom/by-id "upload-button")  
              :click upload))

