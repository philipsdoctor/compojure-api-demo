(ns compojure-api-demo.core
  (:require [ring.adapter.jetty :as jetty]
            [compojure.api.sweet :as compojure-api :refer [GET POST PUT DELETE ANY describe]]
            [ring.util.http-response :as http-response :refer [ok internal-server-error bad-request not-found content-type unsupported-media-type content-type]]
            [ring.util.request :as request]
            [cheshire.core :as json]
            [compojure-api-demo.routes :as routes])
  (:gen-class))

(defn supported-content-type? [req]
  (if (request/content-type req)
    (= "application/json" (request/content-type req))
    true))

(defn wrap-enforce-content-type
  "Ensures an appropriate Content-Type is set in the request and returns a 415 response if it is not."
  [handler]
  (fn [req]
    (if (supported-content-type? req)
      (handler req)
      ;; This error happens before compojure api's middleware, so we'll need to handle serialization
      ;; to json ourselves.
      (let [body-json (json/generate-string
                        {:type :bad-content-type
                         :message "Did you forget to set the 'Content-Type' header to 'application/json'?"})]
        (-> body-json
            unsupported-media-type
            (content-type "application/json"))))))

(defn custom-error-handler [^Exception e data request]
  (prn "A bad thing happened: " data)
  (internal-server-error {:type "Badness"
                          :message "So Bad."}))

(defn build-app []
  (let [api-info {:exceptions {:handlers {:compojure.api.exception/default custom-error-handler}}
                  :swagger {:ui "/ui"
                            :spec "/ui/swagger.json"
                            :data {:info {:title "Compojure API Demo"
                                          :description "A simple HTTP client for the demo."
                                          :tags [{:name "Another Tag"
                                                  :description "Something totally different."}
                                                 {:name "Info Operations"
                                                  :description "These return some basic information about the app."}]}}}}]

  (->> routes/app-routes ;; gives us a handler
       (compojure-api/api api-info) ;; uses that handler for compojure-api
       (wrap-enforce-content-type)))) ;; custom wrapper

(defn -main
  "Start the Server"
  [& args]
  ;; start server
  (let [server-options {:host "127.0.0.1"
                        :ssl? false
                        :join? false
                        :port 8889}]
    (jetty/run-jetty (build-app) server-options))
  (prn "Server is running...."))
