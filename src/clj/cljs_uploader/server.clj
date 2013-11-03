(ns cljs-uploader.server
  (:require [ring.adapter.jetty :as jetty] 
            [ring.middleware.resource :as resources]
            [ring.middleware.multipart-params :as mp] 
            [ring.util.response :as response] 
            [clojure.java.io :as io] 
            [compojure.core :refer :all] 
            [compojure.route :as route])
  (:gen-class))


;; Создадим главную страницу, на
;; странице у нас будет находится
;; одна форма с полем для ввода
;; текста и полем для отправки файла.
;;
;; Также, кнопку для того
;; что-бы можно было отправить форму
;; на сервер.
;;
;; Подключим сгенерированный js 
;; и вызовем метод который запустит
;; наш клиентский код.
;;
(def html-page 
"<!DOCTYPE html>
<html>
  <head>
    <link rel='stylesheet' 
       href='css/page.css' />
  </head>
  <body>
  <div> 
    <form id='form' 
      enctype='multipart/form-data' method='POST'>

      Username: <input name='username' type='text' > 
      <br/><br/>

      <input name='userfile' type='file' /> 
         <br/><br/>

      <input type='button' id='upload-button'
              value='Upload'  />
     </form>
  </div>
  </body>
  <script src='js/cljs.js'></script>
  <script > 
    cljs_uploader.client.init();
  </script>
</html>")


;; Приватная функция,
;; проверяем что ```x``` не ```nil```
(defn- not-nil [x] ((complement nil?) x))

;; Еще одна приватная функция,
;; проверяет что строка не ```nil``` и не пустая
(defn- not-blank [x] ((complement clojure.string/blank?) x))


;; Функция которая отвечает за сохранение файла на сервере, 
;; принимает имя пользователя 
;; (которое должно прийти с формы) 
;; и мапу с информацией о файле.
;; 
;; В прекондишенах проверяем что 
;; имя пользователь не пустое (и не ```nil```) 
;; и что информация о файле не ```nil```
;;
;; Выводим в консоль информацию о пользователе 
;; и файле который он отправил 
;; (в реальном приложении должно быть наверное что-то 
;; более интеллектуальное)
;;
;; Создадим папку в которую будем класть файлы от 
;; пользователей (папка создается только один раз). 
;; В принципе проверку на существование папки можно 
;; было не делать а просто вызвать ```(.mkdir (io/file "uploaded"))```
;; эффект будет одинаковый, 
;; mkdir не должен создавать папку если она уже существует. 
;; Но для наглядности я решил добавить проверку.
;; 
;; Скопируем содержимое tmpf в файл с именем fname
;;
;; Если все прошло успешно - возвращаем строку 'ok'. 
;; Если что то пройдет не так ring вернет 
;; html c кодом ошибки и ексепшеном
;;
(defn save-file 
  [username {tmpf :tempfile fname :filename}] 
  ;; tmpf - временный файл в который веб  
  ;;        сервер сохранит файл отправленный
  ;;        юзером с формы 
  ;;
  ;; fnmae - название файла 
  ;;         отправленного юзером

  {:pre [(not-blank username)
         (not-nil tmpf) 
         (not-blank fname)]}

  (println "Received " fname " from " username)

  (when (not (.exists (io/file "uploaded"))) 
    (.mkdir (io/file "uploaded")))

  (io/copy tmpf (io/file "uploaded" fname))
  "ok")

;; Спецификация путей. 
;;
;; ```GET /``` - отдаем html-page
;;
;; ```POST  '/upload'``` -  загружаем файл.
;;
;; Для этого handler нужно обернуть в 
;; ```mp/wrap-multipart-params```
;;
;; В ```userfile``` будет содержатся информация о 
;; файле, примерно вот такая:
;; <pre>{:size 15,</pre>
;; <pre> :tempfile #&lt;File /var/folders/ring-multipart-10.tmp&gt;, </pre>
;; <pre> :content-type text/plain, </pre>
;; <pre> :filename New Text Document.txt}</pre>
;;
;; Все остальные запросы  ```GET /*``` будет мапить на
;; содержимое из ресурсов, для загрузки CSS и JS
;;
(defroutes app-routes 
  (GET "/" [] html-page)
  (mp/wrap-multipart-params
    (POST "/upload" [username userfile] (save-file username userfile)))
  (route/resources "/"))

(defn -main [& args]
  (jetty/run-jetty app-routes {:port 3000}))

